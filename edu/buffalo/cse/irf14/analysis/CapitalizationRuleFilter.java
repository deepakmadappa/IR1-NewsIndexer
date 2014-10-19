package edu.buffalo.cse.irf14.analysis;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CapitalizationRuleFilter extends TokenFilter {
	public CapitalizationRuleFilter(TokenStream stream) {
		super(stream);
	}
	/*
	@Override
	public boolean increment() throws TokenizerException {
		while(true) {	//loop through all the sentences
			int index = 0;
			List<Token> sentence = new ArrayList<Token>();
			boolean bIsAllUpperSentence = getNextSentence(sentence);
			if(sentence.isEmpty()) {
				break;
			}
			Token firstWordInSentence = sentence.get(index);
			index++;
			boolean bIsAllCaps = isAllCaps(firstWordInSentence.toString());
			if(!bIsAllCaps && !bIsAllUpperSentence) {
				firstWordInSentence.setTermText(firstWordInSentence.toString().toLowerCase());
			} 
			mOutputList.add(firstWordInSentence);
			//loop through remaining sentence
			boolean bIsCamelCase;
			while(index < sentence.size()) {	
				Token nextWordInSentence = sentence.get(index);
				index++;
				bIsAllCaps = isAllCaps(nextWordInSentence.toString());
				if(bIsAllCaps && !bIsAllUpperSentence) {
					mOutputList.add(nextWordInSentence);
					continue;
				}
				
				//handling "weird case" like iOS
				if(isWeirdCase(nextWordInSentence.toString())) {
					mOutputList.add(nextWordInSentence);
					continue;
				}
				boolean bRetainCamel = false;
				
				if(isCamelCase(nextWordInSentence.toString())) {
					bRetainCamel = true;
					while(index < sentence.size()) {
						if( isCamelCase(sentence.get(index).toString()) || isAllCaps(sentence.get(index).toString()))  {
							nextWordInSentence.merge(sentence.get(index));
							index++;
						}
						else {
							break;
						}
					}
				}
				if(!bRetainCamel) {
					nextWordInSentence.setTermText(nextWordInSentence.toString().toLowerCase());
				} 
				mOutputList.add(nextWordInSentence);
			}
		}
		mInputStream.mTokens.clear();
		mInputStream.reset();
		mInputStream.mTokens.addAll(mOutputList);
		return false;
	}
	*/
	public boolean increment() throws TokenizerException {
		for (Token token : mInputStream.mTokens) {
			token.setTermText(token.getTermText().toLowerCase());
		}
		return false;
	}
	
	@Override
	public TokenStream getStream() {
		return mInputStream; 
	}

	private  boolean getNextSentence(List<Token> sentence) {
		boolean bIsAllUpper = true;
		Pattern lowerPattern = Pattern.compile("[a-z]+");
		while(true) {
			Token nextToken = mInputStream.next();
			if(nextToken == null) {
				break;
			}
			String nextTokenStr = nextToken.toString();
			if(bIsAllUpper) {
				Matcher matcher = lowerPattern.matcher(nextTokenStr);
				if(matcher.find()) {
					bIsAllUpper = false;
				}
			}
			sentence.add(nextToken);
			if(nextTokenStr.endsWith("."))
				break;
		}

		return bIsAllUpper;
	}
	
	private boolean isAllCaps(String word) {
		Pattern lowerPattern = Pattern.compile("[a-z]+");
		Matcher matcher = lowerPattern.matcher(word);
		if(!matcher.find()) {
			return true;
		}
		return false;
	}
	
	private boolean isCamelCase(String word) {
		Pattern camelPattern1 = Pattern.compile("^[A-Z][a-z\\p{Punct}]+");
		Matcher matcher1 = camelPattern1.matcher(word);
		if(matcher1.find()) {
			return true;
		}
		return false;
	}
	
	private boolean isWeirdCase(String word) {
		Pattern camelPattern2 = Pattern.compile("^[a-z][A-Z\\p{Punct}]+");
		Matcher matcher2 = camelPattern2.matcher(word);
		if(matcher2.find()) {
			return true;
		}
		return false;
	}
}
