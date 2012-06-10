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
import org.junit.Rule;
import org.junit.rules.ExpectedException;
import org.junit.Test;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.hamcrest.CoreMatchers.*;

import java.util.ArrayList;

import synthbio.Util;
import synthbio.models.*;

/**
 * Testing the SimulationSetting object.
 * 
 * @author jieter
 */
public class TestSimulationSetting{
	double delta=0.0001;

	@Rule
	public ExpectedException thrown = ExpectedException.none();

/**
	 * Test if addSimulationInput clears all but (H|L).
	 */
	@Test
	public void testAddSimulationInput() throws Exception {
		SimulationSetting ss = new SimulationSetting();
		
		ss.addInput("A", "L /H LL");
		assertThat(ss.getInput("A"), equalTo("LHLL"));

		ss.addInput("B", "HHHhsblsLL");
		assertThat(ss.getInput("B"), equalTo("HHHLL"));
	}
	
	/**
	 * Define some inputs and check if getSimulationInputAt() returns
	 * things we expect it to return...
	 */
	@Test
	public void testInputSignals_inputAt() throws Exception {
		SimulationSetting ss = new SimulationSetting();
		ss.addInput("A", "LLLH");
		ss.addInput("B", "HLHL");
		
		assertThat(ss.getInputAt("A", 0), equalTo("L"));
		assertThat(ss.getInputAt("A", 1), equalTo("L"));
		assertThat(ss.getInputAt("A", 2), equalTo("L"));
		assertThat(ss.getInputAt("A", 3), equalTo("H"));
		assertThat(ss.getInputAt("A", 30), equalTo("H"));
		
		//should tick 40 be included? Since 40 ticks starting at 0
		//yields 39 as upper boundary...
		//If we decide to change: change assertion in
		//Circuit::getSimulationInputAt()
		assertThat(ss.getInputAt("A", 40), equalTo("H"));
		
		assertThat(ss.getInputAt("B", 0), equalTo("H"));
		assertThat(ss.getInputAt("B", 1), equalTo("L"));
		assertThat(ss.getInputAt("B", 2), equalTo("H"));
		assertThat(ss.getInputAt("B", 3), equalTo("L"));
		assertThat(ss.getInputAt("B", 30), equalTo("L"));
		assertThat(ss.getInputAt("B", 40), equalTo("L"));
	}

	/**
	 * Ask for the value of a tick outside the simulated length
	 */
	@Test
	public void testInputSignals_inputAt_outOfBounds() throws Exception {
		thrown.expect(AssertionError.class);
		thrown.expectMessage("Tick should not exceed simulation length.");

		SimulationSetting ss = new SimulationSetting();
		
		ss.setLength(40);
		ss.addInput("A", "L");

		ss.getInputAt("A", 42);
	}
	

	/**
	 * Check if getLevelAt() returns correct values
	 * with different stepsizes.
	 */
	@Test
	public void testGetLevelAt() {
		SimulationSetting ss = new SimulationSetting();
		ss.addInput("A", "LLLLHHHHLLL LLHHLLHHHH HHHHHHHHHH");
		double low = ss.getLowLevel();
		double high = ss.getHighLevel();
		
		
		assertEquals(low, ss.getLevelAt("A", 0), delta);
		assertEquals(low, ss.getLevelAt("A", 1), delta);
		assertEquals(low, ss.getLevelAt("A", 2), delta);
		
		assertEquals(low, ss.getLevelAt("A", 3), delta);
		assertEquals(high, ss.getLevelAt("A", 4), delta);
		
		assertEquals(high, ss.getLevelAt("A", 7), delta);
		assertEquals(low, ss.getLevelAt("A", 8), delta);
	}

}
