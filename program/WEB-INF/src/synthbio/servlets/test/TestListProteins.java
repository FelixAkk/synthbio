/**
 * Synthetic Biology project (Biobrick Modeller/Simulator)
 * https://github.com/FelixAkk/synthbio
 */

package synthbio.servlets.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.FileReader;

import org.junit.*;
import org.junit.Test;
import static org.junit.Assert.*;

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

		String expected=fileToString("src/synthbio/servlets/test/listProteins-expected.json").trim();

		assertEquals(
			expected,
			page.getContent().trim()
		);

		webClient.closeAllWindows();
	}

	/**
	 * quick and dirty file2string method:
	 * 
	 * from: http://stackoverflow.com/questions/5471406
	 */
	private String fileToString(String filename) throws IOException{
		BufferedReader reader = new BufferedReader(new FileReader(filename));
		StringBuilder builder = new StringBuilder();
		String line;    

		// For every line in the file, append it to the string builder
		while((line = reader.readLine()) != null){
			builder.append(line);
		}

		return builder.toString();
	}
   

}