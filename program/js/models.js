/**
 * Project Zelula
 *
 * Contextproject TI2800
 * TU Delft - University of Technology
 *
 * Authors:
 *  Felix Akkermans, Niels Doekemeijer, Thomas van Helden
 *  Albert ten Napel, Jan Pieter Waagmeester
 *
 * https://github.com/FelixAkk/synthbio
 *
 * @author Thomas van Helden & Felix Akkermans & Jan-Pieter Waagmeester & Niels Doekemeijer
 *
 * JavaScript Document concerning definition of models.
 */

/*jslint devel: true, browser: true, vars: true, plusplus: true, regexp: true, sloppy: true, white: true, maxerr: 50, indent: 4 */
/*global $, synthbio */

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
		this.x = x[0];
		this.y = x[1];
	} else {
		this.x = x;
		this.y = y;
	}
};
synthbio.Point.fromMap=function(map){
	return new synthbio.Point(map.x, map.y);
};
synthbio.Point.fromJSON=function(json){
	return synthbio.Point.fromMap($.parseJSON(json));
};

synthbio.Point.prototype.toString = function(){
	return "X = " + this.getX() + ", Y = "+ this.getY();
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
	return Math.sqrt(
		Math.pow(this.getX() - that.getX(), 2) + 
		Math.pow(this.getY() - that.getY(), 2)
	);
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

	this.kind = t;
	this.setPosition(position);
};
synthbio.Gate.fromMap = function(map){
	return new synthbio.Gate(map.kind, synthbio.Point.fromMap(map.position));
};
synthbio.Gate.fromJSON = function(json){
	return synthbio.Gate.fromMap($.parseJSON(json));
};

synthbio.Gate.prototype.toString = function(){
	return this.getKind() + ": (" + this.position.toString() + ")";
};
synthbio.Gate.prototype.getKind = function(){
	return this.kind;
};
synthbio.Gate.prototype.getX = function(){
	return this.position.getX();
};
synthbio.Gate.prototype.getY = function(){
	return this.position.getY();
};
synthbio.Gate.prototype.setPosition = function(position){
	if(position instanceof synthbio.Point) {
		this.position = position;
	} else if($.isArray(position) && !isNaN(parseInt(position[0], 10)) && !isNaN(parseInt(position[1], 10))) {
		this.position = new synthbio.Point(position);
	} else {
		throw "Position could not be parsed";
	}
};
synthbio.Gate.prototype.getImage = function(html){
	var img = "gates/" + this.getKind() + ".svg";
	if (html) {
		return '<img src="img/' + img + '" />';
	} else {
		return img;
	}
};
synthbio.Gate.prototype.getInputCount = function(){
	if(this.getKind() === "not") {
		return 1;
	} else if (this.getKind() === "and") {
		return 2;
	} else {
		throw "Cannot determine number of input gates";
	}
};
synthbio.Gate.prototype.getOutputCount = function(){
	if(this.getKind() === "not") {
		return 1;
	} else if (this.getKind() === "and") {
		return 1;
	} else {
		throw "Cannot determine number of output gates";
	}
};


/**
 * Signal
 * Signals hold proteins, an origin and a destination. A.K.A. wire, connection.
 * 
 * Note: To convert Signals to JSON use JSON.stringify(Signal)
 */
synthbio.Signal = function(prot, from, to, fromEndpoint, toEndpoint){
	this.protein = prot;
	this.from = from;
	this.to = to;
	this.fromEndpoint = fromEndpoint;
	this.toEndpoint = toEndpoint;
};
synthbio.Signal.fromMap = function(map){
	return new synthbio.Signal(map.protein, map.from, map.to, map.fromEndpoint, map.toEndpoint);
};
synthbio.Signal.fromJSON = function(json){
	return synthbio.Signal.fromMap($.parseJSON(json));
};

synthbio.Signal.prototype.toString = function(){
	return this.getProtein() + " links " + this.getFrom() + " with " + this.getTo();
};
synthbio.Signal.prototype.getProtein = function () {
	return this.protein;
};
synthbio.Signal.prototype.getFrom = function () {
	return this.from;
};
synthbio.Signal.prototype.getTo = function () {
	return this.to;
};
synthbio.Signal.prototype.isInput = function () {
	return this.getFrom() === 'input';
};
synthbio.Signal.prototype.isOutput = function () {
	return this.getTo() === 'output';
};

