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
 
package synthbio.simulator;
 
import javax.xml.stream.XMLStreamException;

import synthbio.models.Circuit;
import synthbio.simulator.CircuitConverter;

import org.apache.commons.math.ode.DerivativeException;
import org.sbml.jsbml.Model;
import org.sbml.jsbml.SBMLException;
import org.sbml.jsbml.validator.ModelOverdeterminedException;
import org.sbml.jsbml.xml.stax.SBMLReader;
import org.simulator.math.odes.AbstractDESSolver;
import org.simulator.math.odes.EulerMethod;
import org.simulator.math.odes.MultiTable;
import org.simulator.sbml.SBMLinterpreter;

import java.util.ArrayList;

import org.json.*;

/**
 * A class for solving SBML-files and Model-objects.
 * @author Albert ten Napel
 */
public class Solver {

	/**
 	 * Converts a SBML-string to a Model.
 	 */
	public static Model sbmlToModel(String sbml)
	throws XMLStreamException, ModelOverdeterminedException, SBMLException, DerivativeException {
		return (new SBMLReader()).readSBMLFromString(sbml).getModel();
	}

	/**
 	 * Solves a Circuit.
 	 */
	public static MultiTable solve(Circuit c)
	throws XMLStreamException, ModelOverdeterminedException, SBMLException, DerivativeException {
		return solve(sbmlToModel(CircuitConverter.convert(c)), CircuitConverter.getInputs(c));	
	}

	/**
	 * Solves a Model-object with a certain stepsize and simulation length (only used in the tests)
	 * @param		model 		the Model-object to simulate.
	 * @param		stepSize 	the size of the timesteps
	 * @param		timeEnd 	the endtime of the simulation 
	 * @return 						A MultiTable-object containing the solution
	 */
	public static MultiTable solve(Model model, double stepSize, double timeEnd)
	throws ModelOverdeterminedException, SBMLException, DerivativeException {
		// Setup solver
		AbstractDESSolver solver = new EulerMethod();
		solver.setStepSize(stepSize);
		SBMLinterpreter interpreter = new SBMLinterpreter(model);
		solver.setStepSize(stepSize);
		if (solver instanceof AbstractDESSolver) {
			((AbstractDESSolver)solver).setIncludeIntermediates(false);
		}
		// Compute the solution
		MultiTable solution = solver.solve(interpreter, interpreter.getInitialValues(), 0d, timeEnd);
		
		return solution;
	}

	/**
 	 * solves a Model with specific inputs
 	 * @param		model 		the Model-object to simulate.
	 * @param		inputs		a MultiTable containing the inputs of the model (see CircuitConverter/getInputs)
	 * @return 						A MultiTable-object containing the solution
 	 */
	public static MultiTable solve(Model model, MultiTable inputs)
	throws ModelOverdeterminedException, SBMLException, DerivativeException {
		// Setup solver
		AbstractDESSolver solver = new EulerMethod();
		SBMLinterpreter interpreter = new SBMLinterpreter(model);
		if (solver instanceof AbstractDESSolver) {
			((AbstractDESSolver)solver).setIncludeIntermediates(false);
		}
		// Compute the solution
		MultiTable solution = solver.solve(interpreter, inputs.getBlock(0), interpreter.getInitialValues());
		return solution;
	}
}
 
