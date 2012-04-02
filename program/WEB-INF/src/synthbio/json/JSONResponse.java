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

	public static final String keySuccess = "success";
	public static final String keyMessage = "message";
	public static final String keyData = "data";

	//TODO: final?
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
		try{
			JSONWriter json=new JSONStringer()
				.object()
					.key(this.keySuccess).value(this.success)
					.key(this.keyMessage).value(this.message)
					.key(this.keyData).value(this.data)
				.endObject();
			return json.toString();
		}catch(JSONException e){
			//TODO: Possibly endless loop?
			return new JSONResponse(false,
				"JSON convert error: "+e.getMessage()).toJSONString();
		}
		
	}
}