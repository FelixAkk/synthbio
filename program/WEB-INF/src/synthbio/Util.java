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
import java.io.FileWriter;
import java.io.BufferedWriter;

import java.util.ArrayList;
import org.simulator.math.odes.MultiTable;
import org.json.*;

import java.util.Arrays;

/**
 * Utility class.
 *
 * Collection of utility function
 */
public final class Util {

	/**
	 * Disallow construction of utility classes.
	 */
	private Util() {
	}

	/**
	 * Quick and dirty file2string method.
	 *
	 * from: http://stackoverflow.com/questions/5471406
	 *
	 * @param filename The filename to be read into a string.
	 * @throws IOException In case of unexistant files and the like.
	 * @return A string containing the file contents.
	 */
	public static String fileToString(String filename) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(filename));
		StringBuilder builder = new StringBuilder();
		String line;

		// For every line in the file, append it to the string builder
		while ((line = reader.readLine()) != null) {
			builder.append(line);
		}

		return builder.toString();
	}

	/**
	 * Dump a string to a file.
	 *
	 * @param filename The filename to dump to.
	 * @param content The contents to dump.
	 * @throws IOException In case of failing file operations.
	 */
	public static void stringToFile(String filename, String content) throws IOException {
		BufferedWriter out = new BufferedWriter(new FileWriter(filename));

		out.write(content);
		out.close();
	}

	/**
	 * Return a JSONObject for a file.
	 *
	 * @param filename The file to read and convert to JSONObject
	 * @return The JSONObject.
	 * @throws JSONObject If converting file contents to JSON fails.
	 * @throws IOException In case of missing files.
	 */
	public static JSONObject fileToJSONObject(String filename) throws JSONException, IOException {
		return new JSONObject(fileToString(filename));
	}

	/**
	 * Helpful method for getting a string of tabs.
	 * (used in CircuitConverter and Reaction).
	 *
	 * @param n Number of tabs to insert.
	 * @return String containing n tab characters.
	 */
	public static String tabs(int n) {
		String r = "";
		if (n > 0) {
			for (int i = 0; i < n; i++) {
				r += "\t";
			}
		}
		return r;
	}

	/**
 	 * Converts a MultiTable to a JSON-string of the format:
 	 * 	{
 	 * 		"names": [A, B],
 	 * 		"length": 10,
 	 * 		"step": 1,
 	 *		"time": [1, 2, 3, 4, 5, 6, 7, 8, 9, 10],
 	 * 		"data": {
 	 *			"A": [0, 0, 0, 0, 0, 600, 600, 600, 600, 600],
 	 *			"B": [...]
 	 * 		}
 	 * 	}
 	 */
	public static JSONObject multiTableToJSON(MultiTable m) throws JSONException {
		double[] timePoints = m.getTimePoints();
		double timeLength = timePoints.length;
		double step = timePoints[1] - timePoints[0];
		
		// get all names
		int cc = m.getColumnCount() - 3; // - 3 because gene, cell and empty are unused.
		ArrayList<String> names = new ArrayList<String>(cc);
		for(int i = 0; i < cc + 3; i++) {
			String cur = m.getColumnName(i);
			if(!cur.equals("gene") && !cur.equals("cell") && !cur.equals("empty") && !cur.equals("Time"))
				names.add(cur);
		}

		// creating the JSON object
		JSONObject r = new JSONObject();

		r.put("names", new JSONArray(names));
		r.put("length", timeLength);
		r.put("step", step);
		r.put("time", Arrays.asList(timePoints));	
		// for every name, get the data in the column of that name.
		JSONObject data = new JSONObject();
		for(String name: names) {
			ArrayList<Double> cur = new ArrayList<Double>((int)timeLength);
			for(Double d: m.getColumn(name))
				cur.add(d);
			data.put(name, new JSONArray(cur));
		}
		r.put("data", data);

		return r;	
	}
}

