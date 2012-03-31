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

 
public class JSONResponse implements JSONString{

	public boolean success;
	public String message="";
	public Object data;
	
	public String toJSONString(){
		//convert array to JSONArray to make it serializeable.
		if(this.data instanceof Collection){
			try{
				this.data=new JSONArray(this.data);
			}catch(Exception e){
				this.data=null;	
				this.message=
					"JSON convert error: could not convert array to JSONArray "+
					e.getMessage();
			}
		}

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
			return "{exception:\""+e.getMessage()+"\"}";
		}
		
	}
}