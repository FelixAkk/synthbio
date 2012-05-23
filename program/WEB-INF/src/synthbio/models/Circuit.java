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

import synthbio.models.CircuitException;
import synthbio.files.BioBrickRepository;
import synthbio.simulator.CircuitConverter;

/**
 * Circuit representation.
 *
 * A Circuit is defined by a name, description and three lists: Gates,
 * input proteins and output proteins.
 *
 * Circuits' fromJSON is guaranteed to return a valid circuit. When
 * constructing the Circuit manually, a validate() method is supplied to
 * check validity.
 * 
 * @author jieter
 */
public class Circuit implements JSONString {

	/**
	 * The filename.
	 */
	private final String name;

	/**
	 * The Circuit description.
	 */
	private final String description;

	/**
	 * List of Gates
	 */
	private final ArrayList<Gate> gates=new ArrayList<Gate>();

	/**
	 * List of input proteins.
	 */
	private final Set<String> inputs=new HashSet<String>();

	/**
	 * Simulation length, defaults to 40 ticks.
	 */
	private final int simulationLength=40;

	/**
	 * For each input protein, define a String of High/Low (H/L) for
	 * each tick in the simulation.
	 */
	private final Map<String, String> simulationInput=new HashMap<String, String>();
	
	/**
	 * List of output proteins.
	 */
	private final Set<String> outputs=new HashSet<String>();
	
	/**
	 * Construct the Circuit.
	 *
	 * Initilizes gates and signals to empty HashMap. Constructor
	 * allows invalid circuits to be created.
	 *
	 * @param name 			Name of the circuit
	 * @param description	Description of the circuit
	 * @param gates			Gates in the circuit
	 */
	public Circuit(String name, String description, Collection<Gate> gates) {
		this.name=name;
		this.description=description;

		if(gates != null){
			this.gates.addAll(gates);
		}
	}

	/**
	 * Construct the Circuit with name and description
	 */
	public Circuit(String name, String description) {
		this(name, description, null);
	}
	
	/**
	 * Construct the Circuit with only a name.
	 */
	public Circuit(String name) {
		this(name, "");
	}

	/**
	 * Check if string p looks like a protein string.
	 *
	 * @param p The protein name to check.
	 */
	public void assertIsProtein(String p) {
		assert p.length() == 1 : "Protein is a one letter String";
	}
	
	/**
	 * Check if string p is an input protein.
	 * 
	 * @param p The protein name to check.
	 */
	public void assertIsInputProtein(String p) {
		this.assertIsProtein(p);
		assert this.hasInput(p) : "Circuit should have the input " + p + ".";
	}

	/**
	 * Get the name of the circuit.
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Get the description of the circuit.
	 */
	public String getDescription() {
		return this.description;
	}

	/**
	 * Get the collection of gates for the circuit.
	 */
	public Collection<Gate> getGates() {
		return this.gates;
	}

	/**
	 * Get a collection of input proteins for the circuit.
	 *
	 * @return A collection of one letter strings representing protein names.
	 */
	public Set<String> getInputs() {
		return this.inputs;
	}

	/**
	 * Get the length of the simulation to be executed on this circuit.
	 */
	public int getSimulationLength() {
		return this.simulationLength;
	}

	/**
	 * Get the simulation input for each input protein.
	 */
	public Map<String, String> getSimulationInput() {
		assert this.getInputs().size() == this.simulationInput.size() : "Number of simulation inputs should equal the number of circuit inputs.";
		return this.simulationInput;
	}
	
	/**
	 * Get the simulation input for one protein.
	 *
	 * @param p The name of the protein.
	 */
	public String getSimulationInput(String p) {
		assertIsProtein(p);
		assert this.hasInput(p) : "Circuit does not have such an input protein.";

		return this.simulationInput.get(p);
	}

	/**
	 * Return high or low for the protein 'p' at tick 'tick'
	 *
	 * @param p Input protein.
	 * @param tick Tick to get a result for.
	 */
	public String getSimulationInputAt(String p, int tick) {
		assertIsInputProtein(p);
		assert tick <= this.getSimulationLength() : "Tick should not exceed simulation length.";

		String input=this.getSimulationInput(p);
		if (tick>input.length()) {
			//return last defined tick if requested tick exceeds the
			//defined input length.
			return input.substring(input.length() - 1);
		} else {
			//return the character at position tick.
			return input.substring(tick, tick + 1);
		}
	}
	
	/**
	 * Get the set of output proteins.
	 */
	public Set<String> getOutputs() {
		return this.outputs;
	}

	/**
	 * Add a gate to the circuit.
	 *
	 * @param g Gate to be added.
	 */
	public void addGate(Gate g) {
		assert g != null : "Gate should not be null";
		this.gates.add(g);
	}

	/**
	 * Define a protein as an input to the circuit.
	 *
	 * @param p One letter string containing the name of the protein.
	 */
	public void addInput(String p) {
		assertIsProtein(p);
		this.inputs.add(p);
	}

	/**
	 * Set the number of ticks the circuit should be simulated.
	 *
	 * @param length The number of ticks.
	 */
	public void setSimulationLength(int length) {
		assert length > 0 : "Simulation length should be greather than 0";
		this.simulationLength=length;
	}

	/**
	 * Define a protein as an output from the circuit.
	 *
	 * @param p One letter string containing the name of the protein.
	 */
	public void addOutput(String p) {
		assertIsProtein(p);
		this.outputs.add(p);
	}
	
