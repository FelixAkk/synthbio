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
public class AndPromotor extends Promotor implements JSONString{

	/**
	 * Names of the Transcription factors
	 */
	public String tf1;
	public String tf2;
	
	public AndPromotor(String tf1, String tf2, double k1, double km, int n){
		this.tf1=tf1;
		this.tf2=tf2;

		this.k1=k1;
		this.km=km;
		this.n=n;
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
		JSONWriter json=null;
		try{
			json=new JSONStringer()
				.object()
					.key("tf1").value(this.tf1)
					.key("tf2").value(this.tf2)
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