package edu.buffalo.cse.irf14.analysis;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class SymbolRuleFilter extends TokenFilter {

	public SymbolRuleFilter(TokenStream stream) {
		super(stream);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean increment() throws TokenizerException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public TokenStream getStream(){
		
		Token symboltok = null;
		Pattern symbolPattern1 = Pattern.compile("((^[^A-Za-z0-9])|([^A-Za-z0-9]$))");
		Pattern contraPattern = Pattern.compile("^(aren't|can't|could've|couldn't|couldn't've|didn't|doesn't|don't|hadn't|hadn't've|hasn't|haven't|he'd|he'd've|he'll|he's|how'd|how'll|how's|I'd|I'd've|I'll|I'm|I've|isn't|it'd|it'd've|it'll|it's|let's|ma'am|mightn't|mightn't've|might've|mustn't|must've|needn't|not've|o'clock|shan't|she'd|she'd've|she'll|she's|should've|shouldn't|shouldn't've|that's|there'd|there'd've|there're|there's|they'd|they'd've|they'll|they're|they've|wasn't|we'd|we'd've|we'll|we're|we've|weren't|what'll|what're|what's|what've|when's|where'd|where's|where've|who'd|who'll|who're|who's|who've|why'll|why're|why's|won't|would've|wouldn't|wouldn't've|y'all|y'all'd've|you'd|you'd've|you'll|you're|you've|em)$");
		Pattern apostroPattern1= Pattern.compile("(^[A-Za-z0-9]+'s)$");
		Pattern apostroPattern2= Pattern.compile("(^[A-Za-z0-9]+')");
		Pattern hyphenPattern = Pattern.compile("((^[A-Za-z]+)(-)([A-Za-z]+)$)");
		Pattern hyphenPattern2 = Pattern.compile("(^A-Za-z)");
		HashMap<String,String> contraMap = new HashMap<String,String>();
		contraMap.put("aren't","are not");
		contraMap.put("can't","cannot");
		contraMap.put("could've","could have");
		contraMap.put("couldn't","could not");
		contraMap.put("couldn't've","could not have");
		contraMap.put("didn't","did not");
		contraMap.put("doesn't","does not");
		contraMap.put("don't","do not");
		contraMap.put("hadn't","had not");
		contraMap.put("hadn't've","had not have");
		contraMap.put("hasn't","has not");
		contraMap.put("haven't","have not");
		contraMap.put("he'd","he would");
		contraMap.put("he'd've","he would have");
		contraMap.put("he'll","he will");
		contraMap.put("he's","he is");
		contraMap.put("how'd","how would");
		contraMap.put("how'll","how will");
		contraMap.put("how's","how is");
		contraMap.put("I'd","I would");
		contraMap.put("I'd've","I would have");
		contraMap.put("I'll","I will");
		contraMap.put("I'm","I am");
		contraMap.put("I've","I have");
		contraMap.put("isn't","is not");
		contraMap.put("it'd","it would");
		contraMap.put("it'd've","it would have");
		contraMap.put("it'll","it will");
		contraMap.put("it's","it is");
		contraMap.put("let's","let us");
		contraMap.put("ma'am","madam");
		contraMap.put("mightn't","might not");
		contraMap.put("mightn't've","might not have");
		contraMap.put("might've","might have");
		contraMap.put("mustn't","must not");
		contraMap.put("must've","must have");
		contraMap.put("needn't","need not");
		contraMap.put("not've","not have");
		contraMap.put("o'clock","of the clock");
		contraMap.put("shan't","shall not");
		contraMap.put("she'd","she would");
		contraMap.put("she'd've","she would have");
		contraMap.put("she'll","she will");
		contraMap.put("she's","she is");
		contraMap.put("should've","should have");
		contraMap.put("shouldn't","should not");
		contraMap.put("shouldn't've","should not have");
		contraMap.put("that's","that is");
		contraMap.put("there'd","there would");
		contraMap.put("there'd've","there would have");
		contraMap.put("there're","there are");
		contraMap.put("there's","there is");
		contraMap.put("they'd","they would");
		contraMap.put("they'd've","they would have");
		contraMap.put("they'll","they will");
		contraMap.put("they're","they are");
		contraMap.put("they've","they have");
		contraMap.put("wasn't","was not");
		contraMap.put("we'd","we would");
		contraMap.put("we'd've","we would have");
		contraMap.put("we'll","we will");
		contraMap.put("we're","we are");
		contraMap.put("we've","we have");
		contraMap.put("weren't","were not");
		contraMap.put("what'll","what will");
		contraMap.put("what're","what are");
		contraMap.put("what's","what is");
		contraMap.put("what've","what have");
		contraMap.put("when's","when is");
		contraMap.put("where'd","where did");
		contraMap.put("where's","where is");
		contraMap.put("where've","where have");
		contraMap.put("who'd","who would");
		contraMap.put("who'll","who will");
		contraMap.put("who're","who are");
		contraMap.put("who's","who is");
		contraMap.put("who've","who have");
		contraMap.put("why'll","why will");
		contraMap.put("why're","why are");
		contraMap.put("why's","why is");
		contraMap.put("won't","will not");
		contraMap.put("would've","would have");
		contraMap.put("wouldn't","would not");
		contraMap.put("wouldn't've","would not have");
		contraMap.put("y'all","you all");
		contraMap.put("y'all'd've","you all would have");
		contraMap.put("you'd","you would");
		contraMap.put("you'd've","you would have");
		contraMap.put("you'll","you will");
		contraMap.put("you're","you are");
		contraMap.put("you've","you have");
		contraMap.put("em","them");
		String outone = null;
		String outtwo = null;
		String outthree = null;
		while((symboltok = mInputStream.next())!= null) {
			
			outone = symboltok.toString();
			Matcher symbolMatcher1 = symbolPattern1.matcher(outone);
			if(symbolMatcher1.find()) {
				outone = symbolMatcher1.replaceAll("");
			}
			Matcher contraMatcher = contraPattern.matcher(outone);
			if (contraMatcher.find()){
				String valueReplace = contraMap.get(outone);
				symboltok.setTermText(valueReplace);
				mOutputList.add(symboltok);
				break;
			}
				outtwo=outone;
				outthree=outone;
				Matcher apostroMatcher1 = apostroPattern1.matcher(outone);
				if(apostroMatcher1.find()){
					outtwo = apostroMatcher1.replaceAll("");
					}
				Matcher apostroMatcher2 = apostroPattern2.matcher(outtwo);
				if(apostroMatcher2.find()){
					StringBuilder builder = new StringBuilder();
					int i = 0;
					while(apostroMatcher2.find()){
					
						   builder.append(outtwo.substring(i, apostroMatcher2.start()));
						   i = apostroMatcher2.end();
					}
					builder.append(outtwo.substring(i, outtwo.length()));
					outthree=builder.toString();
				}
				Matcher hyphenMatcher = hyphenPattern.matcher(outthree);
				if(hyphenMatcher.find()){
					
					Matcher hyphenMatcher2 =hyphenPattern2.matcher(outthree);
					StringBuilder builder1 = new StringBuilder();
					int i1 = 0;
					String valueReplace = " ";
					while(hyphenMatcher2.find()){
					
						   builder1.append(outtwo.substring(i1, hyphenMatcher2.start()));
						   builder1.append(valueReplace);
						   i1 = hyphenMatcher2.end();
					}
					builder1.append(outtwo.substring(i1, outtwo.length()));
					outthree=builder1.toString();
				}
				symboltok.setTermText(outthree);
				mOutputList.add(symboltok);
		}
		return new TokenStream(mOutputList);
	}
}
