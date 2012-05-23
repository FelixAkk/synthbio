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
 
import java.io.IOException;
import javax.xml.stream.XMLStreamException;

import synthbio.models.Circuit;
import synthbio.models.CircuitException;
import synthbio.models.CircuitFactory;
import org.json.JSONException;
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
import synthbio.Util;
import java.util.ArrayList;
import java.util.Collections;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONString;

/**
 * A class for solving SBML-files and Model-objects.
 * @author Albert ten Napel
 */
public class Solver {
	/**
	 * Solves a .syn file.
	 */
	public static MultiTable solveWithSynFile(String fileName, double stepSize, double timeEnd)
	throws XMLStreamException, IOException, ModelOverdeterminedException, SBMLException, DerivativeException, CircuitException, JSONException {
		// Convert the SBML-file to a Model-object.
		String sbml = (new CircuitConverter()).convertFromFile(fileName);
		return solveSBML(sbml, stepSize, timeEnd);
	}

	public static MultiTable solveWithSynFile(String fileName)
	throws XMLStreamException, IOException, ModelOverdeterminedException, SBMLException, DerivativeException, CircuitException, JSONException {
		// Convert the SBML-file to a Model-object.
		Circuit c = (new CircuitFactory()).fromJSON(Util.fileToString(fileName));
		return solve(c);
	}

	/**
	 * Solves a .syn String.
	 */
	public static MultiTable solve(String syn, double stepSize, double timeEnd)
	throws XMLStreamException, IOException, ModelOverdeterminedException, SBMLException, DerivativeException, CircuitException, JSONException {
		String sbml = (new CircuitConverter()).convert(syn);
		return solveSBML(sbml, stepSize, timeEnd);
	}

	/**
	 * Solves a Circuit-object.
	 */
	public static MultiTable solve(Circuit c, double stepSize, double timeEnd)
	throws XMLStreamException, IOException, ModelOverdeterminedException, SBMLException, DerivativeException, CircuitException, JSONException {
		String sbml = (new CircuitConverter()).convert(c);
		return solveSBML(sbml, stepSize, timeEnd);
	}

	/**
 	 * Solves a Circuit-object.
 	 */
	public static MultiTable solve(Circuit c)
	throws XMLStreamException, IOException, ModelOverdeterminedException, SBMLException, DerivativeException, CircuitException, JSONException {
		CircuitConverter cc = new CircuitConverter();
		String sbml = cc.convert(c);
		MultiTable input = cc.getInputs(c);
		return solve(sbmlToModel(sbml), input);	
	}

	/**
 	 * Converts a SBML-string to a Model.
 	 */
	public static Model sbmlToModel(String sbml)
	throws XMLStreamException, IOException, ModelOverdeterminedException, SBMLException, DerivativeException {
		Model model = (new SBMLReader()).readSBMLFromString(sbml).getModel();
		return model;
	}
	
	/**
	 * Solve SBML String.
	 */
	public static MultiTable solveSBML(String sbml, double stepSize, double timeEnd)
	throws XMLStreamException, IOException, ModelOverdeterminedException, SBMLException, DerivativeException {
		// Convert the SBML-string to a Model-object.
		Model model = (new SBMLReader()).readSBMLFromString(sbml).getModel();
		return solve(model, stepSize, timeEnd);
	}

	/**
	 * Solves a SBML file.
	 * @param		fileName	The location of the SBML-file
	 * @param		stepSize	the size of the timesteps
	 * @param		timeEnd		the amount of time to simulate 
	 * @return						A MultiTable-object containing the solution
	 */
	public static MultiTable solveSBMLFile(String fileName, double stepSize, double timeEnd)
	throws XMLStreamException, IOException, ModelOverdeterminedException, SBMLException, DerivativeException {
		// Convert the SBML-file to a Model-object.
		Model model = (new SBMLReader()).readSBML(fileName).getModel();
		return solve(model, stepSize, timeEnd);
	}

