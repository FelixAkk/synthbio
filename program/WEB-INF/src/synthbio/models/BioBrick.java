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

package synthbio.models;

import org.json.JSONObject;
import org.json.JSONString;

/**
 * BioBrick base class
 * 
 * @author jieter
 */
abstract class BioBrick implements JSONString{
	
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