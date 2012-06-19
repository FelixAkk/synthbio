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
 * @author Felix Akkermans & Niels Doekemeijer
 *
 * GUI JavaScript Document, concerns all workspace matters (circuit visualization & modelling).
 */

/*jslint devel: true, browser: true, forin: true, vars: true, plusplus: true, sloppy: true, white: true, maxerr: 50, indent: 4 */
/*global $, synthbio, jsPlumb */

/**
 * syntbio package.
 */
var synthbio = synthbio || {};
synthbio.gui = synthbio.gui || {};

/**
 * Max. length of the description when on display. Tell after how many characters to crop and suffix with ...
 */
synthbio.gui.descrDisplayCropLength = 40;
synthbio.gui.fileDisplayCropLength = 15;

/**
 * Strings that display when no circuit filename and/or description is provided.
 */
synthbio.gui.defaultFilenameString = "circuit filename";
synthbio.gui.defaultDescriptionString = "Circuit description.";
/**
 * Width of the <aside> element with all the gates in pixels.
 */
synthbio.gui.gatesTabWidth = $('#gates-tab').width();

/**
 * Dimensions of elements in pixels.
 */
synthbio.gui.navbarHeight = $('.navbar').height();
synthbio.gui.simulationTabsNavbarHeight = $("#simulation-tabs .nav-tabs.navbar").height();
synthbio.gui.statusBarHeight = $(".status").height();

synthbio.gui.simulationTabsVisible = false;
synthbio.gui.simulationTabsInFullscreen = false;
/**
 * Get normal gates dimensions
 */
synthbio.gui.gateDimensions = (function () {
	// Create
	var dummyGate = $('<div class="gate"></div>').hide().appendTo("body");
	// Get
	var dimensions = {
		width: dummyGate.width(),
		height: dummyGate.height()
	};
	// Clean
	dummyGate.remove();
	// Assign
	return dimensions;
}());

/**
 * Clear the workspace GUI. If called the model is not cleared! Should always be called as part of a circuit loading,
 * reloading of the GUI, or clearing of the model.
 */
synthbio.gui.resetWorkspace = function() {
	jsPlumb.removeEveryEndpoint();

	var id;
	for (id in synthbio.gui.displayGateIdMap) {
		synthbio.gui.removeDisplayGate(id);
	}

	synthbio.gui.displayGateIdMap = {};
	synthbio.gui.displaySignalIdMap = {};

	//Reset plots
	synthbio.gui.plotSeries([]);

	//Workaround for bug in jQuery/jsPlumb (Firefox only)
	jsPlumb.addEndpoint("grid-container").setVisible(false);

	// Also reset the circuit details
	synthbio.gui.setCircuitDetails("","");
	
	//Reset input/output gate positions
	$(".input, .output").css("top", "");
	$(".input, .output").css("left", "");
	$(".input").css("left", "10px");
	$(".output").css("right", "10px");

	//Reset tabs
	synthbio.gui.displayValidation(synthbio.gui.defaultValidityTabHTML, 4, true);
	$("#simulation-tabs").css("height", "");
	$("#simulation-tabs").css("bottom", "-" + $("#simulation-tabs").height() + "px");
	$("#grid-container").css("bottom", "");
	synthbio.gui.plotResize();
};

/**
 * Utility function to display validation results. Has a special hacky use where you supply an interger
 * instead of a boolean for the validity parameter to set some other extra alert classes.
 *
 * @param message HTML message string
 * @param valid Boolean, but can also be an integer, use 0: error, 1: success, 3: info, 4: plain.
 * @noTabSwitch Automatically switch to the validation tab?
 */
synthbio.gui.displayValidation = function (message, valid, noTabSwitch) {
	var element = $('#tab-validate .alert');
	element.html(message);
	if(arguments.length > 1) {
		// Important that these are only 2 equal characters, needs lose compare here to also accept booleans.
		if(valid == 0){
			// Green
			element.attr("class", "alert alert-error");
			// Same for this if clause head
		} else if(valid == 1) {
			// Red
			element.attr("class", "alert alert-success");
		} else if(valid === 2) {
			// Blue
			element.attr("class", "alert alert-info");
		} else if(valid === 3) {
			// Yellow
			element.attr("class", "alert");
		}
	}

	// Oh, phunny double negatives :>
	if(!noTabSwitch) {
		$('#simulation-tabs a[href="#tab-validate"]').tab("show");
	}

	synthbio.gui.showSimulationTabs(true);
};

