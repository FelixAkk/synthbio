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
 * Circuit representation
 * @author jieter
 */
public class Circuit implements JSONString{
	private String name;
	private String description;

	private ArrayList<Gate> gates=new ArrayList<Gate>();
	

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

	public void addGate(Gate g){
		this.gates.add(g);
	}

	/**
	 * Do we have a gate with index?
	 */
	public boolean hasGateAt(int index){
		return index >= 0 && index < this.gates.size();
	}

	
	public Gate gateAt(int index){
		assert this.hasGateAt(index) : "No such Gate";
		return this.gates.get(index);
	}
	
	
	/**
	 * Validate the use of Proteins in the circuit.
	 *
	 * @return true if the use of proteins is valid.
	 */
/*
	@todo implement
	public boolean validateProteins(){
		Set<String> proteins=new HashSet<String>();

		for(Gate g: this.getGates()){

		}
	}
*/

	/**
	 * Return an collection of signals.
	 *
	 * Since signals are not stored in the Circuit object as such,
	 * they are derived from the set of Gates and references between
	 * them.
	 *
	 * @return A collection of all the signals in the circuit, including
	 * the input and output nodes.
	 *
	 * @todo implement
	 */
/*
	public Collection<Signal> collectSignals(){
		return null;
	}
*/
	
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
		//	todo fix this.
		//	ret.put("signals", this.collectSignals());
		}catch(Exception e){
			return "{\"error\":\"JSONException:"+e.getMessage()+"\"}";
		}
		return ret.toString();
	}

	/**
	 * Deserialize JSON to a Circuit object.
	 *
	 * @param The String containin JSON
	 * @return The Circuit.
	 */
	public static Circuit fromJSON(String json) throws CircuitException,JSONException {
		return Circuit.fromJSON(new JSONObject(json));
	}

	/**
	 * Deserialize JSON to a Circuit object.
	 * 
	 * Since Java data structures are more advanced than the JSON
	 * counterpart this method takes care of the non-trivial mappings of
	 * dumb Signals and Gates to a set of Gates connected by references.
	 *
	 * @param The JSONObject containing the circuit information.
	 * @return The Circuit.
	 */
	public static Circuit fromJSON(JSONObject json) throws CircuitException,JSONException {
		Circuit ret=new Circuit(
			json.getString("name"),
			json.getString("description")
		);
		
		
		/* Fetch the gates, add them in their order to a local list of
		 * gates.
		 */
		JSONArray JSONGates=json.getJSONArray("gates");
		
		//If no gates present, don't bother about the Signals, just return.
		if(JSONGates.length()<=0){
			return ret;
		}
		
		for(int i=0; i<JSONGates.length(); i++){
			JSONObject JSONGate=JSONGates.getJSONObject(i);

			Gate gate=new Gate(Position.fromJSON(JSONGate.getJSONObject("position")));
			gate.setKind(JSONGate.getString("kind"));
			
			ret.addGate(gate);
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
				if(!ret.hasGateAt(from)){
					throw new CircuitException("Signal[from] points to non-existant Gate.");
				}
				
				//update from gate with the right CDS.
				if(ret.gateAt(from).getCDS()==null){
					ret.gateAt(from).setCDS(
						bbr.getCDS(signal.getString("protein"))
					);
				}else{
					//this is not the first connection from gate 'from'
					//check if this signal protein is the same as
					//already present, if not, throw an exception.
					if(!ret.gateAt(from).getCDS().getName().equals(signal.getString("protein"))){
						throw new CircuitException("CDS for gate "+from+" is ambigious");
					}
				}
			}else{
				//input signal, nothing to do at the input side.
			}

			//signal from another Gate, or output.
			if(signal.get("to") instanceof Integer){
				to=signal.getInt("to");
				if(!ret.hasGateAt(to)){
					throw new CircuitException("Signal[to] points to non-existant Gate.");
				}

				//Not Gate can be connected right away.
				if(ret.gateAt(to).getKind().equals("not")){
					ret.gateAt(to).setPromotor(
						bbr.getNotPromotor(signal.getString("protein"))
					);
				}else{
					//and gate, requires two inputs, so wait till whe have two.
					if(!tmpTF.containsKey(to)){
						tmpTF.put(to, signal.getString("protein"));
					}else{
						ret.gateAt(to).setPromotor(
							bbr.getAndPromotor(signal.getString("protein"), tmpTF.get(to))
						);
						tmpTF.remove(to);
					}
				}
			}else{
				//output signal, nothing to do at the output.
			}
		}

		if(tmpTF.size()!=0){
			throw new CircuitException("At least one AND gate has only one input.");
		}

/*		@todo fix this.
		if(!ret.validateProteins()){
			throw new CircuitException("Incorrect Protein assignment");
		}
*/

		return ret;
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
	public String toString(){
		String ret="Circuit: "+this.getName()+" | "+this.getDescription()+"\n";
		for(Gate g: this.getGates()){
			ret+=g.toString()+"\n";
		}
		return ret;
	}
}
