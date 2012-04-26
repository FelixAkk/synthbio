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

package synthbio.models.test;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

import synthbio.models.Position;

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

		assertEquals(x, p.getX(), delta);
		assertEquals(y, p.getY(), delta);
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
		//reverse
		assertEquals(Math.sqrt(2), q.distanceTo(p), delta);
	}

	@Test
	public void testFromJSON() throws Exception {
		String json="{\"x\":2.0, \"y\": 3.14}";

		Position p=Position.fromJSON(json);

		assertEquals(2.0, p.getX(), delta);
		assertEquals(3.14, p.getY(), delta);
	}

}
