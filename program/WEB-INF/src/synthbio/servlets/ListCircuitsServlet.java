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

import synthbio.files.SynRepository;
import synthbio.json.JSONResponse;

import synthbio.Util;

/**
 * Servlet ListCircuitServlet serves a list of circuit files.
 *
 * API functions documented at:
 * https://github.com/FelixAkk/synthbio/wiki/Zelula-HTTP-API
 * 
 * @author Jieter, Thomas 
 */
@SuppressWarnings("serial")
public class ListCircuitsServlet extends CircuitServlet {
	
	/**
	 * Get request
	 */
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException{
		
		//create new JSONResponse for this request.
		JSONResponse json=new JSONResponse();

		response.setContentType("application/json");
		PrintWriter out = response.getWriter();

		String folderName = request.getParameter("folderName");
		/* Load syn repository  */
		try{
			this.synRepository = this.getRepository(folderName);
		}catch(Exception e) {
			json.fail("Could not load .syn files: "+e.getMessage());
			out.println(json.toJSONString());
			return;
		}
		
		try{
			json.data=this.getFiles();
			json.success=true;
		}catch(JSONException e) {
			json.fail("JSON conversion error: "+e.getMessage());
		}

		out.println(json.toJSONString());
	}

	private JSONArray getFiles() throws JSONException{
		ArrayList<JSONObject> files = new ArrayList<JSONObject>();
		for(String filename: this.synRepository.getFileList()) {
			JSONObject file=new JSONObject();
			file.put("filename", filename);
			file.put("modified", this.synRepository.lastModified(filename));
			files.add(file);
		}
		return new JSONArray(files);
	}
}
