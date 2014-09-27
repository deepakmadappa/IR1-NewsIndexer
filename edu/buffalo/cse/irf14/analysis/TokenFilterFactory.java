/**
 * 
 */
package edu.buffalo.cse.irf14.analysis;


/**
 * Factory class for instantiating a given TokenFilter
 * @author nikhillo
 *
 */
public class TokenFilterFactory {
	/**
	 * Static method to return an instance of the factory class.
	 * Usually factory classes are defined as singletons, i.e. 
	 * only one instance of the class exists at any instance.
	 * This is usually achieved by defining a private static instance
	 * that is initialized by the "private" constructor.
	 * On the method being called, you return the static instance.
	 * This allows you to reuse expensive objects that you may create
	 * during instantiation
	 * @return An instance of the factory
	 */
	private static TokenFilterFactory mInstance = null;
	public static TokenFilterFactory getInstance() {
		//TODO : YOU MUST IMPLEMENT THIS METHOD
		if(mInstance == null) {
			mInstance = new TokenFilterFactory();
		}
		return mInstance;
	}
	
	/**
	 * Returns a fully constructed {@link TokenFilter} instance
	 * for a given {@link TokenFilterType} type
	 * @param type: The {@link TokenFilterType} for which the {@link TokenFilter}
	 * is requested
	 * @param stream: The TokenStream instance to be wrapped
	 * @return The built {@link TokenFilter} instance
	 */
	public TokenFilter getFilterByType(TokenFilterType type, TokenStream stream) {
		switch (type) {
		case CAPITALIZATION:
			return new CapitalizationRuleFilter(stream);
		case DATE:
			return new DateRuleFilter(stream);
		case NUMERIC:
			return new NumberRuleFilter(stream);
		case STEMMER:
			return new StemmerRuleFilter(stream);
		case ACCENT:
			return new AccentRuleFilter(stream);
		case STOPWORD:
			return new StopWordsRuleFilter(stream);
		case SPECIALCHARS:
			return new SpecialCharacRuleFilter(stream);
		case SYMBOL:
			return new SymbolRuleFilter(stream);
			
			
		default:
			return null;
		}
		
	}
}
