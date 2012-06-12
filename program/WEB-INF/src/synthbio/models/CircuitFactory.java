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

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import synthbio.models.CircuitException;
import synthbio.files.BioBrickRepository;

/**
 * The circuit factory takes care of creating Circuit objects from JSON
 * strings. Since some information from the BioBrickRepositor has to be
 * mixed in, this is separated from the Circuit class, since it has to
 * have no knowledge about the BioBrick Repository.
 */
public class CircuitFactory{
	/**
	 * The BioBrick Repository
	 */ 
	private BioBrickRepository biobrickRepository;

	/**
	 * Construct the factory with a specified repository.
	 *
	 * @param bbr The BioBrick repository to be used.
	 */
	public CircuitFactory(BioBrickRepository bbr){
		this.biobrickRepository=bbr;
	}

	/**
	 * Construct with the default repository.
	 *
	 * @throws CircuitException if the BBR cannot be loaded.
	 */
	public CircuitFactory() throws CircuitException{
		try{
			this.biobrickRepository=new BioBrickRepository();
		}catch(Exception e){
			throw new CircuitException("Could not load BioBrick repository: "+e.getMessage());
		}
	}

	public BioBrickRepository getBbr(){
		return this.biobrickRepository;
	}

	/**
	 * Deserialize JSON to a Circuit object.
	 *
	 * @param json The String containing JSON
	 * @return The Circuit.
	 */
	public Circuit fromJSON(String json) throws CircuitException, JSONException{
		return this.fromJSON(new JSONObject(json));
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
	public Circuit fromJSON(JSONObject json) throws CircuitException, JSONException {
		if(!(json.has("name") && json.has("description"))) {
			throw new CircuitException("JSON should have a name and a description");
		}
		Circuit circuit=new Circuit(
			json.getString("name"),
			json.getString("description")
		);
		
		/* Fetch the gates, add them in their order to a local list of
		 * gates.
		 */
		JSONArray JSONGates=json.getJSONArray("gates");
		
		//If no gates present, 
		if(JSONGates.length()<=0){
			if(json.getJSONArray("signals").length() != 0) {
				throw new CircuitException("Circuit has no gates present, but contains signals.");
			}
			return circuit;
		}
		
		for(int i=0; i<JSONGates.length(); i++){
			JSONObject JSONGate=JSONGates.getJSONObject(i);

			if(!(JSONGate.has("position") && JSONGate.has("kind"))){
				throw new CircuitException("Gate should contain fields: position, kind.");
			}
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
		
		JSONObject signal;
		int from;
		int to;

		//remember the first transcription factor for a And-gate.
		Map<Integer, String> tmpTF=new HashMap<Integer, String>();
		
		for(int i=0; i<JSONSignals.length(); i++){
			signal=JSONSignals.getJSONObject(i);

			if(!(signal.has("protein") && signal.has("from") && signal.has("to"))){
				throw new CircuitException("Signal should contain fields: protein, from, to.");
			}
			
			// Make sure the protein string is not empty.
			if(signal.getString("protein").length() < 1){
				throw new CircuitException("Signal (" + signal.get("from") + " -> " + signal.get("to") + ") should have a protein assigned.");
			}
			//Signal from another Gate or input.
			if(signal.get("from") instanceof Integer){
				from=signal.getInt("from");
				if(!circuit.hasGateAt(from)){
					throw new CircuitException("Signal[from] points to non-existant Gate.");
				}
				
				//update from gate with the right CDS.
				if(circuit.gateAt(from).getCDS()==null){
					circuit.gateAt(from).setCDS(
						this.getBbr().getCDS(signal.getString("protein"))
					);
				}else{
					//this is not the first connection from gate 'from'
					//check if this signal protein is the same as
					//already present, if not, throw an exception.
					if(!circuit.gateAt(from).getCDS().getName().equals(signal.getString("protein"))){
						throw new CircuitException("CDS for gate "+circuit.gateAt(from).toString()+" is ambigious.");
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
						this.getBbr().getNotPromotor(signal.getString("protein"))
					);
				}else{
					//and gate, requires two inputs, so wait till whe have two.
					if(!tmpTF.containsKey(to)){
						tmpTF.put(to, signal.getString("protein"));
					}else{
						//now we have two inputs, add the AndPromtor to the Gate.
						circuit.gateAt(to).setPromotor(
							this.getBbr().getAndPromotor(signal.getString("protein"), tmpTF.get(to))
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

		/* Validate the circuit. validate will throw exceptions when it
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
			SimulationSetting ss = new SimulationSetting();
			
			//store the simulation length
			ss.setLength(inputs.getInt("length"));

			//tickWidth, lowLevel and highLevel are optional.
			if(inputs.has("tickWidth")) {
				ss.setTickWidth(inputs.getDouble("tickWidth"));
			}
			if(inputs.has("lowLevel")) {
				ss.setLowLevel(inputs.getDouble("lowLevel"));
			}
			if(inputs.has("highLevel")) {
				ss.setHighLevel(inputs.getDouble("highLevel"));
			}
			
			//store the simulation values for each protein
			if(inputs.getJSONObject("values") == null) {
				try{
					ss.loadInputCSV(inputs.getString("values"));
				}catch(Exception e) {
					throw new CircuitException("Error parsing CSV: "+e.getMessage());
				}
			}else {
				JSONObject values = inputs.getJSONObject("values");
				
				if(values.length() == 0) {
					throw new CircuitException("No actual simulation inputs defined.");
				}else{
					for(String protein: values.getNames(values)){
						if(!circuit.hasInput(protein)){
							throw new CircuitException("Input definition has a protein which is not an input in the signal section");
						}
						ss.addInput(protein, values.getString(protein));
					}
				}
			}
			circuit.setSimulationSetting(ss);
		}
		
		return circuit;
	}
}
