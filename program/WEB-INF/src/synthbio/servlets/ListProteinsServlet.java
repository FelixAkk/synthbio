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
 
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import synthbio.json.JSONResponse;


/**
 * Servlet ListServlets returns a default JSON-reply with a list of
 * proteins available.
 *
 * @author jieter 
 */
@SuppressWarnings("serial")
public class ListProteinsServlet extends SynthbioServlet {

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

		response.setContentType("application/json");
		PrintWriter out = response.getWriter();

		JSONResponse json=new JSONResponse();
		try{
			json.data=this.getBioBrickRepository().getCDSs();
			json.success=true;
		}catch(Exception e){
			json.fail("Could not load BioBricks: "+e.getMessage());
		}

		out.println(json.toJSONString());
	}
}