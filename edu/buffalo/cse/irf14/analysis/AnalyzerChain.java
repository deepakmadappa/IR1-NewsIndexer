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
			mStream = analyzeForAuthor();
			break;
		case AUTHORORG:
			mStream = analyzeForAuthorOrg();
			break;
		case CATEGORY:
			mStream = analyzeForCategory();
			break;
		case CONTENT:
			mStream = analyzeForContent();
			break;
		case FILEID:
			mStream = analyzeForFileID();
			break;
		case PLACE:
			mStream = analyzeForPlace();
			break;
		case TITLE:
			mStream = analyzeForTitle();
			break;
		case NEWSDATE:
			mStream = analyzeForNewsDate();
			break;
		default:
			mStream = null;
		}
		
	
		// TODO Auto-generated method stub
		return false;
	}

	
	private TokenStream analyzeForAuthor() throws TokenizerException{
		TokenFilter accentFilter = TokenFilterFactory.getInstance().getFilterByType(TokenFilterType.ACCENT, mStream);
		while(accentFilter.increment()) {
		}
		TokenStream inter = accentFilter.getStream();
		
		TokenFilter capFilter = TokenFilterFactory.getInstance().getFilterByType(TokenFilterType.CAPITALIZATION, inter);
		while(capFilter.increment()) {
		}
		inter = capFilter.getStream();
		
		TokenFilter symFilter = TokenFilterFactory.getInstance().getFilterByType(TokenFilterType.SYMBOL, inter);
		while(symFilter.increment()) {
		}
		inter = symFilter.getStream();
		
		return inter;
	}

	private TokenStream analyzeForAuthorOrg() throws TokenizerException{
		TokenFilter accentFilter = TokenFilterFactory.getInstance().getFilterByType(TokenFilterType.ACCENT, mStream);
		while(accentFilter.increment()) {}
		TokenStream inter = accentFilter.getStream();

		TokenFilter symFilter = TokenFilterFactory.getInstance().getFilterByType(TokenFilterType.SYMBOL, inter);
		while(symFilter.increment()) {
		}
		inter = symFilter.getStream();
		
		TokenFilter capFilter = TokenFilterFactory.getInstance().getFilterByType(TokenFilterType.CAPITALIZATION, inter);
		while(capFilter.increment()) {
		}
		inter = capFilter.getStream();
		
		TokenFilter spclFilter = TokenFilterFactory.getInstance().getFilterByType(TokenFilterType.SPECIALCHARS, inter);
		while(spclFilter.increment()) {
		}
		return spclFilter.getStream();
	
	}
	
	private TokenStream analyzeForCategory() throws TokenizerException{
		TokenFilter symFilter = TokenFilterFactory.getInstance().getFilterByType(TokenFilterType.SYMBOL, mStream);
		while(symFilter.increment()) {
			
		}
		TokenStream inter = symFilter.getStream();
		
		TokenFilter capFilter = TokenFilterFactory.getInstance().getFilterByType(TokenFilterType.CAPITALIZATION, inter);
		while(capFilter.increment()) {
		}
		
		return capFilter.getStream();
		

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
		
		TokenFilter spclFilter = TokenFilterFactory.getInstance().getFilterByType(TokenFilterType.SPECIALCHARS, inter);
		while(spclFilter.increment()) {
		}
		inter = spclFilter.getStream();
		
		
		TokenFilter symFilter = TokenFilterFactory.getInstance().getFilterByType(TokenFilterType.SYMBOL, inter);
		while(symFilter.increment()) {
		}
		inter = symFilter.getStream();
		
		
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
		
		TokenFilter spclFilter = TokenFilterFactory.getInstance().getFilterByType(TokenFilterType.SPECIALCHARS, inter);
		while(spclFilter.increment()) {
		}
		inter = spclFilter.getStream();
		
		TokenFilter capFilter = TokenFilterFactory.getInstance().getFilterByType(TokenFilterType.CAPITALIZATION, inter);
		while(capFilter.increment()) {
		}
		inter = capFilter.getStream();
		
		TokenFilter symFilter = TokenFilterFactory.getInstance().getFilterByType(TokenFilterType.SYMBOL, inter);
		while(symFilter.increment()) {
		}
		inter = symFilter.getStream();
		
		return inter;
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
		
		TokenFilter spclFilter = TokenFilterFactory.getInstance().getFilterByType(TokenFilterType.SPECIALCHARS, inter);
		while(spclFilter.increment()) {
		}
		inter = spclFilter.getStream();
		
		TokenFilter symFilter = TokenFilterFactory.getInstance().getFilterByType(TokenFilterType.SYMBOL, inter);
		while(symFilter.increment()) {
		}
		inter = symFilter.getStream();
				
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
		
		TokenFilter spclFilter = TokenFilterFactory.getInstance().getFilterByType(TokenFilterType.SPECIALCHARS, inter);
		while(spclFilter.increment()) {
		}
		inter = spclFilter.getStream();
		
		TokenFilter symFilter = TokenFilterFactory.getInstance().getFilterByType(TokenFilterType.SYMBOL, inter);
		while(symFilter.increment()) {
		}
		inter = symFilter.getStream();
		
		return inter;
	}
	
	@Override
	public TokenStream getStream() {
		
		return mStream;
		
	}

}
