package edu.buffalo.cse.irf14.query.test;

import org.junit.Test;

import edu.buffalo.cse.irf14.query.Query;
import edu.buffalo.cse.irf14.query.QueryParser;

import static org.junit.Assert.assertEquals;

public class QueryParserTest {
	@Test
	public final void testQuery() {
		String input[] = {"hello","hello world","\"hello world\"","orange AND yellow","(black OR blue) AND bruises","Author:Cat AND Mat","Category:oil AND place:Dubai AND ( price OR cost )","place:paris AND government"};
		String output[] = {"{Term:hello}","{Term:hello OR Term:world}","{Term:hello world}","{Term:orange AND Term:yellow}","{[Term:black OR Term:blue] AND Term:bruises}","{Author:cat AND Term:mat}","{Category:oil AND Place:dubai AND [Term:price OR Term:cost]}","{Place:paris AND Term:government}"};
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
