
package edu.buffalo.cse.irf14.query;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.HashMap;
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
			QueryParser.parse("Cat", "AND");
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
		// build parse tree from user string and initialize it to query class object
		String uString = "("+userQuery+")";
		String quoteCheck ;
		Pattern queryPattern = Pattern.compile("\\([^\\(^\\)]*\\)");
		Pattern quotesPattern = Pattern.compile("\"(.*?)\"");
		int i=1;
		Matcher queryMatcher = queryPattern.matcher(uString);
		while (queryMatcher.find()) {

			String exp = "exp_"+i;
			String firstMatch = queryMatcher.group(0);
			Matcher quotesMatcher = quotesPattern.matcher(firstMatch);
			while (quotesMatcher.find()) {

				quoteCheck = quotesMatcher.group(0); 
				quoteCheck = quoteCheck.replace(" ","_");
				//				quoteCheck = quoteCheck.replace("\"","");
				quotesMatcher.replaceFirst(quoteCheck);
			}
			String regExMatch[] = firstMatch.substring(1, firstMatch.length()-2).split(" ");

			for (int j = 0; j < regExMatch.length; j++) {
				regExMatch[j] = regExMatch[j].replace("_"," ");
			}
			TreeNode currentNode = new TreeNode();
			if(regExMatch.length>1) {

				if(regExMatch[1].equalsIgnoreCase("and")) {
					currentNode.mOperator = LogicalOperator.AND;
				}
				else if (regExMatch[1].equalsIgnoreCase("or")) {
					currentNode.mOperator = LogicalOperator.OR;
				}
				else if(regExMatch[1].equalsIgnoreCase("not")) {
					currentNode.mIsNot = true;
					if (defaultOperator.equalsIgnoreCase("and")) {
						currentNode.mOperator = LogicalOperator.AND;
					}else if(defaultOperator.equalsIgnoreCase("or")) {
						currentNode.mOperator = LogicalOperator.OR;
					}else {
						throw new QueryParserException("Unknown default operator");
					}
				}else {
					throw new QueryParserException("Unknown operator");
				}

				setChildAndIndexType(regExMatch[0], ChildType.LEFT, currentNode, trackNodeMap);
				setChildAndIndexType(regExMatch[2], ChildType.RIGHT, currentNode, trackNodeMap);

			}else if(regExMatch.length==1 &&
					regExMatch.length >4 &&
					!(regExMatch[0].substring(0, 3).equalsIgnoreCase("exp_"))) {

				currentNode.mSearchString=regExMatch[0];
				//				currentNode.mLeftChild.mIndexType=IndexType.TERM;

			}			

			trackNodeMap.put(exp,currentNode);
			if(regExMatch.length>3) {
				exp = "(" + exp;
				for(int appInd = 3; appInd<regExMatch.length;appInd++) {
					exp = exp + " " + regExMatch[appInd];
				}			
				exp = exp + ")";
			}
			uString = queryMatcher.replaceFirst(exp);					
			i++;
		} // end of while uQueryMatcher.find()

		String firstKey = (String) trackNodeMap.keySet().toArray()[0];
		qObject.mRootNode = trackNodeMap.get(firstKey);
		System.out.println("String : "+qObject.toString());
		String queryparsertext = qObject.toString();
		if(queryparsertext == null || queryparsertext.isEmpty()) {
			return null;
		}else
			return qObject;

	}// end of parse method

	public static void setChildAndIndexType(String nodeString, ChildType type, 
			TreeNode currentNode, HashMap<String, TreeNode> trackNodeMap) {

		IndexType tempIT = IndexType.TERM;
		TreeNode child = new TreeNode();
		if(nodeString.contains(":")) {

			String partNode[] = nodeString.split(":");
			currentNode.mSearchString = partNode[1];
			if (partNode[0]=="AUTHOR") {
				currentNode.mIndexType = IndexType.AUTHOR;				
			} else if (partNode[0]=="CATEGORY") {
				currentNode.mIndexType = IndexType.CATEGORY;
			}else if (partNode[0]=="PLACE") {
				currentNode.mIndexType = IndexType.PLACE;
			}else {
				currentNode.mIndexType = IndexType.TERM;
			}
		}

		if (nodeString.substring(0, 3).equalsIgnoreCase("exp_")) {
			child = trackNodeMap.get(nodeString);
			trackNodeMap.remove(nodeString);
			if(tempIT != IndexType.TERM) {
				//				propogateIndexType(child);
			}
		}else {
			child.mSearchString = nodeString;
		}
		if(type == ChildType.LEFT) {
			currentNode.setLeftChild(child);
		}else if(type == ChildType.RIGHT) {
			currentNode.setRightChild(child);			
		}

	}

	//TO-DO 
	//	public void propogateIndexType(TreeNode root){
	//		Implement this 
	//	}
}

