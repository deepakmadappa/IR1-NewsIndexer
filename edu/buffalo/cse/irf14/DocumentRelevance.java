package edu.buffalo.cse.irf14;

public class DocumentRelevance {
	public String mDocID;
	public double mRelavance;
	public double mLengthNormalizationTerm;
	
	public DocumentRelevance(String docID) {
		mDocID = docID;
		mRelavance = 0;
		mLengthNormalizationTerm = 0;
	}
	
	public String toString() {
		return  mDocID+ ":" + String.valueOf(mRelavance) + "|";
	}
}
