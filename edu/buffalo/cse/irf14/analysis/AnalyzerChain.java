package edu.buffalo.cse.irf14.analysis;

import edu.buffalo.cse.irf14.document.FieldNames;

public class AnalyzerChain implements Analyzer {

	FieldNames mFieldNames;
	TokenStream mStream;
	TokenStream oStream;
	public AnalyzerChain(FieldNames fieldname, TokenStream stream) {
		mFieldNames = fieldname;
		mStream = stream;
	}
	@Override
	public boolean increment() throws TokenizerException {
		
		switch(mFieldNames) {
		case AUTHOR:
			oStream = analyzeForAuthor();
			break;
		case AUTHORORG:
			oStream = analyzeForAuthorOrg();
			break;
		case CATEGORY:
			oStream = analyzeForCategory();
			break;
		case CONTENT:
			oStream = analyzeForContent();
			break;
		case FILEID:
			oStream = analyzeForFileID();
			break;
		case PLACE:
			oStream = analyzeForPlace();
			break;
		case TITLE:
			oStream = analyzeForTitle();
			break;
		case NEWSDATE:
			oStream = analyzeForNewsDate();
			break;
		default:
			oStream = null;
		}
		
	
		// TODO Auto-generated method stub
		return false;
	}

	
	private TokenStream analyzeForAuthor() throws TokenizerException{
		TokenFilter accentFilter = TokenFilterFactory.getInstance().getFilterByType(TokenFilterType.ACCENT, mStream);
		while(accentFilter.increment()) {
			
		}
		return accentFilter.getStream();
	}

	private TokenStream analyzeForAuthorOrg() throws TokenizerException{
		TokenFilter accentFilter = TokenFilterFactory.getInstance().getFilterByType(TokenFilterType.ACCENT, mStream);
		while(accentFilter.increment()) {}
		TokenStream inter = accentFilter.getStream();

		TokenFilter symFilter = TokenFilterFactory.getInstance().getFilterByType(TokenFilterType.SYMBOL, inter);
		while(symFilter.increment()) {
		}
		inter = symFilter.getStream();
		
		TokenFilter spclFilter = TokenFilterFactory.getInstance().getFilterByType(TokenFilterType.SPECIALCHARS, inter);
		while(spclFilter.increment()) {
		}
		return spclFilter.getStream();
	
	}
	
	private TokenStream analyzeForCategory() throws TokenizerException{
		TokenFilter symFilter = TokenFilterFactory.getInstance().getFilterByType(TokenFilterType.SYMBOL, mStream);
		while(symFilter.increment()) {
			
		}
		return symFilter.getStream();

	}
	
	private TokenStream analyzeForContent() throws TokenizerException{
		TokenFilter accentFilter = TokenFilterFactory.getInstance().getFilterByType(TokenFilterType.ACCENT, mStream);
		while(accentFilter.increment()) {
		}
		TokenStream inter = accentFilter.getStream();
		
		TokenFilter dateFilter = TokenFilterFactory.getInstance().getFilterByType(TokenFilterType.DATE, inter);
		while(dateFilter.increment()) {
		}
		inter = dateFilter.getStream();
		
		TokenFilter numberFilter = TokenFilterFactory.getInstance().getFilterByType(TokenFilterType.NUMERIC, inter);
		while(numberFilter.increment()) {
		}
		inter = numberFilter.getStream();
		
		TokenFilter capFilter = TokenFilterFactory.getInstance().getFilterByType(TokenFilterType.CAPITALIZATION, inter);
		while(capFilter.increment()) {
		}
		inter = capFilter.getStream();
		
		TokenFilter symFilter = TokenFilterFactory.getInstance().getFilterByType(TokenFilterType.SYMBOL, inter);
		while(symFilter.increment()) {
		}
		inter = symFilter.getStream();
		
		TokenFilter spclFilter = TokenFilterFactory.getInstance().getFilterByType(TokenFilterType.SPECIALCHARS, inter);
		while(spclFilter.increment()) {
		}
		inter = spclFilter.getStream();
		
		TokenFilter stopFilter = TokenFilterFactory.getInstance().getFilterByType(TokenFilterType.STOPWORD, inter);
		while(stopFilter.increment()) {
		}
		inter = stopFilter.getStream();
		
		TokenFilter stemFilter = TokenFilterFactory.getInstance().getFilterByType(TokenFilterType.STEMMER, inter);
		while(stemFilter.increment()) {
		}
		return stemFilter.getStream();
	}
	
