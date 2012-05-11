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
 *
 * @author Felix Akkermans & Jan-Pieter Waagmeester & Niels Doekemeijer
 * GUI JavaScript Document, concerns all GUI matters except those about the modeling grid.
 */

/**
 * syntbio package.
 */
var synthbio = synthbio || {};

/**
 * GUI package
 */
synthbio.gui = synthbio.gui || {};

/**
 * Gate counter, used for tracking gates
 */
synthbio.gui.gateCounter = 0;

/**
 * Width of the <aside> element with all the gates in pixels.
 */
synthbio.gui.gatesTabWidth = parseFloat($('#gates-tab').css("width"));

/**
 * Width of the <aside> element with all the gates in pixels.
 */
synthbio.gui.navbarHeight = parseFloat($('.navbar').css("height"));

/**
 * Get normal gates dimensions
 */
synthbio.gui.gateDimensions = function() {
	// Create
	var dummyGate = $("<div class=\"gate\"></div>").hide().appendTo("body");
	// Get
	var dimensions = {
		width: parseFloat(dummyGate.css("width"), 10),
		height: parseFloat(dummyGate.css("height"), 10)
	};
	// Clean
	dummyGate.remove();
	// Assign
	return dimensions;
}();

/**
 * Statusbar info tooltip function
 *
 * @param element jQuery extended DOM element.
 * @param infoMessage String to display.
 */
synthbio.gui.setTooltip = function(element, infoMessage) {
	element.bind("mouseover", function(event) { $("#info").html(infoMessage); });
	// Set bubbling to false, prevents overlapping elements from also firing their tooltip and surpressing the bottom
	element.bind("mouseover", false);
	element.bind("mouseout", synthbio.gui.resetTooltip);
}
synthbio.gui.resetTooltip = function() {
	$("#info").html("Mouse over info something for info.");
}

$(document).ready(function() {
	// Activate zhe Dropdowns Herr Doktor!
	$('.dropdown-toggle').dropdown();
	
	// Load proteins from server.
	$('#list-proteins').on('show', function() {
		synthbio.requests.getCDSs(function(response) {
			if(response instanceof String) {
				$('#list-proteins tbody td').html(data.response);
				return;
			}
			var html='';
			$.each(response, function(i, cds) {
				html+='<tr><td>'+cds.name+'</td><td>'+cds.k2+'</td><td>'+cds.d1+'</td><td>'+cds.d2+'</td></tr>';
			});
			$('#list-proteins tbody').html(html);
		});
	});

	// Initialize gate-dragging
	$('#gates-tab .gate').draggable({ 
		appendTo: "#gates-transport",
		containment: 'window',
		scroll: false,
		helper: 'clone',
		start: function(event) {
			// Prepare transport layer
			$("#gates-transport").css('display', 'block');
		},
		drag: function(event, ui) {
			// Manually set the position of the helper (the thing you see dragged). Works out much nicer!
			ui.position.left = event.pageX - synthbio.gui.gateDimensions.width/2;
			ui.position.top  = event.pageY - synthbio.gui.gateDimensions.height/2;

			// Display gate border if dragging in grid (and gate can be dropped)
			var dragInGrid = event.pageX > synthbio.gui.gatesTabWidth;
			$(ui.helper).toggleClass("gate-border", dragInGrid);
		},
		stop: function(event, ui) {
			// If dragged into the grid
			if(event.pageX > synthbio.gui.gatesTabWidth) {
				// Add new gate to circuit
				var x = event.pageX - (synthbio.gui.gatesTabWidth + synthbio.gui.gateDimensions.width/2);
				var y = event.pageY - (synthbio.gui.navbarHeight + synthbio.gui.gateDimensions.height/2);
				var newGate = synthbio.model.addGate(
					$(this).attr('class').split(' ')[1], // type of gate (second word in class of the element)
					[x, y] // position of the new gate
				);

				// Display gate in grid
				synthbio.gui.displayGate(newGate);
			}

			// Clean up transport layer
			$("#gates-transport .gate").remove();
			$("#gates-transport").css('display', 'none');
		}
	});

	// Start pinging
	synthbio.gui.pingServer();
	// Set default tooltip info-string
	synthbio.gui.resetTooltip();
	// Hook mouseover listeners for all elements to display tooltips
	synthbio.gui.setTooltip($("#gates-basic"),      "Predefined AND and NOT gates (fixed)");
	synthbio.gui.setTooltip($("#gates-compound"),   "User defined compound gates (loaded from .syn)");
	synthbio.gui.setTooltip($("#grid-container"),  "Hold Ctrl and drag a marquee to select a group of gates. " +
		"Alternatively hold Ctrl and click on gates to toggle them as selected.");
	synthbio.gui.setTooltip($("#gate-input"),       "Drag from here to define an input signal for a gate.");
	synthbio.gui.setTooltip($("#gate-output"),      "Drop a signal endpoint in here to output the signal values.");

	var map = {
		"name": "example.syn",
		"description": "Logic for this circuit: D = ~(A^B)",
		"gates": [
			{ "kind": "and", "position": {"x": 250,"y": 223}},
			{ "kind": "not", "position": {"x": 340,"y": 431}}
		],
		"signals": [
			{ "from": "input", "to": 0, "protein": "A"},
			{ "from": "input", "to": 0, "protein": "B"},
			{ "from": 0, "to": 1, "protein": "C"},
			{ "from": 1, "to": "output", "protein": "D"}
		],
		"grouping": [

		]
	};
	var gates=[], signals=[], groups=[];

	$.each(map.gates, function(i, elem) {
		gates[i]=synthbio.Gate.fromMap(elem);
	});

	$.each(map.signals, function(i, elem) {
		signals[i]=synthbio.Signal.fromMap(elem);
	});
	var cir = new synthbio.Circuit(map.name, map.description, gates, signals, groups);
	setTimeout(function() {
		synthbio.loadCircuit(cir);
	}, 500);
});