synthbio.Signal.prototype.setProtein = function(protein) {
	this.protein = protein;
	if (this.onProteinChange instanceof Function) {
		this.onProteinChange(this);
	}
};

synthbio.Signal.prototype.toJSON = function() {
	return {
		"protein": this.getProtein(),
		"from": this.getFrom(),
		"to": this.getTo(),
		"fromEndpoint": this.fromEndpoint,
		"toEndpoint": this.toEndpoint
	};
};
synthbio.Signal.prototype.setFrom = function(from) {
	this.from = from;
};
synthbio.Signal.prototype.setTo = function(to) {
	this.to = to;
};

/**
 * Circuit
 * Circuits hold a name, a description, gates, which signals connect them
 * and optional grouping information.
 * 
 * Note: To convert Circuits into JSON use JSON.stringify(Circuit)
 */
synthbio.Circuit = function(circuitName, desc, gates, signals, groupings, inputs){
	this.name = circuitName;
	this.description = desc;

	//make gates, signals and groupings default to empty an array 
	this.gates = gates || [];
	this.signals = signals || [];
	this.groups = groupings || [];

	//Create the SimulationSetting object.
	this.inputs = inputs || new synthbio.SimulationSetting();
	this.inputs.bindCircuit(this);
};

/**
 *
 * @param map Takes a JavaScript object which is a map representing the Circuit and converts it into the type
 * synthbio.Circuit
 */
synthbio.Circuit.fromMap = function(map) {
	var circuit = new synthbio.Circuit(map.name, map.description);

	//add the gates
	$.each(map.gates, function(i, elem) {
		circuit.addGate(synthbio.Gate.fromMap(elem));
	});

	//and the signals
	$.each(map.signals, function(i, elem) {
		circuit.addSignal(synthbio.Signal.fromMap(elem));
	});

	//If input information is present, add that as well
	if (map.inputs) {
		circuit.setSimulationInput(map.inputs);
	}

	//Copy remaining properties to object, but forget gates/signals/inputs
	map = $.extend({}, map);
	delete map.gates;
	delete map.signals;
	delete map.inputs;

	var p;
	for (p in map) {
		if (map.hasOwnProperty(p) && (circuit[p] === undefined)) {
			circuit[p] = map[p];
		}
	}

	return circuit;
};

/**
 * Parses and constructs a circuit from the provided JSON. Assumes a valid circuit.
 *
 * @param json JSON (usually from a .syn) file representing a circuit.
 */
synthbio.Circuit.fromJSON = function(json) {
	var map = $.parseJSON(json);

	return synthbio.Circuit.fromMap(map);
};

synthbio.Circuit.prototype.toString = function(){
	return this.getName() + ": " + this.getDescription() +
		" consists of gates:{ " + this.getGates().toString() + " }" +
		" and signals:{ " + this.getSignals().toString() + " }" +
		" and groupings:{ " + this.getGroups() + "}" +
		" and SimulationSetting: {" + this.getSimulationSetting() + "}";
};
synthbio.Circuit.prototype.getName = function() {
	return this.name;
};
synthbio.Circuit.prototype.getDescription = function() {
	return this.description;
};
synthbio.Circuit.prototype.setName = function(name) {
	this.name = name;
};
synthbio.Circuit.prototype.setDescription = function(description) {
	this.description = description;
};
synthbio.Circuit.prototype.getGates = function() {
	return this.gates;
};
synthbio.Circuit.prototype.getSignals = function() {
	return this.signals;
};
synthbio.Circuit.prototype.getGroups = function() {
	return this.groups;
};

synthbio.Circuit.prototype.getSimulationSetting = function(){
	return this.inputs;
};
synthbio.Circuit.prototype.setSimulationInput = function(inputs){
	if(inputs instanceof synthbio.SimulationSetting) {
		this.inputs = inputs;
	} else {
		this.inputs = new synthbio.SimulationSetting(inputs);
	}
	this.inputs.bindCircuit(this);
};

/**
 * Return a list of input proteins.
 */
