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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.HashSet;

/**
 * A class for converting Circuit-objects to SBML.
 * @author Albert ten Napel
 */
public class CircuitConverter {
	// Needed for the Reaction-class, but Java doesn't like Enums in inner classes.
	private static enum ReactionType {Transcription, Translation}
	
	// The header and trailer that has to be included in every SBML file.
	private final String header =
		"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
		"<sbml xmlns=\"http://www.sbml.org/sbml/level2/version4\" level=\"2\" version=\"4\">"+
		"\t<model>\n"+
		"\t\t<listOfUnitDefinitions>\n"+
		"\t\t\t<unitDefinition id=\"substance\">\n"+
		"\t\t\t\t<listOfUnits>\n"+
		"\t\t\t\t\t<unit kind=\"mole\"/>\n"+
		"\t\t\t\t</listOfUnits>\n"+
		"\t\t\t</unitDefinition>\n"+
		"\t\t\t<unitDefinition id=\"time\">\n"+
		"\t\t\t\t<listOfUnits>\n"+
		"\t\t\t\t\t<unit kind=\"second\"/>\n"+
		"\t\t\t\t</listOfUnits>\n"+
		"\t\t\t</unitDefinition>\n"+
		"\t\t</listOfUnitDefinitions>\n"+
		"\t\t<listOfCompartments>\n"+
		"\t\t\t<compartment id=\"cell\" size=\"1\" units=\"volume\"/>\n"+
		"\t\t</listOfCompartments>\n"+
		"\t\t<listOfSpecies>\n"+
		"\t\t\t<species id=\"gene\" compartment=\"cell\" initialAmount=\"3\" hasOnlySubstanceUnits=\"true\" boundaryCondition=\"true\" constant=\"true\"/>\n"+
		"\t\t\t<species id=\"empty\" compartment=\"cell\" initialAmount=\"0\"/>\n";
	private final String trailer =
		"\t\t</listOfReactions>\n"+
		"\t</model>\n"+
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
			reactions.add(new Reaction(ReactionType.Transcription, kind, inputs, 'm'+output));
			
			// Second reaction: translation
			// From mRNA of output protein to output protein.
			reactions.add(new Reaction(ReactionType.Translation, kind, 'm'+output, output));
			
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
			r += speciesString(s, 0d);
		r += "\t\t</listOfSpecies>\n";
		// add the reactions
		for(Reaction reaction: reactions)
			r += reaction.getSBMLString();
		
		return r+trailer;
	}
	
	private String speciesString(String species, double amount) {
		return "<species id=\""+species+"\" compartment=\"cell\" initialAmount=\""+amount+"\" substanceUnits=\"substance\"/>\n";
	}
	
	/**
	 * A class that represents a single Reaction.
	 */
	private class Reaction {		
		private String gate;
		private ReactionType type;
		private List<String> fromProteins;
		private String toProtein;
		
		// multiple input constructor
		public Reaction(ReactionType type, String gate, List<String> fromProteins, String toProtein) {
			this.type = type;
			this.fromProteins = fromProteins;
			this.toProtein = toProtein;
			this.gate = gate;
		}
		
		// single input constructor
		public Reaction(ReactionType type, String gate, String fromProtein, String toProtein) {
			this.type = type;
			this.fromProteins = new ArrayList<String>();
			fromProteins.add(fromProtein);
			this.toProtein = toProtein;
			this.gate = gate;
		}
		
		/**
		 * Returns the reaction id of this Reaction.
		 * this id is of the form: ReactionType_gate_inputs__output
		 * for example: Transcription_and_A_B__C
		 */
		private String getReactionId() {
			String r = type + "_" + gate + "_";
			for(String p: fromProteins)
				r += p + "_";
			r += "_" + toProtein;
			return r;
		}
		
		private String listOfInvolved() {
			String r = "";
			
			// Reactants
			// with transcription this is gene.
			// with translation this is mrna of input (translation has only one input).
			r +=
				"\t\t\t\t<listOfReactants>\n"+
				"\t\t\t\t\t<speciesReference species=\"" + (type == ReactionType.Transcription? "gene": "m" + fromProteins.get(0)) + "\"/>\n"+
				"\t\t\t\t</listOfReactants>\n";
			// Products
			// with transcription this is mrna of output
			// with translation this is output
			r +=
				"\t\t\t\t<listOfProducts>\n"+
				"\t\t\t\t\t<speciesReference species=\"" + (type == ReactionType.Transcription? "m": "") + toProtein + "\"/>\n"+
				"\t\t\t\t</listOfProducts>\n";
			// Modifiers
			// with transcription this is the inputs
			// with translation this is empty
			if(type == ReactionType.Transcription) {
				r += "\t\t\t\t<listOfModifiers>\n";
				for(String in: fromProteins)
					r += "\t\t\t\t\t<speciesReference species=\"" + in + "\"/>\n";
				r += "\t\t\t\t</listOfModifiers>";
			}
			
			return r + "\n";
		}
		
		private String kineticLaw() {
			String r = "\t\t\t\t<kineticLaw>\n";
			
			// the parameters
			r += "\t\t\t\t\t<listOfParameters>\n";
			
			r += "\t\t\t\t\t</listOfParameters>\n";
			
			// the math
			r += "\t\t\t\t\t<math xmlns=\"http://www.w3.org/1998/Math/MathML\">\n";
			
			r += "\t\t\t\t\t</math>\n";
			
			return r + "\t\t\t\t</kineticLaw>\n";
		}
		
		/**
		 * Returns the SBML string of this Reaction.
		 */
		public String getSBMLString() {
			String s = "\t\t\t<reaction id=\"" + getReactionId() + "\" reversible=\"false\" fast=\"false\">\n";
			s += listOfInvolved();
			s += kineticLaw();
			return s + "\t\t\t</reaction>\n";
		}
	}
}
