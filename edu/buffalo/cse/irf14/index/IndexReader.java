/**
 * 
 */
package edu.buffalo.cse.irf14.index;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author nikhillo
 * Class that emulates reading data back from a written index
 */
public class IndexReader {
	private String mIndexDir;
	private IndexType mIndexType;
	HashMap<String, IndexEntry> mIndex;
	HashSet<String> mFileIDSet;
	/**
	 * Default constructor
	 * @param indexDir : The root directory from which the index is to be read.
	 * This will be exactly the same directory as passed on IndexWriter. In case 
	 * you make subdirectories etc., you will have to handle it accordingly.
	 * @param type The {@link IndexType} to read from
	 */
	public IndexReader(String indexDir, IndexType type) {
		mIndexDir = indexDir;
		mIndexType = type;
		String indexFile = "";
		String fileIDFile = "";
		switch (type) {
		case AUTHOR: 
			indexFile = "author.index";
			fileIDFile = "author.ids";
			break;
		case CATEGORY:
			indexFile = "category.index";
			fileIDFile = "category.ids";
			break;
		case PLACE:
			indexFile = "place.index";
			fileIDFile = "place.ids";
			break;
		case TERM:
			indexFile = "term.index";
			fileIDFile = "term.ids";
			break;
		}
		try {
			FileInputStream fin = new FileInputStream(indexDir + File.separator + indexFile);
			ObjectInputStream ois = new ObjectInputStream(fin);
			mIndex = (HashMap<String, IndexEntry>)(ois.readObject());
			
			fin = new FileInputStream(indexDir + File.separator + fileIDFile);
			ois = new ObjectInputStream(fin);
			mFileIDSet = (HashSet<String>)(ois.readObject());
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	/**
	 * Get total number of terms from the "key" dictionary associated with this 
	 * index. A postings list is always created against the "key" dictionary
	 * @return The total number of terms
	 */
	public int getTotalKeyTerms() {
		return mIndex.size();
	}

	/**
	 * Get total number of terms from the "value" dictionary associated with this 
	 * index. A postings list is always created with the "value" dictionary
	 * @return The total number of terms
	 */
	public int getTotalValueTerms() {
		return mFileIDSet.size();
	}

	/**
	 * Method to get the postings for a given term. You can assume that
	 * the raw string that is used to query would be passed through the same
	 * Analyzer as the original field would have been.
	 * @param term : The "analyzed" term to get postings for
	 * @return A Map containing the corresponding fileid as the key and the 
	 * number of occurrences as values if the given term was found, null otherwise.
	 */
	public Map<String, Integer> getPostings(String term) {
		IndexEntry indexEntry = mIndex.get(term);
		Map<String, Integer> returnMap = new HashMap<String, Integer>();
		if(indexEntry == null) {
			return null;
		}
		Iterator<DocumentEntry> it = indexEntry.mDocumentList.iterator();
		while(it.hasNext()) {
			DocumentEntry de = it.next();
			returnMap.put(de.mFileID, de.mFrequencyInFile);
		}
		return returnMap;
	}

	/**
	 * Method to get the top k terms from the index in terms of the total number
	 * of occurrences.
	 * @param k : The number of terms to fetch
	 * @return : An ordered list of results. Must be <=k fr valid k values
	 * null for invalid k values
	 */
	public List<String> getTopK(int k) {
		//TODO YOU MUST IMPLEMENT THIS
		return null;
	}

	/**
	 * Method to implement a simple boolean AND query on the given index
	 * @param terms The ordered set of terms to AND, similar to getPostings()
	 * the terms would be passed through the necessary Analyzer.
	 * @return A Map (if all terms are found) containing FileId as the key 
	 * and number of occurrences as the value, the number of occurrences 
	 * would be the sum of occurrences for each participating term. return null
	 * if the given term list returns no results
	 * BONUS ONLY
	 */
	public Map<String, Integer> query(String...terms) {
		//TODO : BONUS ONLY
		return null;
	}
}
