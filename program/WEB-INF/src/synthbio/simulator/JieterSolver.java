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

import synthbio.simulator.Solver;
import synthbio.simulator.Reaction;

/**
 * Naive implementation to solve the system of ODE's for the BioBricks
 * provided.
 */
public class JieterSolver implements Solver{

	/**
	 * The circuit to be solved.
	 */
	public Circuit circuit;

	/**
	 * Reactions in the simulation.
	 */
	public ArrayList<Reaction> reactions;

	/**
	 * Species in the simulation.
	 */
	public HashSet<String> species;

	/**
	 * This is the sheet we work in. For each specie we have an ArrayList
	 * with doubles, one double for each step in the simulation...
	 */
	public HashMap<String, ArrayList<Double>> sheet;

	/**
	 * Store the result for each specie for each calculation in t_minus_one.
	 */
	public HashMap<String, Double> t_minus_one;
	

	/**
	 * Calculation resolution: how many calculations per second.
	 */
	public int calculation_resolution=1000;

	/**
	 * Result resolutuion: how many results per second.
	 */
	public int result_resolution=10;

	/**
	 * Total calculations steps.
	 */
	public int calculation_steps;


	/**
	 * Construct the Solver.
	 */
	public JieterSolver(Circuit circuit) {
		this.circuit=circuit;
		
		reactions = new ArrayList<Reaction>();
		species = new HashSet<String>();

		this.calculation_steps = circuit.getSimulationSetting().getLength() * calculation_resolution;
		
		this.initReactions();
	}

	/**
	 * Initilize the reactions.
	 */
	private void initReactions(){

		// Create the reactions
		for(Gate g: circuit.getGates()) {
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
	 * Retrieve the value for a certain specie on a certain time step.
	 */
	public Double get(String specie, int t){
		return sheet.get(specie).get(t);
	}

	/**
	 * Values for the previous calculation.
	 */
	public Double get_t_minus_one(String specie) {
		return t_minus_one.get(specie);
	}

	/**
	 * Retrieve the time serie for a certain specie
	 */
	public ArrayList<Double> get(String specie){
		return sheet.get(specie);
	}

	

	/**
	 * Put a value for a specie on step t.
	 */
	public void set(String specie, int t, double value){
		t_minus_one.put(specie, value);
		if(t==0 || t % (calculation_resolution/result_resolution) == 0){
			int result_step = (int) Math.floor(t / (calculation_resolution/result_resolution));
			sheet.get(specie).add(result_step, value);
		}
	}

	/**
	 * Get the number of steps in the resulting data set.
	 */
	public int getResultSteps(){
		return this.circuit.getSimulationSetting().getLength() * this.result_resolution;
	}

	/**
	 * Get the simulation level for an input at a certian simulation t.
	 */
	public double getInputLevelAt(String specie, int t){
		t = (int)Math.floor(t/this.calculation_resolution);
		return this.circuit.getSimulationSetting().getLevelAt(specie, t);
	}

	/**
	 * Initialize the return data.
	 */
	private void initSheet(){
		sheet=new HashMap<String, ArrayList<Double>>();
		t_minus_one=new HashMap<String, Double>();
		
		//Values for t=0.
		for(String specie: species){
			sheet.put(specie, new ArrayList<Double>(this.getResultSteps()));
			t_minus_one.put(specie, 0.0);
			
			if(circuit.hasInput(specie)){
				//set input species to their defined levels.
				set(specie, 0, getInputLevelAt(specie, 0));
			}else{
				//set all non-input species to 0.0
				set(specie, 0, 0.0);
			}
		}
	}
	
	public void solve() throws Exception {
		this.initSheet();

		double delta_t = 1.0/calculation_resolution;
		
		//time steps for t >= 1.
		for(int t=1; t<calculation_steps; t++) {
			//insert inputs for this step.
			for(String input: circuit.getInputs()) {
				set(input, t, getInputLevelAt(input, t));
			}
			
			for(Reaction r :reactions) {
				if(r.isTranslation()){
					//translation, from mRNA to Proteins.
					double k2 = r.parameters[0];
					double d2 = r.parameters[1];

					double con_protein = get_t_minus_one(r.getToProtein());
					double con_mRNA = get_t_minus_one(r.getFromProtein());
					
					//d[protein] / dt = k2 * [mRNA] - d2 * [Protein]
					double delta_protein = k2 * con_mRNA - d2 * con_protein;
					set(
						r.getToProtein(),
						t,
						con_protein + delta_protein * delta_t
					);
				}else{
					// Transcription from one or two proteins to mRNA
					double k1 = r.parameters[0];
					double km = r.parameters[1];
					double n = r.parameters[2];
					double d1 = r.parameters[3];

					String mRNA = r.getToProtein();
					double con_mRNA = get_t_minus_one(mRNA);
					
					if(r.getGate().equals("and")){
						// AND gate
						String tf1 = r.getFromProtein(0);
						String tf2 = r.getFromProtein(1);
						double con_tf1 = get_t_minus_one(tf1);
						double con_tf2 = get_t_minus_one(tf2);

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
							con_mRNA + delta_mRNA * delta_t
						);
						// end AND gate

					}else{
						// NOT gate
						double con_tf = get_t_minus_one(r.getFromProtein());
												
						// equation for NOT gate
						// d[mRNA] / dt = (k1*K_m^n) / ( K_m^n + [TF]^n) - d1 * [mRNA]
						double delta_mRNA=
							(
								k1 * Math.pow(km, n)
							) / (
								Math.pow(km, n) + Math.pow(con_tf, n) 
							) - d1 * con_mRNA;
						set(
							mRNA,
							t,
							con_mRNA + delta_mRNA * delta_t
						);
						// end of NOT gate.
					}
				}
				
			}
		}
	}

	/**
 	 * Converts a sheet to a JSON-string of the format:
 	 * 	{
 	 * 		"names": ["A", "B"],
 	 * 		"length": 10,
 	 * 		"step": 1,
 	 * 		"data": {
 	 *			"A": [0, 0, 0, 0, 0, 600, 600, 600, 600, 600],
 	 *			"B": [...]
 	 * 		}
 	 * 	}
 	 *
 	 * Where
 	 *  - names contains a list of species,
 	 *  - length the number of seconds to be displayed
 	 *  - step the time step between data points
 	 *  - data length*(1/step) data points for each specie.
 	 */
 	public JSONObject toJSON() throws JSONException{
		assert sheet != null : "Run solver first!";
		
		JSONObject json = new JSONObject();
		json.put("solver", "JieterSolver");
		json.put("names", species);
		json.put("length", circuit.getSimulationSetting().getLength());
		json.put("step", 1.0/this.result_resolution);
		
		JSONObject data=new JSONObject();
		for(String sp: species){
			data.put(sp, get(sp));
		}
		json.put("data", data);
		
		return json;
	}
}
