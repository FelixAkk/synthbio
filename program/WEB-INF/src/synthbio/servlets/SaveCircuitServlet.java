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

import org.json.JSONException;
import org.json.JSONObject;

import synthbio.files.SynRepository;
import synthbio.json.JSONResponse;

import synthbio.Util;

/**
 * Servlet ListCircuitServlets serves a list of circuit files.
 *
 * API functions documented at:
 * https://github.com/FelixAkk/synthbio/wiki/Zelula-HTTP-API
 * 
 * @author jieter 
 */
@SuppressWarnings("serial")
public class SaveCircuitServlet extends CircuitServlet {
	
	/**
	 * Get requests
	 */
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException{
		
		//create new JSONResponse for this request.
		JSONResponse json=new JSONResponse();

		response.setContentType("application/json");
		PrintWriter out = response.getWriter();

		/* Load syn repository
		 */
		try{
			this.synRepository=this.getSynRepository();
		}catch(Exception e) {
			json.fail("Could not load .syn files: "+e.getMessage());
			out.println(json.toJSONString());
			return;
		}

		String filename = request.getParameter("filename");
		if(filename == null) {
			json.fail("Parameter 'filename' not set.");
			out.println(json.toJSONString());
			return;
		}
		if(!filename.endsWith(".syn")) {
			json.fail("Filename should end with .syn.");
			out.println(json.toJSONString());
		}
		
		String circuit = request.getParameter("circuit");
		if(circuit == null) {
			json.fail("Parameter 'circuit' not set.");
			out.println(json.toJSONString());
			return;
		}
		try{
			this.synRepository.putFile(filename, circuit);
			json.message="Saved succesfully";
			json.success=true;
		}catch(Exception e) {
			json.fail("Could not save .syn-file: "+e.getMessage());
		}
		
		out.println(json.toJSONString());
	}
}
