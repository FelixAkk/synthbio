/**
 * Synthetic Biology project (Biobrick Modeller/Simulator)
 * https://github.com/FelixAkk/synthbio
 */

package synthbio.models.test;

import synthbio.models.Position;

import org.junit.*;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Testing the Position
 *
 * @author jieter
 */
public class TestPosition{
	double delta=0.0001;
	
	/**
	 * Test if what comes out equals what we put in.
	 */
	@Test
	public void testConstructor(){
		double x=2.5;
		double y=4.5;
		
		Position p=new Position(x, y);

		assertEquals(x, p.x, delta);
		assertEquals(y, p.y, delta);
	}
	@Test
	public void testDistanceToHorizontal(){
		Position p=new Position(1, 1);
		Position q=new Position(2, 1);

		assertEquals(1, p.distanceTo(q), delta);
	}

	@Test
	public void testDistanceToVertical(){
		Position p=new Position(1, 1);
		Position q=new Position(1, 2);

		assertEquals(1, p.distanceTo(q), delta);
	}
	
	@Test
	public void testDistanceToDiagonal(){
		Position p=new Position(1, 1);
		Position q=new Position(2, 2);

		assertEquals(Math.sqrt(2), p.distanceTo(q), delta);
	}

}
