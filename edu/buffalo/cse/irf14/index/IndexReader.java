/**
 * 
 */
package edu.buffalo.cse.irf14.index;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author nikhillo
 * Class that emulates reading data back from a written index
 */
public class IndexReader {
	private String mIndexDir;
	private IndexType mIndexType;
	HashMap<String, IndexEntry> mIndex;
	HashSet<String> mFileIDSet;
	
	public HashMap<String, IndexEntry> GetIndex() {
		return mIndex;
	}
	
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

	public static <K,V> Map<K,V> sortByValues(Map<K,V> map, Comparator<Map.Entry<K,V>> comp){

		List<Map.Entry<K,V>> entries = new LinkedList<Map.Entry<K,V>>(map.entrySet());

		Collections.sort(entries, comp);

		Map<K,V> sortedMap = new LinkedHashMap<K,V>();

		for(Map.Entry<K,V> entry: entries){
			sortedMap.put(entry.getKey(), entry.getValue());
		}

		return sortedMap;
	}



	/**
	 * Method to get the top k terms from the index in terms of the total number
	 * of occurrences.
	 * @param k : The number of terms to fetch
	 * @return : An ordered list of results. Must be <=k fr valid k values
	 * null for invalid k values
	 */
	public List<String> getTopK(int k) {
		if(k<=0)
			return null;

		Comparator<Entry<String,IndexEntry>> comparer = new Comparator<Map.Entry<String,IndexEntry>>() {
			@Override
			public int compare(Entry<String, IndexEntry> o1, Entry<String, IndexEntry> o2) {
				// TODO Auto-generated method stub
				int left = o1.getValue().mTotalFrequency;
				int right = o2.getValue().mTotalFrequency;
				if(left < right) return 1;
				if(left > right) return -1;
				return 0;
			}

		};

		Map<String, IndexEntry> sortedMap = sortByValues(mIndex, comparer);
		List<String> returnList = new ArrayList<String>();
		Iterator<Entry<String, IndexEntry>> it = sortedMap.entrySet().iterator();
		int i = 0;
		while(it.hasNext()) {
			Entry<String, IndexEntry> keyval = it.next();
			returnList.add(keyval.getKey());
			i++;
			if(i>=k)
				break;
		}
		return returnList;
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
		List<IndexEntry> entries = new ArrayList<IndexEntry>();
		Map<String, IndexEntryCounter> fileCounter = new HashMap<String, IndexEntryCounter>();

		int length = 0;
		for (String term : terms) {
			length++;
			IndexEntry entry = mIndex.get(term);
			if(entry == null) {
				return null;
			}
			List<DocumentEntry> docList = entry.mDocumentList;
			Iterator<DocumentEntry> it = docList.iterator();
			while(it.hasNext()) {
				DocumentEntry docEntry = it.next();
				String fileID = docEntry.mFileID;
				IndexEntryCounter value = null ;
				if((value = fileCounter.get(fileID)) == null) {
					fileCounter.put(fileID, new IndexEntryCounter(1, docEntry.mFrequencyInFile));
				} else {
					value.mCount++;
					value.mTotalFrequency += docEntry.mFrequencyInFile;
					fileCounter.put(fileID, value);
				}
			}
		}
		Map<String, Integer> returnMap = new HashMap<String, Integer>();
		Iterator<Entry<String,IndexEntryCounter>> it = fileCounter.entrySet().iterator();
		while(it.hasNext()) {
			Entry<String,IndexEntryCounter> next = it.next();
			if(next.getValue().mCount < length)
				continue;
			returnMap.put(next.getKey() , next.getValue().mTotalFrequency);
		}

		if(returnMap.isEmpty())
			return null;
		Comparator<Entry<String,Integer>> comparer = new Comparator<Map.Entry<String,Integer>>() {
			@Override
			public int compare(Entry<String, Integer> o1,
					Entry<String, Integer> o2) {
				if(o1.getValue() > o2.getValue()) return -1;
				if(o1.getValue() < o2.getValue()) return 1;
				return 0;
			}
		};
		return sortByValues(returnMap, comparer);
	}

	private class IndexEntryCounter {
		public int mCount;
		public int mTotalFrequency;

		public IndexEntryCounter(int count, int frequency) {
			mCount = count;
			mTotalFrequency = frequency;
		}

	}
}
