package edu.buffalo.cse.irf14.analysis;

import java.io.FilterReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NumberRuleFilter extends TokenFilter {

	public NumberRuleFilter(TokenStream stream) {
		super(stream);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean increment() throws TokenizerException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public TokenStream getStream() {
		
		Token tok = null;
		Pattern numberPattern = Pattern.compile("[0-9.,]*[0-9]");
		Pattern datePattern = Pattern.compile("[0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9]-[0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9]");
		while((tok = mInputStream.next())!= null) {
			String strToken = tok.toString();
			Matcher numMatcher = numberPattern.matcher(strToken);
			Matcher dateMatcher = datePattern.matcher(strToken);
			if(numMatcher.find() && !dateMatcher.find()) {
				String out = numMatcher.replaceAll("");
				if(!out.isEmpty()) {
					tok.setTermText(out);
					mOutputList.add(tok);
				}
			} else {
				mOutputList.add(tok);
			}
		}
		return new TokenStream(mOutputList);
	}

}
