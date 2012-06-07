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

import synthbio.models.*;
import synthbio.simulator.Reaction;

import static synthbio.Util.tabs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.HashSet;
import java.util.Set;

import org.simulator.math.odes.MultiTable;

/**
 * A class for converting Circuit-objects to SBML.
 * @author Albert ten Napel
 */
public class CircuitConverter {	
	// The header and trailer that has to be included in every SBML file.
	private static final String header =
		"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
		"<sbml xmlns=\"http://www.sbml.org/sbml/level2/version4\" level=\"2\" version=\"4\">\n"+
			tabs(1)+"<model>\n"+
				tabs(2)+"<listOfCompartments>\n"+
					tabs(3)+"<compartment id=\"cell\" size=\"1\" units=\"volume\"/>\n"+
				tabs(2)+"</listOfCompartments>\n"+
				tabs(2)+"<listOfSpecies>\n"+
					tabs(3)+"<species id=\"gene\" compartment=\"cell\" initialConcentration=\"0\" boundaryCondition=\"true\"/>\n";
	private static final String trailer =
			tabs(2)+"</listOfReactions>\n"+
		tabs(1)+"</model>\n"+
		"</sbml>";
	
	/**
	 * Converts a Circuit-object to SBML.
	 * @param		circuit	The Circuit-object to convert.
	 * @return					The SBML-formatted string.
	 */
	public static String convert(Circuit circuit) {
		// First retrieve all gates
		Collection<Gate> gates = circuit.getGates();
		
		// reactions
		ArrayList<Reaction> reactions = new ArrayList<Reaction>(gates.size());
		// set of all the species
		HashSet<String> species = new HashSet<String>();
		
		// Create the reactions
		for(Gate g: gates) {
			String kind = g.getKind();
			List<String> inputs = g.getInputs();
			String output = g.getOutput();
			
			// First reaction: transcription
			// From input proteins to mRNA of output protein.
			Reaction transcription = new Reaction(kind, inputs, 'm'+output);
			transcription.setTypeToTranscription();
			// transcription needs the parameters: k1, km, n, d1
			Promotor prom = g.getPromotor();
			transcription.setParameters(prom.getK1(), prom.getKm(), prom.getN());
			// add the reaction
			reactions.add(transcription);
			// add the degradation reactions
			Reaction deg = new Reaction('m'+output);
			deg.setParameters(g.getCDS().getD1());
			reactions.add(deg);	
			
			// Second reaction: translation
			// From mRNA of output protein to output protein.
			Reaction translation = new Reaction(kind, 'm'+output, output);
			translation.setTypeToTranslation();
			// translation needs the parameters: k2, d2
			CDS c = g.getCDS();
			translation.setParameters(c.getK2());
			// add the reaction
			reactions.add(translation);
			// add the degradation reactions
			deg = new Reaction(output);
			deg.setParameters(c.getD2());
			reactions.add(deg);

			// Add new species to the set (if there are any)
			for(String s: inputs) {
				species.add(s);
			}
			species.add(output);
			species.add("m"+output);
		}
		
		// time to create the SBML-string
		String r = header;
		// add all the species
		Set<String> circInputs = circuit.getInputs();
		for(String s: species) {
			r += tabs(3) + speciesString(s, 0d, circInputs.contains(s));
		}
		r += tabs(2) + "</listOfSpecies>\n";
		// add the reactions
		r += tabs(2) + "<listOfReactions>\n";
		for(Reaction reaction: reactions) {
			r += reaction.getSBMLString();
		}
		
		return r+trailer;
	}
	
	/**
 	 * Returns a species definition of the given species, as used in SBML.
 	 */ 	
	private static String speciesString(String species, double amount, boolean input) {
		return "<species id=\""+species+"\" compartment=\"cell\" initialConcentration=\""+amount+"\" boundaryCondition=\""+input+"\" constant=\""+input+"\"/>\n";
	}
	
	/**
	 * Returns a MultiTable that contains the (simulation)inputs of the circuits.
	 */
	public static MultiTable getInputs(Circuit circuit) {
		double lowLevel = circuit.getSimulationLowLevel();
		double highLevel = circuit.getSimulationHighLevel();
		double ticks = circuit.getSimulationTickWidth();
		int length = circuit.getSimulationLength();
		// setup time points
		double[] timePoints = new double[length];
		for(int i = 0; i < length; i++)
			timePoints[i] = i*ticks;
		// setup names
		String[] names = circuit.getInputs().toArray(new String[circuit.getInputs().size()]);
		// setup data
		double[][] data = new double[length][names.length];
		for(int iName = 0; iName < names.length; iName++) {
			// get inputs
			String val = circuit.getSimulationInput(names[iName]);
			for(int iTime = 0; iTime < length; iTime++) {
				// current char (H or L)
				char cur = (val.length() > iTime? val.charAt(iTime): val.charAt(val.length()-1));
				// set low or high
				data[iTime][iName] = (cur == 'L'? lowLevel: highLevel);
			}
		}
		return new MultiTable(timePoints, data, names);
	}
}

