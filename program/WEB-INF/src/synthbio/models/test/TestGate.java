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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.*;

import synthbio.models.*;

/**
 * Testing the Gate
 * 
 * @author jieter
 */
public class TestGate{
	double delta=0.0001;

	public Gate getAndGate(){
		AndPromotor promotor=new AndPromotor("A", "B", 1, 2, 3);
		CDS cds=new CDS("C", 1, 2, 3);
		Position position=new Position(1, 2);

		return new Gate(promotor, cds, position);
	}
	
	public Gate getNotGate(){
		NotPromotor promotor=new NotPromotor("A", 1, 2, 3);
		CDS cds=new CDS("C", 1, 2, 3);
		Position position=new Position(1, 2);

		return new Gate(promotor, cds, position);
	}
		
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

	/**
	 * Check if the toString method returns something we expect.
	 */
	@Test
	public void testAndGateToString(){
		Gate g=this.getAndGate();

		assertEquals("[and(A,B)->C @(1.0,2.0)]", g.toString());
	}
	@Test
	public void testNotGateToString(){
		Gate g=this.getNotGate();

		assertEquals("[not(A)->C @(1.0,2.0)]", g.toString());
	}

	/**
	 * Check if the gates getInput/getOutput methods yield expected
	 * results.
	 */
	@Test
	public void testGetInputsOutputs(){
		Gate g;

		//for an AND gate
		g=this.getAndGate();
		assertThat(g.getInputs(), hasItems("A", "B"));
		assertThat(g.getOutput(), is("C"));
		
		//for a NOT gate
		g=this.getNotGate();
		assertThat(g.getInputs(), hasItems("A"));
		assertThat(g.getOutput(), is("C"));
	}

	/**
	 * Check the hasInput/hasOutput methods
	 */
	@Test
	public void testHasInputOutput(){
		Gate g;

		//for an AND gate
		g=this.getAndGate();
		assertTrue(g.hasInput("A"));
		assertTrue(g.hasInput("B"));
		assertTrue(g.hasOutput("C"));

		assertFalse(g.hasInput("C"));
		assertFalse(g.hasInput("X"));
		assertFalse(g.hasOutput("A"));
		assertFalse(g.hasOutput("B"));
		
		
		//for a NOT gate
		g=this.getNotGate();
		assertTrue(g.hasInput("A"));
		assertTrue(g.hasOutput("C"));

		assertFalse(g.hasInput("C"));
		assertFalse(g.hasInput("X"));
		assertFalse(g.hasOutput("A"));
		assertFalse(g.hasOutput("B"));
	}
}
