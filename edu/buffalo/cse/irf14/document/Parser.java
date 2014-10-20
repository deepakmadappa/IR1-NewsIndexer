/**
 * 
 */
package edu.buffalo.cse.irf14.document;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * @author nikhillo
 * Class that parses a given file into a Document
 */
public class Parser {
	/**
	 * Static method to parse the given file into the Document object
	 * @param filename : The fully qualified filename to be parsed
	 * @return The parsed and fully loaded Document object
	 * @throws ParserException In case any error occurs during parsing
	 */
	public static Document parse(String filename) throws ParserException {
		Document retDocument = new Document();
		if(filename == null) {
			throw new ParserException("File name is null");
		}
		try {
			getFileIDAndCategory(retDocument, filename.replace(File.separatorChar, '|'));
			Path path = Paths.get(filename);
			File file = new File(filename);
			if(!file.exists()) {
				throw new ParserException("file does not exist");
			}
			List<String> lines = Files.readAllLines(path);
			for (String string : lines) {
				retDocument.mDocumentLenght += string.length();
			}
			getTitleAuthorAndContent(retDocument, lines);
		}
		catch(ParserException pEx){
			throw pEx;
		}
		catch(Exception e) {
			System.out.println(e.getMessage());
		}
		return retDocument;
	}
	
	private static void getFileIDAndCategory(Document doc, String filename) throws ParserException {
		String[] filenameTokens = filename.split("\\|");
		int tokensLength = filenameTokens.length;
		if(tokensLength < 3) {
			throw new ParserException("Invalid file name, directory not deep enough");
		}
		doc.setField(FieldNames.CATEGORY, filenameTokens[tokensLength - 2]);
		doc.setField(FieldNames.FILEID, filenameTokens[tokensLength - 1]);
	}
	
	private static void getTitleAuthorAndContent(Document doc, List<String> lines) throws ParserException{
		int nLineNumber = 0;
		
		/*code to match for Title*/
		Pattern titlePattern = Pattern.compile(".*[ A-Z]+.*");
		boolean bFound = false;
		for(;nLineNumber < lines.size(); nLineNumber++) {
			String line = lines.get(nLineNumber);
			if(line.isEmpty())
				continue;
			
			Matcher matcher = titlePattern.matcher(line);
			if(matcher.find()) {
				doc.setField(FieldNames.TITLE, line);
				bFound = true;
				break;
			}
		}
		if(!bFound) {
			throw new ParserException("Title not found");
		}
		
		/*code to match for Author, Date and Place */
		Pattern datePattern = Pattern.compile("([A-Za-z]+.*?), (Jan.*?[0-9]+|Feb.*?[0-9]+|Mar.*?[0-9]+|Apr.*?[0-9]+|May.*?[0-9]+|Jun.*?[0-9]+|Jul.*?[0-9]+|Aug.*?[0-9]+|Sept.*?[0-9]+|Oct.*?[0-9]+|Nov.*?[0-9]+|Dec.*?[0-9]+)",
				Pattern.CASE_INSENSITIVE);
		Pattern authorPattern = Pattern.compile("<author> *([Bb][Yy])? (.*)</author>", Pattern.CASE_INSENSITIVE);
		boolean bAuthorFound = false;
		boolean bDateFound = false;
		Matcher authorMatcher;
		Matcher dateMatcher;
		List<String> content = new ArrayList<String>();
		for(++nLineNumber; nLineNumber < lines.size(); nLineNumber++) {
			String line = lines.get(nLineNumber);
			if(line.isEmpty())
				continue;
			if(!bAuthorFound) {
				authorMatcher = authorPattern.matcher(line);
				if(authorMatcher.find()) {
					String[] authorPart = authorMatcher.group(2).split(",");
					doc.setField(FieldNames.AUTHOR, authorPart[0]);
					if(authorPart.length > 1) {
						doc.setField(FieldNames.AUTHORORG, authorPart[1].trim());
					}
					bAuthorFound = true;
					continue;
				}
			}
			dateMatcher = datePattern.matcher(line);
			if(dateMatcher.find()) {
				bDateFound = true;
				doc.setField(FieldNames.PLACE, dateMatcher.group(1));
				doc.setField(FieldNames.NEWSDATE, dateMatcher.group(2));
				String remaining = line.substring(dateMatcher.end(), line.length());
				content.add(remaining);
				break;
			}
		}
		
		if(!bDateFound) {
			throw new ParserException("Date/Place not found");
		}
		
		for(++nLineNumber ;nLineNumber < lines.size(); nLineNumber++) {
			String line = lines.get(nLineNumber);
			if(line.isEmpty())
				continue;
			content.add(line);
		}
		StringBuilder sb = new StringBuilder();
		for (String string : content) {
			sb.append(" " + string);
		}
		doc.setField(FieldNames.CONTENT, sb.toString());
	}
	
/*	private static void extractDateAndPlace(Document doc, String str) throws ParserException {
		Pattern datePattern = Pattern.compile("(Jan.*[0-9]+|Feb.*[0-9]+|Mar.*[0-9]+|Apr.*[0-9]+|May.*[0-9]+|Jun.*[0-9]+|Jul.*[0-9]+|Aug.*[0-9]+|Sept.*[0-9]+|Oct.*[0-9]+|Nov.*[0-9]+|Dec.*[0-9]+)");
		Matcher matcher = datePattern.matcher(str);
		matcher.find();
		doc.setField(FieldNames.PLACE, str.substring(0, matcher.start() - 1));
		doc.setField(FieldNames.NEWSDATE, matcher.group());
	}*/

}