	/**
	 * Add the simulator input 'input' for protein 'p'.
	 * If the input string does not span the simulation length, the last
	 * tick is repeated for the rest of the simulation length.
	 *
	 * @param p Protein
	 * @param input Input string consisting of (H|L) for each tick.
	 */
	public void addSimulationInput(String p, String input){
		assertIsInputProtein(p);
		
		//replace all spaces by empty string.
		input=input.replaceAll("[^HL]", "");
		this.simulationInput.put(p, input);
	}
	

	/**
	 * Do we have a gate with index?
	 */
	public boolean hasGateAt(int index){
		return index >= 0 && index < this.gates.size();
	}

	/**
	 * Does the circuit have an input with name p?
	 */
	public boolean hasInput(String p){
		assertIsProtein(p);

		return this.inputs.contains(p);
	}

	/**
	 * Return the gate at position index, provided that such a gate exists. 
	 */
	public Gate gateAt(int index){
		assert this.hasGateAt(index) : "No such Gate";
		return this.gates.get(index);
	}
	
	/**
	 * Validate the Circuit.
	 *
	 * Check:
	 *  - All gates have non-null
	 *  - Valid use of proteins.
	 *
	 * @throws CircuitException
	 */
	public void validate() throws CircuitException{
		/* If there is one gate there should be at least one input and
		 * at least one output.
		 */
		if(this.getGates().size()>0){
			if(this.getInputs().size()<1){
				throw new CircuitException("Circuit with one Gate should have at least one input.");
			}
			if(this.getOutputs().size()<1){
				throw new CircuitException("Circuit with one Gate should have at least one output.");
			}
		}
		
		// Check if each gate has a Promotor and a CDS set.
		for(Gate g: this.getGates()){
			if(g.getPromotor()==null){
				throw new CircuitException("Gate "+g.toString()+" has no Promotor set.");
			}
			if(g.getCDS()==null){
				throw new CircuitException("Gate "+g.toString()+" has no CDS set.");
			}
		}
		
		//validate the protein assignments.
		try{
			this.validateProteins();
		}catch(CircuitException e){
			throw new CircuitException("Incorrect Protein assignment: "+e.getMessage());
		}
	}
	
	/**
	 * Validate the use of proteins in the circuit.
	 * If the circuit is valid, no exception is thrown.
	 *
	 * Could be done in a more efficient manner, but for the time being
	 * this is straightforward and explicit.
	 * 
	 * @throws CircuitException if some error is found in the protein
	 * assignment.
	 */
	public void validateProteins() throws CircuitException{
		/* check if all input Proteins are used by some gate.
		 */
		boolean used=false;
		for(String input: this.getInputs()){
			used=false;
			for(Gate g: this.getGates()){
				if(g.hasInput(input)){
					used=true;
				}
			}
			if(!used){
				throw new CircuitException("Unused input protein ("+input+").");
			}
		}

		/* Check if all gate have
		 *  - inputs from other gates or from the circuits inputs
		 *  - outputs to other gates or to the circuits outputs
		 *  - no outputs which are inputs. 
		 */
		boolean available;
		for(Gate g: this.getGates()){
			//check inputs
			for(String input: g.getInputs()){
				//check the circuits' inputs
				if(!this.getInputs().contains(input)){
					//and if its not in Circuits' inputs, check all Gate
					//outputs.
					available=false;
					for(Gate output: this.getGates()){
						if(output.hasOutput(input)){
							available=true;
						}
					}
					if(!available){
						throw new CircuitException("Input "+input+" of Gate "+g.toString()+" is not connected");
					}
				}
			}
			
			//check outputs
			String output=g.getOutput();
			//check the circuits' outputs
			if(!this.getOutputs().contains(output)){
				//and if its not in Circuits' outputs, check all Gate
				//inputs.
				available=false;
				for(Gate input: this.getGates()){
					if(input.hasInput(output)){
						available=true;
					}
				}
				if(!available){
					throw new CircuitException("Output "+output+" of Gate "+g.toString()+" is not connected");
				}
			}
			//check if gate has a signal from an output to an input.
			if(g.hasInput(g.getOutput())){
				throw new CircuitException("Gate "+g.toString()+" has an output which is connected to one of its inputs");
			}
		}

		//check if all outputs come from a gate.
		for(String output: this.getOutputs()){
			used=false;
			for(Gate g: this.getGates()){
				if(g.hasOutput(output)){
					used=true;
				}
			}
			if(!used){
				throw new CircuitException("Unused output protein ("+output+").");
			}
		}
		//check if one protein is produced by more than one gate or by
		//a gate and by the inputs.
		//@todo implement

		//if we arrive here, the protein assignments are valid.
	}

	/**
	 * Convert the current object state to JSON.
	 *
	 * Not yet used or tested anywhere.
	 * 
	 * @return Serialized JSON string representation of the Circuit.
	 */
	public String toJSONString(){
		JSONObject ret=new JSONObject();

		try{
			ret.put("name", this.getName());
			ret.put("description", this.getDescription());
			ret.put("gates", this.getGates());
			//@todo: include signals...
			//ret.put("signals", <signals>);

			JSONObject inputs=new JSONObject();
			inputs.put("length", this.getSimulationLength());
			inputs.put("values", this.getSimulationInput());
			ret.put("inputs", inputs);
		}catch(Exception e){
			return "{\"error\":\"JSONException:"+e.getMessage()+"\"}";
		}
		return ret.toString();
	}
	
	/**
	 * Convert the circuit to a SBML document.
	 *
	 */
	public String toSBML(){
		return (new CircuitConverter()).convert(this);
	}

	/**
	 * Return a string representation for the Circuit.
	 */
	public String toString(){
		String ret="Circuit: "+this.getName()+" | "+this.getDescription()+"\n";
		for(Gate g: this.getGates()){
			ret+=g.toString()+"\n";
		}
		return ret;
	}
}