synthbio.gui.reset = function() {
	jsPlumb.removeEveryEndpoint();
	for (id in synthbio.gui.displayGateIdMap) {
		synthbio.gui.removeDisplayGate(id);
	}

	synthbio.gui.displayGateIdMap = {};
	synthbio.gui.displaySignalIdMap = {};

	//Workaround for bug in jQuery/jsPlumb (Firefox only)
	jsPlumb.addEndpoint("grid-container").setVisible(false);
}

/**
 * Add specified number of JSPlumb endpoints to a gate
 *
 * @param toId ID of the gate element. UUID of the endpoints will be *id*_input*number* (and id_outputNUMBER)
 * @param inputEndpoints Number of input endpoints
 * @param outputEndpoints Number of output endpoints
 * @return Object with inputEndpoints and outputEndpoints (both arrays)
 */
synthbio.gui.addPlumbEndpoints = function(toId, inputEndpoints, outputEndpoints) {
	var res = {
		inputEndpoints: [],
		outputEndpoints: []
	};
	inputEndpoints--;
	outputEndpoints--;
	
	// Function to calculate the placement (1/2 if there's only one to place, else 1/total)
	var placement = function(num, total) {
		return (total < 1) ? 0.5 : (num / total);
	};

	for (var j = 0; j <= inputEndpoints; j++) {
		var inputUUID = toId + "_input" + j;
		res.inputEndpoints.push(jsPlumb.addEndpoint(toId, synthbio.gui.inputEndpoint, { 
			anchor:[0, placement(j, inputEndpoints), -1, 0], 
			uuid:inputUUID 
		}));
	}
	for (var i = 0; i <= outputEndpoints; i++) {
		var outputUUID = toId + "_output" + i;
		res.outputEndpoints.push(jsPlumb.addEndpoint(toId, synthbio.gui.outputEndpoint, { 
			anchor:[1, placement(i, outputEndpoints), 1, 0], 
			uuid:outputUUID 
		}));
	}

	return res;
}

/**
 * Add the correct number of endpoints to a gate
 *
 * @param gateModel Target element (object or selector)	
 * @return Returns the model, with endpoints properties added
 */
