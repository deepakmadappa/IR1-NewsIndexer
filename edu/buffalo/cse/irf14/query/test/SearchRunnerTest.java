package edu.buffalo.cse.irf14.query.test;

import java.util.ArrayList;
import java.util.List;

import org.junit.BeforeClass;

import edu.buffalo.cse.irf14.index.DocumentEntry;
import edu.buffalo.cse.irf14.SearchRunner;
import edu.buffalo.cse.irf14.SearchRunner.ScoringModel;
import edu.buffalo.cse.irf14.query.Query;
import edu.buffalo.cse.irf14.query.TreeNode;
import static org.junit.Assert.assertEquals;

import org.junit.Test;


public class SearchRunnerTest {
	private static SearchRunner mSearchRunner = null;
	@BeforeClass
	public final static void initSearchRunner() {
		mSearchRunner = new SearchRunner("D:\\test", "", ' ', null);
	}
	
	@Test
	public final void testWithDummyTree() {
		/*TreeNode root = new TreeNode();
		TreeNode left = new TreeNode();
		left.mSearchString = "billion";
		root.mLeftChild = left;
		root.mOperator = LogicalOperator.OR;
		TreeNode right = new TreeNode();
		right.mSearchString = "region";
		root.mRightChild = right;*/
		TreeNode root = new TreeNode();
		root.mSearchString = "chase manhattan";
		root.mIsSingleQuotedString = true;
		List<DocumentEntry> outList = new ArrayList<DocumentEntry>();
		List<String> docs = new ArrayList<String>();
		for (DocumentEntry doc : outList) {
			docs.add(doc.mFileID);
		}
		try {
			mSearchRunner.ApplyInorderTraversal(outList, root);
			Query query = new Query();
			query.mRootNode = root;
			query.mLeafNodes.add(root);
			//query.mLeafNodes.add(right);
		//	mSearchRunner.getRelevantDocs(query, docs, ScoringModel.TFIDF);
		} catch(Exception e) {
			
		}
		assertEquals(outList.size(), 1);
	}

}
