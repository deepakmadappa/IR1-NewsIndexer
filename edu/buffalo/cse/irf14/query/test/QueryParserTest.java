package edu.buffalo.cse.irf14.query.test;

import org.junit.Test;

import edu.buffalo.cse.irf14.query.Query;
import edu.buffalo.cse.irf14.query.QueryParser;

import static org.junit.Assert.assertEquals;

public class QueryParserTest {
	@Test
	public final void testQuery() {
		Query query = null;
		try {
			query = QueryParser.parse("adob", "AND");
		}
		catch (Exception ex) {
			
		}
		String str = query.toString();
		assertEquals("", str);
	}
}
