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
}