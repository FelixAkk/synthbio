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
 * @author Felix Akkermans
 *
 * JavaScript Document concerning the "back-end" circuit.
 */

/*jslint devel: true, browser: true, vars: true, plusplus: true, regexp: true, sloppy: true, white: true, maxerr: 50, indent: 4 */
/*global $, jsPlumb, synthbio */

/**
 * syntbio package.
 */
var synthbio = synthbio || {};

/**
 * The object that will represent the entire circuit that is in the app.
 * Might be a slight delay between synchronization but should correspond
 * to the circuit pretty accurately. This should only matter with trivial
 * things like the position of gates. This object is instantiated as an
 * empty synthbio.Circuit with no name or description.
 */
synthbio.model = {};

/**
 * Cleans the current workspace and loads the provided circuit. Also updates the GUI.
 *
 * @param circuit An instance of synthbio.Circuit
 */
synthbio.loadCircuit = function(circuit) {
	synthbio.util.assert(circuit instanceof synthbio.Circuit, "Provided circuit is not an instance of sythnbio.Circuit.");


	// Clear and refresh the GUI
	synthbio.gui.resetWorkspace(circuit);

	synthbio.model = circuit;

	// Update the input editor (TODO: hacky location, place this in the synthbio.gui.resetWorkspace() function; warning
	// this brings up sequence problems
	synthbio.gui.updateInputEditor();

	if (circuit.gateInputPos) {
		var input = $("#gate-input");
		input.css("left", (circuit.gateInputPos.x || 0) + "px");
		input.css("top", (circuit.gateInputPos.y || 0) + "px");
	}
	if (circuit.gateOutputPos) {
		var output = $("#gate-output");
		output.css("right", "auto");
		output.css("left", (circuit.gateOutputPos.x || 0) + "px");
		output.css("top", (circuit.gateOutputPos.y || 0) + "px");
	}

	// Show the circuit; add all the elements
	$.each(synthbio.model.getGates(), function(index, element) {
		synthbio.gui.displayGate(element);
	});
	$.each(synthbio.model.getSignals(), function(index, element){
		synthbio.gui.displaySignal(element);
	});

	synthbio.gui.setCircuitDetails(circuit.getName(), circuit.getDescription());
};

/**
 * Adds compound circuit to current circuit at given position.
 *
 * @param circuit An instance of synthbio.Circuit
 * @param position Position to drop compound circuit
 */
synthbio.loadCompoundCircuit = function(circuit, position) {
	synthbio.util.assert(circuit instanceof synthbio.Circuit, "Provided circuit is not an instance of sythnbio.Circuit.");
	if(!(position instanceof synthbio.Point)) {
		position = new synthbio.Point(position || [0, 0]);
	}

	//This is the number needed for adjusting the new signals
	var gateCount = synthbio.model.getGates().length;

	var gates = circuit.getGates();
	var signals = circuit.getSignals();
	
	//Calculate top left position for adjustment for other gates
	var origin = [Infinity, Infinity];
	$.each(gates, function(index, element) {
		origin[0] = Math.min(origin[0], element.getX());
		origin[1] = Math.min(origin[1], element.getX());
	});
	origin[0] = position.getX() - origin[0];
	origin[1] = position.getY() - origin[1];
	
	//Add gates
	$.each(gates, function(index, element) {
		//Adjust position
		var x = element.getX() + origin[0];
		var y = element.getY() + origin[1];
		element.setPosition([x, y]);

		//Add gate to model and display
		synthbio.model.addGate(element);
		synthbio.gui.displayGate(element);
	});
	
	//Reset used proteins
	$.each(signals, function(index, signal) {
		if (!synthbio.validProtein(signal.protein) || synthbio.model.isProteinUsed(signal.protein)) {
			signal.setProtein("");
		}
	});
	
	//Add signals (not the ones connected to input/output)
	$.each(signals, function(index, element){
		if (!element.isInput() && !element.isOutput()) {
			//Adjust from/to to make sure they are correct in the new model
			element.setFrom(element.getFrom() + gateCount);
			element.setTo(element.getTo() + gateCount);

			//Add signal to model and display it
			synthbio.model.addSignal(element);
			synthbio.gui.displaySignal(element);
		}
	});

	jsPlumb.repaintEverything();
};


/**
 * Cleans the current workspace and loads an empty circuit.
 */
synthbio.newCircuit = function() {
	synthbio.loadCircuit(new synthbio.Circuit("", ""));
};