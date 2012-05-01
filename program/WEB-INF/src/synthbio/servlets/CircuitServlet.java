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

import synthbio.files.BioBrickRepository;
import synthbio.json.JSONResponse;

/**
 * Servlet ListServlets returns a default JSON-reply with a list of
 * proteins available.
 *
 * @author jieter 
 */
@SuppressWarnings("serial")
public class CircuitServlet extends SynthbioServlet {

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

		response.setContentType("text/plain");
		PrintWriter out = response.getWriter();

		JSONResponse json=new JSONResponse();

		/* Try to load the BioBrick Repository
		 */ 
		BioBrickRepository bbr=null;
		try{
			bbr=this.getBioBrickRepository();
		}catch(Exception e){
			json.success=false;
			json.message="Could not load BioBricks: "+e.getMessage();
			out.println(json.toJSONString());
			return;
		}

		/* Dispatch actions.
		 */
		String action=request.getParameter("action");
		if(action.equals("load")){

		}else if(action.equals("save")){
			
		}else if(action.equals("validate")){

		}else{
			json.success=false;
			json.message="Invalid Action";
		}

		/* Send response to browser.
		 */
		out.println(json.toJSONString());
	}
}