/**
 * Toggle simulation tabs block.
 *
 * @param display Optional boolean. True is to display, false is to hide.
 */
synthbio.gui.toggleSimulationTabs = function(display) {
	var tabs = $("#simlation-tab");
	var grid = $("#grid-container");
};

/**
 * Add specified number of JSPlumb endpoints to a gate
 *
 * @param toId ID of the gate element. UUID of the endpoints will be *id*_input*number* (and id_outputNUMBER)
 * @param inputEndpoints Number of input endpoints
 * @param outputEndpoints Number of output endpoints
 * @return Object with inputEndpoints and outputEndpoints (both arrays)
 */
synthbio.gui.addPlumbEndpoints = function(toId, inputEndpoints, outputEndpoints) {
	var i;
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

	for (i = 0; i <= inputEndpoints; i++) {
		var inputUUID = toId + ":: input:: " + i;
		res.inputEndpoints.push(jsPlumb.addEndpoint(toId, synthbio.gui.inputEndpoint, { 
			anchor:[0, placement(i, inputEndpoints), -1, 0], 
			uuid: inputUUID 
		}));
	}
	for (i = 0; i <= outputEndpoints; i++) {
		var outputUUID = toId + ":: output:: " + i;
		res.outputEndpoints.push(jsPlumb.addEndpoint(toId, synthbio.gui.outputEndpoint, { 
			anchor:[1, placement(i, outputEndpoints), 1, 0], 
			uuid: outputUUID 
		}));
	}

	return res;
};

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
};

/**
 * Returns the index of an endpoint
 *
 * @param endpoint jsPlumb.EndPoint or UUID
 * @return Endpoint index, undefined if unknown
 */
synthbio.gui.getEndpointIndex = function(endpoint) {
	if (endpoint && endpoint.getUuid) {
		endpoint = endpoint.getUuid();
	}

	if (!endpoint || !endpoint.split) {
		return undefined;
	}
	
	var idx = parseInt(endpoint.split(":: ").pop(), 10);
	return (isNaN(idx)) ? undefined : idx;
};

/**
 * Adds a new output endpoint to the input "gate"
 *
 * @param index Number to use for UUID, if undefined it will use a counter
 * @return jsPlumb.Endpoint
 */
synthbio.gui.newInputEndpoint = (function() {
	var inputCounter = 0;

	return function(index) {
		var UUID = "gate-input:: output:: ";
		if (index === undefined) {
			UUID += inputCounter++;
		} else {
			UUID += index;
			inputCounter = Math.max(inputCounter, index + 1);
		}

		return jsPlumb.addEndpoint(
			"gate-input",
			synthbio.gui.outputEndpoint, 
			{ anchor: "Continuous", uuid: UUID }
		);
	};
}());

/**
 * Adds a new input endpoint to the output "gate"
 *
 * @param index Number to use for UUID, if undefined it will use a counter
 * @return jsPlumb.Endpoint
 */
synthbio.gui.newOutputEndpoint = (function() {
	var outputCounter = 0;

	return function(index) {
		var UUID = "gate-output:: input:: ";
		if (index === undefined) {
			UUID += outputCounter++;
		} else {
			UUID += index;
			outputCounter = Math.max(outputCounter, index + 1);
		}

		return jsPlumb.addEndpoint(
			"gate-output",
			synthbio.gui.inputEndpoint, 
			{ anchor: "Continuous", uuid: UUID }
		);
	};
}());

/**
 * Finds a free input or output endpoint for a gate.
 *
 * @param id GUI id for the gate
 * @param input True for input endpoint, false for output endpoint
 * @param index Integer to define which endpoint to return, if undefined the function will return the first non-full endpoint
 * @return jsPlumb.Endpoint or GUI id
 */
