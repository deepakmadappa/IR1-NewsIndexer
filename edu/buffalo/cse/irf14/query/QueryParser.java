/**
 * 
 */
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

		if(defaultOperator.isEmpty()){
			defaultOperator = "OR";
		}

		Query qObject = new Query();
		HashMap<String,TreeNode> mNodeMap;
		mNodeMap = new HashMap <String,TreeNode>();
		// build parse tree from user string and intialize it to query class object
		String uString = "("+userQuery+")";
		Pattern uQueryPattern = Pattern.compile("([^(^)]*)");


		int i=1;
		Matcher uQueryMatcher = uQueryPattern.matcher(uString);
		while (uQueryMatcher.find()) {

			String exp = "exp_"+i;
			String sNodes[]= uQueryMatcher.group(1).split(" ");
			TreeNode sParent = new TreeNode();

			if(sNodes[1].equalsIgnoreCase("and"))
			{
				sParent.mOperator = LogicalOperator.AND;
			}
			else if (sNodes[1].equalsIgnoreCase("or"))
			{
				sParent.mOperator = LogicalOperator.OR;
			}
			else
			{

			}

			TreeNode tempLeftChild = new TreeNode();
			tempLeftChild.mSearchString=sNodes[0];

			TreeNode tempRightChild = new TreeNode();
			tempRightChild.mSearchString=sNodes[2];

			sParent.setLeftChild(tempLeftChild);
			sParent.setRightChild(tempRightChild);

			sParent.mLeftChild.mIndexType=IndexType.TERM;


			mNodeMap.put(exp,sParent);



			i++;
		}

		//compare each parenthesis group with regex and convert to treenode type
		//Simultaneously build string using the rules




		String queryparsertext = qObject.toString();
		if(queryparsertext == null || queryparsertext.isEmpty()) {
			return null;
		}
		else
			return qObject;

	}

}
