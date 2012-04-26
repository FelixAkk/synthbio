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
	private final String TEST_PATH = "src/synthbio/simulator/test/";
	/**
	 * Runs the solver and catches the output.
	 */
	@Test @Ignore
	public void testSolver() {
		SolverInterface si = new SolverInterface();
		String res = si.solve(TEST_PATH+"test2.sbml", TEST_PATH+"test2-time.csv", TEST_PATH+"test2-opt.sbml", TEST_PATH+"test2-output.csv");
		System.out.println(res);
	}
}