synthbio.Circuit.prototype.getInputSignals = function() {
	var inputs=[];
	$.each(this.getSignals(), function(index, signal){
		if(signal.isInput()){
			inputs.push(signal.getProtein());
		}
	});
	return inputs;
};

synthbio.Circuit.prototype.hasInputSignal = function(protein) {
	return $.inArray(protein, this.getInputSignals());
};

/**
 * Returns the index of a gate (-1 if not found)
 * @param gate An instance of synthbio.Gate
 */
synthbio.Circuit.prototype.indexOfGate = function(gate) {
	var idx = this.gates.indexOf(gate);
	if (idx < 0) {
		throw "Gate is not present in circuit";
	}
	return idx;
};

/**
 * Check if gate exists (throws exception if not existent).
 * @return Index of gate
 */
synthbio.Circuit.prototype.checkGateExists = function(gate) {
	var idx = (gate instanceof synthbio.Gate) ? this.indexOfGate(gate) : gate;
	if (!this.gates[idx]) {
		throw "Gate " + gate + " undefined";
	}
	return idx;
};

/**
 * Sort of a setter for gates.
 * @param gate An instance of synthbio.Gate
 */
synthbio.Circuit.prototype.addGate = function(gate, position) {
	if (!(gate instanceof synthbio.Gate)) {
		synthbio.util.assert(position, "Position is not defined");
		gate = new synthbio.Gate(gate, position);
	}
	
	if (this.gates.indexOf(gate) < 0) {
		if(gate instanceof synthbio.Gate) {
			this.gates.push(gate);
		} else {
			throw "Provided gate is not of type synthbio.Gate";
		}
	}

	return gate;
};

/**
 * Remove a gate.
 */
synthbio.Circuit.prototype.removeGate = function(gate) {
	var idx = this.checkGateExists(gate);

	this.removeSignal(idx, undefined);
	this.removeSignal(undefined, idx);

	$.each(this.getSignals(), function(index, signal){
		var f = signal.getFrom();
		var t = signal.getTo();

		if (f > idx) {
			signal.setFrom(f - 1);
		}
		if (t > idx) {
			signal.setTo(t - 1);
		}
	});

	return this.gates.splice(idx, 1);
};

/**
 * Getter for a gate on index. Exception if index is invalid.
 * @param index Index in the array.
 */
synthbio.Circuit.prototype.getGate = function(index) {
	index = this.checkGateExists(index);
	return this.gates[index];
};

/**
 * Returns a list of used proteins.
 * @return Object {protein1: synthbio.Signal (truthy), protein2: synthbio.Signal, ..}
 */
synthbio.Circuit.prototype.getUsedProteins = function() {
	//Select the "best" proteins for the result (prefer ones with input/output relations)
	var res = {};
	var score = {};

	$.each(this.getSignals(), function(idx, signal){
		//Get protein and calculate score for this protein
		var prot = signal.getProtein();
		var scor = (signal.isInput() ? 1 : 0) + (signal.isOutput() ? 1 : 0);

		//Set protein if not set or this protein has higher score than old one
		if (prot && (!score[prot] || scor > score[prot])) {
			score[prot] = scor;
			res[prot] = signal;
		}
	});

	return res;
};

/**
 * Checks if a protein is used in this circuit.
 * @param protein String
 * @return boolean
 */
synthbio.Circuit.prototype.isProteinUsed = function(protein) {
	var proteins = this.getUsedProteins();
	return proteins && proteins[protein];
};

/**
 * Try to determine a protein for a signal based on the from-gate.
 * @param from Gate index.
 * @param endpoint Optional endpoint index.
 * @return Empty string on failure, else the protein.
 */
synthbio.Circuit.prototype.determineProtein = function(from, endpoint) {
	var signals = this.getSignals();

	var i;
	for(i = 0; i < signals.length; i++) {
		var s = signals[i];
		if (
			s.getProtein() &&
			from === s.getFrom() &&
			((endpoint === undefined) || (s.fromEndpoint === endpoint))
		) {
			return s.getProtein();
		}
	}

	return "";
};

/**
 * Update signals originating from the same gate.
 * @param signal Signal to determine protein and gate.
 * @return List of changed synthbio.Signal.
 */
