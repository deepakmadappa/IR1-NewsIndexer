package edu.buffalo.cse.irf14;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.ObjectInputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.buffalo.cse.irf14.document.Document;
import edu.buffalo.cse.irf14.document.FieldNames;
import edu.buffalo.cse.irf14.index.DocumentEntry;
import edu.buffalo.cse.irf14.index.DocumentObject;
import edu.buffalo.cse.irf14.index.IndexEntry;
import edu.buffalo.cse.irf14.index.IndexReader;
import edu.buffalo.cse.irf14.index.IndexType;
import edu.buffalo.cse.irf14.index.TFDFSet;
import edu.buffalo.cse.irf14.query.LogicalOperator;
import edu.buffalo.cse.irf14.query.Query;
import edu.buffalo.cse.irf14.query.QueryParser;
import edu.buffalo.cse.irf14.query.QueryParserException;
import edu.buffalo.cse.irf14.query.TreeNode;
import edu.buffalo.cse.irf14.query.test.FileQueryTest;

/**
 * Main class to run the searcher.
 * As before implement all TODO methods unless marked for bonus
 * @author nikhillo
 *
 */
public class SearchRunner {
	public enum ScoringModel {TFIDF, OKAPI};
	private String mIndexDir = null;
	private String mCorpusDir = null;
	private char mMode;
	private PrintStream mOutStream = null;
	HashMap<String, DocumentObject> mAllDocs = null;
	private HashMap<String, IndexEntry> mTermIndex = null;
	private HashMap<String, IndexEntry> mPlaceIndex = null;
	private HashMap<String, IndexEntry> mAuthorIndex = null;
	private HashMap<String, IndexEntry> mCategoryIndex = null;
	private double mAverageLength = 0;
	private double B = 0.75f;
	private double K1 = 1.3f;
	/**
	 * Default (and only public) constuctor
	 * @param indexDir : The directory where the index resides
	 * @param corpusDir : Directory where the (flattened) corpus resides
	 * @param mode : Mode, one of Q or E
	 * @param stream: Stream to write output to
	 */
	public SearchRunner(String indexDir, String corpusDir, 
			char mode, PrintStream stream) {
		mIndexDir = indexDir;
		mCorpusDir = corpusDir;
		mMode = mode;
		mOutStream = stream;
		mTermIndex = new IndexReader(mIndexDir, IndexType.TERM).GetIndex();
		mPlaceIndex = new IndexReader(mIndexDir, IndexType.PLACE).GetIndex();
		mAuthorIndex = new IndexReader(mIndexDir, IndexType.AUTHOR).GetIndex();
		mCategoryIndex = new IndexReader(mIndexDir, IndexType.CATEGORY).GetIndex();
		try {
			FileInputStream fin = new FileInputStream(indexDir + File.separator + "docs.list");
			ObjectInputStream ois = new ObjectInputStream(fin);
			mAllDocs = (HashMap<String, DocumentObject>)(ois.readObject());
			long len = 0;
			int count = 0;
			for (DocumentObject docObj : mAllDocs.values()) {
				len += docObj.mDocument.mDocumentLenght;
				count++;
			}
			mAverageLength = len/count;
		}catch(Exception ex) {
			System.out.println("error occured while reading from file");
		}

	}

