/**
 * Synthetic Biology project (Biobrick Modeller/Simulator)
 * https://github.com/FelixAkk/synthbio
 */

package synthbio.models;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Point representation
 * @author jieter
 */
public class Position{
	private double x;
	private double y;

	public Position(double x, double y){
		this.x=x;
		this.y=y;
	}
	
	/**
	 * Create a undefined position defaulting to 0,0
	 */
	public Position(){
		this(0, 0);
	}
	
	/**
	 * getters
	 */
	public double getX(){
		return this.x;
	}
	public double getY(){
		return this.y;
	}

	/**
	 * Calculate the distance between two positions
	 *
	 * @param that Position to calculate distance to.
	 * @return The distance between this and that.
	 */
	public double distanceTo(Position that){
		return Math.sqrt(
			Math.pow(this.getX()-that.getX(), 2) +
			Math.pow(this.getY()-that.getY(), 2)
		);
	}

	/**
	 * Return a simple String representation
	 */
	public String toString(){
		return "("+this.getX()+","+this.getY()+")";
	}
	
	/**
	 * Deserialize JSON to a Position
	 */
	public static Position fromJSON(String json) throws JSONException{
		return Position.fromJSON(new JSONObject(json));
	}
	public static Position fromJSON(JSONObject json) throws JSONException{
		return new Position(json.getDouble("x"), json.getDouble("y"));
	}
}
