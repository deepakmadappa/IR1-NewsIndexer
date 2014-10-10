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
	
	public TreeNode getmRightChild() {
		return mRightChild;
	}
	public void setmRightChild(TreeNode mRightChild) {
		mRightChild.mParent=this;
		this.mRightChild = mRightChild;
	}
	
	public TreeNode getmLeftChild() {
		return mLeftChild;
	}
	public void setmLeftChild(TreeNode mLeftChild) {
		mLeftChild.mParent=this;
		this.mLeftChild = mLeftChild;
	}
}

