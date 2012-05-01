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
import static org.junit.Assert.assertNull;

import java.util.ArrayList;

import synthbio.Util;
import synthbio.models.*;

/**
 * Testing the Gate
 * 
 * @author jieter
 */
public class TestGate{
	double delta=0.0001;

	/**
	 * Test empty gate constructor
	 */
	@Test
	public void testEmptyConstructor(){
		Gate g=new Gate();

		assertNull(g.getPromotor());
		assertNull(g.getCDS());
		assertEquals(new Position(), g.getPosition());
	}

	@Test
	public void testFullConstructor(){
		AndPromotor promotor=new AndPromotor("A", "B", 1, 2, 3);
		CDS cds=new CDS("A", 1, 2, 3);
		Position position=new Position(1, 2);

		Gate g=new Gate(promotor, cds, position);

		assertEquals(promotor, g.getPromotor());
		assertEquals(cds, g.getCDS());
		assertEquals(position, g.getPosition());
	}

	@Test
	public void testToString(){
		AndPromotor promotor=new AndPromotor("A", "B", 1, 2, 3);
		CDS cds=new CDS("C", 1, 2, 3);
		Position position=new Position(1, 2);

		Gate g=new Gate(promotor, cds, position);

		assertEquals("[and(A,B)->C @(1.0,2.0)]", g.toString());
	}
	
	
	
}
