package edu.buffalo.cse.irf14.analysis;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DateRuleFilter extends TokenFilter {

	public DateRuleFilter(TokenStream stream) {
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
		String DDMMMYYYY = "(?i)(.?)([0-9][0-9]?)(st|nd|rd|th)? *(jan(uary)?|feb(ruary)?|mar(ch)?|apr(il)?|may|jun(e)?|jul(y)?|aug(ust)?|sep(tember)?|oct(ober)?|nov(ember)?|dec(ember)?)([ ,]*([0-9][0-9]?[0-9]?[0-9]?))?(.?)"; //1 3 16
		String MMMDDYYYY = "(?i)(.?)(jan(uary)?|feb(ruary)?|mar(ch)?|apr(il)?|may|jun(e)?|jul(y)?|aug(ust)?|sep(tember)?|oct(ober)?|nov(ember)?|dec(ember)?) *([0-9][0-9]?)(, ([0-9][0-9]?[0-9]?[0-9]?))?(.?)";	//1 13 15
		String ADBC = "(?i)(.?)([0-9][0-9]?[0-9]?[0-9]?) ?(AD|BC)(.?)"; // 1 2
		String HHMMSS = "(?i)(.?)([0-9]?[0-9])(:([0-9][0-9]))?(:([0-9][0-9]))? ?(AM|PM|o'clock)(.?)";	// 1 3 5 6
		String YYYYYY = "(.?)([0-9][0-9][0-9][0-9])-([0-9][0-9])(.?)";//1 2
		String YYYY = "(.?)([0-9][0-9][0-9][0-9])(.?)";
		
		String input = putTokensTogether();
		if(input.isEmpty())
			return new TokenStream(mOutputList);
		
		Matcher matcher = Pattern.compile(DDMMMYYYY).matcher(input);
		if(matcher.find()) { 
			if(!matcher.group(18).matches("[0-9]") && !matcher.group(1).matches("[0-9]"))
				input = matcher.replaceAll(replaceDDMMMYYYY(matcher.group(0), matcher.group(2), matcher.group(4), matcher.group(17), null, matcher.group(18), matcher.group(1))); 
		}
		matcher = Pattern.compile(MMMDDYYYY).matcher(input);
		if(matcher.find()) {
			if(!matcher.group(17).matches("[0-9]") && !matcher.group(1).matches("[0-9]"))
				input = matcher.replaceAll(replaceDDMMMYYYY(matcher.group(0), matcher.group(14), matcher.group(2), matcher.group(16), null, matcher.group(17), matcher.group(1)));
		}
		matcher = Pattern.compile(HHMMSS).matcher(input);
		if(matcher.find()) {
			if(!matcher.group(8).matches("[0-9]") && !matcher.group(1).matches("[0-9]"))
				input = matcher.replaceAll(replaceHHMMSS(matcher.group(0), matcher.group(2), matcher.group(4), matcher.group(6), matcher.group(7), matcher.group(8), matcher.group(1)));
		}
		matcher = Pattern.compile(ADBC).matcher(input);
		if(matcher.find()) {
			if(!matcher.group(4).matches("[0-9]") && !matcher.group(1).matches("[0-9]"))
				input = matcher.replaceAll( replaceDDMMMYYYY(matcher.group(0), null, null, matcher.group(2), matcher.group(3), matcher.group(4), matcher.group(1)));
		}
		matcher = Pattern.compile(YYYYYY).matcher(input);
		if(matcher.find()) {
			if(!matcher.group(4).matches("[0-9]") && !matcher.group(1).matches("[0-9]"))
				input = matcher.replaceAll(replaceYYYYYY(matcher.group(0), matcher.group(2), matcher.group(3), matcher.group(4), matcher.group(1)));
		}
		matcher = Pattern.compile(YYYY).matcher(input);
		if(matcher.find()) { 
			if(!matcher.group(3).matches("[0-9]") && !matcher.group(1).matches("[0-9]"))
				input = matcher.replaceAll( replaceDDMMMYYYY(matcher.group(0), null, null, matcher.group(2), null, matcher.group(3), matcher.group(1)));
		}
		
		TokenStream tkstr = null;
		try {
			tkstr = new Tokenizer().consume(input);
		}
		catch (Exception e) {
			
		}
		
		return tkstr;
	}
	
	//put humpty dumpty back together again 
	private String putTokensTogether() {
		Token token;
		String fullString = new String();
		token = mInputStream.next();
		if(token != null) {
			fullString = fullString.concat(token.toString());
		}
		while((token = mInputStream.next()) != null) {
			fullString = fullString.concat(" ").concat(token.toString());
		}
		return fullString;
	}
	
	private String replaceDDMMMYYYY(String orig, String date, String month, String year, String adbc, String suffix, String prefix) {
		if(suffix.matches("[0-9]"))
			return orig;
		if(prefix.matches("[0-9]"))
			return orig;
		String out = new String(prefix);
		if(adbc != null && adbc.toLowerCase().equalsIgnoreCase("bc"))
			out += "-";
		if(year == null || year.isEmpty()) {
			out = out + "1900";
		} else {
			if(year.length() == 3) {
				out+= "0";
			} else if(year.length() == 2) {
				out += "00";
			} else if(year.length() == 1) {
				out += "000";
			}
			out = out + year;
		}
		
		if(month != null)
			out += getMonthNumber(month);
		else
			out += "01";
		
		String properDate = new String();
		if(date == null || date.isEmpty()) {
			properDate += "01";
		} else {
			if(date.length() == 1) {
				properDate += "0";
			}
			properDate += date;
		}
		out += properDate;
		
		return out + suffix;
	}
	
	private String replaceHHMMSS(String orig, String hh, String mm, String ss, String ampm, String suffix, String prefix) {
		if(suffix.matches("[0-9]"))
			return orig;
		if(prefix.matches("[0-9]"))
			return orig;
		if(ampm != null && mm != null && ss != null) {
			//just hh is present, this can even be an ordinary number needs atleast hh:mm or hh AM/PM/o'clock
			return orig;
		}
		String out = new String(prefix);
		try {
			if(!ampm.isEmpty() && ampm.equalsIgnoreCase("pm")) {
				int h = Integer.parseInt(hh);
				h+= 12;
				out+= h;
			} else {
				out += hh;
			}
		} catch(Exception e) {
			out += hh;
		}
		
		if(mm==null) {
			out+= ":00";
		} else {
			out = out + ":" + mm;
		}
		
		if(ss==null) {
			out+= ":00";
		} else {
			out = out + ":" + ss;
		}
		
		return out + suffix;
	}
	
	private String replaceYYYYYY (String orig, String year1, String year2, String suffix, String prefix) {
		if(suffix.matches("[0-9]"))
			return orig;
		if(prefix.matches("[0-9]"))
			return orig;
		String out = new String(prefix);
		
		out = out + year1 +"01" + "01-" + year1.charAt(0) + year1.charAt(1) + year2 + "01" + "01";
		return out + suffix;
	}
	
	private String getMonthNumber(String monthName) {
		String iMonthName = monthName.toLowerCase();
		if(iMonthName.equalsIgnoreCase("january")  || iMonthName.equalsIgnoreCase("jan")) {
			return "01";
		} else if(iMonthName.equalsIgnoreCase("february") || iMonthName.equalsIgnoreCase("feb")) {
			return "02";
		} else if(iMonthName.equalsIgnoreCase("march") || iMonthName.equalsIgnoreCase("mar")) {
			return "03";
		} else if(iMonthName.equalsIgnoreCase("april") || iMonthName.equalsIgnoreCase("apr")) {
			return "04";
		} else if(iMonthName.equalsIgnoreCase("may")) {
			return "05";
		} else if(iMonthName.equalsIgnoreCase("june") || iMonthName.equalsIgnoreCase("jun")) {
			return "06";
		} else if(iMonthName.equalsIgnoreCase("july") || iMonthName.equalsIgnoreCase("jul")) {
			return "07";
		} else if(iMonthName.equalsIgnoreCase("august") || iMonthName.equalsIgnoreCase("aug")) {
			return "08";
		} else if(iMonthName.equalsIgnoreCase("september") || iMonthName.equalsIgnoreCase("sep")) {
			return "09";
		} else if(iMonthName.equalsIgnoreCase("october") || iMonthName.equalsIgnoreCase("oct")) {
			return "10";
		} else if(iMonthName.equalsIgnoreCase("november") || iMonthName.equalsIgnoreCase("nov")) {
			return "11";
		} else if(iMonthName.equalsIgnoreCase("december")|| iMonthName.equalsIgnoreCase("dec")) {
			return "12";
		}
		return "";
	}
}
