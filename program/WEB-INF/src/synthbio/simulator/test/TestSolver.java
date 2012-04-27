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

import org.junit.Test;

import synthbio.simulator.Solver;

/**
 * Testing Solver.
 * @author Albert ten Napel
 */
public class TestSolver {
	private final String sbmlfile1 = "src/synthbio/simulator/test.sbml";
	
	@Test
	public void testSBMLSolve() {
		Solver s = new Solver();
		MultiTable solution = s.solveWithFile(sbmlfile1);
		System.out.println(solution.toString());
	}
}