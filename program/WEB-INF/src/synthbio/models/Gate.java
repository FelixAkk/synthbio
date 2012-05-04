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

import java.util.List;
import org.json.JSONObject;
import org.json.JSONString;

/**
 * Gate representation
 * @author jieter
 */
public class Gate implements JSONString{

	/**
	 * Kind of Gate (and, not).
	 */
	private String kind;

	/**
	 * The Promotor object.
	 */
	private Promotor promotor;

	/**
	 * The Coding Sequence.
	 */
	private CDS cds;

	/**
	 * The Position the gate is at.
	 */
	private Position position;

	/**
	 * Construct a Gate
	 */
	public Gate(Promotor promotor, CDS cds, Position position){
		this.setPromotor(promotor);
		if(promotor!=null){
			this.setKind(promotor.kind());
		}
		this.setCDS(cds);
		this.setPosition(position);
	}
	public Gate(){
		this(null, null, new Position());
	}
	public Gate(Position position){
		this(null, null, position);
	}

	/*
	 * Getters
	 */
	public String getKind(){
		return this.kind;
	}
	public Promotor getPromotor(){
		return this.promotor;
	}
	public CDS getCDS(){
		return this.cds;
	}
	public Position getPosition(){
		return this.position;
	}

	/*
	 * Setters
	 */
	public void setKind(String kind){
		this.kind=kind;
	}
	public void setPromotor(Promotor p){
		this.promotor=p;
	}
	public void setCDS(CDS c){
		this.cds=c;
	}
	public void setPosition(Position p){
		this.position=p;
	}

	/**
	 * Return a list of inputs for this Gate
	 */
	public List<String> getInputs(){
		if(this.getPromotor()==null){
			return null;
		}
		return this.getPromotor().listTfs();
	}
		
	/**
	 * Does the Gate have a certain input protein?
	 */
	public boolean hasInput(String protein){
		if(this.getPromotor()==null){
			return false;
		}
		return this.getPromotor().hasTf(protein);
	}
	
	/**
	 * Return the output for this gate or Null if it is not defined.
	 */
	public String getOutput(){
		if(this.getCDS()==null){
			return null;
		}
		return this.getCDS().getName();
	}
	
	/**
	 * Does the Gate have a certain output protein?
	 */
	public boolean hasOutput(String protein){
		return this.getOutput().equals(protein);
	}
	
	
	/**
	 * Convert Gate to JSON string.
	 */
	public String toJSONString(){
		JSONObject ret=new JSONObject();
		try{
			ret.put("kind", this.getKind());
			ret.put("position", this.getPosition());
			
		}catch(Exception e){
			return "{\"error\":\"JSONException:"+e.getMessage()+"\"}";
		}
		return ret.toString();
	}
	
	/**
	 * Handy human readable String representation in the form of.
	 *
	 * [and(A,B)->C @(x,y)]
	 * [not(D)->E @(x,y)]
	 */
	public String toString(){
		String ret=this.getKind();
		if(this.getPromotor()==null){
			ret+="(?)";
		}else{
			ret+="("+this.getPromotor()+")";
		}
		ret+="->";
		if(this.getCDS()==null){
			ret+="?";
		}else{
			ret+=this.getCDS();
		}
		ret+=" @";
		if(this.getPosition()==null){
			ret+="(?,?)";
		}else{
			ret+=this.getPosition();
		}
		return "["+ret+"]";
	}

}
