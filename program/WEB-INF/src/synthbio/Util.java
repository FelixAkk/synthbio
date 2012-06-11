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

import org.json.*;

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

	public static String repeat(char s, int n) {
		String res = "";
		for(int i = 0; i < n; i++) {
			res += s;
		}
		return res;
	}
}

