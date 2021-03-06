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

import synthbio.models.Circuit;
import synthbio.models.CircuitException;
import synthbio.models.CircuitFactory;
import synthbio.json.JSONResponse;


/**
 * Servlet ValidateCircuitServlet validates a circuit.
 *
 * API functions documented at:
 * https://github.com/FelixAkk/synthbio/wiki/Zelula-HTTP-API
 * 
 * @author jieter 
 */
@SuppressWarnings("serial")
public class ValidateCircuitServlet extends CircuitServlet {
	
	/**
	 * Get requests
	 */
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException{
		
		//create new JSONResponse for this request.
		JSONResponse json=new JSONResponse();

		response.setContentType("application/json");
		PrintWriter out = response.getWriter();

		try{
			this.biobrickRepository=this.getBioBrickRepository();
		}catch(Exception e){
			json.fail("Could not load BioBrick repostiory: "+e.getMessage());
			out.println(json.toJSONString());
			return;
		}
		
		try{
			this.circuitFactory=new CircuitFactory(this.biobrickRepository);
		}catch(Exception e){
			json.fail("Could not load Circuit factory: "+e.getMessage());
			out.println(json.toJSONString());
			return;
		}

		String circuit=request.getParameter("circuit");
		if(circuit==null){
			json.fail("Parameter 'circuit' not set");
			out.println(json.toJSONString());
			return;
		}
		try{
			Circuit c=this.circuitFactory.fromJSON(circuit);
			json.success=true;
			json.message="Circuit validates!";
		}catch(CircuitException e){
			json.fail("Error in Circuit: "+e.getMessage());
		}catch(JSONException e){
			json.fail("Malformed JSON: "+e.getMessage());
		}
		
		out.println(json.toJSONString());
	}
}
