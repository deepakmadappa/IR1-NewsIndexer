package edu.buffalo.cse.irf14.index;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

public class IndexEntry {
	public int mTotalFrequency;
	public LinkedList<DocumentEntry> mDocumentList;
	
	public IndexEntry() {
		mTotalFrequency = 0;
		mDocumentList = new LinkedList<DocumentEntry>();
	}
	
	public String toString() {
		ListIterator<DocumentEntry> it = mDocumentList.listIterator();
		String out = String.valueOf(mTotalFrequency) + "|";
		while(it.hasNext()) {
			out = out + it.next().toString() + "->";
		}
		return out;
	}
	
}

