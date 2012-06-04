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

	// Show the circuit; add all the elements
	synthbio.gui.resetWorkspace();
	synthbio.model = circuit;

	$.each(synthbio.model.getGates(), function(index, element) {
		synthbio.gui.displayGate(element);
	});
	$.each(synthbio.model.getSignals(), function(index, element){
		synthbio.gui.displaySignal(element);
	});
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

	//Add gates
	$.each(circuit.getGates(), function(index, element) {
		//Adjust position
		var x = element.getX() + position.getX();
		var y = element.getY() + position.getY();
		element.setPosition([x, y]);

		//Add gate to model and display
		synthbio.model.addGate(element);
		synthbio.gui.displayGate(element);
	});

	//Add signals (not the ones connected to input/output)
	$.each(circuit.getSignals(), function(index, element){
		if (!element.isInput() && !element.isOutput()) {
			//Adjust from/to to make sure they are correct in the new model
			element.setFrom(element.getFrom() + gateCount);
			element.setTo(element.getTo() + gateCount);

			//Check if protein is used, reset if it is
			if (synthbio.model.isProteinUsed(element.getProtein())) {
				element.setProtein("");
			}

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