/**
 * Synthetic Biology project (Biobrick Modeller/Simulator)
 * https://github.com/FelixAkk/synthbio
 */

package synthbio.servlets.test;



import org.junit.*;
import org.junit.Test;
import static org.junit.Assert.*;

import synthbio.Util;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.TextPage;

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
		final TextPage page = webClient.getPage(url);

		String expected=Util.fileToString("src/synthbio/servlets/test/listProteins-expected.json").trim();

		assertEquals(
			expected,
			page.getContent().trim()
		);

		webClient.closeAllWindows();
	}


   

}