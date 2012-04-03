/**
 * Synthetic Biology project (Biobrick Modeller/Simulator)
 * https://github.com/FelixAkk/synthbio
 */

package synthbio.json;

import java.util.Collection;


import org.json.JSONString;
import org.json.JSONObject;

/**
 * JSONResponse is the default wrapper for all the date in response to
 * client requests.
 */
public class JSONResponse implements JSONString{

	//TODO: final?
	public boolean success;
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
	
	
	/**
	 * toJSONString returns a String with the JSON representatation of
	 * the current object state.
	 *
	 * The implementation of JSONString takes care of converting types
	 * to compatible types and will throw an exception if it fails to do
	 * so. In case of an exception, a reply with success=false and the
	 * Exceptions error message will be returned.
	 *
	 * @return String
	 */
	public String toJSONString(){
		JSONObject json=new JSONObject(this);
		return json.toString();
	}
}