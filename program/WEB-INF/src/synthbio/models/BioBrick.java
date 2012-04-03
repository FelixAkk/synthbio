/**
 * Synthetic Biology project (Biobrick Modeller/Simulator)
 * https://github.com/FelixAkk/synthbio
 */

package synthbio.models;

import org.json.JSONException;
import org.json.JSONString;
import org.json.JSONObject;

abstract class BioBrick{
	
	/**
	 * Returns a String containing a JSON representation for the current
	 * object state.
	 *
	 * Just passes current object to JSONObject, which will contain all
	 * properties which have getters.
	 *
	 * @return	String
	 */
	public String toJSONString(){

		JSONObject json=new JSONObject(this);
		return json.toString();
	}

}