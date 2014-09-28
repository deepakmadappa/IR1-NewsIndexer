package edu.buffalo.cse.irf14.analysis;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class StopWordsRuleFilter extends TokenFilter {

	public StopWordsRuleFilter(TokenStream stream) {
		super(stream);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean increment() throws TokenizerException {
		Token stopwordtok = null;
		Pattern stopwordPattern = Pattern.compile("^(a|able|about|across|after|all|almost|also|am|among|an|and|any|are|as|at|be|because|been|but|by|can|cannot|could|dear|did|do|does|either|else|ever|every|for|from|get|got|had|has|have|he|her|hers|him|his|how|however|i|if|in|into|is|it|its|just|least|let|like|likely|may|me|might|most|must|my|neither|no|nor|not|of|off|often|on|only|or|other|our|own|rather|said|say|says|she|should|since|so|some|than|that|the|their|them|then|there|these|they|this|tis|to|too|twas|us|wants|was|we|were|what|when|where|which|while|who|whom|why|will|with|would|yet|you|your)$");
		//Loop to iterate over the tokens in a tokenstream
		while((stopwordtok = mInputStream.next())!= null) {
			String input = stopwordtok.toString();
			Matcher stopwordMatcher = stopwordPattern.matcher(input);
			// Condition to not add stop words
			if(!stopwordMatcher.find()) {
				mOutputList.add(stopwordtok);
			}
		}
		mInputStream.mTokens.clear();
		mInputStream.reset();
		mInputStream.mTokens.addAll(mOutputList);
		return false;
	}

	@Override
	public TokenStream getStream(){

		return mInputStream; 
	}
}
