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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.batik.transcoder.Transcoder;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.JPEGTranscoder;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.apache.fop.svg.PDFTranscoder;

/**
 * ExportGraphServlet converts provided svg images to different
 * file formats.
 *
 * Inspiration from 
 * https://github.com/highslide-software/highcharts.com/blob/master/exporting-server/index.php
 * but use a more native Java approach.
 *
 * The pain is in finding out which of the huge pile of jars to include
 * in the WEB-INF/lib directory. In the end I decided to just include
 * them all, to avoid clutter, an ant task like this can be used to put
 * it together:
 * 
 *	<target name="zip-batik">
 *		<jar id="batik" jarfile="lib/batik-all.jar">
 *			<zipgroupfileset dir="lib/batik-1.7/lib/" includes="*.jar"/>
 *		</jar>
 *	</target>
 *
 * There is another pitfall with JPEGTranscoder when using OpenJDK. Problems disappear
 * when using Oracle JDK.
 */
@SuppressWarnings("serial")
public class ExportGraphServlet extends SynthbioServlet {

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		response.setContentType("text/plain");

		String type = request.getParameter("type");
		String svg = request.getParameter("svg");
		String filename = request.getParameter("filename");
		
		//default filename to 'chart'
		if(filename == null){
			filename="chart";
		}
		
		// determine file type to return.
		String extension="";
		if(type.equals("image/png")) {
			extension = "png";
		} else if(type.equals("image/jpeg")) {
			extension = "jpg";
		} else if(type.equals("application/pdf")) {
			extension = "pdf";
		} else if(type.equals("image/svg+xml")) {
			extension = "svg";
		}else{
			response.getWriter().println("No extension provided.");
		}

		// set content type and file name.
		response.setHeader("Content-disposition", "attachment; filename=" + filename + "." +extension);
		response.setHeader("Content-type", type);

		// If svg, just output.
		if(extension.equals("svg")){
			response.getWriter().print(svg);
		}else{
			//rig transcoder and input/output streams
			Transcoder transcoder=null;
			InputStream svgInputStream = new ByteArrayInputStream(svg.getBytes());
			TranscoderInput input = new TranscoderInput(svgInputStream);
			TranscoderOutput output = new TranscoderOutput(response.getOutputStream());

			if(type.equals("image/png")) {
				transcoder = new PNGTranscoder();
			} else if(type.equals("image/jpeg")) {
				transcoder = new JPEGTranscoder();
			} else if(type.equals("application/pdf")) {
				transcoder = new PDFTranscoder();
			}
			try{
				transcoder.transcode(input, output);
			}catch(Exception e){
				this.log(e);
			}
		}
	}
	
}
