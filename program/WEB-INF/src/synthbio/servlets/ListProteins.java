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
import synthbio.files.BioBrickReader;
import synthbio.models.CDS;


/**
 * Servlet ListServlets returns a default JSON-reply with a list of
 * proteins available.
 *
 * @author jieter 
 */
public class ListProteins extends HttpServlet {

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

		response.setContentType("text/plain");
		PrintWriter out = response.getWriter();

		String path=this.getServletContext().getRealPath("/")+"WEB-INF/biobricks/";

		JSONResponse json=new JSONResponse();
		try{
			json.data=new BioBrickReader(path).getCDSs();
			json.success=true;
		}catch(Exception e){
			
			json.success=false;
			json.message="Could not load BioBricks: "+e.getMessage();
		}

		out.println(json.toJSONString());
	}
}