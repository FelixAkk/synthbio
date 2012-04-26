/**
 * Synthetic Biology project (Biobrick Modeller/Simulator)
 * https://github.com/FelixAkk/synthbio
 */
package synthbio;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.FileReader;

public class Util{

	/**
	 * quick and dirty file2string method:
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
}