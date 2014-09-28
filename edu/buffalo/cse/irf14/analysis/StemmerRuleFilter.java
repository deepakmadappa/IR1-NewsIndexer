package edu.buffalo.cse.irf14.analysis;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StemmerRuleFilter extends TokenFilter{

	public StemmerRuleFilter(TokenStream stream) {
		super(stream);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean increment() throws TokenizerException {
		
		Token tok = null;
		Stemmer stemmer = new Stemmer();
		Pattern patternNotAlpha = Pattern.compile("[^a-z]");
		while((tok = mInputStream.next()) != null) {
			if(patternNotAlpha.matcher(tok.toString()).find()) {
				//non alpha chars found skip stemming this word
				mOutputList.add(tok);
				continue;
			}
			stemmer.add(tok.toString().toCharArray(), tok.toString().length());
			stemmer.stem();
			tok.setTermText(stemmer.toString());
			mOutputList.add(tok);
		}
		mInputStream.mTokens.clear();
		mInputStream.reset();
		mInputStream.mTokens.addAll(mOutputList);
		return false;
	}

	@Override
	public TokenStream getStream() {

		return mInputStream; 
	}

}
