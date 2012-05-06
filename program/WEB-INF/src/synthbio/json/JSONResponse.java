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

package synthbio.json;

import org.json.JSONObject;
import org.json.JSONString;

/**
 * JSONResponse is the default wrapper for all the data in response to
 * client requests.
 *
 * @author jieter
 */
public final class JSONResponse implements JSONString{

	/**
	 * Should the response be considered as a success?
	 */
	public boolean success;

	/**
	 * If the response is not a success, the message will explain why.
	 */
	public String message="";

	/**
	 * Data to be sent.
	 * May be a lot of things which can be converted to String.
	 *
	 * Default to JSONOBjecect.NULL to correctly serialize to null in JSON.
	 */
	public Object data=JSONObject.NULL;

	public JSONResponse(){
		this.success=true;
	}
	public JSONResponse(boolean success){
		this.success=success;
	}
	public JSONResponse(boolean success, String message){
		this(success);
		this.message=message;
	}

	//define getters for JSONObject() serialisation.
	public boolean getSuccess(){
		return this.success;
	}
	public String getMessage(){
		return this.message;
	}
	public Object getData(){
		return this.data;
	}

	public void fail(String message){
		this.success=false;
		this.message=message;
	}

	/**
	 * toJSONString returns a String with the JSON representatation of
	 * the current object state.
	 *
	 * @return String
	 */
	public String toJSONString(){
		return new JSONObject(this).toString();
	}
}