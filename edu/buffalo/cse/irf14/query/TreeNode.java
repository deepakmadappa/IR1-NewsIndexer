package edu.buffalo.cse.irf14.query;

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

