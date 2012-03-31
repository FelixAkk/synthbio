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
	 * Parse from CSV row.
	 */
	public static NotPromotor fromCSV(String row){
		String[] tokens=row.split(",");
		if(tokens.length!=4){
			throw new IllegalArgumentException("CSV row must have exactly four fields");
		}
		
		return new NotPromotor(
			tokens[0],
			Double.parseDouble(tokens[1]),
			Double.parseDouble(tokens[2]),
			Integer.parseInt(tokens[3])
		);
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