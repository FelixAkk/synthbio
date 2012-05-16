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
public class Circuit implements JSONString{

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
	private ArrayList<Gate> gates=new ArrayList<Gate>();

	/**
	 * List of input proteins.
	 */
	private Set<String> inputs=new HashSet<String>();

	/**
	 * Simulation length
	 */
	private int simulationLength=0;

	/**
	 * For each input protein, define a String of High/Low (H/L) for
	 * each tick in the simulation.
	 */
	private Map<String, String> simulationInput=new HashMap<String, String>();
	
	/**
	 * List of output proteins.
	 */
	private Set<String> outputs=new HashSet<String>();
	
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
	public Circuit(String name, String description, Collection<Gate> gates){
		this.name=name;
		this.description=description;

		if(gates!=null){
			this.gates.addAll(gates);
		}
	}

	/**
	 * Construct the Circuit with name and description
	 */
	public Circuit(String name, String description){
		this(name, description, null);
	}
	
	/**
	 * Construct the Circuit with only a name.
	 */
	public Circuit(String name){
		this(name, "");
	}

	/*
	 * Getters
	 */
	public String getName(){
		return this.name;
	}
	public String getDescription(){
		return this.description;
	}
	public Collection<Gate> getGates(){
		return this.gates;
	}
	public Set<String> getInputs(){
		return this.inputs;
	}
	public int getSimulationLength(){
		return this.simulationLength;
	}

	/**
	 * Get the simulation input for each input protein.
	 */
	public Map<String, String> getSimulationInput(){
		assert this.getInputs().size() == this.simulationInput.size() : "Number of simulation inputs should equal the number of circuit inputs.";
		return this.simulationInput;
	}
	
	/**
	 * Get the simulation input for one protein.
	 *
	 * @param p The name of the protein.
	 */
	public String getSimulationInput(String p){
		assertIsProtein(p);
		assert this.hasInput(p) : "Circuit does not have such an input protein";

		return this.simulationInput.get(p);
	}
	
	/**
	 * Get the set of output proteins.
	 */
	public Set<String> getOutputs(){
		return this.outputs;
	}

	/* setters
	 */
	public void addGate(Gate g){
		this.gates.add(g);
	}
	public void addInput(String p){
		assertIsProtein(p);
		this.inputs.add(p);
	}
	public void addOutput(String p){
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
		assertIsProtein(p);
		assert this.hasInput(p): "Circuit should have the input "+p;
		
		//replace all spaces by empty string.
		//@todo: replace all but (H|L) by empty string
		input=input.replace(" ", "");
		this.simulationInput.put(p, input);
	}
	
