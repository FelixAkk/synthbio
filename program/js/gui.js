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
 * @author Felix Akkermans & Jan-Pieter Waagmeester & Niels Doekemeijer
 * GUI JavaScript Document, concerns all GUI matters except those about the modeling grid.
 */

/*jslint devel: true, browser: true, forin: true, vars: true, plusplus: true, sloppy: true, white: true, maxerr: 50, indent: 4 */
/*global $, synthbio, jsPlumb */

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
synthbio.gui.gateDimensions = (function () {
	// Create
	var dummyGate = $('<div class="gate"></div>').hide().appendTo("body");
	// Get
	var dimensions = {
		width: parseFloat(dummyGate.css("width"), 10),
		height: parseFloat(dummyGate.css("height"), 10)
	};
	// Clean
	dummyGate.remove();
	// Assign
	return dimensions;
}());

/**
 * Variable that references the function that is to be used for handling a file selection in the file dialog. This is
 * a little trickery/hackery because we use 1 dialog for all file operations (save as, open, view), and always set this
 * variable to the corresponding event handler.
 *
 * @param index This function is always given an integer index. This correspons to the selected file in the array the
 * synthbio.requests.listFiles() AJAX RPC provided just before the file was selected.
 */
synthbio.gui.fileOpHandler = function(index) {
	console.error("Oops! No file selection event handler has been set. This is the default which should never fire!");
};

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
};
synthbio.gui.resetTooltip = function() {
	$("#info").html("Mouse over info something for info.");
};

/**
 * Functions to support the input editor.
 */

/**
 * Create the input editor.
 */
synthbio.gui.inputEditor = function(){
	console.log('entered inputEditor');
	
	//clear input signals container.
	$('#input-signals').html('');

	var i;
	var inputs=synthbio.model.getInputs();

	//@todo check if all inputs are defined.

	
	//iterate over signals and create signal input editors.
	$.each(
		inputs.values,
		function(name, ticks){
			var signalEditor=$('<div class="signal" id="signal'+name+'">'+name+': <i class="toggle-highlow low icon-resize-vertical" title="Set signal always on, always off or costum"></i> </div>');
			var levels='<div class="levels">';
			var currentLevel="L";
			for(i=0; i<inputs.length; i++){
				if(i < ticks.length){
					currentLevel=ticks.charAt(i);
				}
				levels+='<div class="tick '+(currentLevel==="H" ? 'high': 'low')+'"></div>';
			}
			levels+='</div>';
					
			signalEditor.append(levels);
			$('#input-signals').append(signalEditor);
		}
	);
	
	//attach click listener to the high/low button.
	$('.toggle-highlow').click(function(){
		var self=$(this);
		self.toggleClass('high').toggleClass('low');
		self.parent().find('.levels div').toggleClass('high').toggleClass('low');
	});
	
	//click listener for each .levels div containing ticks.
	$('.levels').click(function(event){
		if($(event.target).hasClass('tick')){
			$(event.target).toggleClass('low').toggleClass('high');
			$(this).find('toggle-highlow').removeClass('low').removeClass('high');
		}
	});
};

/**
 * Save inputs back to circuit.
 * 
 * @param circuit the circuit to save to, defaults to syntbio.model
 */
synthbio.gui.saveInputs = function(circuit){
	circuit=circuit || synthbio.model;
	var inputs={
		"length": circuit.getSimulationLength(),
		"values": {}
	};
	$('.signal').each(function(index, elem){
		var signal='';
		$(this).find('.tick').each(function(index, elem){
			if($(elem).hasClass('high')){
				signal+='H';
			}else{
				signal+='L';
			}
		});

		inputs.values[$(this).attr('id').substr(-1)]=signal;
	});
	circuit.setInputs(inputs);
};

