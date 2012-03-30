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
 * Protein value object.
 *
 * or CDS
 */
public class Protein implements JSONString{
	
	public String name;
	public double k2;
	public double d1;
	public double d2;

	public Protein(String name, double k2, double d1, double d2){
		this.name=name;

		this.k2=k2;
		this.d1=d1;
		this.d2=d2;
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
					.key("name").value(this.name)
					.key("k2").value(this.k2)
					.key("d1").value(this.d1)
					.key("d2").value(this.d2)
				.endObject();
				
		}catch(JSONException e){
			return "{}";
		}
		return json.toString();
	}
}