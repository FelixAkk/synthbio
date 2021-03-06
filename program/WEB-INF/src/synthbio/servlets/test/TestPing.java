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

import org.junit.Test;
import static org.junit.Assert.*;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.TextPage;

/**
 * Testing the Servlet ListProteins
 *
 */
public class TestPing{
	 public static final String url="http://localhost:8080/Ping";

	@Test
	public void testResponse() throws Exception{
		WebClient webClient = new WebClient();
		TextPage page = webClient.getPage(url);
		assertEquals("Pong", page.getContent());
		webClient.closeAllWindows();
	}

}