$(document).ready(function() {
	// Activate zhe Dropdowns Herr Doktor!
	$('.dropdown-toggle').dropdown();
	
	// Load proteins from server.
	$('#list-proteins').on('show', function() {
		synthbio.requests.getCDSs(function(response) {
			if (response instanceof String) {
				$('#list-proteins tbody td').html(response);
				return;
			}
			var html='';
			$.each(response, function(i, cds) {
				html+='<tr><td>'+cds.name+'</td><td>'+cds.k2+'</td><td>'+cds.d1+'</td><td>'+cds.d2+'</td></tr>';
			});
			$('#list-proteins tbody').html(html);
		});
	});

	// Validate
	$('#validate').on('click', function(){

		synthbio.model.getInputs();
		
		console.log('Started validating circuit...');
		synthbio.requests.validate(
			function(response){
				if(response.message !== '') {
					console.log(response.message);
					console.log(synthbio.model);
				}
			},
			synthbio.model
		);
	});

	// Build the input thing.
	$('#define-inputs').on('show', function() {

		// build the editor
		synthbio.gui.inputEditor();

		// attach action to save button.
		$('#save-inputs').click(function(){
			synthbio.gui.saveInputs();
		});
	});

	

	// Setup file operation dialog on menu clicks
	$("#save-as").on("click", function() {
		$("#files .modal-header h3").html("Save As…");
		$("#files .modal-footer .btn-primary").html("Save As…");
	});
	$("#open").on("click", function() {
		$("#files .modal-header h3").html("Open…");
		$("#files .modal-footer .btn-primary").html("Open…");
	});
	// Cleanup time; prepare it for another time
	$('#files').on('hidden', function() {
		$("#files tbody").html('<tr><td>Loading ...</td></tr>');
		var inputfield = $("#files .modal-footer input");
		// clear entered text
		inputfield[0].value = '';
		// clear autocomplete
		inputfield.typeahead({
			source: []
		});

	});

	// List files from server.
	$('#files').on('show', function(event) {
		// Request stuff from server and define what happens next
		synthbio.requests.listFiles(function(response) {
			// problemu technicznego
			if(response instanceof String) {
				$('#list-files tbody td').html(response);
				return;
			}
			// Setup the text input box for entering the filename operate on
			$("#files .modal-footer input").typeahead({
				// The possible auto completions, the same as the files listed
				source: response
			});
			var html='';
			$.each(response, function(i, file) {
				html+='<tr><td>'+file+'</td><td>x</td><td>x</td></tr>';
			});
			$('#files tbody').html(html);

			// Make each row respond to selection
			$("#files tbody tr").each(function(index, element) {
				element = $(element); // extend to provide the .on() function
				element.on("click", function() {
					synthbio.gui.fileOpHandler(index);
				});
			});
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
	synthbio.gui.setTooltip($("#gates-basic"),      "Predefined AND and NOT gates (fixed). Drag into the modelling grid.");
	synthbio.gui.setTooltip($("#gates-compound"),   "User defined compound gates (loaded from .syn). Drag into the modelling grid.");
	synthbio.gui.setTooltip($("#grid-container"),   "Hold Ctrl and drag a marquee to select a group of gates. " +
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
			{ "from": "input", "to": 0, "protein": "A", "fromEndpoint": 1, "toEndpoint": 1},
			{ "from": "input", "to": 0, "protein": "B", "fromEndpoint": 0, "toEndpoint": 0},
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
	var id;
	jsPlumb.removeEveryEndpoint();
	for (id in synthbio.gui.displayGateIdMap) {
		synthbio.gui.removeDisplayGate(id);
	}

	synthbio.gui.displayGateIdMap = {};
	synthbio.gui.displaySignalIdMap = {};

	//Workaround for bug in jQuery/jsPlumb (Firefox only)
	jsPlumb.addEndpoint("grid-container").setVisible(false);
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
		var inputUUID = toId + "::input::" + i;
		res.inputEndpoints.push(jsPlumb.addEndpoint(toId, synthbio.gui.inputEndpoint, { 
			anchor:[0, placement(i, inputEndpoints), -1, 0], 
			uuid:inputUUID 
		}));
	}
	for (i = 0; i <= outputEndpoints; i++) {
		var outputUUID = toId + "::output::" + i;
		res.outputEndpoints.push(jsPlumb.addEndpoint(toId, synthbio.gui.outputEndpoint, { 
			anchor:[1, placement(i, outputEndpoints), 1, 0], 
			uuid:outputUUID 
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
	
	var idx = parseInt(endpoint.split("::").pop(), 10);
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
		var UUID = "gate-input::output::";
		if (index === undefined) {
			UUID += inputCounter++;
		} else {
			UUID += index;
			inputCounter = Math.max(inputCounter, index + 1);
		}

		return jsPlumb.addEndpoint(
			"gate-input",
			synthbio.gui.outputEndpoint, 
			{ anchor: "Continuous", uuid:UUID }
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
		ep = jsPlumb.getEndpoint(id + "::" + ((input) ? "input" : "output") + "::" + index);

		// If endpoint does not exist for gate-input, create it
		if (!ep && !input && id === "gate-input") {
			ep = synthbio.gui.newInputEndpoint(index);
		}
		
		// Return endpoint if found, else the GUI id
		return ep || id;
	}

	// Check if index is in the endpoint array
	if (index && index[ep]) {
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
		+ '<div class="mask"></div>'
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
		connection = jsPlumb.connect({
			source: synthbio.gui.getFreeEndpoint(src, false, signal.fromEndpoint),
			target: synthbio.gui.getFreeEndpoint(dst, true, signal.toEndpoint),
			parameters: {signal: signal}
		});
	}

	var res = {connection: connection, signal: signal};
	synthbio.model.addSignal(signal);
	synthbio.gui.displaySignalIdMap[connection.id] = res;
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
 * @return Object Deleted object.
 */
synthbio.gui.removeDisplaySignal = function(id) {
	var obj = synthbio.gui.displaySignalIdMap[id];
	if (obj) {
		if (obj.connection) {
			jsPlumb.detach(obj.connection);
		}
		if (obj.signal) {
			synthbio.model.removeSignal(obj.signal);
		}
		delete synthbio.gui.displaySignalIdMap[id];
	}
	return obj;
};

/**
 * Ping server to check for connection 'vitals'. Shown a warning if things go really bad. Declared as a closure to keep
 * variables local.
 */
synthbio.gui.pingServer = (function () {
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
				if(fCount <= limit+1) {
					// Keep counting untill the dialog was shown
					fCount++;
				}
				if(fCount === limit){
					// Show the dialog once
					$('#connection-modal').modal();
				}
			})
			.always(function() { setTimeout(synthbio.gui.pingServer, frequency); });
	};
}());
