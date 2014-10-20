package edu.buffalo.cse.irf14.query.test;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;

import edu.buffalo.cse.irf14.SearchRunner;

public class FileQueryTest {

	@Test
	public final void testQuery() {
		SearchRunner mSearchRunner = new SearchRunner("D:\\test", "", ' ', System.out);
		
		
		
			File file = new File("d:\\queryFile");
			if(file.exists()) {
				mSearchRunner.query(file);
			}
		
	}
}