synthbio.Circuit.prototype.updateSignals = function(signal) {
	var from = signal.getFrom();
	var ep = signal.fromEndpoint;
	var prot = signal.getProtein() /*|| this.determineProtein(from, ep)*/;

	var res = [];
	$.each(this.getSignals(), function(idx, s){
		if (
			s.getProtein() !== prot &&
			s.getFrom() === from && 
			(ep === undefined || s.fromEndpoint === ep)
		) {
			s.setProtein(prot);
			res.push(s);
		}
	});

	return res;
};

/**
 * Sort of a setter for signals.
 * @param signal An instance of synthbio.Signal
 * @return Added synthbio.Signal
 */
synthbio.Circuit.prototype.addSignal = function(signal, from, to, fromEndpoint, toEndpoint) {
	if (!(signal instanceof synthbio.Signal) && from !== undefined && to !== undefined){
		signal = new synthbio.Signal(signal, from, to, fromEndpoint, toEndpoint);
	}
	
	if (this.signals.indexOf(signal) < 0) {
		if(signal instanceof synthbio.Signal) {
			this.signals.push(signal);
		} else {
			throw "Provided signal is not of type synthbio.Signal";
		}
	}
	
	//If signal is an input signal, initialize to low.
	var prot = signal.getProtein();
	if (prot !== "") {
		//this.updateSignals(signal);
		if (signal.isInput() && !this.inputs.values[prot]) {
			this.inputs.values[prot] = "L";
		}
	}

	var self = this;
	signal.onProteinChange = function(s) {
		self.updateSignals(s);
	};
	return signal;
};

/**
 * Removes signals based on origin/destination.
 * Calling removeSignal() without parameters removes all signals.
 * 
 * @param origin An instance of synthbio.Signal or an integer. Undefined to accept any origin.
 * @param destination Destination integer (not used if protein is a synthbio.Signal). Undefined to accept any destination.
 * @param fullMatch If fullmatch is true, origin and destination must match exactly (undefined is not alllowed).
 * @return array Returns array of removed signals.
 */
