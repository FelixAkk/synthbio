/**
 * Info from http://api.jquery.com/
 * Synthetic Biology project (Biobrick Modeller/Simulator)
 * https://github.com/FelixAkk/synthbio
 * @author	Thomas van Helden
 * Definition of objects
 */

/**
 * Circuits
 * Circuits hold a name, a description, gates, how gates are grouped and which signals connect them
 * Note: To convert Circuits into JSON use JSON.stringify(Circuit)
 */
function Circuit(circuitName,description, gate, signal, groupings){
	this.name = circuitName;
	this.desc = description;
	this.gates = gate;
	this.signals = signal;
	this.groups = groupings;
}
Circuit.prototype.toString = function(){
	return this.name + ": " + this.desc + " consists of gates:{ " + this.gates.toString() + " } and signals:{ " + this.signals.toString() + " } and groupings:{ " + this.groups + "}";
}
Circuit.fromJSON= function(json){
	var temp = $.parseJSON(json);
	return new Circuit(temp.name, temp.desc, temp.gates, temp.signals, temp.groups);
}
Circuit.prototype.eval = function(){
	return false;
}
 
/** 
 * Gates 
 * Gates hold a type, an X coordinate and a Y coordinate
 * Note: To convert Gates to JSON use JSON.stringify(Gate)
 */
function Gate(t, xCoord, yCoord){
	this.type = t;
	this.x = xCoord;
	this.y = yCoord;
}
Gate.prototype.toString = function(){
	return this.type + ": X = " + this.x + ", Y = "+ this.y;
}
Gate.fromJSON = function(json){
	var temp = $.parseJSON(json);
	return new Gate(temp.type, temp.x, temp.y);
}

/**
 * Signals
 * Signals hold proteins, an origin and a destination
 * Note: To convert Signals to JSON use JSON.stringify(Signal)
 */
function Signal(prot, origin, destination){
	this.protein = prot;
	this.from = origin;
	this.to = destination;
}
Signal.prototype.toString = function(){
	return this.protein + " links " + this.from + " with " + this.to;
}
Signal.fromJSON = function(json){
	var temp = $.parseJSON(json);
	return new Signal(temp.protein, temp.from, temp.to);
}

/**
 * CDSs
 * CDSs holds proteinName, k2, d1, d2
 * Note: To convert CDSs to JSON use JSON.stringify(CDS)
 */
function CDS(proteinName, k, dOne, dTwo) {
	this.name = proteinName;
	this.k2 = k;
	this.d1 = dOne;
	this.d2 = dTwo;
}
CDS.prototype.toString = function(){
	return "CDS - name = " + this.name + ", k2 = "+ this.k2+ ", d1 = " + this.d1+ ", d2 = " + this.d2;
}
CDS.fromJSON = function(json){
	var temp = $.parseJSON(json);
	return new CDS(temp.name, temp.k2, temp.d1, temp.d2);
}