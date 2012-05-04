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
 * AndPromotor value object.
 *
 * Except for the two transcription factors, AndPromotor inherits all
 * parameters from Promotor.
 *
 * @author jieter
 */
public class AndPromotor extends Promotor {

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
	public String kind(){
		return "and";
	}

	/**
	 * Return the transcription factors for the AndPromotor
	 */
	public List<String> listTfs(){
		return Arrays.asList(this.getTf1(), this.getTf2());
	}
	
	/**
	 * Does this AndPromotor react on transcription factor tf?
	 */
	public boolean hasTf(String tf){
		return this.getTf1().equals(tf) || this.getTf2().equals(tf);
	}
		
	/**
	 * Is this AndPromotor composed of these two TF's?
	 * 
	 */
	public boolean hasTfs(String tf1, String tf2){
		return
			this.tf1.equals(tf1) && this.tf2.equals(tf2) ||
			this.tf2.equals(tf1) && this.tf1.equals(tf2);
	}

	/**
	 * Does this equals other?
	 */
	public boolean equals(Object other){
		if(!super.equals(other)){
			return false;
		}
		if(!(other instanceof AndPromotor)){
			return false;
		}
		AndPromotor that=(AndPromotor) other;

		return this.hasTfs(that.getTf1(), that.getTf2());
	}
		
	/**
	 * Return a simple string representation.
	 */
	public String toString(){
		return this.getTf1()+","+this.getTf2();
	}
	/**
	 * Parse an AndPromotor from CSV row.
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
