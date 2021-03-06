/**
 * Project Zelula
 *
 * Contextproject TI2800 
 * TU Delft - University of Technology
 *  
 * Authors: 
 * 	Felix Akkermans, Niels Doekemeijer, Thomas van Helden
 * 	Albert ten Napel, Jan Pieter Waagmeester
 * 
 * https://github.com/FelixAkk/synthbio
 */

package synthbio.models;

import java.util.Arrays;
import java.util.List;


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

	public String kind(){
		return "not";
	}

	public List<String> listTfs(){
		return Arrays.asList(this.getTf());
	}

	public boolean hasTf(String tf){
		return this.getTf().equals(tf);
	}

	public boolean equals(Object other){
		if(!super.equals(other)){
			return false;
		}
		if(!(other instanceof NotPromotor)){
			return false;
		}
		NotPromotor that=(NotPromotor) other;

		return this.getTf().equals(that.getTf());
	}
	/**
	 * Return a simple string representation.
	 */
	public String toString(){
		return this.getTf();
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
