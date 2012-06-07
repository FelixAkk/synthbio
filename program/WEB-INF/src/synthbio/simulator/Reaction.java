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
	private static enum ReactionType { Transcription, Translation, Degradation }
	private String gate;
	private ReactionType type;
	private List<String> fromProteins;
	private String toProtein;
	
	// parameters contains the paramters for this reaction
	// for transcription these are [k1, km, n and d1]
	// for translation these are [k2, d2]
	// for degradation its [d1/d2]
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

	// no output constructor (degradation constructor)
	public Reaction(String toProtein) {
		this.fromProteins = new ArrayList<String>();
		this.toProtein = toProtein;
		this.gate = "degradation";
		setTypeToDegradation();
	}
	
	// set the type of the reaction
	public void setTypeToTranscription() {
		type = ReactionType.Transcription;
	}
	
	public void setTypeToTranslation() {
		type = ReactionType.Translation;
	}

	public void setTypeToDegradation() {
		type = ReactionType.Degradation;
	}
	
	// setParameters for transcription: k1, Km, n and d1
	public void setParameters(double... p) {
		parameters = p;
	}
	
	/**
	 * Returns the reaction id of this Reaction.
	 * this id is of the form: ReactionType_gate_inputs__output
	 * for example: Transcription_and_A_B__C
	 */
	private String getReactionId() {
		String r = type + (type != ReactionType.Degradation? "_" + gate: "") + "_";
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
				tabs(5)+"<speciesReference species=\"";
		if(type == ReactionType.Transcription)
			r += "gene";
		else if(type == ReactionType.Translation)
			r+= fromProteins.get(0);
		else
			r+= toProtein;		
		r += "\"/>\n"+
			tabs(4)+"</listOfReactants>\n";

		// Products
		// with transcription this is mrna of output
		// with translation this is output
		r +=
			tabs(4)+"<listOfProducts>\n"+
				tabs(5)+"<speciesReference species=\"" + (type == ReactionType.Degradation? "gene": toProtein) + "\"/>\n"+
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
					tabs(5)+"<!-- (k1*km^n/km^n+" + fromProteins.get(0) + "^n) -->\n"+
						tabs(6)+"<apply>\n"+
							tabs(7)+"<divide/>\n"+
							tabs(7)+"<apply>\n"+
								tabs(8)+"<times/>\n"+
								tabs(8)+"<cn>"+parameters[0]+"</cn>\n"+
								tabs(8)+"<apply>\n"+
									tabs(9)+"<power/>\n"+
									tabs(9)+"<cn>"+parameters[1]+"</cn>\n"+
									tabs(9)+"<cn>"+parameters[2]+"</cn>\n"+
								tabs(8)+"</apply>\n"+
							tabs(7)+"</apply>\n"+
							tabs(7)+"<apply>\n"+
								tabs(8)+"<plus/>\n"+
								tabs(8)+"<apply>\n"+
									tabs(9)+"<power/>\n"+
									tabs(9)+"<cn>"+parameters[1]+"</cn>\n"+
									tabs(9)+"<cn>"+parameters[2]+"</cn>\n"+
								tabs(8)+"</apply>\n"+
								tabs(8)+"<apply>\n"+
									tabs(9)+"<power/>\n"+
									tabs(9)+"<ci>" + fromProteins.get(0) + "</ci>\n"+
									tabs(9)+"<cn>"+parameters[2]+"</cn>\n"+
								tabs(8)+"</apply>\n"+
							tabs(7)+"</apply>\n"+
						tabs(6)+"</apply>\n";
			} else if(gate.equals("and")) {
				String temp = fromProteins.get(0) + "*" + fromProteins.get(1);
				r +=
					tabs(5)+"<!-- (k1(" + temp + ")^n/km^n+(" + temp + ")^n) -->\n"+
						tabs(6)+"<apply>\n"+
							tabs(7)+"<divide/>\n"+
							tabs(7)+"<apply>\n"+
								tabs(8)+"<times/>\n"+
								tabs(8)+"<cn>"+parameters[0]+"</cn>\n"+
								tabs(8)+"<apply>\n"+
									tabs(9)+"<power/>\n"+
									tabs(9)+"<apply>\n"+
										tabs(10)+"<times/>\n"+
										tabs(10)+"<ci>" + fromProteins.get(0) + "</ci>\n"+
										tabs(10)+"<ci>" + fromProteins.get(1) + "</ci>\n"+
									tabs(9)+"</apply>\n"+
									tabs(9)+"<cn>"+parameters[2]+"</cn>\n"+
								tabs(8)+"</apply>\n"+
							tabs(7)+"</apply>\n"+
							tabs(7)+"<apply>\n"+
								tabs(8)+"<plus/>\n"+
								tabs(8)+"<apply>\n"+
									tabs(9)+"<power/>\n"+
									tabs(9)+"<cn>"+parameters[1]+"</cn>\n"+
									tabs(9)+"<cn>"+parameters[2]+"</cn>\n"+
								tabs(8)+"</apply>\n"+
								tabs(8)+"<apply>\n"+
									tabs(9)+"<power/>\n"+
									tabs(9)+"<apply>\n"+
										tabs(10)+"<times/>\n"+
										tabs(10)+"<ci>" + fromProteins.get(0) + "</ci>\n"+
										tabs(10)+"<ci>" + fromProteins.get(1) + "</ci>\n"+
									tabs(9)+"</apply>\n"+
									tabs(9)+"<cn>"+parameters[2]+"</cn>\n"+
								tabs(8)+"</apply>\n"+
							tabs(7)+"</apply>\n"+
						tabs(6)+"</apply>\n";
			}
		} else if(type == ReactionType.Translation) {
			r +=
				tabs(5)+"<!-- k2*" + fromProteins.get(0) + " -->\n"+
					tabs(6)+"<apply>\n"+
						tabs(7)+"<times/>\n"+
						tabs(7)+"<cn>" + parameters[0] + "</cn>\n"+
						tabs(7)+"<ci>" + fromProteins.get(0) + "</ci>\n"+
					tabs(6)+"</apply>\n";
		} else {
			r +=
				tabs(5)+"<!-- d*" + toProtein + " -->\n"+
					tabs(6)+"<apply>\n"+
						tabs(7)+"<times/>\n"+
						tabs(7)+"<cn>" + parameters[0] + "</cn>\n"+
						tabs(7)+"<ci>" + toProtein + "</ci>\n"+
					tabs(6)+"</apply>\n";
		}
		
		r += tabs(5)+"</math>\n";

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
