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
 * Servlet LoadCircuitServlet returns a circuit in JSON representation.
 *
 * API functions documented at:
 * https://github.com/FelixAkk/synthbio/wiki/Zelula-HTTP-API
 * 
 * @author Jieter, Thomas 
 */
@SuppressWarnings("serial")
public class LoadCircuitServlet extends CircuitServlet {
	
	/**
	 * Get request
	 */
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException{
		
		//create new JSONResponse for this request.
		JSONResponse json=new JSONResponse();

		response.setContentType("application/json");
		PrintWriter out = response.getWriter();

		String folderName =request.getParameter("folderName");
		/* Load syn repository  */
		try{
			this.synRepository = this.getRepository(folderName);
		}catch(Exception e) {
			json.fail("Could not load .syn files: "+e.getMessage());
			out.println(json.toJSONString());
			return;
		}

		String filename=request.getParameter("filename");
		if(filename == null) {
			json.fail("Parameter 'filename' not set");
			out.println(json.toJSONString());
			return;
		}
		try{
			json.data = new JSONObject(this.synRepository.getFile(filename));
			json.success=true;
		}catch(Exception e) {
			json.fail("Could not load .syn-file: "+filename);
		}

		out.println(json.toJSONString());
	}
}
