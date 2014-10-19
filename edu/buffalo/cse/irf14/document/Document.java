/**
 * 
 */
package edu.buffalo.cse.irf14.document;

import java.io.Serializable;
import java.util.HashMap;

import edu.buffalo.cse.irf14.index.DocumentEntry;

/**
 * @author nikhillo
 * Wrapper class that holds {@link FieldNames} to value mapping
 */
public class Document implements Serializable{
	//Sample implementation - you can change this if you like
	private HashMap<FieldNames, String[]> map;
	public HashMap<String, DocumentEntry> mTermDocEntryMap;
	public double mNormalizationValue;
	
	/**
	 * Default constructor
	 */
	public Document() {
		//TBD filling in the initial capacity may make it more efficient
		map = new HashMap<FieldNames, String[]>();
		mTermDocEntryMap = new HashMap<String, DocumentEntry>();
		mNormalizationValue = 0;
	}
	
	/**
	 * Method to set the field value for the given {@link FieldNames} field
	 * @param fn : The {@link FieldNames} to be set
	 * @param o : The value to be set to
	 */
	public void setField(FieldNames fn, String... o) {
		map.put(fn, o);
	}
	
	public void setArray(FieldNames fn, String[] o) {
		map.put(fn, o);
	}
	
	/**
	 * Method to get the field value for a given {@link FieldNames} field
	 * @param fn : The field name to query
	 * @return The associated value, null if not found
	 */
	public String[] getField(FieldNames fn) {
		return map.get(fn);
	}
}
