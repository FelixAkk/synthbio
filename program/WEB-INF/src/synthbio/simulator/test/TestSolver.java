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
import java.util.ArrayList;
import java.io.IOException;
import javax.xml.stream.XMLStreamException;
import org.sbml.jsbml.SBMLException;
import org.sbml.jsbml.validator.ModelOverdeterminedException;
import org.apache.commons.math.ode.DerivativeException;

import org.simulator.math.odes.MultiTable;
import synthbio.simulator.Solver;
import synthbio.simulator.CircuitConverter;

import java.awt.Dimension;
import java.util.Arrays;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import java.util.Collections;
import org.sbml.jsbml.Model;
import org.sbml.jsbml.xml.stax.SBMLReader;
import org.simulator.math.odes.AbstractDESSolver;
import org.simulator.math.odes.EulerMethod;
import org.simulator.sbml.SBMLinterpreter;
import org.json.JSONException;
import synthbio.models.CircuitException;
import synthbio.models.Circuit;
import synthbio.models.CircuitFactory;
import synthbio.Util;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
 
/**
 * Testing Solver.
 * @author Albert ten Napel
 */
public class TestSolver {
	private final String not = "data/test/simulator/not.sbml";
	private final String nand = "data/test/simulator/nand.sbml";
	
	private final String tc1 = "data/test/simulator/00001-sbml-l2v4.xml";
	private final String tc2 = "data/test/simulator/00002-sbml-l2v4.xml";

	private final String circ1 = "data/test/simulator/inputCircuit.syn";
	private final String de1 = "data/test/simulator/de1.syn";

	public static String convertFromFile(String filename) throws CircuitException, JSONException, IOException {
		return CircuitConverter.convert((new CircuitFactory()).fromJSON(Util.fileToString(filename)));
	}
	/**
 	 * Solve a Syn file
 	 */	
	private static MultiTable solveSyn(String fileName)
	throws XMLStreamException, IOException, ModelOverdeterminedException, SBMLException, DerivativeException, CircuitException, JSONException {
		return Solver.solve((new CircuitFactory()).fromJSON(Util.fileToString(fileName)));
	}

	/**
	 * Solves a SBML file.
	 */
	private static MultiTable solveSBML(String fileName, double stepSize, double timeEnd)
	throws XMLStreamException, IOException, ModelOverdeterminedException, SBMLException, DerivativeException {
		Model m = (new SBMLReader()).readSBML(fileName).getModel();
		return Solver.solve(m, stepSize, timeEnd);
	}
	
	/**
	 * Testing one of the files included with the testsuite of SBMLsimulator.
	 */
	@Test
	public void tc1() throws XMLStreamException, IOException, ModelOverdeterminedException, SBMLException, DerivativeException {
		MultiTable solution = solveSBML(tc1, 1, 100);
		double s1 = solution.getColumn("S1").getValue(99);
		double s2 = solution.getColumn("S2").getValue(99);
		assertThat(s2, is(greaterThan(s1)));
	}
	
	/**
	 * Testing one of the files included with the testsuite of SBMLsimulator.
	 */
	@Test
	public void tc2() throws XMLStreamException, IOException, ModelOverdeterminedException, SBMLException, DerivativeException {
		MultiTable solution = solveSBML(tc2, 1, 100);
		double s1 = solution.getColumn("S1").getValue(99);
		double s2 = solution.getColumn("S2").getValue(99);
		assertTrue(s2 > s1);
		assertThat(s2, is(greaterThan(s1)));
	}
	
	/**
	 * Testing a SBML-file containing a not-gate (A -> not -> B, where A is high).
	 */
	@Ignore
	@Test
	public void testSBMLnot() throws XMLStreamException, IOException, ModelOverdeterminedException, SBMLException, DerivativeException {
		MultiTable solution = solveSBML(not, 10, 1000);
		showMultiTable(solution);
		double a = solution.getColumn("a").getValue(99);
		double b = solution.getColumn("b").getValue(99);
		assertThat(b, is(greaterThan(a)));
	}
	
	/**
	 * Testing a SBML-file containing a nand-gate.
	 */
	@Test
	public void testSBMLnand() throws XMLStreamException, IOException, ModelOverdeterminedException, SBMLException, DerivativeException {
		MultiTable solution = solveSBML(nand, 0.1, 100);
		double c = solution.getColumn("c").getValue(99);
		double d = solution.getColumn("d").getValue(99);
		assertThat(c, is(greaterThan(d)));
	}

	/**
 	 * Testing the solving of a Syn file.
 	 */	
	@Test
	public void testCircuit() throws XMLStreamException, IOException, ModelOverdeterminedException, SBMLException, DerivativeException, CircuitException, JSONException {	
		MultiTable solution = solveSyn(circ1);
		double a = solution.getColumn("A").getValue(39);
		double d = solution.getColumn("D").getValue(39);
		assertThat(d, is(lessThan(a)));
	}

	/**
 	 * Testing the solving of a Syn file, testing if the degradation works.
 	 */	
	@Test
	public void testCircuit2() throws XMLStreamException, IOException, ModelOverdeterminedException, SBMLException, DerivativeException, CircuitException, JSONException {	
		MultiTable solution = solveSyn(de1);
		double in1 = solution.getColumn("A").getValue(30);
		double out1 = solution.getColumn("B").getValue(30);
		double in2 = solution.getColumn("A").getValue(70);
		double out2 = solution.getColumn("B").getValue(70);
		double in3 = solution.getColumn("A").getValue(110);
		double out3 = solution.getColumn("B").getValue(110);
		assertThat(in1, is(lessThan(out1)));
		assertThat(in2, is(greaterThan(out2)));
		assertThat(in3, is(lessThan(out3)));
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
