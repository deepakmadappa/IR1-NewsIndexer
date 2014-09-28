package edu.buffalo.cse.irf14.analysis;

import java.util.HashMap;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class AccentRuleFilter extends TokenFilter {
	public HashMap<String,String> mAccentMap = new HashMap<String,String>(); // to save the accent values for replacement
	Pattern mAccentPatter;
	public AccentRuleFilter(TokenStream stream) {
		super(stream);
		// TODO Auto-generated constructor stub

		mAccentMap.put("\u00C0","A");
		mAccentMap.put("\u00C1","A");
		mAccentMap.put("\u00C2","A");
		mAccentMap.put("\u00C3","A");
		mAccentMap.put("\u00C4","A");
		mAccentMap.put("\u00C5","A");
		mAccentMap.put("\u00C6","AE");
		mAccentMap.put("\u00C7","C");
		mAccentMap.put("\u00C8","E");
		mAccentMap.put("\u00C9","E");
		mAccentMap.put("\u00CA","E");
		mAccentMap.put("\u00CB","E");
		mAccentMap.put("\u00CC","I");
		mAccentMap.put("\u00CD","I");
		mAccentMap.put("\u00CE","I");
		mAccentMap.put("\u00CF","I");
		mAccentMap.put("\u0132","IJ");
		mAccentMap.put("\u00D0","D");
		mAccentMap.put("\u00D1","N");
		mAccentMap.put("\u00D2","O");
		mAccentMap.put("\u00D3","O");
		mAccentMap.put("\u00D4","O");
		mAccentMap.put("\u00D5","O");
		mAccentMap.put("\u00D6","O");
		mAccentMap.put("\u00D8","O");
		mAccentMap.put("\u0152","OE");
		mAccentMap.put("\u00DE","TH");
		mAccentMap.put("\u00D9","U");
		mAccentMap.put("\u00DA","U");
		mAccentMap.put("\u00DB","U");
		mAccentMap.put("\u00DC","U");
		mAccentMap.put("\u00DD","Y");
		mAccentMap.put("\u0178","Y");
		mAccentMap.put("\u00E0","a");
		mAccentMap.put("\u00E1","a");
		mAccentMap.put("\u00E2","a");
		mAccentMap.put("\u00E3","a");
		mAccentMap.put("\u00E4","a");
		mAccentMap.put("\u00E5","a");
		mAccentMap.put("\u00E6","ae");
		mAccentMap.put("\u00E7","c");
		mAccentMap.put("\u00E8","e");
		mAccentMap.put("\u00E9","e");
		mAccentMap.put("\u00EA","e");
		mAccentMap.put("\u00EB","e");
		mAccentMap.put("\u00EC","i");
		mAccentMap.put("\u00ED","i");
		mAccentMap.put("\u00EE","i");
		mAccentMap.put("\u00EF","i");
		mAccentMap.put("\u0133","ij");
		mAccentMap.put("\u00F0","d");
		mAccentMap.put("\u00F1","n");
		mAccentMap.put("\u00F2","o");
		mAccentMap.put("\u00F3","o");
		mAccentMap.put("\u00F4","o");
		mAccentMap.put("\u00F5","o");
		mAccentMap.put("\u00F6","o");
		mAccentMap.put("\u00F8","o");
		mAccentMap.put("\u0153","oe");
		mAccentMap.put("\u00DF","ss");
		mAccentMap.put("\u00FE","th");
		mAccentMap.put("\u00F9","u");
		mAccentMap.put("\u00FA","u");
		mAccentMap.put("\u00FB","u");
		mAccentMap.put("\u00FC","u");
		mAccentMap.put("\u00FD","y");
		mAccentMap.put("\u00FF","y");
		mAccentMap.put("\uFB00","ff");
		mAccentMap.put("\uFB01","fi");
		mAccentMap.put("\uFB02","fl");
		mAccentMap.put("\uFB03","fi");
		mAccentMap.put("\uFB04","fl");
		mAccentMap.put("\uFB05","ft");
		mAccentMap.put("\uFB06","st");

		mAccentPatter = Pattern.compile("\u00C0|\u00C1|\u00C2|\u00C3|\u00C4|\u00C5|\u00C6|\u00C7|\u00C8|\u00C9|\u00CA|\u00CB|\u00CC|\u00CD|\u00CE|\u00CF|\u0132|\u00D0|\u00D1|\u00D2|\u00D3|\u00D4|\u00D5|\u00D6|\u00D8|\u0152|\u00DE|\u00D9|\u00DA|\u00DB|\u00DC|\u00DD|\u0178|\u00E0|\u00E1|\u00E2|\u00E3|\u00E4|\u00E5|\u00E6|\u00E7|\u00E8|\u00E9|\u00EA|\u00EB|\u00EC|\u00ED|\u00EE|\u00EF|\u0133|\u00F0|\u00F1|\u00F2|\u00F3|\u00F4|\u00F5|\u00F6|\u00F8|\u0153|\u00DF|u00F9|\u00FA|\u00FB|\u00FC|\u00FD|\u00FF|\uFB00|\uFB01|\uFB02|\uFB03|\uFB04|\uFB05|\uFB06");

	}
	@Override
	public boolean increment() throws TokenizerException {
		Token accenttok = null;
		//Loop to iterate over the tokens in a tokenstream
		while((accenttok = mInputStream.next())!= null) {
			String input = accenttok.toString();
			Matcher matcher = mAccentPatter.matcher(input);
			StringBuilder builder = new StringBuilder(); // to store the new string without the accent character and replaced with new character set.
			int i = 0;
			String tReplace = null;
			String valueReplace = null;
			while(matcher.find()) {
				tReplace = matcher.group(0);
				valueReplace = mAccentMap.get(tReplace); //replacer string
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
