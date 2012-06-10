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

package synthbio.models;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONString;

import org.simulator.math.odes.MultiTable;

public class SimulationSetting {

	/**
	 * Simulation length, defaults to 210 seconds.
	 */
	private int length = 210;

	/**
	 * The length in seconds for each tick.
	 */
	private double tickWidth = 1;
	
	/**
	 * Simulation low value.
	 * in micromole/L
	 */
	private double lowLevel = 0;
	/**
	 * Simulation high value.
	 * in micromole/L
	 */
	private double highLevel = 200;

	/**
	 * For each input protein, define a String of High/Low (H/L) for
	 * each tick in the simulation.
	 */
	private final Map<String, String> timeseries=new HashMap<String, String>();
	
	public SimulationSetting(){

	}

	/**
	 * Get the length of the simulation to be executed on this circuit.
	 */
	public int getLength() {
		return this.length;
	}
	/**
	 * Get the width of a tick in seconds.
	 */
	public double getTickWidth() {
		return this.tickWidth;
	}

	/**
	 * Simulation low value
	 * Concentration in micromole/liter inputted as the low level.
	 */
	public double getLowLevel() {
		return this.lowLevel;
	}

	/**
	 * Simulation high value
	 * Concentration in micromole/liter inputted as the high level.
	 */
	public double getHighLevel() {
		return this.highLevel;
	}

	/**
	 * Return a set of the input species in an array.
	 */
	public String[] getInputs(){
		return this.timeseries.keySet().toArray(new String[this.timeseries.size()]);
	}
	
	/**
	 * Get the simulation input for each input protein.
	 */
	public Map<String, String> getTimeseries() {
		return this.timeseries;
	}
	
	/**
	 * Get the simulation input for one protein.
	 *
	 * @param p The name of the protein.
	 */
	public String getTimeserie(String protein) {
		//assertIsProtein(p);
		//assert this.hasInput(p) : "Circuit does not have such an input protein.";

		return this.timeseries.get(protein);
	}
	public String getInput(String protein){
		return this.getTimeserie(protein);
	}

	/**
	 * Return high or low for the protein 'p' at tick 'second'
	 *
	 * @param p Input protein.
	 * @param second Second to get a result for.
	 */
	public String getInputAt(String protein, int second) {
		//assertIsInputProtein(p);
		assert second <= this.getLength() : "Tick should not exceed simulation length.";

		String input=this.getTimeserie(protein);
		if (second>=input.length()) {
			//return last defined tick if requested tick exceeds the
			//defined input length.
			return input.substring(input.length() - 1);
		} else {
			//return the character at position tick.
			return input.substring(second, second + 1);
		}
	}

	/**
	 * Return a concentration for protein p at second
	 */ 
	public Double getLevelAt(String p, int second) {
		if(this.getInputAt(p, second).equals("H")){
			return this.getHighLevel();
		}else{
			return this.getLowLevel();
		}
	}
	/**
	 * Set the number of seconds the circuit should be simulated.
	 *
	 * @param length The number of seconds
	 */
	public void setLength(int length) {
		assert length > 0 : "Simulation length should be greather than 0.";
		this.length = length;
	}

	/**
	 * Set simulation low level.
	 *
	 * @param level Concentration used in simulation as logic low level.
	 */
	public void setLowLevel(double level) {
		assert level >= 0 : "Low level concentration should be positive.";
		this.lowLevel = level;
	}

	/**
	 * Set tick width.
	 * That is: What does one H/L mean in the input string...
	 */
	public void setTickWidth(double tickWidth) {
		assert tickWidth >= 0;
		this.tickWidth = tickWidth;
	}
	
	/**
	 * Set simulation high level.
	 *
	 * @param level Concentration used in simulation as logic high level.
	 */
	public void setHighLevel(double level) {
		assert level >= 0 : "High level concentration should be positive.";
		this.highLevel = level;
	}

	/**
	 * Add the simulator input 'input' for protein 'protein'.
	 * If the input string does not span the simulation length, the last
	 * tick is repeated for the rest of the simulation length.
	 *
	 * @param protein Protein
	 * @param input Input string consisting of (H|L) for each tick.
	 */
	public void addInput(String protein, String input){
		//assertIsInputProtein(protein);
		
		//replace all spaces by empty string.
		input=input.replaceAll("[^HL]", "");
		this.timeseries.put(protein, input);
	}

	/**
	 * Returns a MultiTable that contains the (simulation)inputs of the circuits.
	 */
	public MultiTable getInputMultiTable() {

		// setup time points
		double[] timePoints = new double[this.getLength()];
		for(int i = 0; i < length; i++) {
			timePoints[i] = i*this.getTickWidth();
		}
		
		// setup names
		String[] names = this.getInputs();
		
		// setup data
		double[][] data = new double[length][names.length];
		for(int iName = 0; iName < names.length; iName++) {
			for(int iTime = 0; iTime < length; iTime++) {
				data[iTime][iName] = this.getLevelAt(names[iName], iTime);
			}
		}

		return new MultiTable(timePoints, data, names);
	}
	
	/**
	 * Construct from JSON
	 */
	public static SimulationSetting fromJSON(JSONObject json) {
		return new SimulationSetting();
	}
}
