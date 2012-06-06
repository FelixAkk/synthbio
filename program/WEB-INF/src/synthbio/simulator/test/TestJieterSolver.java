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

package synthbio.simulator.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.*;

import synthbio.simulator.JieterSolver;
import synthbio.models.*;

/**
 * Testing Solver.
 */
public class TestJieterSolver {

	public Circuit getAndCircuit(){
		Circuit c=new Circuit("AND test circuit");

		c.addGate(new Gate(
			new AndPromotor("A", "B", 4.5272, 238.9569, 3),
			new CDS("C", 4.1585, 0.0235, 0.8338),
			new Position()
		));
		c.addInput("A");
		c.addInput("B");
		c.addOutput("C");

		c.addSimulationInput("A", "LLLLHHHHHH HHHHHHHHHH HHHHHHHHHH");
		c.addSimulationInput("B", "HHHHHHHHHH HHHHHHHHHH HHHHHHHHHH");
		c.setSimulationLength(40);
		return c;
	}
	public Circuit getNotCircuit(){
		Circuit c=new Circuit("NOT test circuit");
		c.addGate(new Gate(
			new NotPromotor("A", 4.7313, 224.0227, 1),
			new CDS("B", 4.6122, 0.0205, 0.8627),
			new Position()
		));
		c.addInput("A");
		c.addOutput("B");

		c.addSimulationInput("A", "LLLLHHHHLLL LLHHLLHHHH HHHHHHHHHH");
		c.setSimulationLength(40);
		return c;
	}
	
	@Ignore
	@Test
	public void testAndCircuit() throws Exception {
		Circuit c = this.getAndCircuit();

		JieterSolver js=new JieterSolver(c);

		js.solve();
		
	}
	@Test
	public void testNotCircuit() throws Exception {
		Circuit c = this.getNotCircuit();

		JieterSolver js=new JieterSolver(c);

		js.solve();
		
	}
}