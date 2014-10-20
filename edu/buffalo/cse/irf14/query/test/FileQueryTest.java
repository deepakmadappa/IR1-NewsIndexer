package edu.buffalo.cse.irf14.query.test;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

import org.junit.Test;

import edu.buffalo.cse.irf14.SearchRunner;

public class FileQueryTest {

	@Test
	public final void testQuery() {
		Scanner in = new Scanner(System.in);
		SearchRunner mSearchRunner = new SearchRunner("D:\\test", "", ' ', System.out);
		while(true) {
			System.out.println("Please Enter Query");
			String userQuery = in.nextLine();
			Path path = Paths.get("d:\\queryFile");
			File file = new File("d:\\queryFile");
			if(file.exists()) {
				mSearchRunner.query(file);
			}
		}
	}
}
