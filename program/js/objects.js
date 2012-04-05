/**
 * Info from http://api.jquery.com/
 * Synthetic Biology project (Biobrick Modeller/Simulator)
 * https://github.com/FelixAkk/synthbio
 *
 * Definition of objects
 */

/** 
 * Gates 
 * Gates hold a type, an X coordinate and a Y coordinate
 */
function Gate(t, xCoord, yCoord){
	this.type = t;
	this.x = xCoord;
	this.y = yCoord;
}
Gate.prototype.toString = function(){
	return this.type + ": X = " + this.x + ", Y = "+ this.y;
}

/**
 * Signals
 * Signals hold proteins, an origin and a destination
 */
function Signal(prot, origin, destination){
	this.protein = prot;
	this.from = origin;
	this.to = destination;
}
Signal.prototype.toString = function(){
	return this.protein + " links " + this.from + " with " + this.to;
}

/**
 * CDSs
 * CDSs holds proteinName, k2, d1, d2
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