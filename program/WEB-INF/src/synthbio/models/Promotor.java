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

import java.util.List;

/**
 * Promotor defines common functionality for *Promotors.
 *
 * @author jieter
 */
abstract class Promotor extends BioBrick{
	public final double k1;
	public final double km;
	public final int n;

	public Promotor(Double k1, Double km, int n) {
		this.k1 = k1;
		this.km = km;
		this.n = n;
	}

	public double getK1(){
		return this.k1;
	}
	public double getKm(){
		return this.km;
	}
	public double getN(){
		return this.n;
	}
	
	/**
	 * List of transcription factors for this Promotor.
	 *
	 * Not named getTfs because the JSONObject uses all get*-methods to
	 * create serialized output. If we want to use get*-methods, we
	 * should provide a list of properties to include.
	 */
	public abstract List<String> listTfs();

	/**
	 * Does this Promotor consists of at lesat this transcription factor?
	 */
	public abstract boolean hasTf(String tf);
	

	/**
	 * Does this Promotor equals the other.
	 */
	public boolean equals(Object other){
		if(!(other instanceof Promotor)){
			return false;
		}
		Promotor that=(Promotor) other;

		return
			this.getK1()==that.getK1() &&
			this.getKm()==that.getKm() &&
			this.getN()==that.getN();
	}

	/**
	 * Return the kind of Promotor we are representing.
	 *
	 * Since the JSON serialisation methods use the get*-methods to
	 * fetch the data from the objects, we do not use such a name scheme
	 * for this method.
	 */
	public abstract String kind();
}
