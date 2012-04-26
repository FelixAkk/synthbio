/**
 * Synthetic Biology project (Biobrick Modeller/Simulator)
 * https://github.com/FelixAkk/synthbio
 */

package synthbio.models;


import org.json.JSONObject;

/**
 * Gate representation
 * @author jieter
 */
public class Gate{

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
	 * String representation in the form of
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
