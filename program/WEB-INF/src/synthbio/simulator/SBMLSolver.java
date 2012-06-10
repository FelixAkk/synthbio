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
import java.util.Arrays;

import org.json.*;

/**
 * A class for solving SBML-files and Model-objects.
 * @author Albert ten Napel
 */
public class SBMLSolver implements Solver {
	
	Circuit circuit = null;

	MultiTable inputs;
	
	MultiTable result;
	
	public SBMLSolver(Circuit c){
		this.circuit = c;
	}
	
	public Circuit getCircuit() {
		assert this.circuit != null;
		return this.circuit;
	}
	
	public String getSBML(){
		return CircuitConverter.convert(this.getCircuit());
	}

	public JSONObject toJSON() throws JSONException {
		double[] timePoints = this.result.getTimePoints();
		
		double timeLength = timePoints.length;
		double step = timePoints[1] - timePoints[0];
		
		// get all names
		int cc = this.result.getColumnCount() - 3; // - 3 because gene, cell and empty are unused.
		ArrayList<String> names = new ArrayList<String>(cc);
		for(int i = 0; i < cc + 3; i++) {
			String cur = this.result.getColumnName(i);
			if(!cur.equals("gene") && !cur.equals("cell") && !cur.equals("empty") && !cur.equals("Time")){
				names.add(cur);
			}
		}

		// creating the JSON object
		JSONObject json = new JSONObject();
		json.put("solver", "SBMLSimulator");
		json.put("names", new JSONArray(names));
		json.put("length", timeLength);
		json.put("step", step);
		json.put("time", Arrays.asList(timePoints));
		
		// for every name, get the data in the column of that name.
		JSONObject data = new JSONObject();
		for(String name: names) {
			ArrayList<Double> cur = new ArrayList<Double>((int)timeLength);
			for(Double d: this.result.getColumn(name)) {
				cur.add(d);
			}
			data.put(name, new JSONArray(cur));
		}
		json.put("data", data);

		return json;
	}

	public MultiTable getResult(){
		return this.result;
	}
	/**
 	 * Solves a Circuit.
 	 */
	public void solve() throws XMLStreamException, ModelOverdeterminedException, DerivativeException {
		this.result = solve(
			sbmlToModel(this.getSBML()),
			circuit.getSimulationSetting().getInputMultiTable()
		);
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
	
	/**
 	 * Converts a SBML-string to a jsbml Model.
 	 */
	public static Model sbmlToModel(String sbml) throws XMLStreamException, ModelOverdeterminedException, SBMLException, DerivativeException {
		return (new SBMLReader()).readSBMLFromString(sbml).getModel();
	}
}
 
