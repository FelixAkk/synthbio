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

import synthbio.json.JSONResponse;


/**
 * Servlet GetFile returns a default list saved files.
 */
@SuppressWarnings("serial")
public class GetFile extends SynthbioServlet {

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

		response.setContentType("text/plain");
		PrintWriter out = response.getWriter();

		JSONResponse json=new JSONResponse();
		try{
		//No data stored yet so this is a mockup
			json.data= null;
			json.success=true;
		}catch(Exception e){
			
			json.success=false;
			json.message="Could not load BioBricks: "+e.getMessage();
		}

		out.println(json.toJSONString());
	}
}