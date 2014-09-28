/**
 * 
 */
package edu.buffalo.cse.irf14.index;

import java.io.File;
import java.io.PrintWriter;
import java.util.HashMap;
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
	Map<String, IndexEntry> mTermIndex;
	Map<String, IndexEntry> mAuthorIndex;
	Map<String, IndexEntry> mCategoryIndex;
	Map<String, IndexEntry> mPlaceIndex;
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
		try {
			String[] author = d.getField(FieldNames.AUTHOR);
			if(author != null) {
				stream = mTokenizer.consume(author[0]);

				Analyzer AuthorAnalyzer = AnalyzerFactory.getInstance().getAnalyzerForField( FieldNames.AUTHOR, stream);
				while(AuthorAnalyzer.increment()) {}
				TokenStream indexableStream = AuthorAnalyzer.getStream();
				buildIndex(indexableStream, FieldNames.AUTHOR, d);
			}

			String[] content = d.getField(FieldNames.CONTENT);
			stream = mTokenizer.consume(content[0]);
			Analyzer contentAnalyzer = AnalyzerFactory.getInstance().getAnalyzerForField( FieldNames.CONTENT, stream);
			while(contentAnalyzer.increment()) {}
			TokenStream indexableStream = contentAnalyzer.getStream();
			buildIndex(indexableStream, FieldNames.CONTENT, d);

			String[] title = d.getField(FieldNames.TITLE);
			stream = mTokenizer.consume(title[0]);
			Analyzer titleAnalyzer = AnalyzerFactory.getInstance().getAnalyzerForField( FieldNames.CONTENT, stream);
			while(titleAnalyzer.increment()) {}
			indexableStream = titleAnalyzer.getStream();
			buildIndex(indexableStream, FieldNames.CONTENT, d);
			
			String[] authorOrg = d.getField(FieldNames.AUTHORORG);
			if(authorOrg != null) {
				stream = mTokenizer.consume(authorOrg[0]);
				Analyzer authorOrgAnalyzer = AnalyzerFactory.getInstance().getAnalyzerForField( FieldNames.AUTHORORG, stream);
				while(authorOrgAnalyzer.increment()) {}
				indexableStream = authorOrgAnalyzer.getStream();
				buildIndex(indexableStream, FieldNames.CONTENT, d);
			}
			
			String[] place = d.getField(FieldNames.PLACE);
			stream = mTokenizer.consume(place[0]);
			Analyzer placeAnalyzer = AnalyzerFactory.getInstance().getAnalyzerForField( FieldNames.PLACE, stream);
			while(placeAnalyzer.increment()) {}
			indexableStream = placeAnalyzer.getStream();
			buildIndex(indexableStream, FieldNames.PLACE, d);
			
			/*String[] fileID = d.getField(FieldNames.FILEID);
			stream = mTokenizer.consume(fileID[0]);
			buildIndex(stream, FieldNames.FILEID, d);*/
			
			String[] newsDate = d.getField(FieldNames.NEWSDATE);
			stream = mTokenizer.consume(newsDate[0]);
			Analyzer newsDateAnalyzer = AnalyzerFactory.getInstance().getAnalyzerForField( FieldNames.NEWSDATE, stream);
			while(newsDateAnalyzer.increment()) {}
			indexableStream = newsDateAnalyzer.getStream();
			buildIndex(indexableStream, FieldNames.NEWSDATE, d);
			
			String[] category = d.getField(FieldNames.CATEGORY);
			stream = mTokenizer.consume(category[0]);
			Analyzer categoryAnalyzer = AnalyzerFactory.getInstance().getAnalyzerForField( FieldNames.CATEGORY, stream);
			while(categoryAnalyzer.increment()) {}
			indexableStream = categoryAnalyzer.getStream();
			buildIndex(indexableStream, FieldNames.CATEGORY, d);
			
		}
		catch (TokenizerException e) {

		}
	}

	private void buildIndex (TokenStream stream, FieldNames fieldname, Document doc) {
		Map<String, IndexEntry> indexMap = null;
		switch(fieldname) {
		case AUTHOR:
			indexMap = mAuthorIndex;
			break;
		case CATEGORY:
			indexMap = mCategoryIndex;
			break;
		case CONTENT:	//using as a placeholder for term
		case NEWSDATE:
		case AUTHORORG:
		case TITLE:
			indexMap = mTermIndex;
			break;
		case PLACE:
			indexMap = mPlaceIndex;
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
		Iterator it = mTermIndex.entrySet().iterator();
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

		}
	}
}
