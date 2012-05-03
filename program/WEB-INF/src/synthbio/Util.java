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

package synthbio;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.FileReader;
import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Utility class.
 *
 * Collection of utility function
 */
public class Util{

	/**
	 * Quick and dirty file2string method:
	 * 
	 * from: http://stackoverflow.com/questions/5471406
	 */
	public static String fileToString(String filename) throws IOException{
		BufferedReader reader = new BufferedReader(new FileReader(filename));
		StringBuilder builder = new StringBuilder();
		String line;    

		// For every line in the file, append it to the string builder
		while((line = reader.readLine()) != null){
			builder.append(line);
		}

		return builder.toString();
	}

	/**
	 * Return a JSONObject for a file.
	 */
	public static JSONObject fileToJSONObject(String filename) throws JSONException, IOException{
		return new JSONObject(fileToString(filename));
	}
}
