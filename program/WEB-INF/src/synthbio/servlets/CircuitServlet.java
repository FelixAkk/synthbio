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

import synthbio.files.BioBrickRepository;
import synthbio.files.SynRepository;
import synthbio.models.Circuit;
import synthbio.models.CircuitException;
import synthbio.json.JSONResponse;



/**
 * Servlet ListServlets returns a default JSON-reply with a list of
 * proteins available.
 *
 * @author jieter 
 */
@SuppressWarnings("serial")
public class CircuitServlet extends SynthbioServlet {
	
	/**
	 * The JSON response object.
	 */
	private JSONResponse json;
	private SynRepository synRepository;
	
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		
		//create new JSONResponse for this request.
		this.json=new JSONResponse();

		response.setContentType("text/plain");
		PrintWriter out = response.getWriter();

		/* Load syn repository
		 */
		try{
			this.synRepository=this.getSynRepository();
		}catch(Exception e){
			json.fail("Could not load .syn files: "+e.getMessage());
		}
		
		/* Dispatch actions.
		 */
		String action=request.getParameter("action");
		if(action==null){
			json.fail("Parameter 'action' not set.");
			out.println(json.toJSONString());
			return;
		}

		//listFiles
		if(action.equals("list")){
			this.doList();

		//loadFile(filename)
		}else if(action.equals("load")){
			String filename=request.getParameter("filename");
			if(filename==null){
				json.fail("Parameter 'filename' not set");
				out.println(json.toJSONString());
				return;
			}
			this.doLoad(filename);
		
		//saveFile(filename, circuit)
		}else if(action.equals("save")){
			String filename=request.getParameter("filename");
			if(filename==null){
				json.fail("Parameter 'filename' not set");
				out.println(json.toJSONString());
				return;
			}
			
			String circuit="{}";
			this.doSave(filename, circuit);
		
		//validate(circuit)
		}else if(action.equals("validate")){
			String circuit="{}";
			this.doValidate(circuit);

		//all other cases: invalid action
		}else{
			json.fail("CircuitServlet: Invalid Action: "+action);
		}

		/* Send response to browser.
		 */
		out.println(json.toJSONString());
	}

	/* Action methods.
	 */
	public void doList(){
		this.json.data=this.synRepository.getFileList();
		this.json.success=true;
	}
	
	public void doLoad(String filename){
		try{
			json.data=new JSONObject(this.synRepository.getFile(filename));
			json.success=true;
		}catch(Exception e){
			json.success=false;
			json.message="Could not load .syn-file.";
		}
	}

	public void doSave(String filename, String circuit){
		
	}
	public void doValidate(String circuit){
		try{
			Circuit c=Circuit.fromJSON(circuit);
		}catch(CircuitException e){

		}catch(JSONException e){

		}
	}
}