synthbio.gui.getFreeEndpoint = function(id, input, index) {
	// Get the proper array of endpoints
	var gate = synthbio.gui.getGateById(id, false);
	var ep = (input) ? gate.inputEndpoints : gate.outputEndpoints;

	if (!ep) {
		// If gate has no tracked endpoints, try to get endpoint by UUID
		ep = jsPlumb.getEndpoint(id + ":: " + ((input) ? "input" : "output") + ":: " + index);

		// If endpoint does not exist for gate-input, create it
		if (!ep && !input && id === "gate-input") {
			ep = synthbio.gui.newInputEndpoint(index);
		}
		// If endpoint does not exist for gate-output, create it
		if (!ep && input && id === "gate-output") {
			ep = synthbio.gui.newOutputEndpoint(index);
		}
		
		// Return endpoint if found, else the GUI id
		return ep || id;
	}

	// Check if index is in the endpoint array
	if (ep[index]) {
		return ep[index];
	} else {
		// Else find the first non-full endpoint
		var i;
		for(i = 0; i < ep.length; i++) {
			if (!ep[i].isFull()) {
				return ep[i];
			}
		}

		// Return ep[0] if none found
		return ep[0];
	}
};

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
 * Returns gate object by GUI id
 * @param id string
 * @param noException Truthy to not throw exception
 * @return Object with element, model and endpoints (exception if not found, false if noException)
 */
synthbio.gui.getGateById = function(id, noException) {
	if (id === "gate-input") {
		return "input";
	} else if (id === "gate-output") {
		return "output";
	} else if (synthbio.gui.displayGateIdMap[id]) {
		return synthbio.gui.displayGateIdMap[id];
	} else if (noException) {
		return false;
	} else {
		throw "Cannot map id to gate";
	}
};

/**
 * Returns gate index by GUI id
 * @param id string
 * @param noException Truthy to not throw exception
 * @return index (exception if id not found, false if noException)
 */
synthbio.gui.getGateIndexById = function(id, noException) {
	var gate = synthbio.gui.getGateById(id, noException);
	if (gate.model) {
		gate = synthbio.model.indexOfGate(gate.model);
	}
	return gate;
};

/**
 * Returns gate GUI id by index
 * @param idx integer
 * @param noException Truthy to not throw exception
 * @return id (exception if not found, false if noException)
 */
synthbio.gui.getGateIdByIndex = function(idx, noException) {
	if (idx === "input") {
		return "gate-input";
	}else if (idx === "output") {
		return "gate-output";
	} else {
		var gate = synthbio.model.getGate(idx);
		var id;
		for(id in synthbio.gui.displayGateIdMap) {
			if (synthbio.gui.displayGateIdMap[id].model === gate) {
				return id;
			}
		}
	
		if (noException) {
			return false;
		} else {
			throw "Cannot map index to id";
		}
	}
};

/**
 * Adds a new gate DOM element to be used within the modelling grid.
 *
 * @param gateModel synthbio.Gate
 * @return Object with element, model and endpoints.
 */
synthbio.gui.displayGate = function(gateModel) {
	synthbio.util.assert(gateModel instanceof synthbio.Gate, "Provided gate object must be an instance of 'synthbio.Gate'");

	// Create new display element
	var element = $('<div class="gate ' + gateModel.getKind() + '">'
		+ gateModel.getImage(true)
		+ "</div>");

	// Place new element in grid
	$('#grid-container').append(element);
	element.css("left", gateModel.getX() + "px");
	element.css("top", gateModel.getY() + "px");

	// Make the gate draggable
	jsPlumb.draggable(element, {
		stop: function(event, ui) {
			gateModel.setPosition([ui.position.left, ui.position.top]);
			jsPlumb.repaintEverything();
		}
	});

	// Add endpoints
	var res = synthbio.gui.addGateEndpoints({element: element, model: gateModel});
	var id = element.attr("id");

	// Add to ID map
	synthbio.gui.displayGateIdMap[id] = res;

	// Delete on double click
	element.dblclick(function() {
		if (confirm("Delete " + gateModel.toString() + "?")) {
			synthbio.gui.removeDisplayGate(id);
		}
	});

	return res;
};

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
};

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
 * Returns signal object by GUI id
 * @param id string
 * @param noException Truthy to not throw exception
 * @return Object with connection and signal (exception if not found, false if noException)
 */
