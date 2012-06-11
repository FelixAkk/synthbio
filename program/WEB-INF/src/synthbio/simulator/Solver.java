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
 
package synthbio.simulator;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Solver interface
 */
public interface Solver {

	
	public void solve() throws Exception;

	/**
 	 * Converts a Solve result to a JSON-string of the format:
 	 * 	{
 	 * 		"names": ["A", "B"],
 	 * 		"length": 10,
 	 * 		"step": 1,
 	 * 		"data": {
 	 *			"A": [0, 0, 0, 0, 0, 200, 200, 200, 200, 200],
 	 *			"B": [...]
 	 * 		}
 	 * 	}
 	 */
	public JSONObject toJSON() throws JSONException;
}
 
