/**
 * 
 */
package edu.buffalo.cse.irf14.index;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import edu.buffalo.cse.irf14.analysis.Analyzer;
import edu.buffalo.cse.irf14.analysis.AnalyzerFactory;
import edu.buffalo.cse.irf14.analysis.Token;
import edu.buffalo.cse.irf14.analysis.TokenStream;
import edu.buffalo.cse.irf14.analysis.Tokenizer;
import edu.buffalo.cse.irf14.analysis.TokenizerException;
import edu.buffalo.cse.irf14.document.Document;
import edu.buffalo.cse.irf14.document.FieldNames;
import edu.buffalo.cse.irf14.document.ParserException;

/**
 * @author nikhillo
 * Class responsible for writing indexes to disk
 */
public class IndexWriter {
	String mIndexDir;
	HashMap<String, IndexEntry> mTermIndex;
	HashMap<String, IndexEntry> mAuthorIndex;
	HashMap<String, IndexEntry> mCategoryIndex;
	HashMap<String, IndexEntry> mPlaceIndex;
	HashSet<String> mTermFileIDSet;
	HashSet<String> mAuthorFileIDSet;
	HashSet<String> mCategoryFileIDSet;
	HashSet<String> mPlaceFileIDSet;
	private Tokenizer mTokenizer;
	/**
	 * Default constructor
	 * @param indexDir : The root directory to be sued for indexing
	 */
	public IndexWriter(String indexDir) {
		//TODO : YOU MUST IMPLEMENT THIS
		mIndexDir = new String(indexDir);
		mTermIndex = new HashMap<String, IndexEntry>();
		mAuthorIndex = new HashMap<String, IndexEntry>();
		mCategoryIndex = new HashMap<String, IndexEntry>();
		mPlaceIndex = new HashMap<String, IndexEntry>();
		mTokenizer = new Tokenizer();
		mTermFileIDSet = new HashSet<String>();
		mAuthorFileIDSet = new HashSet<String>();
		mCategoryFileIDSet = new HashSet<String>();
		mPlaceFileIDSet = new HashSet<String>();
		System.setProperty("INDEX.DIR", indexDir);
	}

	/**
	 * Method to add the given Document to the index
	 * This method should take care of reading the filed values, passing
	 * them through corresponding analyzers and then indexing the results
	 * for each indexable field within the document. 
	 * @param d : The Document to be added
	 * @throws IndexerException : In case any error occurs
	 */
	public void addDocument(Document d) throws IndexerException {
		TokenStream stream = null;
		TokenStream indexableStream = null;
		try {
			String[] author = d.getField(FieldNames.AUTHOR);
			if(author != null) {
				stream = mTokenizer.consume(author[0]);

				Analyzer AuthorAnalyzer = AnalyzerFactory.getInstance().getAnalyzerForField( FieldNames.AUTHOR, stream);
				while(AuthorAnalyzer.increment()) {}
				indexableStream = AuthorAnalyzer.getStream();
				buildIndex(indexableStream, FieldNames.AUTHOR, d);
			}

			String[] content = d.getField(FieldNames.CONTENT);
			if(content != null) {
				stream = mTokenizer.consume(content[0]);
				Analyzer contentAnalyzer = AnalyzerFactory.getInstance().getAnalyzerForField( FieldNames.CONTENT, stream);
				while(contentAnalyzer.increment()) {}
				indexableStream = contentAnalyzer.getStream();
				buildIndex(indexableStream, FieldNames.CONTENT, d);
			}

			String[] title = d.getField(FieldNames.TITLE);
			if(title!=null) {
				stream = mTokenizer.consume(title[0]);
				Analyzer titleAnalyzer = AnalyzerFactory.getInstance().getAnalyzerForField( FieldNames.CONTENT, stream);
				while(titleAnalyzer.increment()) {}
				indexableStream = titleAnalyzer.getStream();
				buildIndex(indexableStream, FieldNames.CONTENT, d);
			}

			String[] authorOrg = d.getField(FieldNames.AUTHORORG);
			if(authorOrg != null) {
				stream = mTokenizer.consume(authorOrg[0]);
				Analyzer authorOrgAnalyzer = AnalyzerFactory.getInstance().getAnalyzerForField( FieldNames.AUTHORORG, stream);
				while(authorOrgAnalyzer.increment()) {}
				indexableStream = authorOrgAnalyzer.getStream();
				buildIndex(indexableStream, FieldNames.CONTENT, d);
			}

			String[] place = d.getField(FieldNames.PLACE);
			if(place != null) {
				stream = mTokenizer.consume(place[0]);
				Analyzer placeAnalyzer = AnalyzerFactory.getInstance().getAnalyzerForField( FieldNames.PLACE, stream);
				while(placeAnalyzer.increment()) {}
				indexableStream = placeAnalyzer.getStream();
				buildIndex(indexableStream, FieldNames.PLACE, d);
			}

			/*String[] fileID = d.getField(FieldNames.FILEID);
			stream = mTokenizer.consume(fileID[0]);
			buildIndex(stream, FieldNames.FILEID, d);*/

			String[] newsDate = d.getField(FieldNames.NEWSDATE);
			if(newsDate != null) {
				stream = mTokenizer.consume(newsDate[0]);
				Analyzer newsDateAnalyzer = AnalyzerFactory.getInstance().getAnalyzerForField( FieldNames.NEWSDATE, stream);
				while(newsDateAnalyzer.increment()) {}
				indexableStream = newsDateAnalyzer.getStream();
				buildIndex(indexableStream, FieldNames.NEWSDATE, d);
			}

			String[] category = d.getField(FieldNames.CATEGORY);
			if(category != null) {
				stream = mTokenizer.consume(category[0]);
				Analyzer categoryAnalyzer = AnalyzerFactory.getInstance().getAnalyzerForField( FieldNames.CATEGORY, stream);
				while(categoryAnalyzer.increment()) {}
				indexableStream = categoryAnalyzer.getStream();
				buildIndex(indexableStream, FieldNames.CATEGORY, d);
			}

		}
		catch (TokenizerException e) {

		}
	}

