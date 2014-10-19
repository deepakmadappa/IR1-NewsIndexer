package edu.buffalo.cse.irf14.query.test;

import org.junit.Test;

import edu.buffalo.cse.irf14.query.Query;
import edu.buffalo.cse.irf14.query.QueryParser;

import static org.junit.Assert.assertEquals;

public class QueryParserTest {
	@Test
	public final void testQuery() {
		String input[] = {"hello","hello world","\"hello world\"","orange AND yellow","(black OR blue) AND bruises"};
		String output[] = {"{Term:hello}","{Term:hello OR Term:world}","{Term:\"hello world\"}","{Term:orange AND Term:yellow}","{[Term:black OR Term:blue] AND Term:bruises}"};
		Query query = null;
		try {
			for (int i = 0; i < 6 ; i++) {
				query = QueryParser.parse(input[i], "OR");
				assertEquals(output[i] , query.toString());
			}
		}
		catch (Exception ex) {
			
		}
	}
}
