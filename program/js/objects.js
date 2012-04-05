/**
 * Info from http://api.jquery.com/
 * Synthetic Biology project (Biobrick Modeller/Simulator)
 * https://github.com/FelixAkk/synthbio
 *
 * Definition of objects
 */

/**
 * Circuits
 */
function Circuit(circuitName,description, gate, signals, groupings){
	this.name = circuitName;
	this.desc = description;
	this.gates = gate;
	this.signals = signal;
	this.groups = groupings;
}
Circuit.prototype.toString = function(){
	var gateString = "";
	for(var i in this.gates){
		gateString+=i.toString() + " - ";
	}
	var signalString = "";
	for(var i in this.signals){
		signalString+=i + " - ";
	}
	return this.name + ": " + this.desc + " constists of gates; " + gateString() + " and signals; " + signalString + " and groupings; " + this.groups;
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
Gate.prototype.fromJSON = function(json){
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
Signal.prototype.fromJSON = function(json){
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
CDS.prototype.fromJSON = function(json){
	var temp = $.parseJSON(json);
	return new CDS(temp.name, temp.k2, temp.d1, temp.d2);
}