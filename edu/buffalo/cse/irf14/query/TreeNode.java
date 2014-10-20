package edu.buffalo.cse.irf14.query;

import java.util.ArrayList;
import java.util.List;

import edu.buffalo.cse.irf14.index.DocumentEntry;
import edu.buffalo.cse.irf14.index.IndexType;

public class TreeNode {
	public TreeNode mLeftChild;
	public TreeNode mRightChild;
	public TreeNode mParent;
	public LogicalOperator mOperator;
	public IndexType mIndexType;
	public String mSearchString;
	public boolean mIsNot;
	public boolean mIsSingleQuotedString;
	public List<DocumentEntry> mDocsForTerm = null;
	
	public TreeNode() {
		mIsNot = false;
		mIsSingleQuotedString = false;
		mIndexType = IndexType.TERM;
		mOperator = LogicalOperator.AND;
		mLeftChild = mRightChild = null;
		mDocsForTerm = new ArrayList<DocumentEntry>();
	}
	
	public TreeNode getRightChild() {
		return mRightChild;
	}
	public void setRightChild(TreeNode rightChild) {
		rightChild.mParent=this;
		this.mRightChild = rightChild;
	}
	
	public TreeNode getmLeftChild() {
		return mLeftChild;
	}
	public void setLeftChild(TreeNode leftChild) {
		leftChild.mParent=this;
		this.mLeftChild = leftChild;
	}
}

