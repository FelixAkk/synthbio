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
import java.util.HashMap;
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
 	 *			"A": [0, 0, 0, 0, 0, 200, 200, 200, 200, 200],
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
		JSONObject json = new JSONObject();
		json.put("solver", "SBMLSimulator");
		json.put("names", new JSONArray(names));
		json.put("length", timeLength);
		json.put("step", step);
		json.put("time", Arrays.asList(timePoints));
		
		// for every name, get the data in the column of that name.
		JSONObject data = new JSONObject();
		for(String name: names) {
			ArrayList<Double> cur = new ArrayList<Double>((int)timeLength);
			for(Double d: m.getColumn(name)) {
				cur.add(d);
			}
			data.put(name, new JSONArray(cur));
		}
		json.put("data", data);

		return json;
	}

	/**
 	 * Converts a CSV-string defining the inputs and returns a JSONObject, formatted like:
 	 *{ 
   *	"length": 40,
   *	"tickWidth": 1,
   *	"lowLevel": 0,
   *	"highLevel": 200,
   *	"values": {
   *  	"A": "H",
   *	 	"B": "LLLLLLLLLLH",
   * 		"C": "LLHHLLHHLL HHLLHHLLHH LLHHLLHHLL HHLLHHLLHH"
   *	}
   *}
	 */
	public static JSONObject csvInputsToJSON(String csv) throws JSONException {
		// in the given csv files the tickWidth, lowLevel and highLevel is not defined so here are some constants
		double tickWidth = 10;
		double lowLevel = 0;
		double highLevel = 200;
		// get the lines
		String[] lines = csv.split("\n");		
		// the names
		String[] names = lines[0].split(",");
		// stepsize in the csv
		int stepsize = 70;
		// length = ticks
		double length = ((lines.length-1)*70)/tickWidth;
		// looping through the inputs
		// gets (for example)
		// name = [1, 0, 1]
		HashMap<String, ArrayList<Boolean>> map = new HashMap<String, ArrayList<Boolean>>();
		for(int i = 1; i < lines.length; i++) {
			String[] parts = lines[i].split(",");
			for(int j = 1; j < names.length; j++) {
				String cname = names[j];
				if(map.get(cname) == null)
					map.put(cname, new ArrayList<Boolean>());
				map.get(cname).add(Integer.parseInt(parts[j]) == 1);
			}
		}
		
		// creating the JSON object
		JSONObject json = new JSONObject();
		json.put("length", (double)length);
		json.put("tickWidth", tickWidth);
		json.put("lowLevel", lowLevel);
		json.put("highLevel", highLevel);
		
		// for every name, get the values.
		JSONObject values = new JSONObject();
		int size = lines.length-1;
		for(int i = 1; i < names.length; i++) {
			String cur = "";
			for(int j = 0; j < size; j++)
				cur += repeat(map.get(names[i]).get(j)? "H": "L", (int)(length/size));
			values.put(names[i], cur);
		}
		json.put("values", values);

		return json;
	}

	private static String repeat(String s, int n) {
		String res = "";
		for(int i = 0; i < n; i++)
			res += s;
		return res;
	}
}

