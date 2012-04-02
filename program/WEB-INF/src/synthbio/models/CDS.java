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
 * CDS value object.
 * 
 * CDS == Gene coding sequence 
 */
public class CDS implements JSONString{
	
	public static final String keyName = "name";
	public static final String keyK2 = "k2";
	public static final String keyD1 = "d1";
	public static final String keyD2 = "d2";

	public final String name;
	public final double k2;
	public final double d1;
	public final double d2;

	public CDS(String name, double k2, double d1, double d2){
		this.name=name;

		this.k2=k2;
		this.d1=d1;
		this.d2=d2;
	}

	/**
	 * Parse from CSV row.
	 */
	public static CDS fromCSV(String row){
		String[] tokens=row.split(",");
		if(tokens.length!=4){
			throw new IllegalArgumentException("CSV row must have exactly four fields");
		}
		
		return new CDS(
			tokens[0],
			Double.parseDouble(tokens[1]),
			Double.parseDouble(tokens[2]),
			Double.parseDouble(tokens[3])
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
					.key(this.keyName).value(this.name)
					.key(this.keyK2).value(this.k2)
					.key(this.keyD1).value(this.d1)
					.key(this.keyD2).value(this.d2)
				.endObject();
			return json.toString();
		}catch(JSONException e){
			return "{}";
		}
	}
}