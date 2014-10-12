package edu.buffalo.cse.irf14.index;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class DocumentEntry implements Serializable{
	public String mFileID;
	public int mFrequencyInFile;
	public HashSet<Integer> mPositionList;
	public DocumentEntry(String fileID) {
		mFileID = fileID;
		mFrequencyInFile = 0;
		mPositionList = new HashSet<Integer>();
	}
	
	public String toString() {
		return mFileID + ":" + String.valueOf(mFrequencyInFile);
	}
}
