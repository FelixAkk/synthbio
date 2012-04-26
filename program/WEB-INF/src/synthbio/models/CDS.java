/**
 * Synthetic Biology project (Biobrick Modeller/Simulator)
 * https://github.com/FelixAkk/synthbio
 */

package synthbio.models;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONString;


/**
 * CDS value object.
 * 
 * CDS == Gene coding sequence
 *
 * @author jieter
 */
public class CDS extends BioBrick{

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

	public String getName(){
		return this.name;
	}
	public double getK2(){
		return this.k2;
	}
	public double getD1(){
		return this.d1;
	}
	public double getD2(){
		return this.d2;
	}

	/**
	 * Return a simple string representation.
	 */
	public String toString(){
		return this.getName();
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
}