/**
 * Synthetic Biology project (Biobrick Modeller/Simulator)
 * https://github.com/FelixAkk/synthbio
 */

package synthbio.models;



/**
 * Gate representation
 * @author jieter
 */
public class Gate{

	private Promotor promotor;
	private CDS cds;

	private Position position;

	public Gate(String kind, Position p){

	}
	public Gate(Signal inA, Signal inB, Signal out, Position p){
		this.promotor=Promotor.fromSignals(inA, inB);
		this.cds=CDS.fromSignal(out);
		this.position=p;
	}
	public Gate(Signal inA, Signal inB, Signal out){
		this(inA, inB, out, null);
	}
	
	public Gate(Signal in, Signal out, Position p){
		this.promotor=Promotor.fromSignal(in);
		this.cds=CDS.fromSignal(out);
		this.position=p;
	}
	public Gate(Signal in, Signal out){
		this(in, out, new Position());
	}
	
	public boolean gateInvariant(){
		return true;
	}
}