	/**
	 * Method to execute given query in the Q mode
	 * @param userQuery : Query to be parsed and executed
	 * @param model : Scoring Model to use for ranking results
	 */
	public void query(String userQuery, ScoringModel model) {
		Query parsedQuery = null;
		try {
			long startTime = System.currentTimeMillis();
			parsedQuery = QueryParser.parse(userQuery, "OR");
			List<DocumentEntry> outDocumentList = new ArrayList<DocumentEntry>();
			boolean isRootNot = ApplyInorderTraversal(outDocumentList , parsedQuery.mRootNode);
			Set<String> docSet = null;
			if(isRootNot) {
				docSet = invertDocs(outDocumentList);
			}
			else {
				docSet = new HashSet<String>();
				for (DocumentEntry doc : outDocumentList) {
					docSet.add(doc.mFileID);
				}
			}

			List<DocumentRelevance> relevantDocs = getRelevantDocs(parsedQuery, docSet, model);

			long endTime = System.currentTimeMillis();
			String totalTime = String.valueOf(endTime-startTime);
			mOutStream.println("Query:" + userQuery);
			mOutStream.println("Query Time:" + totalTime);


			//we got the docs now write them to printStream
			int count = 0;
			for (DocumentRelevance documentRelevance : relevantDocs) {
				count++;
				double relevance = ((double)Math.round(documentRelevance.mRelavance * 100000))/100000 ;
				mOutStream.println("Result Rank:" + String.valueOf(count) + ", Document:" + documentRelevance.mDocID + ", Result Relavancy:" + String.valueOf(relevance));
				String snippet = getSnippetForDoc(documentRelevance.mDocID, parsedQuery.mLeafNodes);
				mOutStream.println(snippet);
				if(count == 10)
					break; //only top 10
			}

		} catch (Exception ex) {

		}
		if(parsedQuery == null) {
			System.out.println("QueryParser.parse returned null");
		}
	}

	private String getSnippetForDoc(String mDocID, 
			List<TreeNode> mLeafNodes) {
		String retString = "";
		try {
			Document doc = mAllDocs.get(mDocID).mDocument;
			retString = "<b>" + doc.getField(FieldNames.TITLE)[0] + "</b>";
			String fileName = mCorpusDir + File.separatorChar + mDocID;
			BufferedReader br = new BufferedReader(new FileReader(fileName));
			String line; 
			List<String> lines = new ArrayList<String>();
			while( (line = br.readLine()) != null && lines.size() < 3) {
				boolean bUseThisLine = false;
				for (TreeNode treeNode : mLeafNodes) {

					Pattern pat = Pattern.compile(treeNode.mSearchString, Pattern.CASE_INSENSITIVE);
					Matcher mat = pat.matcher(line);
					if(mat.find()) {
						line = mat.replaceAll("<b>" + treeNode.mSearchString + "</b>");
						lines.add(line);
						break;
					}

				}
			}
			for (String string : lines) {
				retString += "\n" + string;
			}
		} catch (Exception ex) {

		}
		return retString;
	}

	public List<DocumentRelevance> getRelevantDocs(Query parsedQuery,
			Set<String> docList, ScoringModel model) {
		List<DocumentRelevance> unSortedList = null;
		if(model == ScoringModel.TFIDF) {
			unSortedList = getTFIDFRelevance(parsedQuery, docList, model);
		} else if (model == ScoringModel.OKAPI) {
			unSortedList = getOkapiRelevance(parsedQuery, docList, model);
		}
		unSortedList.sort(new Comparator<DocumentRelevance>() {
			public int compare(DocumentRelevance o1, DocumentRelevance o2) {
				if(o1.mRelavance < o2.mRelavance) {
					return 1;
				} else if(o1.mRelavance > o2.mRelavance) {
					return -1;
				}
				return 0;
			}
		});
		if(ScoringModel.OKAPI == model) {
			double maxRelavance = unSortedList.get(0).mRelavance;
			for (DocumentRelevance documentRelevance : unSortedList) {
				documentRelevance.mRelavance = documentRelevance.mRelavance/ maxRelavance;
			}
		}
		return unSortedList;
	}

