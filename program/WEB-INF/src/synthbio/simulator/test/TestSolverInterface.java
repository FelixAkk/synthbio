/**
 * Synthetic Biology project (Biobrick Modeller/Simulator)
 * https://github.com/FelixAkk/synthbio
 * @author Albert ten Napel
 */

package synthbio.simulator.test;

import org.junit.*;
import org.junit.Test;
import static org.junit.Assert.*;

import synthbio.simulator.SolverInterface;

/*
 * Testing the execution of the solver and catching the results.
 */
public class TestSolverInterface {

	/**
	 * Runs the solver and catches the output.
	 */
	@Test @Ignore
	public void testSolver() {
		SolverInterface si = new SolverInterface();
		String result = si.solve("test.sbml").getStringResult();
		assertEquals("FAIL", result);
	}
}