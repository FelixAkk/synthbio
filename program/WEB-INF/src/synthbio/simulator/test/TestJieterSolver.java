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
import org.junit.rules.ExpectedException;
import static org.junit.Assert.assertEquals;
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

		c.getSimulationSetting().addInput("A", "LLLLHHHHHH HHHHHHHHHH HHHHHHHHHH");
		c.getSimulationSetting().addInput("B", "HHHHHHHHHH HHHHHHHHHH HHHHHHHHHH");
		c.getSimulationSetting().setLength(40);
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
		c.getSimulationSetting().setLength(40);
		c.getSimulationSetting().addInput("A", "L");
		
		return c;
	}

	
	@Rule
	public ExpectedException thrown = ExpectedException.none();

	/**
	 * Trying to call toJSON on an unsolved solver should thrown an
	 * AssertionError
	 */
	@Test
	public void test_notSolved() throws Exception {
		thrown.expect(AssertionError.class);
		thrown.expectMessage("Run solver first!");

		Circuit c = this.getNotCircuit();
		JieterSolver js=new JieterSolver(c);

		js.toJSON();
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
		c.getSimulationSetting().addInput("A", "LLLLLLLLLLLLLLLLLLLL HHHHHHHHHHHHHHHHHHHH");
		
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
		c.getSimulationSetting().addInput("A", "LLLLLLLLLL LLLLLLLLLL HHHHHHHHHH HHHHHHHHHH");
		c.getSimulationSetting().addInput("B", "LLLLLLLLLL HHHHHHHHHH HHHHHHHHHH LLLLLLLLLL");

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

		assertEquals(c.getSimulationSetting().getLength(), json.getInt("length"));
		assertEquals(1.0/js.result_resolution, json.getDouble("step"), delta);
		
		assertTrue(json.has("data"));
		//todo: check if data has an entry for every name
		//todo: check if every entry has equal length.
		
	}
}
