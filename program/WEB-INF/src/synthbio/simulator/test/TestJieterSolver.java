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

import org.junit.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

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
		bbr = new BioBrickRepository();
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
		c.setSimulationLength(40);
		c.addSimulationInput("A", "L");
		
		return c;
	}

	/**
	 * Check if getSimulationLevelAt() returns correct values
	 * with different stepsizes.
	 */
	@Test
	public void testSimulationLevelAt() {
		Circuit c = this.getNotCircuit();

		double low = c.getSimulationLowLevel();
		double high = c.getSimulationHighLevel();
		c.addSimulationInput("A", "LLLLHHHHLLL LLHHLLHHHH HHHHHHHHHH");
		
		JieterSolver js=new JieterSolver(c);

		js.calculation_resolution = 1000;
		assertEquals(low, js.getInputLevelAt("A", 0), delta);
		assertEquals(low, js.getInputLevelAt("A", 1), delta);
		assertEquals(low, js.getInputLevelAt("A", 2), delta);
		
		assertEquals(low, js.getInputLevelAt("A", 3999), delta);
		assertEquals(high, js.getInputLevelAt("A", 4000), delta);
		
		assertEquals(high, js.getInputLevelAt("A", 7999), delta);
		assertEquals(low, js.getInputLevelAt("A", 8000), delta);

		js.calculation_resolution = 100;
		assertEquals(low, js.getInputLevelAt("A", 0), delta);
		assertEquals(low, js.getInputLevelAt("A", 1), delta);
		assertEquals(low, js.getInputLevelAt("A", 2), delta);
		
		assertEquals(low, js.getInputLevelAt("A", 399), delta);
		assertEquals(high, js.getInputLevelAt("A", 400), delta);
		
		assertEquals(high, js.getInputLevelAt("A", 799), delta);
		assertEquals(low, js.getInputLevelAt("A", 800), delta);
	}


	/**
	 * Test solving a NOT circuit.
	 *
	 * 
	 */
	@Test
	public void testNotCircuit() throws Exception {
		Circuit c = this.getNotCircuit();
		
		//20s low, 20s high
		c.addSimulationInput("A", "LLLLLLLLLLLLLLLLLLLL HHHHHHHHHHHHHHHHHHHH");
		
		JieterSolver js=new JieterSolver(c);

		js.solve();

		// output starts lower than after 20s.
		assertThat(
			js.get("B", 0 * js.result_resolution),
			is(lessThan(js.get("B", 20 * js.result_resolution)))
		);
		
		// Fully set output for a Low input is higher than the fully set
		// output for a High input.
		assertThat(
			js.get("B", 20 * js.result_resolution),
			is(greaterThan(js.get("B", 39 * js.result_resolution)))
		);
		
	}
	
	/**
	 * Test some characteristics of an AND gate. 
	 */
	@Test
	public void testAndCircuit() throws Exception {
		Circuit c = this.getAndCircuit();
		c.addSimulationInput("A", "LLLLLLLLLL LLLLLLLLLL HHHHHHHHHH HHHHHHHHHH");
		c.addSimulationInput("B", "LLLLLLLLLL HHHHHHHHHH HHHHHHHHHH LLLLLLLLLL");

		JieterSolver js=new JieterSolver(c);
		js.solve();

		//no rising values if inputs are low.
		assertThat(
			js.get("C", 0 * js.result_resolution),
			is(closeTo(js.get("C", 9 * js.result_resolution), 0.1))
		);

		//no rising values if one input is high and one is low.
		assertThat(
			js.get("C", 0 * js.result_resolution),
			is(closeTo(js.get("C", 19 * js.result_resolution), 0.1))
		);

		//rising value if both inputs are high
		assertThat(
			js.get("C", 0 * js.result_resolution),
			is(lessThan(js.get("C", 21 * js.result_resolution)))
		);
		assertThat(
			js.get("C", 21 * js.result_resolution),
			is(lessThan(js.get("C", 29 * js.result_resolution)))
		);

		//check if we have a falling edge if one input gets lower.
		assertThat(
			js.get("C", 30 * js.result_resolution),
			is(greaterThan(js.get("C", 32 * js.result_resolution)))
		);
		assertThat(
			js.get("C", 32 * js.result_resolution),
			is(greaterThan(js.get("C", 39 * js.result_resolution)))
		);
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
		assertEquals(1.0/js.result_resolution, json.getDouble("step"), delta);
		
		assertTrue(json.has("data"));
		//todo: check if data has an entry for every name
		//todo: check if every entry has equal length.
		
	}
}
