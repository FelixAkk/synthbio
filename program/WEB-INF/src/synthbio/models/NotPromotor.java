/**
 * Synthetic Biology project (Biobrick Modeller/Simulator)
 * https://github.com/FelixAkk/synthbio
 */

package synthbio.models;


/**
 * NotPromotor value object.
 *
 * @author jieter
 */
public class NotPromotor extends Promotor {

	/**
	 * Name of the transcription factor
	 */
	public final String tf;
		
	public NotPromotor(String tf, double k1, double km, int n){
		super(k1, km, n);
		this.tf=tf;
	}

	public String getTf(){
		return this.tf;
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
}