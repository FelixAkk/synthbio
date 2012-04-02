/**
 * Synthetic Biology project (Biobrick Modeller/Simulator)
 * https://github.com/FelixAkk/synthbio
 */

package synthbio.json;

import java.util.Collection;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONString;
import org.json.JSONStringer;
import org.json.JSONWriter;

/**
 * JSONResponse is the default wrapper for all the date in response to
 * client requests.
 */
public class JSONResponse implements JSONString{

	public boolean success;
	public String message="";

	/**
	 * Data to be sent.
	 * May be a lot of things which can be converted to String.
	 */
	public Object data;

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
		JSONWriter json=null;
		try{
			json=new JSONStringer()
				.object()
					.key("success").value(this.success)
					.key("message").value(this.message)
					.key("data").value(this.data)
				.endObject();
			return json.toString();
		}catch(JSONException e){
			return new JSONResponse(false,
				"JSON convert error: "+e.getMessage()).toJSONString();
		}
		
	}
}