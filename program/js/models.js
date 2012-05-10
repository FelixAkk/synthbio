/**
 * Project Zelula
 *
 * Contextproject TI2800
 * TU Delft - University of Technology
 *
 * Authors:
 * Felix Akkermans, Niels Doekemeijer, Thomas van Helden
 * Albert ten Napel, Jan Pieter Waagmeester
 *
 * https://github.com/FelixAkk/synthbio
 *
 * @author	Thomas van Helden, JanPieter Waagmeester, Felix Akkermans, Niels Doekenmeijer
 *
 * Definition of models.
 * More info on jquery http://api.jquery.com/
 */

/*jslint devel: true, browser: true, sloppy: true, stupid: false, white: true, maxerr: 50, indent: 4 */
/*global $ */

/**
 * syntbio package.
 */
var synthbio = synthbio || {};


/**
 * Point class
 * represents a point in R^2.
 *
 * @author jieter
 * 
 * Constructor can be called with:
 *  - two numbers: x, y
 *  - an array with two numbers: [x, y]
 */
synthbio.Point = function(x, y){
	//construct from array x if y is undefined.
	if(y === undefined) {
		this.x=x[0];
		this.y=x[1];
	} else {
		this.x=x;
		this.y=y;
	}
};
synthbio.Point.prototype.getX = function(){
	return this.x;
};
synthbio.Point.prototype.getY = function(){
	return this.y;
};
synthbio.Point.prototype.distanceTo = function(that){
	if(!(that instanceof synthbio.Point)){
		throw "Argument must be of type Point";
	}
	return Math.sqrt(Math.pow(this.x-that.x, 2) + Math.pow(this.y-that.y, 2));
};

synthbio.Point.fromMap=function(map){
	return new synthbio.Point(map.x, map.y);
};
synthbio.Point.fromJSON=function(json){
	return synthbio.Point.fromMap($.parseJSON(json));
};

/** 
 * Gates 
 * Gates hold a type, an X coordinate and a Y coordinate
 * Note: To convert Gates to JSON use JSON.stringify(Gate)
 */
synthbio.Gate = function(t, position){
	this.type = t;
	if(position instanceof synthbio.Point) {
		this.position=position;
	// if position is an array (i.d. has a length property)
	} else if(position.length && !isNaN(parseInt(position[0], 10)) && !isNaN(parseInt(position[1], 10))) {
		this.position = new synthbio.Point(position);
	} else {
		throw "Position could not be parsed";
	}
};

synthbio.Gate.prototype.getX = function(){
	return this.position.getX();
};
synthbio.Gate.prototype.getY = function(){
	return this.position.getY();
};
synthbio.Gate.prototype.toString = function(){
	return this.type + ": X = " + this.position.getX() + ", Y = "+ this.position.getY();
};
synthbio.Gate.fromMap = function(map){
	return new synthbio.Gate(map.type, synthbio.Point.fromMap(map.position));
};
synthbio.Gate.fromJSON = function(json){
	return synthbio.Gate.fromMap($.parseJSON(json));
};


/**
 * Signals
 * Signals hold proteins, an origin and a destination
 * Note: To convert Signals to JSON use JSON.stringify(Signal)
 */
synthbio.Signal = function(prot, origin, destination){
	this.protein = prot;
	this.from = origin;
	this.to = destination;
};
synthbio.Signal.prototype.toString = function(){
	return this.protein + " links " + this.from + " with " + this.to;
};
synthbio.Signal.fromMap = function(map){
	return new synthbio.Signal(map.protein, map.from, map.to);
};
synthbio.Signal.fromJSON = function(json){
	return synthbio.Signal.fromMap($.parseJSON(json));
};


/**
 * Circuits
 * Circuits hold a name, a description, gates, how gates are grouped and which signals connect them
 * Note: To convert Circuits into JSON use JSON.stringify(Circuit)
 */
synthbio.Circuit = function(circuitName, desc, gates, signal, groupings){
	this.name = circuitName;
	this.description = desc;

	//make gates, signals and groupings default to empty an array 
	this.gates = gates || [];
	this.signals = signal || [];
	this.groups = groupings || []; 
};

synthbio.Circuit.prototype.toString = function(){
	return this.name + ": " + this.desc + " consists of gates:{ " + this.gates.toString() + " } and signals:{ " + this.signals.toString() + " } and groupings:{ " + this.groups + "}";
};

synthbio.Circuit.fromMap = function(map){
	var gates=[], signals=[], groups=[];
	$.each(map.gates, function(i, elem){
		gates[i]=synthbio.Gate.fromMap(elem);
	});

	$.each(map.signals, function(i, elem){
		signals[i]=synthbio.Signal.fromMap(elem);
	});

	//TODO: implement grouping.
	//~ $.each(map.groups, function(i, elem){
	//~		groups[i]=synthbio.Group.fromMap(elem);
	//~ });
	return new synthbio.Circuit(map.name, map.description, gates, signals, groups);
};
synthbio.Circuit.fromJSON= function(json){
	return synthbio.Circuit.fromMap($.parseJSON(json));
};

// (Jieter): eval is a reserved keyword in javascript, could be used...
// What exactly is your idea for the function of eval?
//~ synthbio.Circuit.prototype.eval = function(){
	//~ return false;
//~ };
//~ 

/**
 * CDSs
 * CDSs holds proteinName, k2, d1, d2
 * Note: To convert CDSs to JSON use JSON.stringify(CDS)
 */
synthbio.CDS = function(proteinName, k, dOne, dTwo) {
	this.name = proteinName;
	this.k2 = k;
	this.d1 = dOne;
	this.d2 = dTwo;
};
synthbio.CDS.prototype.toString = function(){
	return "CDS - name = " + this.name + ", k2 = "+ this.k2+ ", d1 = " + this.d1+ ", d2 = " + this.d2;
};
synthbio.CDS.fromMap = function(map){
	return new synthbio.CDS(map.name, map.k2, map.d1, map.d2);
};
synthbio.CDS.fromJSON = function(json){
	return synthbio.CDS.fromMap($.parseJSON(json));	
};

/**
 * The object that will represent the entire circuit that is in the app. Might be a slight delay between synchronization
 * but should correspond to the circuit pretty accurately. This should only matter with trivial things like the position
 * of gates. This object is instantiated as an empty circuit with no name or description.
 */
synthbio.model = new synthbio.Circuit("", "");