synthbio.gui.getSignalById = function(id, noException) {
	var signal = synthbio.gui.displaySignalIdMap[id];
	if (signal) {
		return signal;
	} else if (noException) {
		return false;
	} else {
		throw "Cannot map id to signal";
	}
};

/**
 * Adds a new wire to the grid.
 *
 * @param signal synthbio.Signal
 * @param jsPlumb.Connection Optional jsPlumb connection object, will create connection if undefined.
 * @return Object with signal and connection.
 */
synthbio.gui.displaySignal = function(signal, connection) {
	synthbio.util.assert(signal instanceof synthbio.Signal, "Provided signal ojbect must be an instance of 'synthbio.Signal'");

	// If it's a new one
	if (!connection) {
		var src = synthbio.gui.getGateIdByIndex(signal.getFrom());
		var dst = synthbio.gui.getGateIdByIndex(signal.getTo());
		connection = jsPlumb.connect({
			source: synthbio.gui.getFreeEndpoint(src, false, signal.fromEndpoint),
			target: synthbio.gui.getFreeEndpoint(dst, true, signal.toEndpoint),
			parameters: {signal: signal}
		});
	}
	var res = {connection: connection, signal: signal};
	synthbio.model.addSignal(signal);
	synthbio.gui.displaySignalIdMap[connection.id] = res;

	if(signal.getProtein() !== "") {
		synthbio.gui.updateInputEditor();
	}
	return res;
};

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

	// Calculate source/target endpoints
	var fromEndpoint = synthbio.gui.getEndpointIndex(connection.endpoints[0]);
	var toEndPoint = synthbio.gui.getEndpointIndex(connection.endpoints[1]);
	
	// Add signal to circuit and display
	var signal = synthbio.model.addSignal("", fromIndex, toIndex, fromEndpoint, toEndPoint);
	return synthbio.gui.displaySignal(signal, connection);
};

/**
 * Removes a signal from circuit based on GUI id.
 *
 * @param id String
 * @param allowReconnect False to force deletion of the jsPlumb connection
 * @return Object Deleted object.
 */
synthbio.gui.removeDisplaySignal = function(id, allowReconnect) {
	var obj = synthbio.gui.displaySignalIdMap[id];
	if (obj) {
		if (obj.connection && !allowReconnect) {
			jsPlumb.detach(obj.connection);
		}
		if (obj.signal) {
			synthbio.model.removeSignal(obj.signal, undefined, true);
			// Also remove it from the input editor if a protein was assigned
			if(obj.signal.getProtein() !== "") {
				synthbio.gui.updateInputEditor();
			}
		}
		delete synthbio.gui.displaySignalIdMap[id];
	}
	return obj;
};

/**
 * Base options for jQuery.draggable()
 *
 * The stop function should be overriden, it can now be used as a callback
 * that returns coords if it succeeds (else it returns false).
 */
synthbio.gui.draggableOptions = { 
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
		var res = false;
		// If dragged into the grid
		if(event.pageX > synthbio.gui.gatesTabWidth) {
			// Add new gate to circuit
			var x = event.pageX - (synthbio.gui.gatesTabWidth + synthbio.gui.gateDimensions.width/2);
			var y = event.pageY - (synthbio.gui.navbarHeight + synthbio.gui.gateDimensions.height/2);
			res = [x, y];
		}

		// Clean up transport layer
		$("#gates-transport .gate").remove();
		$("#gates-transport").css('display', 'none');

		return res;
	}
};

/**
 * Adds a compound gate to the list in the gui.
 *
 * @param circuit Map that maps to synthbio.Circuit
 */
