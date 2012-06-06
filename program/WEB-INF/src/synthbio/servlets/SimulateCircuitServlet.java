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
import synthbio.simulator.Solver;
import synthbio.simulator.JieterSolver;

import synthbio.Util;

/**
 * Servlet SimulateCircuitServlet serves a simulation for a provided
 * circuit..
 *
 * API functions documented at:
 * https://github.com/FelixAkk/synthbio/wiki/Zelula-HTTP-API
 * 
 * @author jieter 
 */
@SuppressWarnings("serial")
public class SimulateCircuitServlet extends CircuitServlet {
	
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

		// which solver to use?
		String solver="jsbml";
		if(request.getParameter("solver") != null) {
			if(request.getParameter("solver").equals("jieter")) {
				solver="jieter";
			}
		}

		String circuit=request.getParameter("circuit");
		if(circuit==null){
			json.fail("Parameter 'circuit' not set");
			out.println(json.toJSONString());
			return;
		}

		Circuit c;
		try {
			c = this.circuitFactory.fromJSON(circuit);
		} catch(Exception e) {
			json.fail("Circuit does not validate, please use validate to correct errors.");
			out.println(json.toJSONString());
			return;
		}

		if(solver.equals("jieter")) {
			//use Jieter's Solver.
			try {
				JieterSolver js=new JieterSolver(c);
				js.solve();
				
				json.data = js.toJSON();
				json.success = true;
			} catch(Exception e) {
				json.fail("Failed solving: "+e.getMessage());
			}
		}else{
			//use JSBML's Solver.
			try {
				json.data = Util.multiTableToJSON(Solver.solve(c));
				json.success = true;
			} catch(Exception e) {
				json.fail("Failed solving: "+e.getMessage());
			}
		}
		
		out.println(json.toJSONString());
	}
}
