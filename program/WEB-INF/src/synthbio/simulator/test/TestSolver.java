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

import java.awt.Dimension;

import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import org.sbml.jsbml.Model;
import org.sbml.jsbml.xml.stax.SBMLReader;
import org.simulator.math.odes.AbstractDESSolver;
import org.simulator.math.odes.EulerMethod;
import org.simulator.sbml.SBMLinterpreter;

/**
 * Testing Solver.
 * @author Albert ten Napel
 */
public class TestSolver {
	private final String not = "src/synthbio/simulator/test/not.xml";
	private final String nand = "src/synthbio/simulator/test/nand.xml";
	
	private final String tc1 = "src/synthbio/simulator/test/00001-sbml-l2v4.sbml";
	private final String tc2 = "src/synthbio/simulator/test/00002-sbml-l2v4.sbml";
	
	/**
	 * Testing one of the files included with the testsuite of SBMLsimulator.
	 */
	@Test
	public void tc1() throws XMLStreamException, IOException, ModelOverdeterminedException, SBMLException, DerivativeException {
		MultiTable solution = (new Solver()).solveWithFile(tc1, 1, 100);
		double s1 = solution.getColumn("S1").getValue(99);
		double s2 = solution.getColumn("S2").getValue(99);
		assertTrue(s2 > s1);
	}
	
	/**
	 * Testing one of the files included with the testsuite of SBMLsimulator.
	 */
	@Test
	public void tc2() throws XMLStreamException, IOException, ModelOverdeterminedException, SBMLException, DerivativeException {
		MultiTable solution = (new Solver()).solveWithFile(tc2, 1, 100);
		double s1 = solution.getColumn("S1").getValue(99);
		double s2 = solution.getColumn("S2").getValue(99);
		assertTrue(s2 > s1);
	}
	
	/**
	 * Testing a SBML-file containing a not-gate (A -> not -> B, where A is high).
	 */
	@Test
	public void testSBMLnot() throws XMLStreamException, IOException, ModelOverdeterminedException, SBMLException, DerivativeException {
		MultiTable solution = (new Solver()).solveWithFile(not, 0.1, 100);
		
		double a = solution.getColumn("a").getValue(99);
		double b = solution.getColumn("b").getValue(99);
		
		assertTrue(a > b);
	}
	
	/**
	 * Testing a SBML-file containing a nand-gate.
	 */
	@Test
	public void testSBMLnand() throws XMLStreamException, IOException, ModelOverdeterminedException, SBMLException, DerivativeException {
		MultiTable solution = (new Solver()).solveWithFile(nand, 0.1, 100);
		
		double c = solution.getColumn("c").getValue(4);
		double d = solution.getColumn("d").getValue(4);
		
		assertTrue(c > d);
	}
	
	/**
	 * A visual representation of the data for manual testing purposes
	 */
	private void showMultiTable(MultiTable solution) {
		JScrollPane resultDisplay = new JScrollPane(new JTable(solution));
		resultDisplay.setPreferredSize(new Dimension(800, 600));
		JOptionPane.showMessageDialog(null, resultDisplay, "Solution", JOptionPane.INFORMATION_MESSAGE);
	}
}