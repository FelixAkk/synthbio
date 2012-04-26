/**
 * Synthetic Biology project (Biobrick Modeller/Simulator)
 * https://github.com/FelixAkk/synthbio
 */

package synthbio.servlets;
 
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import synthbio.json.JSONResponse;
import synthbio.files.BioBrickRepository;
import synthbio.models.CDS;


/**
 * Synthbio abstract servlet containing shared functionality.
 *
 * @author jieter 
 */
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