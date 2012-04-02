/**
 * Synthetic Biology project (Biobrick Modeller/Simulator)
 * https://github.com/FelixAkk/synthbio
 */

package synthbio.models;

import org.json.JSONString;

abstract class Promotor implements JSONString {
	public static final String keyK1 = "k1";
	public static final String keyKm = "km";
	public static final String keyN = "n";

	public final double k1;
	public final double km;
	public final int n;

	public Promotor(Double k1, Double km, int n) {
		this.k1 = k1;
		this.km = km;
		this.n = n;
	}
}