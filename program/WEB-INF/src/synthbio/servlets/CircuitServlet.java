/**
 * Synthetic Biology project (Biobrick Modeller/Simulator)
 * https://github.com/FelixAkk/synthbio
 */

package synthbio.servlets;
 
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import synthbio.json.JSONResponse;
import synthbio.files.BioBrickRepository;
import synthbio.models.CDS;


/**
 * Servlet ListServlets returns a default JSON-reply with a list of
 * proteins available.
 *
 * @author jieter 
 */
public class CircuitServlet extends SynthbioServlet {

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

		response.setContentType("text/plain");
		PrintWriter out = response.getWriter();

		JSONResponse json=new JSONResponse();

		/* Try to load the BioBrick Repository
		 */ 
		BioBrickRepository bbr;
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