	/**
	 * Check if string p looks like a protein string.
	 */
	public void assertIsProtein(String p){
		assert p.length()==1 : "Protein is a one letter String";
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
		/* if there is one gate there should be at least one input and
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
	 * Deserialize JSON to a Circuit object.
	 *
	 * @param json The String containing JSON
	 * @return The Circuit.
	 */
	public static Circuit fromJSON(String json) throws CircuitException, JSONException{
		return Circuit.fromJSON(new JSONObject(json));
	}

	/**
	 * Deserialize JSON to a Circuit object.
	 * 
	 * Since Java data structures are more advanced than the JSON
	 * counterpart this method takes care of the non-trivial mappings of
	 * dumb Signals and Gates to a set of Gates connected by references.
	 *
	 * @param json The JSONObject containing the circuit information.
	 * @return The Circuit.
	 */
	public static Circuit fromJSON(JSONObject json) throws CircuitException, JSONException{
		Circuit circuit=new Circuit(
			json.getString("name"),
			json.getString("description")
		);
		
		/* Fetch the gates, add them in their order to a local list of
		 * gates.
		 */
		JSONArray JSONGates=json.getJSONArray("gates");
		
		//If no gates present, don't bother about the Signals, just return.
		if(JSONGates.length()<=0){
			return circuit;
		}
		
		for(int i=0; i<JSONGates.length(); i++){
			JSONObject JSONGate=JSONGates.getJSONObject(i);

			Gate gate=new Gate(Position.fromJSON(JSONGate.getJSONObject("position")));
			gate.setKind(JSONGate.getString("kind"));
			
			circuit.addGate(gate);
		}

		/* Use the data from the signals to update the Gates with the
		 * right Promototors and CDSs
		 */
		JSONArray JSONSignals=json.getJSONArray("signals");
		if(JSONSignals.length()<=0){
			/* Circuit is only defined with gates and signals, so fail if
			 * no signals are present.
			 */
			throw new CircuitException("Circuit has no signals.");
		}
		
		/* Create BioBrick repository.
		 */
		BioBrickRepository bbr;
		try{
			bbr=new BioBrickRepository();
		}catch(Exception e){
			throw new CircuitException("Could not load BioBrick Repository.");
		}
		
		JSONObject signal;
		int from;
		int to;

		//remember the first transcription factor for a And-gate.
		Map<Integer, String> tmpTF=new HashMap<Integer, String>();
		
		for(int i=0; i<JSONSignals.length(); i++){
			signal=JSONSignals.getJSONObject(i);

			//Signal from another Gate or input.
			if(signal.get("from") instanceof Integer){
				from=signal.getInt("from");
				if(!circuit.hasGateAt(from)){
					throw new CircuitException("Signal[from] points to non-existant Gate.");
				}
				
				//update from gate with the right CDS.
				if(circuit.gateAt(from).getCDS()==null){
					circuit.gateAt(from).setCDS(
						bbr.getCDS(signal.getString("protein"))
					);
				}else{
					//this is not the first connection from gate 'from'
					//check if this signal protein is the same as
					//already present, if not, throw an exception.
					if(!circuit.gateAt(from).getCDS().getName().equals(signal.getString("protein"))){
						throw new CircuitException("CDS for gate "+from+" is ambigious");
					}
				}
			}else{
				//input signal, add to the input list.
				if(!signal.getString("from").equals("input")){
					throw new CircuitException("Signal[from] should be either a integer index pointing to a gate or the string 'input'");
				}
				circuit.addInput(signal.getString("protein"));
			}

			//signal from another Gate, or output.
			if(signal.get("to") instanceof Integer){
				to=signal.getInt("to");
				if(!circuit.hasGateAt(to)){
					throw new CircuitException("Signal[to] points to non-existant Gate.");
				}

				//Not Gate can be connected right away.
				if(circuit.gateAt(to).getKind().equals("not")){
					circuit.gateAt(to).setPromotor(
						bbr.getNotPromotor(signal.getString("protein"))
					);
				}else{
					//and gate, requires two inputs, so wait till whe have two.
					if(!tmpTF.containsKey(to)){
						tmpTF.put(to, signal.getString("protein"));
					}else{
						//now we have two inputs, add the AndPromtor to the Gate.
						circuit.gateAt(to).setPromotor(
							bbr.getAndPromotor(signal.getString("protein"), tmpTF.get(to))
						);
						tmpTF.remove(to);
					}
				}
			}else{
				if(!signal.getString("to").equals("output")){
					throw new CircuitException("Signal[to] should be either a integer index pointing to a gate or the string 'output'");
				}
				//output signal, add to the output list.
				circuit.addOutput(signal.getString("protein"));
			}
		}

		//check voor leftovers 
		if(tmpTF.size()!=0){
			throw new CircuitException("At least one AND gate has only one input.");
		}

		/* validate the circuit. validate will throw exceptions when it
		 * encounters invalid things...
		 * Calling this will double some checks and is quite expensive,
		 * but since all circuits will be quite small, it should be no
		 * problem.
		 */
		circuit.validate();

		/* Add the input signal specification if present in JSON. 
		 */
		if(json.has("inputs")){
			JSONObject inputs=json.getJSONObject("inputs");
			
			if(!(inputs.has("length") && inputs.has("values"))){
				throw new CircuitException("JSON input definition should contain a length value and a set of input definitions for each input protein");
			}
			JSONObject values= inputs.getJSONObject("values");
			for(String p: JSONObject.getNames(values)){
				circuit.addSimulationInput(p, values.getString(p));
			}
		}
		
		return circuit;
	}
	
	/**
	 * Convert the current object state to JSON
	 * 
	 * @return Serialized JSON string representation of the Circuit.
	 */
	public String toJSONString(){
		JSONObject ret=new JSONObject();

		try{
			ret.put("name", this.getName());
			ret.put("description", this.getDescription());
			ret.put("gates", this.getGates());
			//@todo include signals...
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
	 * Return an SBML document for the current circuit.
	 *
	 * @todo implement
	 */
	public String toSBML(){
		//TODO: Implement toSBML
		return null;
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
