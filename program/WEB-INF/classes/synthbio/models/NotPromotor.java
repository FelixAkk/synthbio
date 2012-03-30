/**
 * Synthetic Biology project (Biobrick Modeller/Simulator)
 * https://github.com/FelixAkk/synthbio
 */

package synthbio.models;

import org.json.JSONException;
import org.json.JSONString;
import org.json.JSONStringer;
import org.json.JSONWriter;


/**
 * NotPromotor value object.
 */
public class NotPromotor extends Promotor implements JSONString{

	/**
	 * Name of the transcription factor
	 */
	public String tf;
		
	public NotPromotor(String tf, double k1, double km, int n){
		this.tf=tf;
		
		this.k1=k1;
		this.km=km;
		this.n=n;
	}

	/**
	 * Returns a String containing a JSON representation for the current
	 * object state.
	 */
	public String toJSONString(){
		JSONWriter json=null;
		try{
			json=new JSONStringer()
				.object()
					.key("tf").value(this.tf)
					.key("k1").value(this.k1)
					.key("km").value(this.km)
					.key("n").value(this.n)
				.endObject();
		}catch(JSONException e){
			return "{}";
		}
		return json.toString();
	}
	
}