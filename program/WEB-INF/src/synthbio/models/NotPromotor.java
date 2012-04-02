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
public class NotPromotor extends Promotor {

	public static final String keyTf = "tf";

	/**
	 * Name of the transcription factor
	 */
	public final String tf;
		
	public NotPromotor(String tf, double k1, double km, int n){
		super(k1, km, n);
		this.tf=tf;
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
		try{
			JSONWriter json=new JSONStringer()
				.object()
					.key(this.keyTf).value(this.tf)
					//todo: Reuse possible parent toJSONString for following?
					.key(this.keyK1).value(this.k1)
					.key(this.keyKm).value(this.km)
					.key(this.keyN).value(this.n)
				.endObject();
			return json.toString();
		}catch(JSONException e){
			return "{}";
		}
	}
	
}