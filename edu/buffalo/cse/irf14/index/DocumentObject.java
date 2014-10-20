package edu.buffalo.cse.irf14.index;

import java.io.Serializable;
import java.util.HashMap;

import edu.buffalo.cse.irf14.document.Document;

public class DocumentObject implements Serializable {
	public HashMap<String, TFDFSet> mTFDFMap = null;
	public Document mDocument = null;
	
	public DocumentObject(Document doc) {
		mDocument = doc;
	}
}
