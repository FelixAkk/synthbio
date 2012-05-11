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
 * @author	Thomas van Helden, Jan Pieter Waagmeester, Felix Akkermans, Niels Doekemeijer
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
 *
 * @param t Type/class of gate, should be either "not", "and" or "compound"
 */
synthbio.Gate = function(t, position){
	synthbio.util.assert(t === "not" || t === "and", "Only 'not' and 'and' allowed as type");

	this.type = t;
	this.setPosition(position);
};
synthbio.Gate.prototype.getType = function(){
	return this.type;
}
synthbio.Gate.prototype.getX = function(){
	return this.position.getX();
};
synthbio.Gate.prototype.getY = function(){
	return this.position.getY();
};
synthbio.Gate.prototype.setPosition = function(position){
	if(position instanceof synthbio.Point) {
		this.position=position;
	// if position is an array (i.d. has a length property)
	} else if(position.length && !isNaN(parseInt(position[0], 10)) && !isNaN(parseInt(position[1], 10))) {
		this.position = new synthbio.Point(position);
	} else {
		throw "Position could not be parsed";
	}
};
synthbio.Gate.prototype.getImage = function(html){
	var img = "gates/" + this.getType() + ".svg";
	if (html)
		return "<embed src=\"img/" + img + "\" type=\"image/svg+xml\" />";
	else
		return img;
}
synthbio.Gate.prototype.getInputCount = function(){
	//TODO: better way to determine number of inputs/outputs
	if(this.type == "not")
		return 1;
	else if (this.type == "and")
		return 2;
	else
		throw "Cannot determine number of input gates";
}
synthbio.Gate.prototype.getOutputCount = function(){
	if(this.type == "not")
		return 1;
	else if (this.type == "and")
		return 1;
	else
		throw "Cannot determine number of output gates";
}
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
synthbio.Circuit = function(circuitName, desc, gates, signals, groupings){
	this.name = circuitName;
	this.description = desc;

	//make gates, signals and groupings default to empty an array 
	this.gates = gates || [];
	this.signals = signals || [];
	this.groups = groupings || []; 
};

synthbio.Circuit.prototype.toString = function(){
	return this.name + ": " + this.desc + " consists of gates:{ " + this.gates.toString() + " } and signals:{ " + this.signals.toString() + " } and groupings:{ " + this.groups + "}";
};

/**
 * Parses and constructs a circuit from the provided JSON. Assumes a valid circuit.
 *
 * @param json JSON (usually from a .syn) file representing a circuit.
 */
synthbio.Circuit.fromJSON = function(json) {
	var map = $.parseJSON(json);
	
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

	return synthbio.Circuit(map.name, map.description, gates, signals, groups);
};

/**
 * Cleans the current workspace and loads the provided circuit.
 *
 * @param circuit An instance of synthbio.Circuit
 */
synthbio.loadCircuit = function(circuit) {
	synthbio.util.assert(circuit instanceof synthbio.Circuit, "Provided circuit is not an instance of sythnbio.Circuit."
	+ " This is required.");

	// Install model
	synthbio.model = circuit;
	// Clear grid
	$('grid-container').html = "";
	// Setup inputs/outputs
	synthbio.gui.addInputOutputFields();
	// Show the circuit; add all the elements
	$.each(model.gates, function(index, element) {
		synthbio.gui.displayGate(element);
	})
	$.each(model.signals, function(index, element){
		signals[i]=synthbio.Signal.fromMap(elem);
	});
};

/**
 * Returns the index of a gate (-1 if not found)
 * @param gate An instance of synthbio.Gate
 */
synthbio.Circuit.prototype.indexOfGate = function(gate) {
	var idx = this.gates.indexOf(gate);
	if (idx < 0)
		throw "Gate is not present in circuit";
	return idx;
}

/**
 * Sort of a setter for gates.
 * @param gate An instance of synthbio.Gate
 */
synthbio.Circuit.prototype.addGate = function(gate, position) {
	if (!(gate instanceof synthbio.Gate) && position)
		gate = new synthbio.Gate(gate, position);

	if(gate instanceof synthbio.Gate) {
		this.gates.push(gate);
	} else {
		throw "Provided gate is not of type synthbio.Gate";
	}

	return gate;
};

/**
 * Getter for the array of gates.
 */
synthbio.Circuit.prototype.getGates = function() {
	return this.gates;
};
/**
 * Getter for a gate on index. Only returns if there is a gate on that index.
 * @param index Index in the array.
 */
synthbio.Circuit.prototype.getGate = function(index) {
	var gate = this.gates[index];
	if(gate == undefined) {
		throw "No gate on index " + index + ".";
	} else {
		return gate;
	}
}

/**
 * Sort of a setter for signals.
 * @param signal An instance of synthbio.Signal
 */
synthbio.Circuit.prototype.addSignal = function(signal, origin, destination) {
	if (!(signal instanceof synthbio.Signal) && origin !== undefined && destination !== undefined)
		signal = new synthbio.Signal(signal, origin, destination);

	if(signal instanceof synthbio.Signal) {
		this.signals.push(signal);
	} else {
		throw "Provided signal is not of type synthbio.Signal";
	}

	return signal;
};

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