synthbio.gui.addCompoundGate = function(circuit) {
	var model = synthbio.Circuit.fromMap(circuit);

	// Create new display element
	var element = $('<div class="gate compound"><img src="img/gates/compound.svg"/>'
		+ '<h4>' + (model.getName() || 'Compound gate') + '</h4>'
		+ '<p class="subscript">' + (model.getDescription() || 'Compound') + '</p>'
		+ "</div>");

	// Place new element in grid
	$('#gates-compound').append(element);

	// Initialize new gate-dragging
	element.draggable($.extend({}, synthbio.gui.draggableOptions, {
		stop: function() {
			var pos = synthbio.gui.draggableOptions.stop.apply(this, arguments);
			if (pos !== false) {
				//From map again here, to make sure new objects are used
				var c = synthbio.Circuit.fromMap(circuit);
				synthbio.loadCompoundCircuit(c, pos);
			}
		}
	}));
};

/**
 * Loads all compound gates from server and displays them in the list
 */
synthbio.gui.loadCompounds = function() {
	// Delete old compounds
	$('#gates-compound').empty();

	// Request names from server and define what happens next
	synthbio.requests.listFiles(synthbio.compoundFolder, function(response) {
		// problemu technicznego
		if(!$.isArray(response)) {
			return;
		}

		//Get all compounds and add each one to the list
		$.each(response, function(i, file) {
			synthbio.requests.getFile(file.filename, synthbio.compoundFolder, function(response) {
				if(response.success === false) {
					console.error(response.message);
				} else {
					synthbio.gui.addCompoundGate(response.data);
				}
			});
		});
	});
};

/**
 * Saves and displays the details on display the circuit title/description in the main GUI.
 * @param filename String with or without the .syn file extension, may be empty, wil be trimmed.
 * @param description String with the description, may be empty, will be trimmed.
 */
synthbio.gui.setCircuitDetails = function(filename, description) {
	filename = filename.trim();
	description = description.trim();
	// If a filename was provided, extend it with ".syn"
	if(filename.length > 0) {
		filename = synthbio.gui.filenameExtension(filename);
	}
	// Save them if a model is present (which is not the case when resetting the workspace for example)
	if(synthbio.model instanceof synthbio.Circuit) {
		synthbio.model.setName(filename);
		synthbio.model.setDescription(description);
	}

	// Comes down to; allow it to eat space of the description when the description isn't taking up too much
	var maxlength = synthbio.gui.fileDisplayCropLength +
		((synthbio.gui.descrDisplayCropLength - description.length <= 0) ? 0 :
			(synthbio.gui.descrDisplayCropLength - description.length));
	if(filename.length === 0) {
		// Set default value to show the it wasn't set
		filename = synthbio.gui.defaultFilenameString;
		// Crop the display string if needed. But don't take the file extension into account, so minus ".syn" is -4.
	} else if(filename.length - 4 > maxlength) {
		filename = filename.substring(0, maxlength) + "...";
	}
	// Vice versa; allow it to eat space of the filename when the description isn't taking up too much
	maxlength = synthbio.gui.descrDisplayCropLength +
		((synthbio.gui.fileDisplayCropLength - filename.length <= 0) ? 0 :
			(synthbio.gui.fileDisplayCropLength - filename.length));
	if(description.length === 0) {
		// Set default value to show the it wasn't set
		description = synthbio.gui.defaultDescriptionString;
	} else if(description.length > maxlength) {
		// Crop the display string if needed
		description = description.substring(0, maxlength) + "...";
	}

// And update this in the GUI
	$("#circuit-filename").html(filename);
	$("#circuit-description").html('"' + description + '"');
};
/**
 * Start or stop editing the circuit title/description in the main GUI. This mainly concerns replacing DOM elements.
 * Declared as a closure so it can store the original DOM state locally.
 */
