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
		while((splchartok = mInputStream.next())!= null) {
			String input = splchartok.toString();
			Matcher splcharMatcher = splcharPattern.matcher(input);
			if(splcharMatcher.find()) {
				String out = splcharMatcher.replaceAll("");
				splchartok.setTermText(out);
				mOutputList.add(splchartok);
			}
			else
			{
				mOutputList.add(splchartok);
			}
			}
			
		return new TokenStream(mOutputList);
	
	}
}
