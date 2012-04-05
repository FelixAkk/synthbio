/**
 * Synthetic Biology project (Biobrick Modeller/Simulator)
 * https://github.com/FelixAkk/synthbio
 */

package synthbio.models;

import org.json.JSONException;
import org.json.JSONString;
import org.json.JSONObject;

/**
 * BioBrick base class
 * 
 * @author jieter
 */
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
		return new JSONObject(this).toString();
	}

}