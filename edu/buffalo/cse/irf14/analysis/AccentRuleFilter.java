package edu.buffalo.cse.irf14.analysis;

import java.util.HashMap;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class AccentRuleFilter extends TokenFilter {

	public AccentRuleFilter(TokenStream stream) {
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
	Token accenttok = null;
	HashMap<String,String> accentMap = new HashMap<String,String>(); // to save the accent values for replacement
	   accentMap.put("\u00C0","A");
	   accentMap.put("\u00C1","A");
	   accentMap.put("\u00C2","A");
	   accentMap.put("\u00C3","A");
	   accentMap.put("\u00C4","A");
	   accentMap.put("\u00C5","A");
	   accentMap.put("\u00C6","AE");
	   accentMap.put("\u00C7","C");
	   accentMap.put("\u00C8","E");
	   accentMap.put("\u00C9","E");
	   accentMap.put("\u00CA","E");
	   accentMap.put("\u00CB","E");
	   accentMap.put("\u00CC","I");
	   accentMap.put("\u00CD","I");
	   accentMap.put("\u00CE","I");
	   accentMap.put("\u00CF","I");
	   accentMap.put("\u0132","IJ");
	   accentMap.put("\u00D0","D");
	   accentMap.put("\u00D1","N");
	   accentMap.put("\u00D2","O");
	   accentMap.put("\u00D3","O");
	   accentMap.put("\u00D4","O");
	   accentMap.put("\u00D5","O");
	   accentMap.put("\u00D6","O");
	   accentMap.put("\u00D8","O");
	   accentMap.put("\u0152","OE");
	   accentMap.put("\u00DE","TH");
	   accentMap.put("\u00D9","U");
	   accentMap.put("\u00DA","U");
	   accentMap.put("\u00DB","U");
	   accentMap.put("\u00DC","U");
	   accentMap.put("\u00DD","Y");
	   accentMap.put("\u0178","Y");
	   accentMap.put("\u00E0","a");
	   accentMap.put("\u00E1","a");
	   accentMap.put("\u00E2","a");
	   accentMap.put("\u00E3","a");
	   accentMap.put("\u00E4","a");
	   accentMap.put("\u00E5","a");
	   accentMap.put("\u00E6","ae");
	   accentMap.put("\u00E7","c");
	   accentMap.put("\u00E8","e");
	   accentMap.put("\u00E9","e");
	   accentMap.put("\u00EA","e");
	   accentMap.put("\u00EB","e");
	   accentMap.put("\u00EC","i");
	   accentMap.put("\u00ED","i");
	   accentMap.put("\u00EE","i");
	   accentMap.put("\u00EF","i");
	   accentMap.put("\u0133","ij");
	   accentMap.put("\u00F0","d");
	   accentMap.put("\u00F1","n");
	   accentMap.put("\u00F2","o");
	   accentMap.put("\u00F3","o");
	   accentMap.put("\u00F4","o");
	   accentMap.put("\u00F5","o");
	   accentMap.put("\u00F6","o");
	   accentMap.put("\u00F8","o");
	   accentMap.put("\u0153","oe");
	   accentMap.put("\u00DF","ss");
	   accentMap.put("\u00FE","th");
	   accentMap.put("\u00F9","u");
	   accentMap.put("\u00FA","u");
	   accentMap.put("\u00FB","u");
	   accentMap.put("\u00FC","u");
	   accentMap.put("\u00FD","y");
	   accentMap.put("\u00FF","y");
	   accentMap.put("\uFB00","ff");
	   accentMap.put("\uFB01","fi");
	   accentMap.put("\uFB02","fl");
	   accentMap.put("\uFB03","fi");
	   accentMap.put("\uFB04","fl");
	   accentMap.put("\uFB05","ft");
	   accentMap.put("\uFB06","st");
	   
	   Pattern accentPattern = Pattern.compile("\u00C0|\u00C1|\u00C2|\u00C3|\u00C4|\u00C5|\u00C6|\u00C7|\u00C8|\u00C9|\u00CA|\u00CB|\u00CC|\u00CD|\u00CE|\u00CF|\u0132|\u00D0|\u00D1|\u00D2|\u00D3|\u00D4|\u00D5|\u00D6|\u00D8|\u0152|\u00DE|\u00D9|\u00DA|\u00DB|\u00DC|\u00DD|\u0178|\u00E0|\u00E1|\u00E2|\u00E3|\u00E4|\u00E5|\u00E6|\u00E7|\u00E8|\u00E9|\u00EA|\u00EB|\u00EC|\u00ED|\u00EE|\u00EF|\u0133|\u00F0|\u00F1|\u00F2|\u00F3|\u00F4|\u00F5|\u00F6|\u00F8|\u0153|\u00DF|u00F9|\u00FA|\u00FB|\u00FC|\u00FD|\u00FF|\uFB00|\uFB01|\uFB02|\uFB03|\uFB04|\uFB05|\uFB06");
	   //Loop to iterate over the tokens in a tokenstream
	   while((accenttok = mInputStream.next())!= null) {
	   String input = accenttok.toString();
	   Matcher matcher = accentPattern.matcher(input);
	   StringBuilder builder = new StringBuilder(); // to store the new string without the accent character and replaced with new character set.
	   int i = 0;
	   String tReplace = null;
	   String valueReplace = null;
	   while(matcher.find()) {
	   
	   tReplace = matcher.group(0);
	   valueReplace = accentMap.get(tReplace); //replacer string
	   builder.append(input.substring(i, matcher.start()));
	   builder.append(valueReplace);
	   i = matcher.end();
	   		   
	  }
	   
	   builder.append(input.substring(i, input.length()));
	   String out=builder.toString();
	   if(!out.isEmpty()) {
			accenttok.setTermText(out);
			mOutputList.add(accenttok); // setting output stream with current token
		}
	   else
		   mOutputList.add(accenttok);
	}// end while
	
	   return new TokenStream(mOutputList);
	}
}