	private List<DocumentRelevance> getOkapiRelevance(Query parsedQuery,
			Set<String> docList, ScoringModel model) {

		Hashtable<String, DocumentRelevance> docRelevanceTable = new Hashtable<String, DocumentRelevance>();
		for (TreeNode node : parsedQuery.mLeafNodes) {
			if(node.mIsNot) {
				continue;	//ignore not nodes
			}
			List<DocumentEntry> docsForTerm = node.mDocsForTerm;
			double idf = ((double)mAllDocs.size())/((double) docsForTerm.size());
			idf = Math.log10(idf);
			for (DocumentEntry documentEntry : docsForTerm) {
				if(!docList.contains(documentEntry.mFileID))
					continue;	//need to apply only on final list
				int tf = documentEntry.mFrequencyInFile;
				double tfw = 1 + Math.log10(tf);
				DocumentRelevance docRelevanceToUpdate = null;
				DocumentObject docObj = mAllDocs.get(documentEntry.mFileID);
				double denom = (1-B) + B * (docObj.mDocument.mDocumentLenght / mAverageLength);
				double rsvd = idf * (((K1+1)*tfw)/(K1 * denom + tfw));
				if(docRelevanceTable.containsKey(documentEntry.mFileID)) {
					docRelevanceToUpdate = docRelevanceTable.get(documentEntry.mFileID);
				} else {
					docRelevanceToUpdate = new DocumentRelevance(documentEntry.mFileID);
					docRelevanceTable.put(documentEntry.mFileID, docRelevanceToUpdate);
				}
				docRelevanceToUpdate.mRelavance += rsvd;
			}
		}
		List<DocumentRelevance> outList = new ArrayList<DocumentRelevance>(docRelevanceTable.values());	
		return outList;
	}

	private List<DocumentRelevance> getTFIDFRelevance(Query parsedQuery,
			Set<String> docList, ScoringModel model) {
		List<DocumentRelevance> outList = new ArrayList<DocumentRelevance>();
		Hashtable<String, DocumentRelevance> docRelevanceTable = new Hashtable<String, DocumentRelevance>();
		for (TreeNode node : parsedQuery.mLeafNodes) {
			if(node.mIsNot) {
				continue;	//ignore not nodes
			}
			//iterate term by term
			List<DocumentEntry> docsForTerm = node.mDocsForTerm;
			double idf = ((double)mAllDocs.size())/((double) docsForTerm.size());
			idf = Math.log10(idf);
			for (DocumentEntry documentEntry : docsForTerm) {
				if(!docList.contains(documentEntry.mFileID))
					continue;	//need to apply only on final list
				int tf = documentEntry.mFrequencyInFile;
				double tfw = 1 + Math.log10(tf);
				double tfidf = tfw * idf;
				DocumentRelevance docRelevanceToUpdate = null;
				if(docRelevanceTable.containsKey(documentEntry.mFileID)) {
					docRelevanceToUpdate = docRelevanceTable.get(documentEntry.mFileID);
				} else {
					docRelevanceToUpdate = new DocumentRelevance(documentEntry.mFileID);
					docRelevanceTable.put(documentEntry.mFileID, docRelevanceToUpdate);
				}
				docRelevanceToUpdate.mRelavance += tfidf;
			}
		}

		for (DocumentRelevance documentRelevance : docRelevanceTable.values()) {
			//documentRelevance.mRelavance = documentRelevance.mRelavance/(getDocumentNormalizationTerm(documentRelevance.mDocID) * Math.sqrt(parsedQuery.mLeafNodes.size()) );
			double normalizationTerm = getDocumentNormalizationTerm(documentRelevance.mDocID);
			documentRelevance.mRelavance = documentRelevance.mRelavance/(normalizationTerm * Math.sqrt(parsedQuery.mLeafNodes.size()) );
			outList.add(documentRelevance);
		}
		return outList;
	}

	private double getDocumentNormalizationTerm(String docID) {
		HashMap<String, TFDFSet> document = mAllDocs.get(docID).mTFDFMap;
		int N = mAllDocs.size();
		double normalizationTerm = 0;
		for (Entry<String, TFDFSet> term : document.entrySet()) {
			TFDFSet tfdf = term.getValue();
			double tfidf = (1 + Math.log10(tfdf.mTF)) * (Math.log10(((double)N)/((double)(tfdf.mDF))));
			normalizationTerm += Math.pow(tfidf, 2);
		}
		return Math.sqrt(normalizationTerm);
	}

	private Set<String> invertDocs(List<DocumentEntry> outDocumentList) {
		Set<String> outlist = new HashSet<String>();
		for (DocumentEntry documentEntry : outDocumentList) {
			if(!mAllDocs.containsKey(documentEntry.mFileID)) {
				outlist.add(documentEntry.mFileID);
			}
		}
		return outlist;
	}

