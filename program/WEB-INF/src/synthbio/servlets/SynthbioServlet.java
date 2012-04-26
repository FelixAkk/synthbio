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


/**
 * Synthbio abstract servlet containing shared functionality.
 *
 * @author jieter 
 */
@SuppressWarnings("serial")
public abstract class SynthbioServlet extends HttpServlet {


	/**
	 * Return the BioBrick repository.
	 *
	 * @return A BioBrickReader object.
	 */
	public BioBrickRepository getBioBrickRepository() throws Exception{
		String path=this.getServletContext().getRealPath("/")+"WEB-INF/biobricks/";
		return new BioBrickRepository(path);
	}
}