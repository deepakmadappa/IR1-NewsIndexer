/**
 * 
 */
package edu.buffalo.cse.irf14.document;

/**
 * @author nikhillo
 * Generic wrapper exception class for parsing exceptions
 */
public class ParserException extends Exception {

	public ParserException(String message) {
        super(message);
    }
	
	public ParserException() {
		
	}
	private static final long serialVersionUID = 4691717901217832517L;

}