synthbio.Circuit.prototype.removeSignal = function(origin, destination, fullMatch) {
	if (origin instanceof synthbio.Signal) {
		var idx = this.signals.indexOf(origin);
		if (idx >= 0) {
			return [this.signals.splice(idx, 1)];
		}
		
		if (fullMatch) {
			return [];
		}

		destination = origin.getTo();
		origin = origin.getFrom();
	}

	var removed = [];
	var i;
	for(i = 0; i < this.signals.length; i++) {
		if (((!fullMatch && origin      === undefined) || (this.signals[i].getFrom() === origin)) &&
		    ((!fullMatch && destination === undefined) || (this.signals[i].getTo() === destination))) 
		{
			removed.push(this.signals.splice(i, 1));
			i--;
		}
	}
	return removed;
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
synthbio.CDS.fromMap = function(map){
	return new synthbio.CDS(map.name, map.k2, map.d1, map.d2);
};
synthbio.CDS.fromJSON = function(json){
	return synthbio.CDS.fromMap($.parseJSON(json));	
};
synthbio.CDS.prototype.toString = function(){
	return "CDS - name = " + this.name + ", k2 = "+ this.k2+ ", d1 = " + this.d1+ ", d2 = " + this.d2;
};

/**
 * SimulationSetting records settings to run the simulation.
 * 
 * Might be initialized with two maps or with one map.
 *
 * One argument: 'options' should contain a <protein, values>-map in
 * the values field, the onther fields defining the simulation parameters.
 *
 * Two-argument: 'options' contains simulation parameters, 'values' is a
 * <protein, values>-hashmap.
 */
synthbio.SimulationSetting = function(options, values) {
	this.options = $.extend(
		{
			"length": 40,				//total ticks.
			"tickWidth": 10,			//length in seconds for one tick.
			"lowLevel": 0,				//concentration regarded as low.
			"highLevel": 200			//concentration regarded as high.
		}, options);
		
	/**
	 * Hashmap containing the values for each input protein.
	 */
	this.values = values || {};
	
	//if values is undefined, but set in the options map, copy it.
	if(!values && this.options.values) {
		this.values = this.options.values;
		delete this.options.values;
	}

	/**
	 * Reference to the circuit the SimulationSetting object belongs to.
	 */
	this.circuit = undefined;
}; 

/**
 * Bind the circuit to the the SimulationSetting object to be able
 * to ask about its inputs.
 */
synthbio.SimulationSetting.prototype.bindCircuit = function(circuit){
	this.circuit=circuit;
	this.updateInputs();
};
synthbio.SimulationSetting.prototype.getCircuit = function() {
	return this.circuit;
};

synthbio.SimulationSetting.prototype.setValue = function(protein, value) {
	if(this.getCSV()!=="") {
		throw "Should not try to get values when using CSV";
	}
	this.updateInputs();
	if(!this.values[protein]){
		throw "No such input signal in circuit: "+protein; 
	}
	//remove all but (H|L) from input string.
	this.values[protein]=value.replace(/[^HL]/g, "");
};

/**
 * Get the CSV values, or "" if values is an object.
 */
synthbio.SimulationSetting.prototype.getCSV = function() {
	if(this.values instanceof Object){
		return "";
	}else{
		return this.values;
	}
};

/**
 * Set the CSV values. Will replace existing values object.
 */
synthbio.SimulationSetting.prototype.setCSV = function(csv) {
	this.values = csv;
};

synthbio.SimulationSetting.prototype.getLength = function() {
	return this.options.length;
};
synthbio.SimulationSetting.prototype.getTickWidth = function() {
	return this.options.tickWidth;
};
synthbio.SimulationSetting.prototype.getLowLevel= function() {
	return this.options.lowLevel;
};
synthbio.SimulationSetting.prototype.getHighLevel = function() {
	return this.options.highLevel;
};

synthbio.SimulationSetting.prototype.setLength = function(length) {
	this.options.length = length;
};
synthbio.SimulationSetting.prototype.setTickWidth = function(width) {
	console.log(this);
	this.options.tickWidth = width;
};
synthbio.SimulationSetting.prototype.setLowLevel = function(level) {
	this.options.lowLevel = level;
};
synthbio.SimulationSetting.prototype.setHighLevel = function(level) {
	this.options.highLevel = level;
};


/**
 * Retrieve the Simulation input values for an input signal.
 */
synthbio.SimulationSetting.prototype.getValue = function(protein) {
	if(this.getCSV()!=="") {
		throw "Should not try to get values when using CSV";
	}
	this.updateInputs();
	if(!this.values[protein]){
		throw "No such input signal in circuit: "+protein; 
	}
	return this.values[protein];
};

/**
 * Fetch the list of input signals from the circuit and make sure all
 * input signals are present in this.inputs; Throw away signals that are
 * not input signals anymore.
 */
synthbio.SimulationSetting.prototype.updateInputs = function(){
	if(!this.circuit){
		throw "Circuit should be bound to SimulationSetting first!";
	}
	
	if(this.getCSV() !== "") {
		console.log('csv active, dont change...');
		return;
	}
	
	//Make a copy of the current values.
	var newValues={};
	var self=this;
	$.each(this.circuit.getInputSignals(), function(index, protein){
		if(self.values[protein]){
			//If contained in the old object, keep value
			newValues[protein]=self.values[protein];
		}else{
			//otherwise, insert a low.
			newValues[protein]="L";
		}
	});
	//copy new values to values property
	this.values=newValues;
};

/**
 * Get a <protein, values> map for each input signal
 */
synthbio.SimulationSetting.prototype.getValues = function() {
	synthbio.util.assert(this.getCircuit !== undefined, "Can only get values after circuit is bound to simulation");
	if(this.getCSV() === "") {
		this.updateInputs();
	}
	return this.values;
};

/**
 * toJSON method.
 * Change structure a little...
 */
synthbio.SimulationSetting.prototype.toJSON = function() {
	this.updateInputs();
	return $.extend({}, this.options, {values: this.values});
};

/**
 * toString method.
 */
synthbio.SimulationSetting.prototype.toString = function() {
	return 'Simulation bound to ' + this.getCircuit().getName()+ ' has options { length: '+this.getLength()+' , tick width: '+this.getTickWidth()+' , low level: '+this.getLowLevel()+' , high level: '+this.getHighLevel()+'}';
};
