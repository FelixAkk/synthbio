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

import synthbio.models.Circuit;
import synthbio.models.CircuitException;
import synthbio.models.Gate;
import synthbio.models.CDS;
import synthbio.models.Promotor;
import synthbio.simulator.Reaction;
import static synthbio.Util.tabs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.HashSet;

/**
 * A class for converting Circuit-objects to SBML.
 * @author Albert ten Napel
 */
public class CircuitConverter {	
	// The header and trailer that has to be included in every SBML file.
	private final String header =
		"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
		"<sbml xmlns=\"http://www.sbml.org/sbml/level2/version4\" level=\"2\" version=\"4\">\n"+
			tabs(1)+"<model>\n"+
				tabs(2)+"<listOfUnitDefinitions>\n"+
					tabs(3)+"<unitDefinition id=\"substance\">\n"+
						tabs(4)+"<listOfUnits>\n"+
							tabs(5)+"<unit kind=\"mole\"/>\n"+
						tabs(4)+"</listOfUnits>\n"+
					tabs(3)+"</unitDefinition>\n"+
					tabs(3)+"<unitDefinition id=\"time\">\n"+
						tabs(4)+"<listOfUnits>\n"+
							tabs(5)+"<unit kind=\"second\"/>\n"+
						tabs(4)+"</listOfUnits>\n"+
					tabs(3)+"</unitDefinition>\n"+
				tabs(2)+"</listOfUnitDefinitions>\n"+
				tabs(2)+"<listOfCompartments>\n"+
					tabs(3)+"<compartment id=\"cell\" size=\"1\" units=\"volume\"/>\n"+
				tabs(2)+"</listOfCompartments>\n"+
				tabs(2)+"<listOfSpecies>\n"+
					tabs(3)+"<species id=\"gene\" compartment=\"cell\" initialAmount=\"3\" hasOnlySubstanceUnits=\"true\" boundaryCondition=\"true\" constant=\"true\"/>\n"+
					tabs(3)+"<species id=\"empty\" compartment=\"cell\" initialAmount=\"0\"/>\n";
	private final String trailer =
			tabs(2)+"</listOfReactions>\n"+
		tabs(1)+"</model>\n"+
		"</sbml>";
	
	/**
	 * Converts a Circuit-object to SBML.
	 * @param		circuit	The Circuit-object to convert.
	 * @return					The SBML-formatted string.
	 */
	public String convert(Circuit circuit) {
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
			transcription.setParameters(prom.getK1(), prom.getKm(), prom.getN(), g.getCDS().getD1());
			// add the reaction
			reactions.add(transcription);
			
			// Second reaction: translation
			// From mRNA of output protein to output protein.
			Reaction translation = new Reaction(kind, 'm'+output, output);
			translation.setTypeToTranslation();
			// translation needs the parameters: k2, d2
			CDS c = g.getCDS();
			translation.setParameters(c.getK2(), c.getD2());
			// add the reaction
			reactions.add(translation);
			
			// Add new species to the set (if there are any)
			for(String s: inputs)
				species.add(s);
			species.add(output);
			species.add("m"+output);
		}
		
		// time to create the SBML-string
		String r = header;
		// add all the species
		for(String s: species)
			r += tabs(3) + speciesString(s, 0d);
		r += tabs(2) + "</listOfSpecies>\n";
		// add the reactions
		r += tabs(2) + "<listOfReactions>\n";
		for(Reaction reaction: reactions)
			r += reaction.getSBMLString();
		
		return r+trailer;
	}
	
	private String speciesString(String species, double amount) {
		return "<species id=\""+species+"\" compartment=\"cell\" initialAmount=\""+amount+"\" substanceUnits=\"substance\"/>\n";
	}
}
