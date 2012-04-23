/**
 * Synthetic Biology project (Biobrick Modeller/Simulator)
 * https://github.com/FelixAkk/synthbio
 */

package synthbio.models;



/**
 * Point representation
 * @author jieter
 */
public class Position{
	public double x;
	public double y;

	public Position(double x, double y){
		this.x=x;
		this.y=y;
	}

	public double distanceTo(Position that){
		return Math.sqrt(
			Math.pow(this.x-that.x, 2) + Math.pow(this.y-that.y, 2)
		);
	}
}
