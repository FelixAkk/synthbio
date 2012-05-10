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
		"<sbml xmlns=\"http://www.sbml.org/sbml/level2/version4\" level=\"2\" version=\"4\">\n"+
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
	 * Helpful method for getting a string of tabs
	 */
	private String tabs(int n) {
		String r = "";
		if(n > 0) {
			for(int i = 0; i < n; i++)
				r += "\t";
		}
		return r;
	}
	
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
			Reaction transcription = new Reaction(ReactionType.Transcription, kind, inputs, 'm'+output);
			// transcription needs the parameters: k1, km, n, d1
			Promotor prom = g.getPromotor();
			transcription.setParameters(prom.getK1(), prom.getKm(), prom.getN(), g.getCDS().getD1());
			// add the reaction
			reactions.add(transcription);
			
			// Second reaction: translation
			// From mRNA of output protein to output protein.
			Reaction translation = new Reaction(ReactionType.Translation, kind, 'm'+output, output);
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
			r += "\t\t\t" + speciesString(s, 0d);
		r += "\t\t</listOfSpecies>\n";
		// add the reactions
		r += "\t\t<listOfReactions>\n";
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
		
		// parameters contains the paramters for this reaction
		// for transcription these are [k1, km, n and d1]
		// for translation these are [k2, d2]
		private double[] parameters;
		
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
		
		// setParameters for transcription: k1, Km, n and d1
		public void setParameters(double k1, double km, double n, double d1) {
			parameters = new double[] {k1, km, n, d1};
		}
		
		// setParameters for translation: k2 and d2
		public void setParameters(double k2, double d2) {
			parameters = new double[] {k2, d2};
		}
		
		public double[] getParameters() {
			return parameters;
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
				"\t\t\t\t\t<speciesReference species=\"" + (type == ReactionType.Transcription? "gene": fromProteins.get(0)) + "\"/>\n"+
				"\t\t\t\t</listOfReactants>\n";
			// Products
			// with transcription this is mrna of output
			// with translation this is output
			r +=
				"\t\t\t\t<listOfProducts>\n"+
				"\t\t\t\t\t<speciesReference species=\"" + toProtein + "\"/>\n"+
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
			// transcription has:
			//		k1, Km and n of the input
			//		d1 of output
			// translation has:
			//		k2 of input
			//		d2 of output
			r += "\t\t\t\t\t<listOfParameters>\n";
			if(type == ReactionType.Transcription) {
				r += "\t\t\t\t\t\t<parameter id=\"k1\" value=\"" + parameters[0] + "\" units=\"substance\"/>\n";
				r += "\t\t\t\t\t\t<parameter id=\"km\" value=\"" + parameters[1] + "\" units=\"substance\"/>\n";
				r += "\t\t\t\t\t\t<parameter id=\"n\" value=\"" + parameters[2] + "\" units=\"substance\"/>\n";
				r += "\t\t\t\t\t\t<parameter id=\"d1\" value=\"" + parameters[3] + "\" units=\"substance\"/>\n";
			} else {
				r += "\t\t\t\t\t\t<parameter id=\"k2\" value=\"" + parameters[0] + "\" units=\"substance\"/>\n";
				r += "\t\t\t\t\t\t<parameter id=\"d2\" value=\"" + parameters[1] + "\" units=\"substance\"/>\n";
			}
			r += "\t\t\t\t\t</listOfParameters>\n";
			
			// the math
			r += "\t\t\t\t\t<math xmlns=\"http://www.w3.org/1998/Math/MathML\">\n";
			
			// adding a comment to indicate the reaction formula
			if(type == ReactionType.Transcription) {
				if(gate.equals("not")) {
					r +=
						"\t\t\t\t\t<!-- (k1*km^n/km^n+" + fromProteins.get(0) + "^n)-d1*" + toProtein + " -->\n"+
						"\t\t\t\t\t\t<apply>\n"+
						"\t\t\t\t\t\t\t<minus/>\n"+
						"\t\t\t\t\t\t\t\t<apply>\n"+
						"\t\t\t\t\t\t\t\t\t<divide/>\n"+
						"\t\t\t\t\t\t\t\t\t\t<apply>\n"+
						"\t\t\t\t\t\t\t\t\t\t\t<times/>\n"+
						"\t\t\t\t\t\t\t\t\t\t\t<ci>k1</ci>\n"+
						"\t\t\t\t\t\t\t\t\t\t\t<apply>\n"+
						"\t\t\t\t\t\t\t\t\t\t\t\t<power/>\n"+
						"\t\t\t\t\t\t\t\t\t\t\t\t<ci>km</ci>\n"+
						"\t\t\t\t\t\t\t\t\t\t\t\t<ci>n</ci>\n"+
						"\t\t\t\t\t\t\t\t\t\t\t</apply>\n"+
						"\t\t\t\t\t\t\t\t\t\t</apply>\n"+
						"\t\t\t\t\t\t\t\t\t\t<apply>\n"+
						"\t\t\t\t\t\t\t\t\t\t\t<plus/>\n"+
						"\t\t\t\t\t\t\t\t\t\t\t\t<apply>\n"+
						"\t\t\t\t\t\t\t\t\t\t\t\t\t<power/>\n"+
						"\t\t\t\t\t\t\t\t\t\t\t\t\t<ci>km</ci>\n"+
						"\t\t\t\t\t\t\t\t\t\t\t\t\t<ci>n</ci>\n"+
						"\t\t\t\t\t\t\t\t\t\t\t\t</apply>\n"+
						"\t\t\t\t\t\t\t\t\t\t\t\t<apply>\n"+
						"\t\t\t\t\t\t\t\t\t\t\t\t\t<power/>\n"+
						"\t\t\t\t\t\t\t\t\t\t\t\t\t<ci>" + fromProteins.get(0) + "</ci>\n"+
						"\t\t\t\t\t\t\t\t\t\t\t\t\t<ci>n</ci>\n"+
						"\t\t\t\t\t\t\t\t\t\t\t\t</apply>\n"+
						"\t\t\t\t\t\t\t\t\t\t\t</apply>\n"+
						"\t\t\t\t\t\t\t\t\t\t</apply>\n"+
						"\t\t\t\t\t\t\t\t\t\t<apply>\n"+
						"\t\t\t\t\t\t\t\t\t\t\t<times/>\n"+
						"\t\t\t\t\t\t\t\t\t\t\t<ci>d1</ci>\n"+
						"\t\t\t\t\t\t\t\t\t\t\t<ci>" + toProtein + "</ci>\n"+
						"\t\t\t\t\t\t\t\t\t\t</apply>\n"+
						"\t\t\t\t\t\t\t\t\t</apply>\n";
				} else if(gate.equals("and")) {
					String temp = fromProteins.get(0) + "*" + fromProteins.get(1);
					r +=
						tabs(5)+"<!-- (k1(" + temp + ")^n/km^n+(" + temp + ")^n)-d1*" + toProtein + " -->\n"+
							tabs(6)+"<apply>\n"+
								tabs(7)+"<minus/>\n"+
								tabs(7)+"<apply>\n"+
									tabs(8)+"<divide/>\n"+
									tabs(8)+"<apply>\n"+
										tabs(9)+"<times/>\n"+
										tabs(9)+"<ci>k1</ci>\n"+
										tabs(9)+"<apply>\n"+
											tabs(10)+"<power/>\n"+
											tabs(10)+"<apply>\n"+
												tabs(11)+"<times/>\n"+
												tabs(11)+"<ci>" + fromProteins.get(0) + "</ci>\n"+
												tabs(11)+"<ci>" + fromProteins.get(1) + "</ci>\n"+
											tabs(10)+"</apply>\n"+
											tabs(10)+"<ci>n</ci>\n"+
										tabs(9)+"</apply>\n"+
									tabs(8)+"</apply>\n"+
									tabs(8)+"<apply>\n"+
										tabs(9)+"<plus/>\n"+
										tabs(9)+"<apply>\n"+
											tabs(10)+"<power/>\n"+
											tabs(10)+"<ci>km</ci>\n"+
											tabs(10)+"<ci>n</ci>\n"+
										tabs(9)+"</apply>\n"+
										tabs(9)+"<apply>\n"+
											tabs(10)+"<power/>\n"+
											tabs(10)+"<apply>\n"+
												tabs(11)+"<times/>\n"+
												tabs(11)+"<ci>" + fromProteins.get(0) + "</ci>\n"+
												tabs(11)+"<ci>" + fromProteins.get(1) + "</ci>\n"+
											tabs(10)+"</apply>\n"+
											tabs(10)+"<ci>n</ci>\n"+
										tabs(9)+"</apply>\n"+
									tabs(8)+"</apply>\n"+
								tabs(7)+"</apply>\n"+
								tabs(7)+"<apply>\n"+
									tabs(8)+"<times/>\n"+
									tabs(8)+"<ci>d1</ci>\n"+
									tabs(8)+"<ci>" + toProtein + "</ci>\n"+
								tabs(7)+"</apply>\n"+
							tabs(6)+"</apply>\n";
				}
			} else {
				r +=
					"\t\t\t\t\t<!-- k2*" + fromProteins.get(0) + " - d2*" + toProtein + " -->\n"+
					"\t\t\t\t\t\t<apply>\n"+
					"\t\t\t\t\t\t\t<minus/>\n"+
					"\t\t\t\t\t\t\t\t<apply>\n"+
					"\t\t\t\t\t\t\t\t\t<times/>\n"+
					"\t\t\t\t\t\t\t\t\t<ci>k2</ci>\n"+
					"\t\t\t\t\t\t\t\t\t<ci>" + fromProteins.get(0) + "</ci>\n"+
					"\t\t\t\t\t\t\t\t</apply>\n"+
					"\t\t\t\t\t\t\t\t<apply>\n"+
					"\t\t\t\t\t\t\t\t\t<times/>\n"+
					"\t\t\t\t\t\t\t\t\t<ci>d2</ci>\n"+
					"\t\t\t\t\t\t\t\t\t<ci>" + toProtein + "</ci>\n"+
					"\t\t\t\t\t\t\t\t</apply>\n"+
					"\t\t\t\t\t\t</apply>\n";
			}
			
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
