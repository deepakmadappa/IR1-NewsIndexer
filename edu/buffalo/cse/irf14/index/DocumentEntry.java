package edu.buffalo.cse.irf14.index;

public class DocumentEntry {
	public String mFileID;
	public int mFrequencyInFile;
	public DocumentEntry(String fileID) {
		mFileID = fileID;
		mFrequencyInFile = 0;
	}
	
	public String toString() {
		return mFileID + ":" + String.valueOf(mFrequencyInFile);
	}
}
