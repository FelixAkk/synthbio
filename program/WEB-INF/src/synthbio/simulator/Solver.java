/**
 * Project Zelula
 *
 * Contextproject TI2800 
 * TU Delft - University of Technology
 *
 * @author Albert ten Napel
 *
 * https://github.com/FelixAkk/synthbio
 */
 
package synthbio.simulator;
 
import org.apache.commons.math.ode.DerivativeException;
import org.sbml.jsbml.Model;
import org.sbml.jsbml.SBMLException;
import org.sbml.jsbml.validator.ModelOverdeterminedException;
import org.sbml.jsbml.xml.stax.SBMLReader;
import org.simulator.math.odes.AbstractDESSolver;
import org.simulator.math.odes.EulerMethod;
import org.simulator.math.odes.MultiTable;
import org.simulator.sbml.SBMLinterpreter;
 
public class Solver {

	/**
	 * Solves a SBML file.
	 */
	public String solveWithFile(String fileName, double stepSize, double timeEnd) {
		
	}

	/**
	 * Solves a Model-object.
	 */
	public String solve(String fileName, double stepSize, double timeEnd) {
		// Load SBML model
		Model model = (new SBMLReader()).readSBML(fileName).getModel();
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
	}
	
} 