	private void buildIndex (TokenStream stream, FieldNames fieldname, Document doc) {
		Map<String, IndexEntry> indexMap = null;
		HashSet<String> fileIDSet = null;
		switch(fieldname) {
		case AUTHOR:
			indexMap = mAuthorIndex;
			fileIDSet = mAuthorFileIDSet;
			break;
		case CATEGORY:
			indexMap = mCategoryIndex;
			fileIDSet = mCategoryFileIDSet;
			break;
		case CONTENT:	//using as a placeholder for term
		case NEWSDATE:
		case AUTHORORG:
		case TITLE:
			indexMap = mTermIndex;
			fileIDSet = mTermFileIDSet;
			break;
		case PLACE:
			indexMap = mPlaceIndex;
			fileIDSet = mPlaceFileIDSet;
			break;
		default:
			break;
		}
		Token tok = null;
		String fileID = doc.getField(FieldNames.FILEID)[0];
		while((tok = stream.next()) != null) {
			String termToIndex = tok.toString();
			IndexEntry indexEntry = indexMap.get(termToIndex);
			if(indexEntry == null) {
				indexEntry = new IndexEntry();
			}
			LinkedList<DocumentEntry> documentList = indexEntry.mDocumentList;
			if(documentList.isEmpty() || !documentList.getFirst().mFileID.equalsIgnoreCase(fileID)) {
				indexEntry.mDocumentList.addFirst(new DocumentEntry(fileID));
				fileIDSet.add(fileID);
			}
			indexEntry.mTotalFrequency++;
			documentList.getFirst().mFrequencyInFile++;
			indexMap.put(termToIndex, indexEntry);
		}
	}

	/**
	 * Method that indicates that all open resources must be closed
	 * and cleaned and that the entire indexing operation has been completed.
	 * @throws IndexerException : In case any error occurs
	 */
	public void close() throws IndexerException {
		/*Iterator it = mTermIndex.entrySet().iterator();
		try {
			PrintWriter writer = new PrintWriter("d:\\testout", "UTF-8");
			while(it.hasNext()) {
				Map.Entry<String, IndexEntry> keyval = (Map.Entry<String, IndexEntry>)(it.next());
				String term = keyval.getKey();

				term = term + " - " + keyval.getValue().toString();
				writer.println(term);
			}
			writer.close();
		}
		catch (Exception e) {

		}*/
		try {
			FileOutputStream termStream = new FileOutputStream(mIndexDir + File.separator + "term.index");
			ObjectOutputStream oos = new ObjectOutputStream(termStream);
			oos.writeObject(mTermIndex);
			oos.close();

			FileOutputStream authorStream = new FileOutputStream(mIndexDir + File.separator + "author.index");
			oos = new ObjectOutputStream(authorStream);
			oos.writeObject(mAuthorIndex);
			oos.close();

			FileOutputStream catergoryStream = new FileOutputStream(mIndexDir + File.separator + "category.index");
			oos = new ObjectOutputStream(catergoryStream);
			oos.writeObject(mCategoryIndex);
			oos.close();

			FileOutputStream placeStream = new FileOutputStream(mIndexDir + File.separator + "place.index");
			oos = new ObjectOutputStream(placeStream);
			oos.writeObject(mPlaceIndex);
			oos.close();
			
			FileOutputStream termFileIDStream = new FileOutputStream(mIndexDir + File.separator + "term.ids");
			oos = new ObjectOutputStream(termFileIDStream);
			oos.writeObject(mTermFileIDSet);
			oos.close();

			FileOutputStream authorFileIDStream = new FileOutputStream(mIndexDir + File.separator + "author.ids");
			oos = new ObjectOutputStream(authorFileIDStream);
			oos.writeObject(mAuthorFileIDSet);
			oos.close();

			FileOutputStream catergoryFileIDStream = new FileOutputStream(mIndexDir + File.separator + "category.ids");
			oos = new ObjectOutputStream(catergoryFileIDStream);
			oos.writeObject(mCategoryFileIDSet);
			oos.close();

			FileOutputStream placeFileIDStream = new FileOutputStream(mIndexDir + File.separator + "place.ids");
			oos = new ObjectOutputStream(placeFileIDStream);
			oos.writeObject(mPlaceFileIDSet);
			oos.close();
		}catch (Exception e) {
			System.out.println("exception occured"+e.getMessage());
			e.printStackTrace();
		}
	}
}
