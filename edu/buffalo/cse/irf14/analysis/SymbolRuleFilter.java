package edu.buffalo.cse.irf14.analysis;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class SymbolRuleFilter extends TokenFilter {
	HashMap<String,String> mContraMap;
	Pattern mContraPattern;
	Pattern mTerminatingPunctAndPossesive;
	Pattern mHyphenAmidstAlpha;
	public SymbolRuleFilter(TokenStream stream) {
		super(stream);
		
		mTerminatingPunctAndPossesive = Pattern.compile("[.,!\\-?']+$|^[\\-']+|('s)$");
		mHyphenAmidstAlpha = Pattern.compile("^([A-Za-z]+)-([A-Za-z]+)$");
		mContraPattern = Pattern.compile("^(aren't|can't|could've|couldn't|couldn't've|didn't|doesn't|don't|hadn't|hadn't've|hasn't|haven't|he'd|he'd've|he'll|he's|how'd|how'll|how's|i'd|i'd've|i'll|i'm|i've|isn't|it'd|it'd've|it'll|it's|let's|ma'am|mightn't|mightn't've|might've|mustn't|must've|needn't|not've|o'clock|shan't|she'd|she'd've|she'll|she's|should've|shouldn't|shouldn't've|that's|there'd|there'd've|there're|there's|they'd|they'd've|they'll|they're|they've|wasn't|we'd|we'd've|we'll|we're|we've|weren't|what'll|what're|what's|what've|when's|where'd|where's|where've|who'd|who'll|who're|who's|who've|why'll|why're|why's|won't|would've|wouldn't|wouldn't've|y'all|y'all'd've|you'd|you'd've|you'll|you're|you've|em)$", Pattern.CASE_INSENSITIVE);
		mContraMap = new HashMap<String,String>();
		mContraMap.put("aren't","are not");
		mContraMap.put("can't","cannot");
		mContraMap.put("could've","could have");
		mContraMap.put("couldn't","could not");
		mContraMap.put("couldn't've","could not have");
		mContraMap.put("didn't","did not");
		mContraMap.put("doesn't","does not");
		mContraMap.put("don't","do not");
		mContraMap.put("hadn't","had not");
		mContraMap.put("hadn't've","had not have");
		mContraMap.put("hasn't","has not");
		mContraMap.put("haven't","have not");
		mContraMap.put("he'd","he would");
		mContraMap.put("he'd've","he would have");
		mContraMap.put("he'll","he will");
		mContraMap.put("he's","he is");
		mContraMap.put("how'd","how would");
		mContraMap.put("how'll","how will");
		mContraMap.put("how's","how is");
		mContraMap.put("i'd","i would");
		mContraMap.put("i'd've","i would have");
		mContraMap.put("i'll","i will");
		mContraMap.put("i'm","i am");
		mContraMap.put("i've","i have");
		mContraMap.put("isn't","is not");
		mContraMap.put("it'd","it would");
		mContraMap.put("it'd've","it would have");
		mContraMap.put("it'll","it will");
		mContraMap.put("it's","it is");
		mContraMap.put("let's","let us");
		mContraMap.put("ma'am","madam");
		mContraMap.put("mightn't","might not");
		mContraMap.put("mightn't've","might not have");
		mContraMap.put("might've","might have");
		mContraMap.put("mustn't","must not");
		mContraMap.put("must've","must have");
		mContraMap.put("needn't","need not");
		mContraMap.put("not've","not have");
		mContraMap.put("o'clock","of the clock");
		mContraMap.put("shan't","shall not");
		mContraMap.put("she'd","she would");
		mContraMap.put("she'd've","she would have");
		mContraMap.put("she'll","she will");
		mContraMap.put("she's","she is");
		mContraMap.put("should've","should have");
		mContraMap.put("shouldn't","should not");
		mContraMap.put("shouldn't've","should not have");
		mContraMap.put("that's","that is");
		mContraMap.put("there'd","there would");
		mContraMap.put("there'd've","there would have");
		mContraMap.put("there're","there are");
		mContraMap.put("there's","there is");
		mContraMap.put("they'd","they would");
		mContraMap.put("they'd've","they would have");
		mContraMap.put("they'll","they will");
		mContraMap.put("they're","they are");
		mContraMap.put("they've","they have");
		mContraMap.put("wasn't","was not");
		mContraMap.put("we'd","we would");
		mContraMap.put("we'd've","we would have");
		mContraMap.put("we'll","we will");
		mContraMap.put("we're","we are");
		mContraMap.put("we've","we have");
		mContraMap.put("weren't","were not");
		mContraMap.put("what'll","what will");
		mContraMap.put("what're","what are");
		mContraMap.put("what's","what is");
		mContraMap.put("what've","what have");
		mContraMap.put("when's","when is");
		mContraMap.put("where'd","where did");
		mContraMap.put("where's","where is");
		mContraMap.put("where've","where have");
		mContraMap.put("who'd","who would");
		mContraMap.put("who'll","who will");
		mContraMap.put("who're","who are");
		mContraMap.put("who's","who is");
		mContraMap.put("who've","who have");
		mContraMap.put("why'll","why will");
		mContraMap.put("why're","why are");
		mContraMap.put("why's","why is");
		mContraMap.put("won't","will not");
		mContraMap.put("would've","would have");
		mContraMap.put("wouldn't","would not");
		mContraMap.put("wouldn't've","would not have");
		mContraMap.put("y'all","you all");
		mContraMap.put("y'all'd've","you all would have");
		mContraMap.put("you'd","you would");
		mContraMap.put("you'd've","you would have");
		mContraMap.put("you'll","you will");
		mContraMap.put("you're","you are");
		mContraMap.put("you've","you have");
		mContraMap.put("em","them");
		
		
	}

	@Override
	public boolean increment() throws TokenizerException {

		Token symboltok = null;
		/*Pattern symbolPattern1 = Pattern.compile("((^[^A-Za-z0-9])|([^A-Za-z0-9]$))");
		Pattern apostroPattern1= Pattern.compile("(^[A-Za-z0-9]+'s)$");
		Pattern apostroPattern2= Pattern.compile("(^[A-Za-z0-9]+')");
		Pattern hyphenPattern = Pattern.compile("((^[A-Za-z]+)(-)([A-Za-z]+)$)");
		Pattern hyphenPattern2 = Pattern.compile("([^A-Za-z])");
		String outone = null;
		String outtwo = null;
		String outthree = null;*/
		while((symboltok = mInputStream.next())!= null) {
			String tokString = symboltok.toString();
			Matcher terminatingPunctAndPossesiveMatcher = mTerminatingPunctAndPossesive.matcher(tokString);
			if(terminatingPunctAndPossesiveMatcher.find())
				tokString = terminatingPunctAndPossesiveMatcher.replaceAll("");
			Matcher contraMatcher = mContraPattern.matcher(tokString);
			while(contraMatcher.find()) {
				if(mContraMap.containsKey(contraMatcher.group(1).toLowerCase())) {
					boolean isEm = tokString.equalsIgnoreCase("em");
					String firstChar = tokString.substring(0, 1);
					tokString = mContraMap.get(contraMatcher.group(1).toLowerCase());
					if(!isEm) {
						tokString = firstChar + tokString.substring(1);
					}
				}
				else {
					//shouldn't have come here check the mContraMap and mContraPattern; they should match
					assert(false);
				}
			}
			
			Matcher hyphenAmidstAlphaMatcher = mHyphenAmidstAlpha.matcher(tokString);
			if(hyphenAmidstAlphaMatcher.find()) {
				tokString = hyphenAmidstAlphaMatcher.group(1) + " " + hyphenAmidstAlphaMatcher.group(2);
			}
			tokString = tokString.replaceAll("'", "");
			if(!tokString.isEmpty()) {
			symboltok.setTermText(tokString);
			mOutputList.add(symboltok);
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
