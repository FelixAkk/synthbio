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

import synthbio.files.BioBrickRepository;
import synthbio.models.Circuit;
import synthbio.models.CircuitException;
import synthbio.models.CircuitFactory;
import synthbio.json.JSONResponse;

/**
 * Servlet ExportCircuitServlets converts a JSON model to SBML.
 *
 * API functions documented at:
 * https://github.com/FelixAkk/synthbio/wiki/Zelula-HTTP-API
 * 
 * @author jieter 
 */
@SuppressWarnings("serial")
public class ExportCircuitServlet extends CircuitServlet {
	
	/**
	 * Get request
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

		String circuitJSON = request.getParameter("circuit");
		if(circuitJSON == null) {
			json.fail("Parameter 'circuit' not set");
			out.println(json.toJSONString());
			return;
		}
		
		try{
			Circuit circuit=this.circuitFactory.fromJSON(circuitJSON);

			// Use 'circuit.sbml' if no circuit name is provided in the circuit.
			String filename="circuit.sbml";
			if(!circuit.getName().equals("")){
				filename=circuit.getName()+".sbml";
			}
			
			// correct SBML mime time
			response.setContentType("application/sbml+xml");
			// Set a friendly filename.
			response.setHeader("Content-Disposition", "attachment; filename=\"" + filename+"\"");
			
			out.println(circuit.toSBML());
			return;
		}catch(Exception e){
			json.fail("Circuit does not validate, please use validate to correct errors.");
		}
		
		out.println(json.toJSONString());
	}
}
