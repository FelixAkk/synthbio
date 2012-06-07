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

import static synthbio.Util.tabs;

import java.util.ArrayList;
import java.util.List;

/**
 * A class that represents a single Reaction for the converter.
 * @author Albert ten Napel
 */
class Reaction {
	private static enum ReactionType { Transcription, Translation }
	private String gate;
	private ReactionType type;
	private List<String> fromProteins;
	private String toProtein;
	
	// parameters contains the paramters for this reaction
	// for transcription these are [k1, km, n and d1]
	// for translation these are [k2, d2]
	private double[] parameters;
	
	// multiple input constructor
	public Reaction(String gate, List<String> fromProteins, String toProtein) {
		this.fromProteins = fromProteins;
		this.toProtein = toProtein;
		this.gate = gate;
	}
	
	// single input constructor
	public Reaction(String gate, String fromProtein, String toProtein) {
		this.fromProteins = new ArrayList<String>();
		fromProteins.add(fromProtein);
		this.toProtein = toProtein;
		this.gate = gate;
	}
	
	// set the type of the reaction
	public void setTypeToTranscription() {
		type = ReactionType.Transcription;
	}
	
	public void setTypeToTranslation() {
		type = ReactionType.Translation;
	}
	
	// setParameters for transcription: k1, Km, n and d1
	public void setParameters(double k1, double km, double n, double d1) {
		parameters = new double[] {k1, km, n, d1};
	}
	
	// setParameters for translation: k2 and d2
	public void setParameters(double k2, double d2) {
		parameters = new double[] {k2, d2};
	}
	
	/**
	 * Returns the reaction id of this Reaction.
	 * this id is of the form: ReactionType_gate_inputs__output
	 * for example: Transcription_and_A_B__C
	 */
	private String getReactionId() {
		String r = type + "_" + gate + "_";
		for(String p: fromProteins) {
			r += p + "_";
		}
		r += "_" + toProtein;
		return r;
	}
	
	/**
	 * Returns the list of Reactants, Products, Modifiers.
	 */
	private String listOfInvolved() {
		String r = "";
		
		// Reactants
		// with transcription this is gene.
		// with translation this is mrna of input (translation has only one input).
		r +=
			tabs(4)+"<listOfReactants>\n"+
				tabs(5)+"<speciesReference species=\"" + (type == ReactionType.Transcription? "gene": fromProteins.get(0)) + "\"/>\n"+
			tabs(4)+"</listOfReactants>\n";
		// Products
		// with transcription this is mrna of output
		// with translation this is output
		r +=
			tabs(4)+"<listOfProducts>\n"+
				tabs(5)+"<speciesReference species=\"" + toProtein + "\"/>\n"+
			tabs(4)+"</listOfProducts>\n";
		// Modifiers
		// with transcription this is the inputs
		// with translation this is empty
		if(type == ReactionType.Transcription) {
			r += tabs(4)+"<listOfModifiers>\n";
			for(String in: fromProteins)
				r += tabs(5)+"<modifierSpeciesReference species=\"" + in + "\"/>\n";
			r += tabs(4)+"</listOfModifiers>\n";
		}
		
		return r;
	}
	
	/**
	 * Returns the kineticLaw part of SBML, which contains the parameters and formulae.
	 */
	private String kineticLaw() {
		String r = tabs(4)+"<kineticLaw>\n";
		
		// the math
		r += tabs(5)+"<math xmlns=\"http://www.w3.org/1998/Math/MathML\">\n";
		
		if(type == ReactionType.Transcription) {
			if(gate.equals("not")) {
				r +=
					tabs(5)+"<!-- (k1*km^n/km^n+" + fromProteins.get(0) + "^n)-d1*" + toProtein + " -->\n"+
						tabs(6)+"<apply>\n"+
							tabs(7)+"<minus/>\n"+
							tabs(7)+"<apply>\n"+
								tabs(8)+"<divide/>\n"+
								tabs(8)+"<apply>\n"+
									tabs(9)+"<times/>\n"+
									tabs(9)+"<ci>k1</ci>\n"+
									tabs(9)+"<apply>\n"+
										tabs(10)+"<power/>\n"+
										tabs(10)+"<ci>km</ci>\n"+
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
										tabs(10)+"<ci>" + fromProteins.get(0) + "</ci>\n"+
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
				tabs(5)+"<!-- k2*" + fromProteins.get(0) + " - d2*" + toProtein + " -->\n"+
					tabs(6)+"<apply>\n"+
						tabs(7)+"<minus/>\n"+
						tabs(7)+"<apply>\n"+
							tabs(8)+"<times/>\n"+
							tabs(8)+"<ci>k2</ci>\n"+
							tabs(8)+"<ci>" + fromProteins.get(0) + "</ci>\n"+
						tabs(7)+"</apply>\n"+
						tabs(7)+"<apply>\n"+
							tabs(8)+"<times/>\n"+
							tabs(8)+"<ci>d2</ci>\n"+
							tabs(8)+"<ci>" + toProtein + "</ci>\n"+
						tabs(7)+"</apply>\n"+
					tabs(6)+"</apply>\n";
		}
		
		r += tabs(5)+"</math>\n";

		// the parameters
		// transcription has:
		//		k1, Km and n of the input
		//		d1 of output
		// translation has:
		//		k2 of input
		//		d2 of output
		r += tabs(5)+"<listOfParameters>\n";
		if(type == ReactionType.Transcription) {
			r +=
				tabs(6)+"<parameter id=\"k1\" value=\"" + parameters[0] + "\" units=\"substance\"/>\n"+
				tabs(6)+"<parameter id=\"km\" value=\"" + parameters[1] + "\" units=\"substance\"/>\n"+
				tabs(6)+"<parameter id=\"n\" value=\"" + parameters[2] + "\" units=\"substance\"/>\n"+
				tabs(6)+"<parameter id=\"d1\" value=\"" + parameters[3] + "\" units=\"substance\"/>\n";
		} else {
			r +=
				tabs(6)+"<parameter id=\"k2\" value=\"" + parameters[0] + "\" units=\"substance\"/>\n"+
				tabs(6)+"<parameter id=\"d2\" value=\"" + parameters[1] + "\" units=\"substance\"/>\n";
		}
		r += tabs(5)+"</listOfParameters>\n";
		
		return r + tabs(4)+"</kineticLaw>\n";
	}
	
	/**
	 * Returns the SBML string of this Reaction.
	 */
	public String getSBMLString() {
		String s = tabs(3)+"<reaction id=\"" + getReactionId() + "\" reversible=\"false\">\n";
		s += listOfInvolved();
		s += kineticLaw();
		return s + tabs(3)+"</reaction>\n";
	}
	
	/**
	 * toString for debugging purposes
	 */
	public String toString() {
		return "Reaction(" + type + ", " + fromProteins + " -> " + toProtein + ")";
	}
}