synthbio.gui.editCircuitDetails = function(event) {
	var details = $("#circuit-details");
	var button = $(event.target);
	// Check in which state the circuit details display thingy is (i.e. editting or displaying)
	if(button.html() === "Edit") {
		// Set new content
		details.html(
			'<input class="span2" type="text" id="circuit-filename" placeholder="filename" value="' + synthbio.model.getName() + '">' +
				'<input class="span4" type="text" id="circuit-description" placeholder="circuit description" value="' + synthbio.model.getDescription() + '">');
		// And the old button with a new label
		details.append(button);
		button.on("click", synthbio.gui.editCircuitDetails);
		button.html("Save");
	} else if(button.html() === "Save") {
		// If we were in editing and save was clicked, get the shizzle for ma nizzle.
		var filename = $("#circuit-filename", details).val();
		var description = $("#circuit-description", details).val();
		// And set the original content again
		details.html(
			'<i id="circuit-filename"></i>' +
				'<strong id="circuit-description"></strong>'
		);
		// Save and display the details on display in the the just set elements
		synthbio.gui.setCircuitDetails(filename, description);
		// And the old button with a new label
		details.append(button);
		button.on("click", synthbio.gui.editCircuitDetails);
		button.html("Edit");
	} else {
		console.error("Circuit details element (top right) entered an invalid state." +
			"Should be either the editting or displaying. Is checked by comparing the button inner HTML.");
	}
};

jsPlumb.ready(function() {

	jsPlumb.setRenderMode(jsPlumb.SVG);
	jsPlumb.Defaults.Container = $("#grid-container");

	//Repaint everything after a window resize
	$(window).resize(function() {
		jsPlumb.repaintEverything();
	});

	jsPlumb.importDefaults({
		DragOptions : { cursor: 'pointer', zIndex: 2000 },

		//Overlays for wires
		ConnectionOverlays : [
			//Arrow overlay
			[ "Arrow", { location: 0.92 } ],

			//Text overlay
			[ "Label", {
				location: 0.5,
				id: "label",
				cssClass: "popover-inner"
			}]
		]
	});			

	// This is the paint style for the connecting lines..
	var pointHoverStyle = {
		lineWidth: 3,
		strokeStyle: "purple"
	},
	connectorPaintStyle = {
		lineWidth: 3,
		strokeStyle: "#deea18",
		joinstyle: "round"
	},
	// .. and this is the hover style. 
	connectorHoverStyle = {
		lineWidth: 5,
		strokeStyle: "#2e2aF8"
	};

	// The definition of input endpoints
	synthbio.gui.inputEndpoint = {
		endpoint: "Rectangle",					
		paintStyle:{ fillStyle: "#558822",width: 11,height: 11 },
		hoverPaintStyle: pointHoverStyle,
		isTarget: true,
		dropOptions: {
			activeClass:'dragActive',
			hoverClass:'dragHover'
		},
		beforeDrop: function(opt) {
			if (!opt.connection) {
				return true;
			}

			var src = opt.connection.endpoints[0] || opt.sourceId;
			var dst = opt.dropEndpoint || opt.targetId;
			var reconnect = false;

			if (opt.sourceId === "gate-input" && !opt.connection.endpoints[0].getUuid()) {
				// Get a free endpoint for "gate-input"
				src = synthbio.gui.getFreeEndpoint(opt.sourceId, false);
				reconnect = true;
			}
			if (opt.targetId === "gate-output" && !opt.connection.endpoints[1].getUuid()) {
				// Get a free endpoint for "gate-output"
				dst = synthbio.gui.getFreeEndpoint(opt.targetId, true);
				reconnect = true;
			} 
			
			if (!reconnect) {
				return true;
			}

			// Reconnect and disallow the old connection
			jsPlumb.connect({
				source: src,
				target: dst
			});

			return false;
		}
	};

	// The definition of target endpoints
	synthbio.gui.outputEndpoint = {
		endpoint: "Dot",
		paintStyle:{ fillStyle: "#225588", radius: 7 },
		connector: ["Bezier", { curviness: 85 } ],
		connectorStyle: connectorPaintStyle,
		hoverPaintStyle: pointHoverStyle,
		connectorHoverStyle: connectorHoverStyle,
		isSource: true,
		maxConnections: -1
	};
	
	jsPlumb.draggable("gate-input", {
		handle: "h4",
		stop: function(event, ui) {
			synthbio.model.gateInputPos = {x: ui.position.left, y: ui.position.top};
		}
	});
	jsPlumb.draggable("gate-output", {
		handle: "h4", 
		start: function() {
			$(".output").css("right", "auto");
		},
		stop: function(event, ui) {
			synthbio.model.gateOutputPos = {x: ui.position.left, y: ui.position.top};
		}
	});

	var outputEndpoint = $.extend(true, {
		anchor: "Continuous",
		deleteEndpointsOnDetach: false
	}, synthbio.gui.outputEndpoint);
	var inputEndpoint = $.extend(true, {
		anchor: "Continuous",
		deleteEndpointsOnDetach: false
	}, synthbio.gui.inputEndpoint);

	jsPlumb.makeSource("gate-input", outputEndpoint);
	jsPlumb.makeTarget("gate-output", inputEndpoint);

	// Listen for new jsPlumb connections
	jsPlumb.bind("jsPlumbConnection", function (connInfo) {
		// Get the signal object corresponding with the one in synthbio.model
		var signal = connInfo.connection.getParameter("signal");
		signal = signal || synthbio.gui.displayConnection(connInfo.connection).signal;

		// Get endpoint indices
		signal.fromEndpoint = synthbio.gui.getEndpointIndex(connInfo.connection.endpoints[0]);
		signal.toEndpoint = synthbio.gui.getEndpointIndex(connInfo.connection.endpoints[1]);

		// Try to determine the protein for this connection
		if (!signal.getProtein()) {
			// Is this a reconnection?
			var oldsignal = connInfo.connection.getParameter("oldsignal");
			var prot = (oldsignal && oldsignal.getProtein) ? oldsignal.getProtein() : "";
		
			// If not, try to determine from model
			prot = prot || synthbio.model.determineProtein(signal.getFrom(), signal.fromEndpoint);

			// Update protein
			signal.setProtein(prot);
		}

		// Set signal/oldsignal parameter for this connection
		connInfo.connection.setParameter("signal", undefined);
		connInfo.connection.setParameter("oldsignal", signal);

		// Create a label
		synthbio.gui.setProteinLabel(signal, connInfo.connection);
	});

	// Listen for disposal of connections; delete endpoints if necessary
	jsPlumb.bind("jsPlumbConnectionDetached", function (connInfo) {
		if (connInfo.sourceId === "gate-input" && !connInfo.sourceEndpoint.connections.length) {
			jsPlumb.deleteEndpoint(connInfo.sourceEndpoint);
		}
		if (connInfo.targetId === "gate-output" && !connInfo.targetEndpoint.connections.length) {
			jsPlumb.deleteEndpoint(connInfo.targetEndpoint);
		}

		synthbio.gui.removeDisplaySignal(connInfo.connection.id, true);
	});

	// Remove connection on double click	
	jsPlumb.bind("dblclick", function(conn) {
		if (confirm("Delete this connection?")) {
			synthbio.gui.removeDisplaySignal(conn.id);
		}
	});
});

