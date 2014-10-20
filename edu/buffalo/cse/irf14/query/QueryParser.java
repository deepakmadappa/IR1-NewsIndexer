
package edu.buffalo.cse.irf14.query;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.HashMap;

import edu.buffalo.cse.irf14.analysis.Analyzer;
import edu.buffalo.cse.irf14.analysis.AnalyzerFactory;
import edu.buffalo.cse.irf14.analysis.Token;
import edu.buffalo.cse.irf14.analysis.TokenStream;
import edu.buffalo.cse.irf14.analysis.Tokenizer;
import edu.buffalo.cse.irf14.analysis.TokenizerException;
import edu.buffalo.cse.irf14.document.FieldNames;
import edu.buffalo.cse.irf14.index.IndexType;

/**
 * @author nikhillo
 * Static parser that converts raw text to Query objects
 */
public class QueryParser {

	private enum ChildType {
		LEFT,RIGHT
	}
	
	public static void main(String[] args) {
		try {
			QueryParser.parse("Paris", "OR");
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	/**
	 * MEthod to parse the given user query into a Query object
	 * @param userQuery : The query to parse
	 * @param defaultOperator : The default operator to use, one amongst (AND|OR)
	 * @return Query object if successfully parsed, null otherwise
	 * @return Query object if successfully parsed, null otherwise\
	 */

	public static Query parse(String userQuery, String defaultOperator) throws QueryParserException {

		if(userQuery == null || userQuery.isEmpty()) {
			throw new QueryParserException("Query entered is null");
		}
		//Stamping the default operator to OR, if user does not provide
		if(defaultOperator.isEmpty()) {
			defaultOperator = "OR";
		}
		Query qObject = new Query();
		HashMap<String,TreeNode> trackNodeMap;
		trackNodeMap = new HashMap <String,TreeNode>();
		String uString = "("+userQuery+")";
		Pattern queryPattern = Pattern.compile("\\([^\\(^\\)]*\\)");// to get the inner parenthesis group
		Pattern spacePattern = Pattern.compile("\\s+(?=([^\"]*\"[^\"]*\")*[^\"]*$)");// to replace all whitespaces except within quoted text
		int i=1;
		TokenStream tStream = null;
		TokenStream qStream = null;
		Tokenizer mTokenizer = new Tokenizer();
		Matcher queryMatcher = queryPattern.matcher(uString); // to check for string within innermost parenthesis
		while (queryMatcher.find()) {

			String exp = "exp_"+i;
			String firstMatch = queryMatcher.group(0).trim();
			
			Matcher spaceMatcher = spacePattern.matcher(firstMatch);
			String secondMatch = spaceMatcher.replaceAll("-");
			String regExMatch[] = secondMatch.substring(1, secondMatch.length()-1).split("-"); // neglecting '(' and ')'
			TreeNode currentNode = new TreeNode();
			int rightChildIndex = 2; // for defaulting operator
			if(regExMatch.length>1) {

				if(regExMatch[1].equalsIgnoreCase("and")) {
					currentNode.mOperator = LogicalOperator.AND;
				}
				else if (regExMatch[1].equalsIgnoreCase("or")) {
					currentNode.mOperator = LogicalOperator.OR;
				}
				else if(regExMatch[1].equalsIgnoreCase("not")) {
					currentNode.mIsNot = true;
					currentNode.mOperator = LogicalOperator.AND;
				}else {
					rightChildIndex = 1;
					currentNode.mOperator = defaultOperator.equals("AND")?LogicalOperator.AND:LogicalOperator.OR;
					//					throw new QueryParserException("Unknown operator");
				}

				setChildAndIndexType(regExMatch[0], ChildType.LEFT, currentNode, trackNodeMap);
				setChildAndIndexType(regExMatch[rightChildIndex], ChildType.RIGHT, currentNode, trackNodeMap);

			}else if(regExMatch.length==1) {
				if(regExMatch[0].contains(":")) {						
					setIndexType(regExMatch[0],currentNode);
				} else {
					currentNode.mIndexType = IndexType.TERM;
				}
				if (regExMatch[0].length() >4 &&
						!(regExMatch[0].substring(0, 3).equalsIgnoreCase("exp_"))) {
					if(regExMatch[0].contains("\""))
						currentNode.mIsSingleQuotedString = true;
					if (currentNode.mSearchString == null) {
						
						try {
								tStream = mTokenizer.consume(regExMatch[0]);
								Analyzer contentAnalyzer = AnalyzerFactory.getInstance().getAnalyzerForField( FieldNames.CONTENT, tStream);
								while(contentAnalyzer.increment()) {}
								qStream = contentAnalyzer.getStream();
								currentNode.mSearchString = qStream.toString().trim();
							} catch (TokenizerException e) {

							}
						}
					//					currentNode.mSearchString=regExMatch[0];

				} else if(regExMatch[0].length() <= 4 ) {
					if(regExMatch[0].contains("\""))
						currentNode.mIsSingleQuotedString = true;
					if (currentNode.mSearchString == null) {
						try {
							tStream = mTokenizer.consume(regExMatch[0]);
							Analyzer contentAnalyzer = AnalyzerFactory.getInstance().getAnalyzerForField( FieldNames.CONTENT, tStream);
							while(contentAnalyzer.increment()) {}
							qStream = contentAnalyzer.getStream();
							currentNode.mSearchString = qStream.toString().trim();
						} catch (TokenizerException e) {

						}
					}
						
				}
			}// end length == 1		

			trackNodeMap.put(exp,currentNode);	
			if(regExMatch.length>rightChildIndex+1) {
				exp = "(" + exp;
				for(int appInd = rightChildIndex+1; appInd<regExMatch.length;appInd++) {
					exp = exp + " " + regExMatch[appInd];
				}			
				exp = exp + ")";
			}
			uString = queryMatcher.replaceFirst(exp);
			queryMatcher = queryPattern.matcher(uString);
			i++;
		} // end of while uQueryMatcher.find() for inner parenthesis terms

		String firstKey = (String) trackNodeMap.keySet().toArray()[0];
		qObject.mRootNode = trackNodeMap.get(firstKey);
		System.out.println("String : "+qObject.toString());//for testing
		String queryparsertext = qObject.toString();

		qObject.populateLeaves();
		for (int j = 0; j < qObject.mLeafNodes.size(); j++) {
			System.out.println(qObject.mLeafNodes.get(j));
		}

		if(queryparsertext == null || queryparsertext.isEmpty()) {
			return null;
		}else
			return qObject;
		

	}// end of parse method

	public static void setIndexType(String nodeString, TreeNode currentNode) {

		String partNode[] = nodeString.split(":");
		TokenStream tStream = null;
		TokenStream qStream = null;
		Tokenizer mTokenizer = new Tokenizer();
		currentNode.mSearchString = partNode[1];
		if (partNode[0].equalsIgnoreCase("Author")) {
			
			try {
				tStream = mTokenizer.consume(partNode[1]);
				Analyzer contentAnalyzer = AnalyzerFactory.getInstance().getAnalyzerForField( FieldNames.AUTHOR, tStream);
				while(contentAnalyzer.increment()) {}
				qStream = contentAnalyzer.getStream();
				currentNode.mSearchString = qStream.toString().trim();
			} catch (TokenizerException e) {

			}
			currentNode.mIndexType = IndexType.AUTHOR;
			
		} else if (partNode[0].equalsIgnoreCase("Category")) {
			
			try {
				tStream = mTokenizer.consume(partNode[1]);
				Analyzer contentAnalyzer = AnalyzerFactory.getInstance().getAnalyzerForField( FieldNames.CATEGORY, tStream);
				while(contentAnalyzer.increment()) {}
				qStream = contentAnalyzer.getStream();
				currentNode.mSearchString = qStream.toString().trim();
			} catch (TokenizerException e) {

			}		
			currentNode.mIndexType = IndexType.CATEGORY;
		}else if (partNode[0].equalsIgnoreCase("Place")) {
			
			try {
				
				tStream = mTokenizer.consume(partNode[1]);
				Analyzer contentAnalyzer = AnalyzerFactory.getInstance().getAnalyzerForField( FieldNames.PLACE, tStream);
				while(contentAnalyzer.increment()) {}
				qStream = contentAnalyzer.getStream();
				currentNode.mSearchString = qStream.toString().trim();
			} catch (TokenizerException e) {

			}
			currentNode.mIndexType = IndexType.PLACE;
		}else {
			
				try {
					tStream = mTokenizer.consume(partNode[1]);
					Analyzer contentAnalyzer = AnalyzerFactory.getInstance().getAnalyzerForField( FieldNames.CONTENT, tStream);
					while(contentAnalyzer.increment()) {}
					qStream = contentAnalyzer.getStream();
					currentNode.mSearchString = qStream.toString().trim();
				} catch (TokenizerException e) {
	
				}
				currentNode.mIndexType = IndexType.TERM;
			}

	}

	public static void setChildAndIndexType(String nodeString, ChildType type, 
			TreeNode currentNode, HashMap<String, TreeNode> trackNodeMap) {

		IndexType tempIT = IndexType.TERM;
		TokenStream tStream = null;
		TokenStream qStream = null;
		Tokenizer mTokenizer = new Tokenizer();
		boolean colonIndicator = false;
		TreeNode child = new TreeNode();
		if(nodeString.contains(":")) {
			setIndexType(nodeString,child);
			colonIndicator = true;

		}else
			child.mIndexType = tempIT;

		if ((nodeString.length() > 4) && (nodeString.substring(0, 4).equalsIgnoreCase("exp_"))) {
			child = trackNodeMap.get(nodeString);
			trackNodeMap.remove(nodeString);

		}else if((nodeString.length() > 4) && nodeString.contains(":") && nodeString.contains("exp_")){
			String splitStrings[] = nodeString.split(":");
			child = trackNodeMap.get(splitStrings[1]);
			trackNodeMap.remove(splitStrings[1]);
			propogateIndexType(child,splitStrings[0]);
		}else {
			if(nodeString.contains("\""))
				child.mIsSingleQuotedString = true;
			if (colonIndicator == false) {

				try {
					tStream = mTokenizer.consume(nodeString);
					Analyzer contentAnalyzer = AnalyzerFactory.getInstance().getAnalyzerForField( FieldNames.CONTENT, tStream);
					while(contentAnalyzer.increment()) {}
					qStream = contentAnalyzer.getStream();
					child.mSearchString = qStream.toString().trim();
				} catch (TokenizerException e) {
	
				}
			}

		}
		if(type == ChildType.LEFT) {
			currentNode.setLeftChild(child);
		}else if(type == ChildType.RIGHT) {
			currentNode.setRightChild(child);			
		}

	}

	//TO-DO 
	public static void propogateIndexType(TreeNode root, String indexTypeStr){
		//			need to Implement this 
		if (root.mSearchString != null) {
			setIndexType(indexTypeStr+":"+root.mSearchString, root);
		}else{
			propogateIndexType(root.mLeftChild,indexTypeStr);
			propogateIndexType(root.mRightChild, indexTypeStr);
		}
	}
}

