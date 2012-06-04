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
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import synthbio.files.BioBrickRepository;
import synthbio.files.SynRepository;
import synthbio.models.Circuit;
import synthbio.models.CircuitException;
import synthbio.models.CircuitFactory;
import synthbio.json.JSONResponse;
import synthbio.simulator.Solver;

import synthbio.Util;

/**
 * Servlet ListServlets has different actions on the circuit and replies
 * with the default JSON response.
 *
 * API functions documented at:
 * https://github.com/FelixAkk/synthbio/wiki/Zelula-HTTP-API
 * 
 * @author jieter 
 */
@SuppressWarnings("serial")
public class CircuitServlet extends SynthbioServlet {
	
	/**
	 * The JSON response object.
	 */
	private JSONResponse json;

	/**
	 * The repository of .syn files.
	 */
	private SynRepository synRepository;

	/**
	 * The BioBrick repository
	 */
	private BioBrickRepository biobrickRepository;

	private CircuitFactory circuitFactory;
	
	/**
	 * Get requests
	 */
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException{
		
		//create new JSONResponse for this request.
		this.json=new JSONResponse();

		response.setContentType("application/json");
		PrintWriter out = response.getWriter();

		/* Load syn repository
		 */
		try{
			this.synRepository=this.getSynRepository();
		}catch(Exception e){
			json.fail("Could not load .syn files: "+e.getMessage());
		}

		try{
			this.biobrickRepository=this.getBioBrickRepository();
		}catch(Exception e){
			json.fail("Could not load BioBrick repostiory: "+e.getMessage());
		}
		
		
		try{
			this.circuitFactory=new CircuitFactory(this.biobrickRepository);
		}catch(Exception e){
			json.fail("Could not load Circuit factory: "+e.getMessage());
		}
			
		
		/* Dispatch actions.
		 */
		String action=request.getParameter("action");
		if(action==null){
			json.fail("Parameter 'action' not set.");
			out.println(json.toJSONString());
			return;
		}

		if(action.equals("list")){
			this.doList();
		}else if(action.equals("load")){
			this.doLoad(request);
		}else if(action.equals("save")){
			this.doSave(request);
		}else if(action.equals("validate")){
			this.doValidate(request);
		}else if(action.equals("exportSBML")){
			this.doExport(request);
		}else if(action.equals("simulate")){
			this.doSimulate(request);
		}else{
			json.fail("CircuitServlet: Invalid Action: "+action);
		}

		/* Send response to browser.
		 */
		out.println(json.toJSONString());
	}

	
	/**
	 * Action: List the files contained in the syn store.
	 */
	public void doList(){
		ArrayList<JSONObject> files = new ArrayList<JSONObject>();
		for(String filename: this.synRepository.getFileList()) {
			try{
				JSONObject file=new JSONObject();
				file.put("filename", filename);
				file.put("modified", this.synRepository.lastModified(filename));
				files.add(file);
			}catch(JSONException e){
				this.json.fail("JSON conversion error: "+e.getMessage());
			}
		}
		
		this.json.data=files;
		this.json.success=true;
	}

	/**
	 * Action: Load a file from the syn store.
	 */
	public void doLoad(HttpServletRequest request){
		String filename=request.getParameter("filename");
		if(filename == null) {
			json.fail("Parameter 'filename' not set");
			return;
		}
		try{
			json.data=new JSONObject(this.synRepository.getFile(filename));
			json.success=true;
		}catch(Exception e){
			json.fail("Could not load .syn-file.");
		}
	}

	/**
	 * Action: Save a file to the syn store.
	 */
	public void doSave(HttpServletRequest request){
		String filename = request.getParameter("filename");
		if(filename == null) {
			json.fail("Parameter 'filename' not set.");
			return;
		}
		if(!filename.endsWith(".syn")) {
			json.fail("Filename should end with .syn.");
			return;
		}
		
		String circuit = request.getParameter("circuit");
		if(circuit == null) {
			json.fail("Parameter 'circuit' not set.");
			return;
		}
		try{
			this.synRepository.putFile(filename, circuit);
			json.message="Saved succesfully";
			json.success=true;
		}catch(Exception e){
			json.fail("Could not save .syn-file: "+e.getMessage());
		}
	}

	/**
	 * Action: Validate a circuit.
	 */
	public void doValidate(HttpServletRequest request){
		String circuit=request.getParameter("circuit");
		if(circuit==null){
			json.fail("Parameter 'circuit' not set");
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
	}

	
	/**
	 * Action: Export to SBML
	 */
	public void doExport(HttpServletRequest request){
		String circuit = request.getParameter("circuit");
		if(circuit == null) {
			json.fail("Parameter 'circuit' not set");
			return;
		}
		
		Circuit c;
		try{
			c=this.circuitFactory.fromJSON(circuit);
		}catch(Exception e){
			json.fail("Circuit does not validate, please use validate to correct errors.");
			return;
		}
		json.data=c.toSBML();
		json.success=true;
	}

	/**
	 * Action: Simulate circuit
	 */
	public void doSimulate(HttpServletRequest request) {
		String circuit=request.getParameter("circuit");
		if(circuit==null){
			json.fail("Parameter 'circuit' not set");
			return;
		}

		Circuit c;
		try {
			c = this.circuitFactory.fromJSON(circuit);
		} catch(Exception e) {
			json.fail("Circuit does not validate, please use validate to correct errors.");
			return;
		}

		try {
			json.data = Util.multiTableToJSON(Solver.solve(c));
			json.success = true;
		} catch(Exception e) {
			json.fail("Failed solving: "+e.getMessage());
		}
	}
}