synthbio.gui.addGateEndpoints = function(gateModel) {
	var endpoints = synthbio.gui.addPlumbEndpoints(
		gateModel.element.attr("id"),
		gateModel.model.getInputCount(),
		gateModel.model.getOutputCount()
	);

	// Extend gateModel with endpoints and return
	return $.extend(true, endpoints, gateModel);
}

/**
 * Maps an (display) element ID to the proper gate object
 */
synthbio.gui.displayGateIdMap = {/*Example:
	elementID: {
		element: JQueryElement,
		model: synthbio.Gate,
		inputAnchors/outputAnchors: array of jsPlumb anchors
	}
*/};

/**
 * Maps an (display) element ID to the proper signal object
 */
synthbio.gui.displaySignalIdMap = {/*Example:
	elementID: {
		connection: jsPlumb connection,
		signal: synthbio.Signal,
	}
*/};

/**
 * Returns gate object by GUI id
 * @param id string
 * @param noException Truthy to not throw exception
 * @return Object with element, model and endpoints (exception if not found, false if noException)
 */
synthbio.gui.getGateById = function(id, noException) {
	if (id == "gate-input")
		return "input";
	else if (id == "gate-output")
		return "output";
	else if (synthbio.gui.displayGateIdMap[id])
		return synthbio.gui.displayGateIdMap[id];
	else if (noException) 
		return false;
	else
		throw "Cannot map id to gate";
}

/**
 * Returns gate index by GUI id
 * @param id string
 * @param noException Truthy to not throw exception
 * @return index (exception if not found, false if noException)
 */
synthbio.gui.getGateIndexById = function(id, noException) {
	var gate = synthbio.gui.getGateById(id, noException);
	if (gate.model)
		gate = synthbio.model.indexOfGate(gate.model);
	return gate;
}

/**
 * Returns gate GUI id by index
 * @param idx integer
 * @param noException Truthy to not throw exception
 * @return id (exception if not found, false if noException)
 */
synthbio.gui.getGateIdByIndex = function(idx, noException) {
	if (idx == "input")
		return "gate-input";
	else if (idx == "output")
		return "gate-output";
	else {
		var gate = synthbio.model.getGate(idx);
		for(var id in synthbio.gui.displayGateIdMap)
			if (synthbio.gui.displayGateIdMap[id].model == gate) {
				return id;
			}
	
		if (noException)
			return false;
		else
			throw "Cannot map index to id";
	}
}

/**
 * Returns signal object by GUI id
 * @param id string
 * @param noException Truthy to not throw exception
 * @return Object with connection and signal (exception if not found, false if noException)
 */
synthbio.gui.getSignalById = function(id, noException) {
	var signal = synthbio.gui.displaySignalIdMap[id];
	if (signal)
		return signal;
	else if (noException)
		return false;
	else
		throw "Cannot map id to signal";
}

/**
 * Adds a new gate DOM element to be used within the modelling grid.
 *
 * @param gateModel synthbio.Gate
 * @return Object with element, model and endpoints.
 */
synthbio.gui.displayGate = function(gateModel) {
	synthbio.util.assert(gateModel instanceof synthbio.Gate, "Provided gate ojbect must be an instance of 'synthbio.Gate'");
	
	// Create new display element
	var element = $("<div class=\"gate " + gateModel.getKind() + "\">"
		+ gateModel.getImage(true)
		+ "<div class=\"mask\"></div>"
		+ "</div>");

	// Place new element in grid
	$('#grid-container').append(element);
	element.css("left", gateModel.getX() + "px");
	element.css("top", gateModel.getY() + "px");


	// Make the gate draggable
	jsPlumb.draggable(element, {
		stop: function(event, ui) {
			gateModel.setPosition([
				parseFloat(element.css("left")), 
				parseFloat(element.css("top"))
			]);
		}
	});

	// Add endpoints
	var res = synthbio.gui.addGateEndpoints({element: element, model: gateModel});
	var id = element.attr("id");

	// Add to ID map
	synthbio.gui.displayGateIdMap[id] = res;

	// Delete on double click
	element.dblclick(function() {
		if (confirm("Delete " + gateModel.toString() + "?"))
			synthbio.gui.removeDisplayGate(id);
	});

	return res;
}

