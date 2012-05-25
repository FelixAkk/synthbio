/**
 * Project Zelula
 *
 * Contextproject TI2800 
 * TU Delft - University of Technology
 *  
 * Authors: 
 * 	Felix Akkermans, Niels Doekemeijer, Thomas van Helden
 * 	Albert ten Napel, Jan Pieter Waagmeester
 * 
 * https://github.com/FelixAkk/synthbio
 */

package synthbio.servlets.test;



import static org.junit.Assert.assertEquals;

import org.junit.Test;

import synthbio.Util;

import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;

/**
 * Testing the Servlet ListProteins
 *
 */
public class TestListProteins{
	/**
	 * Server location
	 */
	 public static final String url="http://localhost:8080/ListProteins";

	/**
	 * compare response from ListProteins to expected response.
	 *
	 * whitespace is not relevant. Could be done better with specialized
	 * JSON matchers...
	 */
	@Test
	public void testResponse() throws Exception{
		final WebClient webClient = new WebClient();
		final Page page = webClient.getPage(url);

		String expected=Util.fileToString("data/test/servlets/listProteins-expected.json").trim();

		assertEquals(
			expected,
			page.getWebResponse().getContentAsString().trim()
		);

		webClient.closeAllWindows();
	}
}