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

package synthbio.servlets;
 
import javax.servlet.http.HttpServlet;

import synthbio.files.BioBrickRepository;
import synthbio.files.SynRepository;


/**
 * Synthbio abstract servlet containing shared functionality.
 *
 * @author jieter 
 */
@SuppressWarnings("serial")
public abstract class SynthbioServlet extends HttpServlet {

	/**
	 * Get the path of the webroot from servletcontext.
	 */
	public String getWebroot(){
		return this.getServletContext().getRealPath("/")+"WEB-INF/";
	}

	/**
	 * Log a message to the servlets log.
	 *
	 * In the case of a default tomcat6 installation @linux, it can be
	 * found in /var/log/tomcat6/localhost-<date>.log
	 *
	 * @param message The message to be logged.
	 */
	public void log(String message){
		this.getServletContext().log(message);
	}
	
	/**
	 * Return the BioBrick repository.
	 *
	 * @return A BioBrickReader object.
	 */
	public BioBrickRepository getBioBrickRepository() throws Exception{
		return new BioBrickRepository(this.getWebroot()+"data/biobricks/new/");
	}

	/**
	 * Return the Syn repository
	 */
	public SynRepository getSynRepository() throws Exception{
		return new SynRepository(this.getWebroot()+"data/synstore/");
	}
}
