/**
 * Synthetic Biology project (Biobrick Modeller/Simulator)
 * https://github.com/FelixAkk/synthbio
 */

package synthbio.models;

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
	 * Return the kind of Promotor we are representing.
	 *
	 * Since the JSON serialisation methods use the get*-methods to
	 * fetch the data from the objects, we do not use such a name scheme
	 * for this method.
	 */
	public abstract String kind();
		
}