/**
 *
 * @param show Optional, true is to show, false is to hide, if none provided, the state is toggled.
 */
synthbio.gui.showSimulationTabs = function(show) {
	var tabs = $("#simulation-tabs");
	var workspace = $("#grid-container");
	// If first argument is of time boolean (can also be used as event object)
	if(show === true || show === false) {
		if(show) {
			// Clear the override, let it return to default
			tabs.css("bottom", "");
			workspace.css("bottom", tabs.height() + synthbio.gui.statusBarHeight + "px");
		} else {
			tabs.css("bottom", "-" + tabs.height() + "px");
			// Clear the override, let it return to default
			workspace.css("bottom", "");
		}
		synthbio.gui.simulationTabsVisible = show;
	} else {
		// toggle using recursive call
		synthbio.gui.showSimulationTabs(!synthbio.gui.simulationTabsVisible);
	}
};
/**
 *
 * @param fullscreen Optional, true is to go fullscreen, false is to go back to a variable size state, if none provided, the state is toggled.
 */
synthbio.gui.simulationTabsFullscreen = function(fullscreen) {
	var tabs = $("#simulation-tabs");
	var workspace = $("#grid-container");
	var tabbar = $(".nav-tabs.navbar", tabs);
	var chevron = $('[class^="icon-chevron-"], [class*=" icon-chevron-"]');
	// If first argument is of time boolean (can also be used as event object)
	if(fullscreen === true || fullscreen === false) {
		if(fullscreen) {
			tabs.css("top", synthbio.gui.navbarHeight);
			// Store the current hight in an attribute
			tabs.attr("data-height", tabs.height());
			tabs.css("height", "auto");
			workspace.css("display", "none");
			tabbar.draggable('disable');
			chevron.attr("class", chevron.attr("class").replace("up", "down")); // Flip the fullscreen toggle arrow
		} else {
			// Clear the overrides, let it return to default
			tabs.css("top", "");
			tabs.css("height", tabs.attr("data-height"));
			tabs.attr("data-height", "");
			workspace.css("display", "");
			tabbar.draggable('enable');
			chevron.attr("class", chevron.attr("class").replace("down", "up")); // Flip the fullscreen toggle arrow
		}
		synthbio.gui.plotResize();
		synthbio.gui.simulationTabsInFullscreen = fullscreen;
	} else {
		// toggle using recursive call
		synthbio.gui.simulationTabsFullscreen(!synthbio.gui.simulationTabsInFullscreen);
	}
};

