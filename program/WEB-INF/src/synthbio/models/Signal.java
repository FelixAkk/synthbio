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




/**
 * Signal representation
 * @author jieter
 */
public class Signal{

	/**
	 * References to Gates from and to this Signal flows.
	 *
	 * If from is null, the signal is an input of the circuit.
	 * If to is null, the signal is an output of the circuit.
	 */
	private Gate from;
	private Gate to;

	/**
	 * Protein representing this Signal.
	 * 
	 * CDS could be used here aswell, but biologically it does not make
	 * sense since the signal is not the Coding Sequence, but just the
	 * protein which is producing it.
	 * It could also be derived from the CDS of this.from, but since
	 * the Signal object acts mainly as a bridge between the JSON
	 * representation and the Circuit model, we'll use an explicit field
	 * anyway.
	 */
	private  String protein;

	public Signal(Gate from, Gate to, String protein){
		this.setFrom(from);
		this.setTo(to);

		this.setProtein(protein);
	}

	/*
	 * Getters
	 */
	public Gate getFrom(){
		return this.from;
	}
	public Gate getTo(){
		return this.to;
	}
	public String protein(){
		return this.protein;
	}

	/*
	 * Setters
	 */
	public void setFrom(Gate from){
		this.from=from;
	}
	public void setTo(Gate to){
		this.to=to;
	}
	public void setProtein(String protein){
		this.protein=protein;
	}
}