package edu.buffalo.cse.irf14.analysis;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class SpecialCharacRuleFilter extends TokenFilter {

	public SpecialCharacRuleFilter(TokenStream stream) {
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
		Token splchartok = null;
		Pattern splcharPattern = Pattern.compile("[^a-zA-Z0-9.,-]");
		Pattern hyphenPattern = Pattern.compile("((^[A-Za-z]+)(-)([A-Za-z]+)$)");
		Pattern hyphenPattern2 = Pattern.compile("([^A-Za-z])");
		while((splchartok = mInputStream.next())!= null) {
			
			String input = splchartok.toString();
			String out = input;
			Matcher splcharMatcher = splcharPattern.matcher(out);
			if(splcharMatcher.find())
			{
				out= splcharMatcher.replaceAll("");
			}
			Matcher hyphenMatcher = hyphenPattern.matcher(out);			
			if(hyphenMatcher.find()) {
			
				Matcher hyphenMatcher2 = hyphenPattern2.matcher(out);	
				String out1 = hyphenMatcher2.replaceAll("");
				splchartok.setTermText(out1);
				mOutputList.add(splchartok);
			}
			else
			{
				splchartok.setTermText(out);
				mOutputList.add(splchartok);
			}
		}
			
		return new TokenStream(mOutputList);
	
	}
}
