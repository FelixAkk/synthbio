/**
 * Synthetic Biology project (Biobrick Modeller/Simulator)
 * https://github.com/FelixAkk/synthbio
 */

package synthbio.models;

/**
 * AndPromotor value object.
 */
public class AndPromotor extends Promotor {

	/**
	 * Names is used to determine which properties to include
	 * when serializing the object to JSON.
	 */
	protected final String[] getNames(){
		String ret[]={"tf1", "tf2", "k1", "km", "n"};
		return ret;
	}
	
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

	public String getTf1(){
		return this.tf1;
	}
	public String getTf2(){
		return this.tf2;
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
}