	/**
	 * Solves a Model-object.
	 * @param		model 		the Model-object to simulate.
	 * @param		stepSize 	the size of the timesteps
	 * @param		timeEnd 	the amount of time to simulate 
	 * @return 						A MultiTable-object containing the solution
	 */
	public static MultiTable solve(Model model, double stepSize, double timeEnd)
	throws XMLStreamException, IOException, ModelOverdeterminedException, SBMLException, DerivativeException {
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
 	 */
	public static MultiTable solve(Model model, MultiTable inputs)
	throws XMLStreamException, IOException, ModelOverdeterminedException, SBMLException, DerivativeException {
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

	 
	/* 
 	 * Converts a MultiTable to a JSON-string of the format:
 	 * 	{
 	 * 		"columns": [...],
 	 * 		"data": [[...],...]
 	 * 	}
 	 *
 	 * 	this format was not useful enough scroll down for the better method.
 	 *
	public String multiTableToJSON(MultiTable m) { 
		StringBuffer json = new StringBuffer("{\n\t\"columns\": ");		
		
		// get all names
		int cc = m.getColumnCount();
		ArrayList<String> names = new ArrayList<String>();
		for(int i = 0; i < cc; i++)
			names.add(m.getColumnName(i));
		names.remove("gene");
		names.remove("cell");
		names.remove("empty");
		names.remove("Time");
		Collections.sort(names);
		names.add(0, "Time");
		
		// add names to json	
		json.append("[" + names.get(0));
		for(int i = 1; i < names.size(); i++)
			json.append(", " + names.get(i));		
		json.append("],\n");

		// add data
		json.append("\t\"data\": [\n");
		int simLength = m.getTimePoints().length;	
		for(int i = 0; i < simLength; i++) {
			json.append("\t\t[");
			for(String name: names) {
				if(name.equals("Time")) {
					json.append(m.getTimePoints()[i] + ", ");
				} else {
					json.append(m.getColumn(name).getValue(i) + ", ");
				}
			}
			json.delete(json.length()-2, json.length());
			json.append("],\n");	
		}
		json.delete(json.length()-2, json.length()-1);
		json.append("\t]\n}");
		return json.toString();
	}
	*/
	
	/**
 	 * Converts a MultiTable to a JSON-string of the format:
 	 * 	{
 	 * 		"names": [Time, A, B],
 	 * 		"length": 10,
 	 * 		"step": 1,
 	 * 		"data": {
 	 *			"Time": [1, 2, 3, 4, 5, 6, 7, 8, 9, 10],
 	 *			"A": [0, 0, 0, 0, 0, 600, 600, 600, 600, 600],
 	 *			"B": [...]
 	 * 		}
 	 * 	}
 	 */
	public static String multiTableToJSON(MultiTable m) {
		double[] timePoints = m.getTimePoints();
		double timeLength = timePoints.length;
		double step = 1;
		
		// get all names
		int cc = m.getColumnCount() - 3; // - 3 because gene, cell and empty are unused.
		ArrayList<String> names = new ArrayList<String>(cc);
		for(int i = 0; i < cc + 3; i++) {
			String cur = m.getColumnName(i);
			if(!cur.equals("gene") && !cur.equals("cell") && !cur.equals("empty"))
				names.add(cur);
		}

		JSONObject r = new JSONObject();
		try {
			r.put("names", new JSONArray(names));
			r.put("length", timeLength);
			r.put("step", step);
			JSONObject data = new JSONObject();
			for(String name: names) {
				ArrayList<Double> cur = new ArrayList<Double>((int)timeLength);
				if(name.equals("Time")) {
					for(Double d: timePoints)
						cur.add(d);
				} else {
					for(Double d: m.getColumn(name))
						cur.add(d);
				}
				data.put(name, new JSONArray(cur));
				
			}
			r.put("data", data);
		} catch(Exception e) {
			return "{\"error\":\"JSONException:"+e.getMessage()+"\"}";
		}

		return r.toString();	
	}
}
 