	/**
	 * 
	 * return: returns if the return if the out
	 */
	public boolean ApplyInorderTraversal(List<DocumentEntry> outDocumentList, TreeNode node) throws QueryParserException{
		if(node.mLeftChild == null && node.mRightChild == null) {
			//this is leaf node we need to evaluate the term here, and fill the outList;
			String searchString = node.mSearchString;
			if(node.mIsSingleQuotedString) {
				return CheckForQuotedString(outDocumentList, node);
			} else {
				IndexEntry ret = getIndexForString(searchString, node.mIndexType);
				if(ret != null) {
					outDocumentList.addAll(ret.mDocumentList);
					node.mDocsForTerm.addAll(ret.mDocumentList);
				}
			}
			return node.mIsNot;
		}
		if(node.mLeftChild == null || node.mRightChild == null) {
			throw new QueryParserException("One of the children can't be null, either both are null or none");
		}

		//To avoid evaluating NOT into a huge list of documents, I'm using a few set theory shortcuts to keep the list as small as possible till the 
		//evaluation reaches root, at only at root not will be applied and we get a huge list.
		//Here are the set theory rules (S represents universal set of all documents)
		// (A U ~B) => (A U (S - B)) => ( ~(B-A))
		// (A ^ ~B) => (A ^ (S - B)) => (A-B)
		// and De'morgans law
		List<DocumentEntry> leftList = new ArrayList<DocumentEntry>();
		List<DocumentEntry> rightList = new ArrayList<DocumentEntry>();
		boolean isLeftNOT = ApplyInorderTraversal(leftList, node.mLeftChild);
		boolean isRightNOT = ApplyInorderTraversal(rightList, node.mRightChild);
		if(node.mOperator == LogicalOperator.AND) {
			if(isLeftNOT == false && isRightNOT == false) { 
				//there is no negation on either side, return the intersection
				outDocumentList.addAll(intersect(leftList, rightList));
				return false;
			}
			if(isLeftNOT == true && isRightNOT == true) {
				//both are negated use De'Morgans law and return negation of UNION
				outDocumentList.addAll(union(leftList, rightList));
				return true;
			}
			if(isLeftNOT == true) {
				//left is negative return right - left;
				outDocumentList.addAll(difference(rightList, leftList));
				return false;
			} else {
				//right is negated return left - right;
				outDocumentList.addAll(difference(leftList, rightList));
				return false;
			}
		} else {	//OR
			if(isLeftNOT == false && isRightNOT == false) { 
				//there is no negation on either side, return the UNION
				outDocumentList.addAll(union(leftList, rightList));
				return false;
			}
			if(isLeftNOT == true && isRightNOT == true) {
				//both are negated use De'Morgans law and return negation of INTERSECTION
				outDocumentList.addAll(intersect(leftList, rightList));
				return true;
			}
			if(isLeftNOT == true) {
				//left is negative return ~(left - right);
				outDocumentList.addAll(difference(leftList, rightList));
				return true;
			} else {
				//right is negated return left - right;
				outDocumentList.addAll(difference(rightList, leftList));
				return true;
			}
		}
	}

	IndexEntry getIndexForString(String searchString, IndexType type) {
		switch (type) {
		case TERM:
			return mTermIndex.get(searchString);
		case CATEGORY:
			return mCategoryIndex.get(searchString);
		case AUTHOR:
			return mAuthorIndex.get(searchString);
		case PLACE:
			return mPlaceIndex.get(searchString);

		default:
			return null;
		}
	}

	/**
	 * Checks for positional adjecency for quoted string
	 * @param outDocumentList
	 * @param node
	 * @return
	 * @throws QueryParserException
	 */

