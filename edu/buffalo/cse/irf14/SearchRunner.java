package edu.buffalo.cse.irf14;

import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import edu.buffalo.cse.irf14.document.ParserException;
import edu.buffalo.cse.irf14.index.DocumentEntry;
import edu.buffalo.cse.irf14.index.IndexEntry;
import edu.buffalo.cse.irf14.index.IndexReader;
import edu.buffalo.cse.irf14.index.IndexType;
import edu.buffalo.cse.irf14.query.LogicalOperator;
import edu.buffalo.cse.irf14.query.Query;
import edu.buffalo.cse.irf14.query.QueryParser;
import edu.buffalo.cse.irf14.query.QueryParserException;
import edu.buffalo.cse.irf14.query.TreeNode;

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
	private HashMap<String, IndexEntry> mTermIndex = null;
	private HashMap<String, IndexEntry> mPlaceIndex = null;
	private HashMap<String, IndexEntry> mAuthorIndex = null;
	private HashMap<String, IndexEntry> mCategoryIndex = null;
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
		mTermIndex = new IndexReader(indexDir, IndexType.TERM).GetIndex();
		mPlaceIndex = new IndexReader(indexDir, IndexType.PLACE).GetIndex();
		mAuthorIndex = new IndexReader(indexDir, IndexType.AUTHOR).GetIndex();
		mCategoryIndex = new IndexReader(indexDir, IndexType.CATEGORY).GetIndex();
	}
	
	/**
	 * Method to execute given query in the Q mode
	 * @param userQuery : Query to be parsed and executed
	 * @param model : Scoring Model to use for ranking results
	 */
	public void query(String userQuery, ScoringModel model) {
		Query parsedQuery = null;
		try {
		 parsedQuery = QueryParser.parse(userQuery, "AND");
		} catch (Exception ex) {
			
		}
		if(parsedQuery == null) {
			System.out.println("QueryParser.parse returned null");
		}
	}
	
	/**
	 * 
	 * return: returns if the return if the out
	 */
	private boolean ApplyInorderTraversal(List<DocumentEntry> outDocumentList, TreeNode node) throws QueryParserException{
		if(node.mLeftChild == null && node.mRightChild == null) {
			//this is leaf node we need to evaluate the term here, and fill the outList;
			String searchString = node.mSearchString;
			if(node.mIsSingleQuotedString) {
				return CheckForQuotedString(outDocumentList, node);
			} else {
				IndexEntry ret = getIndexForString(searchString, node.mIndexType);
				if(ret != null) {
					outDocumentList.addAll(ret.mDocumentList);
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
				outDocumentList = intersect(leftList, rightList);
				return false;
			}
			if(isLeftNOT == true && isRightNOT == true) {
				//both are negated use De'Morgans law and return negation of UNION
				outDocumentList = union(leftList, rightList);
				return true;
			}
			if(isLeftNOT == true) {
				//left is negative return right - left;
				outDocumentList = difference(rightList, leftList);
				return false;
			} else {
				//right is negated return left - right;
				outDocumentList = difference(leftList, rightList);
				return false;
			}
		} else {	//OR
			if(isLeftNOT == false && isRightNOT == false) { 
				//there is no negation on either side, return the UNION
				outDocumentList = union(leftList, rightList);
				return false;
			}
			if(isLeftNOT == true && isRightNOT == true) {
				//both are negated use De'Morgans law and return negation of INTERSECTION
				outDocumentList = intersect(leftList, rightList);
				return true;
			}
			if(isLeftNOT == true) {
				//left is negative return ~(left - right);
				outDocumentList = difference(leftList, rightList);
				return true;
			} else {
				//right is negated return left - right;
				outDocumentList = difference(rightList, leftList);
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
		List<String> terms = new ArrayList<String>(Arrays.asList( node.mSearchString.split(" ")));
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
					if(!lstAllTermsPresentDocs.get(i).get(j).mPositionList.contains(position+j)) {
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
		//TODO: IMPLEMENT THIS METHOD
	}
	
	/**
	 * General cleanup method
	 */
	public void close() {
		//TODO : IMPLEMENT THIS METHOD
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
