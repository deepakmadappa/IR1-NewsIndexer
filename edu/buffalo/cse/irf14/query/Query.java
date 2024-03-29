package edu.buffalo.cse.irf14.query;

import java.util.ArrayList;
import java.util.List;

import edu.buffalo.cse.irf14.index.IndexType;

/**
 * Class that represents a parsed query
 * @author nikhillo
 *
 */
public class Query {
	
	public TreeNode mRootNode = null;
	public List<TreeNode> mLeafNodes;
	private String stringRepresentation = null;
	
	public Query() {
//		mLeafNodes = new ArrayList<TreeNode>();
	}
	/**
	 * Method to convert given parsed query into string
	 */
	public String toString() {

		if (mRootNode!= null) {
			if (stringRepresentation != null) {
				return stringRepresentation;
			}else {
				stringRepresentation = "{"+ buildString(mRootNode) +"}";
				return stringRepresentation;	
			}
		}
		return null;
	}
	
	public void populateLeaves() {
//		mLeafNodes = null;
		mLeafNodes = new ArrayList<TreeNode>();		
		buildLeaves (mRootNode);
		
	}

	public void buildLeaves(TreeNode leafNode){
		
		if (leafNode.mSearchString != null) {
			
			mLeafNodes.add(leafNode);
			return ;
			
		} else {
			
			if(leafNode.mLeftChild.mSearchString != null) {
				mLeafNodes.add(leafNode.mLeftChild);
			}else {
				buildLeaves(leafNode.mLeftChild);
			}
			if(leafNode.mRightChild.mSearchString != null) {
				mLeafNodes.add(leafNode.mRightChild);
			}else {
				buildLeaves(leafNode.mRightChild);
			}
		}
	}
	
	
	public String indexTypeString(IndexType iType) {
		
		switch (iType) {
		case AUTHOR:return "Author";
		case CATEGORY:return "Category";
		case PLACE:return "Place";
		case TERM :return "Term";
		default:
			return "Term";
		}
	}

	public String buildString(TreeNode node) {

		String leftString;
		String rightString;
		String outString;
		if (node.mSearchString!= null) {
//			if (node.mIsNot==true){	
//			outString = "<" + indexTypeString(node.mIndexType) + ":" + node.mSearchString + ">";
//			}
//			else
				outString = indexTypeString(node.mIndexType) + ":" + node.mSearchString;
			return outString;
		}
		if(node.mLeftChild.mSearchString!= null) {
			leftString = indexTypeString(node.mLeftChild.mIndexType) + ":" + node.mLeftChild.mSearchString;
		}else {
			leftString = buildString(node.mLeftChild);
			if (node.mOperator!= node.mLeftChild.mOperator) {
				leftString = "["+leftString+"]";
			}
		}
		
		if(node.mRightChild.mSearchString!=null) {
			rightString = indexTypeString(node. mRightChild.mIndexType) + ":" + node.mRightChild.mSearchString;
		}else {
			rightString = buildString(node.mRightChild);
			if (node.mOperator!= node.mRightChild.mOperator) {
				rightString = "["+rightString+"]";
			}
		}
		
		String combineOperator = (node.mOperator == LogicalOperator.AND)?"AND":"OR";
		
		if (node.mIsNot==true){	
			outString = leftString +" "+combineOperator+" "+"<" + rightString + ">" ;
			return outString;
		}
		else {
		outString = leftString +" "+combineOperator+" "+ rightString;
		return outString;
		}
	}
}
