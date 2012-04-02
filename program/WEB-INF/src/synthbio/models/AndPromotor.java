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
 * AndPromotor value object.
 */
public class AndPromotor extends Promotor {

	public static final String keyTf1 = "tf1";
	public static final String keyTf2 = "tf2";

	/**
	 * Names of the Transcription factors
	 */
	public final String tf1;
	public final String tf2;
	
	public AndPromotor(String tf1, String tf2, double k1, double km, int n){
		super(k1, km, n);		
		this.tf1=tf1;
		this.tf2=tf2;
	}
	
	/**
	 * Parse from CSV row.
	 */
	public static AndPromotor fromCSV(String row){
		String[] tokens=row.split(",");
		if(tokens.length!=5){
			throw new IllegalArgumentException("CSV row must have exactly five fields");
		}
		
		return new AndPromotor(
			tokens[0],
			tokens[1],
			Double.parseDouble(tokens[2]),
			Double.parseDouble(tokens[3]),
			Integer.parseInt(tokens[4])
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
					.key(this.keyTf1).value(this.tf1)
					.key(this.keyTf2).value(this.tf2)
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