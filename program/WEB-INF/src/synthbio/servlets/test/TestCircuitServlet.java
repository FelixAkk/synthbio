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

import org.junit.Ignore;
import com.gargoylesoftware.htmlunit.WebClient;

/**
 * Shared functionality for Circuit Servlet tests.
 *
 */
@Ignore
public class TestCircuitServlet{

	public String getTestPage(String url) throws Exception{
		final WebClient webClient = new WebClient();
		return webClient.getPage(url).getWebResponse().getContentAsString().trim();
	}

}
