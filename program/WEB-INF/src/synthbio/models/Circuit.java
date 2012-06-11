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
import java.util.Set;
import java.util.HashSet;

import synthbio.models.CircuitException;
import synthbio.models.SimulationSetting;
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
public class Circuit {

	/**
	 * The filename.
	 */
	private String name;

	/**
	 * The Circuit description.
	 */
	private String description;

	/**
	 * List of Gates
	 */
	private final ArrayList<Gate> gates = new ArrayList<Gate>();

	/**
	 * List of input proteins.
	 */
	private final Set<String> inputs = new HashSet<String>();

	/**
	 * List of output proteins.
	 */
	private final Set<String> outputs=new HashSet<String>();

	/**
	 * Simulation input object describing the input for the simulation.
	 */
	private SimulationSetting simulationSetting;
	
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

		this.simulationSetting=new SimulationSetting();
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
		assert p.length() > 0 : "Protein is String with at least one character.";
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
	 * Get the simulations settings for this Circuit.
	 */
	public SimulationSetting getSimulationSetting(){
		return this.simulationSetting;
	}

	/**
	 * Set the simulations settings for this Circuit.
	 */
	public void setSimulationSetting(SimulationSetting ss){
		this.simulationSetting = ss;
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
	 * Define a protein as an output from the circuit.
	 *
	 * @param p One letter string containing the name of the protein.
	 */
	public void addOutput(String p) {
		assertIsProtein(p);
		this.outputs.add(p);
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
