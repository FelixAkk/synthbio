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
 
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.HashMap;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import synthbio.models.*;

import synthbio.simulator.Reaction;


public class JieterSolver {

	public Circuit circuit;
	
	public Collection<Gate> gates;
	public ArrayList<Reaction> reactions;
	public ArrayList<String> species;

	public JieterSolver(Circuit circuit) {
		this.circuit=circuit;
		
		// First retrieve all gates
		gates = circuit.getGates();
		
		// reactions
		reactions = new ArrayList<Reaction>(gates.size());
		// set of all the species
		species = new ArrayList<String>();
		
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
			for(String s: inputs) {
				species.add(s);
			}
			species.add(output);
			species.add("m"+output);
		}
	}

	/**
	 * This is the sheet we work in. For each specie we have an ArrayList
	 * with doubles, one double for each step in the simulation...
	 */
	public HashMap<String, ArrayList<Double>> sheet;

	public int steps;

	/**
	 * Retrieve the value for a certain specie on a certain time step.
	 */
	public Double get(String specie, int t){
		return sheet.get(specie).get(t);
	}
	public ArrayList<Double> get(String specie){
		return sheet.get(specie);
	}
	public void set(String specie, int t, double value){
		
		sheet.get(specie).add(t, value);
	}

	public void printSpeciesAt(int t){
		Integer[] times={t};
		printSpeciesAt(times);
	}
	public void printSpeciesAt(Integer[] times){
		List<Integer> list = Arrays.asList(times);

		System.out.print("  t =");
		for(int t: list){
			System.out.print(String.format("%10d", t));
		}
		System.out.println();
		System.out.println("---------------");
		for(String sp: species){
			System.out.print(String.format("%1$#3s = ", sp));
			for(int t: list){
				System.out.print(String.format("%10.2f", get(sp, t)));
			}
			System.out.println();
		}
		System.out.println("---------------");
	}
	//number of steps per tick.
	public int stepsize=100;
	
	public double getInputLevelAt(String specie, int t){
		t = (int)Math.floor(t/this.stepsize);
		return circuit.getSimulationLevelAt(specie, t);
	}
	public void solve() throws Exception{
		System.out.println("about to solve: "+circuit);
		System.out.println("reactions: "+reactions);
		System.out.println("species: "+species);
		System.out.println();
		System.out.println();
		
		this.steps=circuit.getSimulationLength()*stepsize;

		
		sheet=new HashMap<String, ArrayList<Double>>();

		//initial values.
		for(String specie: species){
			sheet.put(specie, new ArrayList<Double>(steps));
			
			if(circuit.hasInput(specie)){
				//set input species to their defined levels.
				set(specie, 0, getInputLevelAt(specie, 0));
			}else{
				//set all non-input species to 0.0
				set(specie, 0, 0.0);
			}
		}

		double delta_t = (double)1/stepsize;
		
		//time steps for t >= 1.
		for(int t=1; t<steps; t++) {
			//insert inputs for this step.
			for(String input: circuit.getInputs()) {
				set(input, t, getInputLevelAt(input, t));
			}
			
			for(Reaction r :reactions) {
				if(r.isTranslation()){
					//translation, from mRNA to Proteins.
					double k2 = r.parameters[0];
					double d2 = r.parameters[1];

					double con_protein = get(r.getToProtein(), t-1);
					double con_mRNA = get(r.getFromProtein(), t-1);
					
					//d[protein] / dt = k2 * [mRNA] - d2 * [Protein]
					double delta_protein = k2 * con_mRNA - d2 * con_protein;
					set(
						r.getToProtein(),
						t,
						delta_protein
					);
				}else{
					// Transcription from one or two proteins to mRNA
					double k1 = r.parameters[0];
					double km = r.parameters[1];
					double n = r.parameters[2];
					double d1 = r.parameters[3];

					String mRNA = r.getToProtein();
					double con_mRNA = get(mRNA, t-1);
					
					if(r.getGate().equals("and")){
						// AND gate
						String tf1 = r.getFromProtein(0);
						String tf2 = r.getFromProtein(1);
						double con_tf1 = get(tf1, t-1);
						double con_tf2 = get(tf2, t-1);

						// equation for AND gate.
						// d[mRNA] / dt = k1 ( [TF1] * [TF2] )^n / K_m^n + ([TF1]*[TF2])^n - d1 * [ mRNA]

						
						double delta_mRNA=
							(
								k1 * Math.pow(con_tf1 * con_tf2, n)
							) / (
								Math.pow(km, n) + Math.pow(con_tf1 * con_tf2, n)
							) - d1 * con_mRNA ;
							
						set(
							mRNA,
							t,
							delta_mRNA 
						);
						// end AND gate

					}else{
						// NOT gate
						String tf = r.getFromProtein();
						double con_tf = get(tf, t-1);
												
						// equation for NOT gate
						// d[mRNA] / dt = (k1*K_m^n) / ( K_m^n + [TF]^n) - d1 * [mRNA]
						double delta_mRNA=
							(
								k1 * Math.pow(km, n)
							) / (
								Math.pow(km, n) + con_tf 
							) - d1 * con_mRNA;
						set(
							mRNA,
							t,
							delta_mRNA 
						);
						// end of NOT gate.
					}
				}
				
			}
		}
		System.out.println();
		Integer[] timepoints= {0, 1, 2, 3, 4, 5, 6, 7, 8, 39};
		printSpeciesAt(timepoints);
		
	}

	/**
 	 * Converts a sheet to a JSON-string of the format:
 	 * 	{
 	 * 		"names": [A, B],
 	 * 		"length": 10,
 	 * 		"step": 1,
 	 *		"time": [1, 2, 3, 4, 5, 6, 7, 8, 9, 10],
 	 * 		"data": {
 	 *			"A": [0, 0, 0, 0, 0, 600, 600, 600, 600, 600],
 	 *			"B": [...]
 	 * 		}
 	 * 	}
 	 */
 	public JSONObject toJSON() throws JSONException{
		assert sheet != null : "Run solver first!";
		
		JSONObject ret = new JSONObject();
		ret.put("names", species);
		ret.put("length", circuit.getSimulationLength());
		ret.put("step", (double)1/this.stepsize);

		ArrayList<Double> time=new ArrayList<Double>();
		for(int t=0; t<this.steps; t++){
			time.add((double)t/this.stepsize);
		}
		//ret.put("time", time);
		
		JSONObject data=new JSONObject();
		for(String sp: species){
			data.put(sp, get(sp));
		}
		ret.put("data", data);

		return ret;
	}

}