$(document).ready(function() {
	// Initialize new gate-dragging
	$('#gates-basic .gate').draggable($.extend({}, synthbio.gui.draggableOptions, { 
		stop: function() {
			var pos = synthbio.gui.draggableOptions.stop.apply(this, arguments);
			if (pos !== false) {
				// Create new gate
				var newGate = synthbio.model.addGate(
					$(this).attr('class').split(' ')[1], // type of gate (second word in class of the element)
					pos
				);

				// Display gate in grid
				synthbio.gui.displayGate(newGate);
			}
		}
	}));

	// Bind listeners to make the tabs resizable
	(function() {
		var tabs = $("#simulation-tabs");
		var tabsContent = $("#simulation-tabs .tab-content");
		var tabbar = $(".nav-tabs.navbar", tabs);
		var workspace = $("#grid-container");

		var currentHeight;
		// Allow resizing of the simulation tabs space
		tabbar.draggable({
			axis: "y",
			distance: 10,
			helper: function() { return $('<div style="display: none">I am the lonely simulation tab helper. I am just a position data sending dummy, and should never appear.</div>'); },
			start: function(event, ui) {
				currentHeight = tabs.height();
			},
			drag: function(event, ui) {
				var offsetY = (ui.originalPosition.top - ui.position.top);
				var newHeight = currentHeight + offsetY;

				// Modify by now allowing to go lower than minimum height
				if(newHeight < parseInt(tabs.css("min-height"), 10)) {
					newHeight = parseInt(tabs.css("min-height"), 10);
				}

				tabs.height(newHeight);
				workspace.css("bottom", newHeight + synthbio.gui.statusBarHeight + "px");
			},
			stop: function() {
				synthbio.gui.plotResize();
				jsPlumb.repaintEverything();
			}
		});

		// Also allow fullscreen toggle by a double click on the window bar like most OS userspaces allow
		tabbar.on('dblclick', synthbio.gui.simulationTabsFullscreen);
	})();

	// Save the default validity tab contents for another day (to show again after a reset for example)
	synthbio.gui.defaultValidityTabHTML = $("#tab-validate .alert").html();
	// Bind listener to the simulation tabs close button
	$("#simulation-tabs .tab-utilities #tabs-close").on("click", function() {
		synthbio.gui.showSimulationTabs(false);
	});
	// Bind listener to the simulation tabs close button
	$("#simulation-tabs .tab-utilities #tabs-size").on("click", synthbio.gui.simulationTabsFullscreen);
	// Bind listener to the menu item to show simulation tabs
	$("#show-tabs").on("click", synthbio.gui.showSimulationTabs);
	// Prepare default/empty workspace
	synthbio.gui.loadCompounds();
	synthbio.newCircuit();
});