	boolean CheckForQuotedString(List<DocumentEntry> outDocumentList, TreeNode node) throws QueryParserException{ 
		String searchString = node.mSearchString.trim();
		searchString = searchString.substring(1, searchString.length() -1);
		List<String> terms = new ArrayList<String>(Arrays.asList( searchString.split(" ")));
		List<IndexEntry> indexEntries = new ArrayList<IndexEntry>(terms.size());
		//Get index for each term
		for (String term : terms) {
			IndexEntry index = getIndexForString(term, node.mIndexType);
			if(index == null || index.mDocumentFrequency == 0) {
				return node.mIsNot;	//one of the terms is not found, no need to continue
			}
			indexEntries.add(index);
		}
		List<DocumentEntry> tempList = new ArrayList<DocumentEntry>(indexEntries.get(0).mDocumentList);
		for(int i=1; i< indexEntries.size(); i++) {
			//TODO:this is just to get a list of docIDs which have all 3 terms, can do it faster 
			tempList = intersect(tempList, new ArrayList<DocumentEntry>(indexEntries.get(i).mDocumentList));
		}
		HashSet<String> docIDsWithAllTerms = new HashSet<String>();
		for (DocumentEntry docEntry : tempList) {
			docIDsWithAllTerms.add(docEntry.mFileID);
		}

		//Now we need to filter and get the list of doc entries which contain all terms. Need to do this for each term thus a loop
		List<List<DocumentEntry>> lstAllTermsPresentDocs = new ArrayList<List<DocumentEntry>>(terms.size());
		for (IndexEntry indexEntry : indexEntries) {
			List<DocumentEntry> lst = new ArrayList<DocumentEntry>();
			for (DocumentEntry documentEntry : indexEntry.mDocumentList) {
				if(docIDsWithAllTerms.contains(documentEntry.mFileID)) {
					lst.add(documentEntry);
				}
			}
			lstAllTermsPresentDocs.add(lst);
		}

		//now we have the lists 
		//verify that they are in position
		for(int i=0; i<lstAllTermsPresentDocs.get(0).size(); i++ ) {
			DocumentEntry docEntryFirstTerm = lstAllTermsPresentDocs.get(0).get(i);
			//check if the terms are in order
			DocumentEntry copyDocEntry = null;
			for (Integer position : docEntryFirstTerm.mPositionList) {
				boolean bPositionsAreCorrect = true;
				//foreach position of the first term check the other terms positions
				for (int j=1; j<lstAllTermsPresentDocs.size(); j++) {
					if(!lstAllTermsPresentDocs.get(j).get(i).mPositionList.contains(position+j)) {
						bPositionsAreCorrect = false;
						break;
					}
				}

				if(bPositionsAreCorrect) {
					//All the positions are correct all this document
					if(copyDocEntry == null) {
						copyDocEntry = new DocumentEntry(docEntryFirstTerm.mFileID);
					}
					copyDocEntry.mFrequencyInFile++;
				}
			}
			if(copyDocEntry!=null) {
				outDocumentList.add(copyDocEntry);
			}
		}
		node.mDocsForTerm.addAll(outDocumentList);
		return node.mIsNot;
	}

	List<DocumentEntry> intersect(List<DocumentEntry> left, List<DocumentEntry> right) {
		List<DocumentEntry> returnList = new ArrayList<DocumentEntry>();
		HashSet<String> hashSet = new HashSet<String>();
		for (DocumentEntry doc : left) {
			hashSet.add(doc.mFileID);
		}

		for (DocumentEntry rightDoc : right) {
			if(hashSet.contains(rightDoc.mFileID)) {
				returnList.add(rightDoc);
			}
		}
		return returnList;
	}

	List<DocumentEntry> union(List<DocumentEntry> left, List<DocumentEntry> right) {
		HashSet<String> hashSet = new HashSet<String>();
		List<DocumentEntry> returnList = new ArrayList<DocumentEntry>();
		for (DocumentEntry doc : left) {
			hashSet.add(doc.mFileID);
			returnList.add(doc);
		}

		for (DocumentEntry doc : right) {
			if(!hashSet.contains(doc.mFileID)) {
				returnList.add(doc);
			}
		}
		return returnList;
	}

	/**
	 * 
	 * @param left
	 * @param right
	 * @return set operation result of left - right
	 */
	List<DocumentEntry> difference(List<DocumentEntry> left, List<DocumentEntry> right) {
		HashMap<String, DocumentEntry> hashMap = new HashMap<String, DocumentEntry>();
		for (DocumentEntry doc : left) {
			hashMap.put(doc.mFileID, doc);
		}

		for (DocumentEntry rightDoc : right) {
			if(hashMap.containsKey(rightDoc.mFileID)) {
				hashMap.remove(rightDoc.mFileID);
			}
		}
		List<DocumentEntry> returnList = new ArrayList<DocumentEntry>(hashMap.values());

		return returnList;
	}