/**
 * Removes a gate from circuit based on GUI id.
 *
 * @param id String
 * @return Object Deleted object.
 */
synthbio.gui.removeDisplayGate = function(id) {
	var obj = synthbio.gui.displayGateIdMap[id];
	if (obj) {
		if (obj.element) {
			jsPlumb.removeAllEndpoints(obj.element);
			obj.element.remove();
		}
		if (obj.model) {
			synthbio.model.removeGate(obj.model);
		}
		delete synthbio.gui.displayGateIdMap[id];
	}
	return obj;
}

/**
 * Adds a new wire to the grid.
 *
 * @param signal synthbio.Signal
 * @param jsPlumb.Connection Optional jsPlumb connection object, will create connection if undefined.
 * @return Object with signal and connection.
 */
synthbio.gui.displaySignal = function(signal, connection) {
	synthbio.util.assert(signal instanceof synthbio.Signal, "Provided signal ojbect must be an instance of 'synthbio.Signal'");

	if (!connection) {
		var src = synthbio.gui.getGateIdByIndex(signal.from);
		var dst = synthbio.gui.getGateIdByIndex(signal.to);
		connection = jsPlumb.connect({source: src, target: dst, parameters: {signal: signal}});
	}

	var res = {connection: connection, signal: signal};
	synthbio.model.addSignal(signal);
	synthbio.gui.displaySignalIdMap[connection.id] = res;
	return res;
}

/**
 * Adds a new wire to the grid.
 *
 * @param connection jsPlumb.ConnectionInfo
 * @return Object with signal and connection.
 */
synthbio.gui.displayConnection = function(connection) {
	synthbio.util.assert(connection.sourceId && connection.targetId, "Provided signal ojbect must be an instance of 'jsPlumb.Connection'");

	// Calculate source/target indices
	var fromIndex = synthbio.gui.getGateIndexById(connection.sourceId);
	var toIndex = synthbio.gui.getGateIndexById(connection.targetId);
	
	// Add signal to circuit and display
	var signal = synthbio.model.addSignal("", fromIndex, toIndex);
	return synthbio.gui.displaySignal(signal, connection);
}

/**
 * Removes a signal from circuit based on GUI id.
 *
 * @param id String
 * @return Object Deleted object.
 */
synthbio.gui.removeDisplaySignal = function(id) {
	var obj = synthbio.gui.displaySignalIdMap[id];
	if (obj) {
		if (obj.connection) {
			obj.connection.endpoints[1].detach();
		}
		if (obj.signal) {
			synthbio.model.removeSignal(obj.signal);
		}
		delete synthbio.gui.displaySignalIdMap[id];
	}
	return obj;
}

/**
 * Ping server to check for connection 'vitals'. Shown a warning if things go really bad. Declared as a closure to keep
 * variables local.
 */
synthbio.gui.pingServer = function() {
	var date = new Date();
	var fCount = 0; // Failure count: The amount of times that connection attempts have failed. Resets to 0 on success.
	var limit = 3; // Amount of times after which a dialog should prompt the user about the failures.
	var frequency = 750; // The delay between ping calls in milliseconds.
	return function () {
		var t = date.getTime();
		$.ajax("/Ping")
			.done(function(data) {
				$('#ping').html('Server status: <b>Connected to server <em class="icon-connected"></em> [latency: '
					+ (date.getTime() - t) + 'ms]</b>');
				fCount = 0;
			})
			.fail(function(data) {
				$('#ping').html('Server status: <b>Warning: not connected to server! <em class="icon-failed"></em></b>');
				$('#ping').attr('class', 'failed');
				if(fCount  <= limit+1) fCount++; // Keep counting untill the dialog was shown
				if(fCount == limit) $('#connection-modal').modal(); // Show the dialog once
			})
			.always(function() { setTimeout(synthbio.gui.pingServer, frequency); });
	};
}();
