package edu.buffalo.cse.irf14.query.test;

import java.util.Scanner;

import org.junit.BeforeClass;
import org.junit.Test;

import edu.buffalo.cse.irf14.SearchRunner;
import edu.buffalo.cse.irf14.SearchRunner.ScoringModel;

public class InterativeQueryTest {
	
	private static SearchRunner mSearchRunner = null;
	@BeforeClass
	public final static void initSearchRunner() {
		mSearchRunner = new SearchRunner("D:\\test", "C:\\Classes\\CSE 535 - IR\\Project1\\news_training\\corpus", ' ', System.out);
	}
	
	@Test
	public final void testQuery() {
		Scanner in = new Scanner(System.in);
		while(true) {
			System.out.println("Please Enter Query");
			String userQuery = in.nextLine();
			mSearchRunner.query(userQuery, ScoringModel.TFIDF);
		}
	}


}
