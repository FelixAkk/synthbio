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

import org.json.JSONObject;

import synthbio.simulator.JieterSolver;
import synthbio.files.BioBrickRepository;
import synthbio.models.*;

/**
 * Testing Solver.
 */
public class TestJieterSolver {
	public double delta=0.01;
	BioBrickRepository bbr;

	@Before
	public void setUp() throws Exception{
		bbr = new BioBrickRepository("data/biobricks/new/");
	}
	
	public Circuit getAndCircuit(){
		Circuit c=new Circuit("AND test circuit");

		c.addGate(new Gate(
			bbr.getAndPromotor("A", "B"),
			bbr.getCDS("C"),
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
			bbr.getNotPromotor("A"),
			bbr.getCDS("B"),
			new Position()
		));
		c.addInput("A");
		c.addOutput("B");

		c.addSimulationInput("A", "LLLLHHHHLLL LLHHLLHHHH HHHHHHHHHH");
		c.setSimulationLength(40);
		return c;
	}

	/**
	 * Check if getSimulationLevelAt() returns correct values
	 * with different stepsizes.
	 */
	@Test
	public void testSimulationLevelAt(){
		Circuit c = this.getNotCircuit();

		JieterSolver js=new JieterSolver(c);
		js.stepsize=10;

		double low=c.getSimulationLowLevel();
		double high=c.getSimulationHighLevel();
		
		assertEquals(low, js.getInputLevelAt("A", 0), delta);
		assertEquals(low, js.getInputLevelAt("A", 1), delta);
		assertEquals(low, js.getInputLevelAt("A", 2), delta);
		assertEquals(low, js.getInputLevelAt("A", 30), delta);
		assertEquals(low, js.getInputLevelAt("A", 39), delta);
		
		assertEquals(high, js.getInputLevelAt("A", 40), delta);
		assertEquals(high, js.getInputLevelAt("A", 49), delta);
	}

	
	@Test
	public void testNotCircuit() throws Exception {
		Circuit c = this.getNotCircuit();

		JieterSolver js=new JieterSolver(c);

		js.solve();
		
	}
	
	@Ignore
	@Test
	public void testAndCircuit() throws Exception {
		Circuit c = this.getAndCircuit();

		JieterSolver js=new JieterSolver(c);

		js.solve();

	}

	/**
	 * Check if the json returned contains all fields
	 * and check some consitancy
	 */
	@Test
	public void testToJSON() throws Exception {
		Circuit c = this.getNotCircuit();

		JieterSolver js=new JieterSolver(c);

		js.solve();

		JSONObject json=js.toJSON();

		assertTrue(json.has("names"));
		//todo: check if all names in js.species exist in names.

		assertEquals(c.getSimulationLength(), json.getInt("length"));
		assertEquals((double)1/js.stepsize, json.getDouble("step"), delta);
		
		assertTrue(json.has("data"));
		//todo: check if data has an entry for every name
		//todo: check if every entry has equal length.
		
	}
}
