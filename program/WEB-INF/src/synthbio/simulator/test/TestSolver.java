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
import org.junit.Test;

import java.io.IOException;
import javax.xml.stream.XMLStreamException;
import org.sbml.jsbml.SBMLException;
import org.sbml.jsbml.validator.ModelOverdeterminedException;
import org.apache.commons.math.ode.DerivativeException;

import org.simulator.math.odes.MultiTable;
import synthbio.simulator.Solver;

/**
 * Testing Solver.
 * @author Albert ten Napel
 */
public class TestSolver {
	private final String sbmlfile1 = "src/synthbio/simulator/test/test.sbml";
	private final String sbmlfile2 = "src/synthbio/simulator/test/test2.sbml";
	
	/**
	 * test.sbml is not valid sbml.
	 */
	@Test
	public void testSBMLSolve() throws XMLStreamException, IOException, ModelOverdeterminedException, SBMLException, DerivativeException {
		MultiTable solution = (new Solver()).solveWithFile(sbmlfile1, 1, 5);
		System.out.println(solution.toString());
	}
	
	@Test
	public void testSBMLSolve2() throws XMLStreamException, IOException, ModelOverdeterminedException, SBMLException, DerivativeException {
		MultiTable solution = (new Solver()).solveWithFile(sbmlfile2, 1, 5);
		assertEquals(
			"[Time] [0.0, 1.0, 2.0, 3.0, 4.0, 5.0], [uVol, input, output, X4], [1.0, 16.0, 0.0, 14.0], [1.0, 16.0, 112.5927312, 14.0], [1.0, 16.0, 225.1854624, 14.0], [1.0, 16.0, 337.7781936, 14.0], [1.0, 16.0, 450.3709248, 14.0], [1.0, 16.0, 562.963656, 14.0]]",
			 solution.toString()
		);
	}
}