	private TokenStream analyzeForFileID() throws TokenizerException {
		
		return mStream;

	}
	
	private TokenStream analyzeForPlace() throws TokenizerException  {
		
		TokenFilter accentFilter = TokenFilterFactory.getInstance().getFilterByType(TokenFilterType.ACCENT, mStream);
		while(accentFilter.increment()) {
		}
		TokenStream inter = accentFilter.getStream();
		
		TokenFilter symFilter = TokenFilterFactory.getInstance().getFilterByType(TokenFilterType.SYMBOL, inter);
		while(symFilter.increment()) {
		}
		inter = symFilter.getStream();
		
		TokenFilter spclFilter = TokenFilterFactory.getInstance().getFilterByType(TokenFilterType.SPECIALCHARS, inter);
		while(spclFilter.increment()) {
		}
		return spclFilter.getStream();

	}
	
	private TokenStream analyzeForTitle() throws TokenizerException {
		
		TokenFilter accentFilter = TokenFilterFactory.getInstance().getFilterByType(TokenFilterType.ACCENT, mStream);
		while(accentFilter.increment()) {
		}
		TokenStream inter = accentFilter.getStream();
		
		TokenFilter dateFilter = TokenFilterFactory.getInstance().getFilterByType(TokenFilterType.DATE, inter);
		while(dateFilter.increment()) {
		}
		inter = dateFilter.getStream();
		
		TokenFilter numberFilter = TokenFilterFactory.getInstance().getFilterByType(TokenFilterType.NUMERIC, inter);
		while(numberFilter.increment()) {
		}
		inter = numberFilter.getStream();
		
		TokenFilter capFilter = TokenFilterFactory.getInstance().getFilterByType(TokenFilterType.CAPITALIZATION, inter);
		while(capFilter.increment()) {
		}
		inter = capFilter.getStream();
		
		TokenFilter symFilter = TokenFilterFactory.getInstance().getFilterByType(TokenFilterType.SYMBOL, inter);
		while(symFilter.increment()) {
		}
		inter = symFilter.getStream();
		
		TokenFilter spclFilter = TokenFilterFactory.getInstance().getFilterByType(TokenFilterType.SPECIALCHARS, inter);
		while(spclFilter.increment()) {
		}
		inter = spclFilter.getStream();
		
		TokenFilter stopFilter = TokenFilterFactory.getInstance().getFilterByType(TokenFilterType.STOPWORD, inter);
		while(stopFilter.increment()) {
		}
		inter = stopFilter.getStream();
		
		TokenFilter stemFilter = TokenFilterFactory.getInstance().getFilterByType(TokenFilterType.STEMMER, inter);
		while(stemFilter.increment()) {
		}
		return stemFilter.getStream();
	
		
	}
	
	private TokenStream analyzeForNewsDate() throws TokenizerException  {
		
		TokenFilter dateFilter = TokenFilterFactory.getInstance().getFilterByType(TokenFilterType.DATE, mStream);
		while(dateFilter.increment()) {
		}
		TokenStream inter = dateFilter.getStream();
		
		TokenFilter numberFilter = TokenFilterFactory.getInstance().getFilterByType(TokenFilterType.NUMERIC, inter);
		while(numberFilter.increment()) {
		}
		inter = numberFilter.getStream();
		

		TokenFilter capFilter = TokenFilterFactory.getInstance().getFilterByType(TokenFilterType.CAPITALIZATION, inter);
		while(capFilter.increment()) {
		}
		inter = capFilter.getStream();
		
		TokenFilter symFilter = TokenFilterFactory.getInstance().getFilterByType(TokenFilterType.SYMBOL, inter);
		while(symFilter.increment()) {
		}
		inter = symFilter.getStream();
		
		TokenFilter spclFilter = TokenFilterFactory.getInstance().getFilterByType(TokenFilterType.SPECIALCHARS, inter);
		while(spclFilter.increment()) {
		}
		return spclFilter.getStream();

	}
	
	@Override
	public TokenStream getStream() {
		
		return oStream;
		
	}

}