	/**
	 * Method to execute queries in E mode
	 * @param queryFile : The file from which queries are to be read and executed
	 */
	public void query(File queryFile) {
		try {
			BufferedReader br = new BufferedReader(new FileReader(queryFile));
			String line = br.readLine();
			if(line == null) {
				br.close();
				throw new Exception();
			}
			String[] parts = line.split("=");
			int numberOfQueries = Integer.parseInt(parts[1]);
			String query = null;
			String queryID = null;
			List<String> outputs = new ArrayList<String>();
			for (int i=0; i < numberOfQueries; i++) {
				line = br.readLine();
				query = getQuery(line);
				queryID = getQueryName(line);
				Query parsedQuery = QueryParser.parse(query, "AND");
				List<DocumentEntry> outDocumentList = new ArrayList<DocumentEntry>();
				boolean isRootNot = ApplyInorderTraversal(outDocumentList , parsedQuery.mRootNode);
				Set<String> docSet = null;
				if(isRootNot) {
					docSet = invertDocs(outDocumentList);
				}
				else {
					docSet = new HashSet<String>();
					for (DocumentEntry doc : outDocumentList) {
						docSet.add(doc.mFileID);
					}
				}

				List<DocumentRelevance> relevantDocs = getRelevantDocs(parsedQuery, docSet, ScoringModel.OKAPI);
				String results = StreamRelevanceScores(relevantDocs);
				results = queryID + ":" + results;
				outputs.add(results);
			}
			mOutStream.println("numResults=" + String.valueOf(outputs.size()));
			for (String str: outputs) {
				mOutStream.println(str);
			}
			br.close();

		} catch (Exception ex) {
			mOutStream.println("File doesn't exist or doesn't have as many lines as advertized");
		}
	}

	private String StreamRelevanceScores(List<DocumentRelevance> relevantDocs) {
		String out = "";
		int count = 0;
		for (DocumentRelevance documentRelevance : relevantDocs) {
			count++;
			double relevance = ((double)Math.round(documentRelevance.mRelavance * 100000))/100000 ;
			out = out+ documentRelevance.mDocID + "#" + relevance + ", ";
			if(count==10)
				break;
		}
		if(!out.isEmpty()) {
			out = out.substring(0, out.length() - 2);
		}
		return "{" + out + "}";
	}

	private String getQuery(String line) {
		line = line.trim();
		int firstColon = line.indexOf(":");
		return line.substring(firstColon + 2, line.length() -1);
	}

	private String getQueryName(String line) {
		line = line.trim();
		int firstColon = line.indexOf(":");
		return line.substring(0, firstColon);
	}

	/**
	 * General cleanup method
	 */
	public void close() {
		mOutStream.close();
	}

	/**
	 * Method to indicate if wildcard queries are supported
	 * @return true if supported, false otherwise
	 */
	public static boolean wildcardSupported() {
		//TODO: CHANGE THIS TO TRUE ONLY IF WILDCARD BONUS ATTEMPTED
		return false;
	}

	/**
	 * Method to get substituted query terms for a given term with wildcards
	 * @return A Map containing the original query term as key and list of
	 * possible expansions as values if exist, null otherwise
	 */
	public Map<String, List<String>> getQueryTerms() {
		//TODO:IMPLEMENT THIS METHOD IFF WILDCARD BONUS ATTEMPTED
		return null;

	}

	/**
	 * Method to indicate if speel correct queries are supported
	 * @return true if supported, false otherwise
	 */
	public static boolean spellCorrectSupported() {
		//TODO: CHANGE THIS TO TRUE ONLY IF SPELLCHECK BONUS ATTEMPTED
		return false;
	}

	/**
	 * Method to get ordered "full query" substitutions for a given misspelt query
	 * @return : Ordered list of full corrections (null if none present) for the given query
	 */
	public List<String> getCorrections() {
		//TODO: IMPLEMENT THIS METHOD IFF SPELLCHECK EXECUTED
		return null;
	}
}
