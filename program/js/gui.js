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
synthbio.gui.gatesTabWidth = $('#gates-tab').width();

/**
 * Width of the <aside> element with all the gates in pixels.
 */
synthbio.gui.navbarHeight = $('.navbar').height();

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
 * This variable holds the array of files coming from the last synthbio.requests.getFiles() call.
 * These are reused later when for example checking if the entered file name is existent when overwriting a file.
 */
synthbio.gui.recentFilesList;

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

	
	var inputs=synthbio.model.getSimulationInputs();

	//@todo check if all inputs are defined.

	//fill advanced settings form fields
	$('#simulate-length').val(inputs.getLength());
	$('#simulate-low-level').val(inputs.getLowLevel());
	$('#simulate-high-level').val(inputs.getHighLevel());
	$('#simulate-tick-width').val(inputs.getTickWidth());
	
	//iterate over signals and create signal input editors.
	$.each(
		inputs.getValues(),
		function(name, ticks){
			var signalEditor=$('<div class="signal" id="signal'+name+'">'+name+': <i class="toggle-highlow low icon-resize-vertical" title="Set signal always on, always off or costum"></i> </div>');
			var levels='<div class="levels">';
			var currentLevel="L";
			var i;
			for(i=0; i<inputs.getLength(); i++){
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
synthbio.gui.saveInputs = function(circuit) {
	circuit = circuit || synthbio.model;

	var simulationInput=circuit.getSimulationInputs();
	
	//copy the options from the form to the object.
	synthbio.util.form2object(
		simulationInput,
		[
			{ selector: '#simulate-tick-width', setter: 'setTickWidth' },
			{ selector: '#simulate-length', setter: 'setLength' },
			{ selector: '#simulate-low-level', setter: 'setLowLevel' },
			{ selector: '#simulate-high-level', setter: 'setHighLevel' }
		]
	);

	//copy the values for each input signal 
	$('.signal').each(function(index, elem) {
		var signal = '';
		$(this).find('.tick').each(function(index, tick) {
			if($(tick).hasClass('high')) {
				signal += 'H';
			} else {
				signal += 'L';
			}
		});
		
		var protein=$(this).attr('id').substr(-1);
		simulationInput.setValue(protein, signal);
	});
	
};

$(document).ready(function() {
	// Activate zhe Dropdowns Herr Doktor!
	$('.dropdown-toggle').dropdown();

	// DataTables objects: are initialized on the first showing of each table, and updated every new showing.
	var lpTable; // For: List proteins dialog
	var fTable; // For: Files dialog
	// Generic options used for the DataTables
	var dtOptions = {
		"sDom": "<'row'lir>t<'row'p>",
		"sPaginationType": "bootstrap",
		"oLanguage": {"sLengthMenu": "_MENU_ per page"},
		"bAutoWidth": false,
		"bDestroy": true,
		"bFilter": true,
		"bInfo": false,
		"bLengthChange": false,
		"bPaginate": false
	};

	// Load proteins from server.
	$('#list-proteins').on('show', function() {
		synthbio.requests.getCDSs(function(response) {
			if(response instanceof String) {
				$('#list-proteins tbody td').html(response);
				return;
			}
			// construct table body contents
			var html = '';
			$.each(response, function(i, cds) {
				html += '<tr><td>'+cds.name+'</td><td>'+cds.k2+'</td><td>'+cds.d1+'</td><td>'+cds.d2+'</td></tr>';
			});

			$('#list-proteins tbody').html(html);

			// Convert the new content into a DataTable; clear the variable if it was used before
			if (lpTable) { lpTable.fnClearTable(false); }
			lpTable = $('#list-proteins table').dataTable(dtOptions);
			// Hook up custom search/filter input box for this table. Only the `keyup` event seems give a good result
			$("#list-proteins .modal-footer input").bind("keyup", function(event) {
				lpTable.fnFilter($(this).val());
			});
		});
	});

	// Validate
	$('#validate').on('click', function(){
		console.log('Started validating circuit...');
		synthbio.requests.validate(
			synthbio.model,
			function(response){
				if(response.message !== '') {
					$('#validate-alert p').html(response.message);
					if(!response.success){
						
						$('#validate-alert').addClass("invalid");
					}
					$('#validate-alert').modal();
				}else{
					//call
					alert('simulation valid, display...');
				}
			}
		);

	});
	$("#validate-alert").bind('closed', function(){
		$(this).find("p").html('');
		$('#validate-alert').addClass("invalid");
	});
	
	/**
	 * Dump the circuit to console.
	 */
	$('#dump-circuit').on('click', function() {
		console.log(synthbio.model);
		console.log(JSON.stringify(synthbio.model));
	});
		

	/**
	 *  Build the input editor.
	 */
	$('#define-inputs').on('show', function() {
		// build the editor
		synthbio.gui.inputEditor();

		// attach action to save button.
		$('#save-inputs').click(function(){
			synthbio.gui.saveInputs();
		});
	});

	/**
	 * Run simulation
	 */
	$('#simulate').on('click', function() {
		console.log('Simulate initiated.');
	
		synthbio.requests.simulate(
			synthbio.model,
			function(response){
				console.log("synthbio.request.simulate called response callback");
				if(response.message !== '') {
					$('#validate-alert p').html(response.message);
					if(!response.success){
						
						$('#validate-alert').addClass("invalid");
					}
					
					console.log(synthbio.model);
					$('#validate-alert').modal();
				}else{
					//@todo: implement output visualisation here.
					console.log("Simulation result: ", response.data);
				}
			}
		);
		
	});

	/**
	 * Setup/rig file operation dialog when the `Save As` menu item is clicked.
	 */
	$("#save-as").on("click", function() {
		$("#files .modal-header h3").html("Save As…");
		$("#files .modal-footer .btn-primary").html("Save As…");
		$("#files .modal-footer input").attr("placeholder", "Filename...");
		synthbio.gui.fileModalDesignation = $(this).attr('id');

		// (Re)set to false. Represents whether we have prompted the user for confirmation once before
		var confirmation = false;
		$("#files form").on("submit", function(event) {
			// Surpress default redirection due to <form action="destination.html"> action
			event.stopPropagation();
			event.preventDefault();
			// get the filename
			var input = $("input", this)[0].value.trim();
			// Check if the user is about to overwrite an existing file and hasn't confirmed yet
			if(
				(
					$.inArray(input, synthbio.gui.recentFilesList) >= 0 ||
					$.inArray(input+".syn", synthbio.gui.recentFilesList) >= 0
				)
				&& !confirmation) {
				synthbio.gui.showAdModalAlert('files', 'alert-error',
					"<strong>Overwrite file?</strong> Press enter again to confirm");
				confirmation = true;
				return false;
			}
			// Check if filename is empty
			if(input == "") {
				// Show alert
				synthbio.gui.showAdModalAlert('files', 'alert-error',
					"<strong>Incorrect filename:</strong> Filename may not be empty or consist of spaces/tabs.");
				return false;
			}
			// Save the file, let's see if it works
			synthbio.requests.putFile(input, synthbio.model, function(response) {
				if(response.success === false) {
					synthbio.gui.showAdModalAlert('files', 'alert-error',
						'<strong>File was not saved.</strong> ' + response.message + '</div>');
					console.error(response.message);
				}
				// We're done; hide
				$("#files").modal("hide");
			});
			return false; // would prevent the form from making us go anywhere if .preventDefault() fails
		});
	});
	/**
	 * Setup/rig file operation dialog when the `Open` menu item is clicked.
	 */
	$("#open").on("click", function() {
		synthbio.resetProteins();
		$("#files .modal-header h3").html("Open…");
		$("#files .modal-footer .btn-primary").html("Open…");
		$("#files .modal-footer input").attr("placeholder", "Search...");
		synthbio.gui.fileModalDesignation = $(this).attr('id');

		$("#files form").on("submit", function(event) {
			// Surpress default redirection due to <form action="destination.html"> action
			event.stopPropagation();
			event.preventDefault();
			// Get the filename
			var input = $("input", this)[0].value.trim();

			// Check if filename is empty
			if(input == "") {
				// Show alert
				synthbio.gui.showAdModalAlert('files', 'alert-error',
					'<strong>Incorrect filename:</strong> Filename may not be empty or consist of spaces/tabs.');
				return false;
			}

			// Load the file, let's see if it works
			synthbio.requests.getFile(input, function(response) {
				if(response.success === false) {
					console.error(response.message);
					synthbio.gui.showAdModalAlert('files', 'alert-error',
						'<strong>File was not opened.</strong> ' + response.message);
				}
				synthbio.loadCircuit(synthbio.Circuit.fromMap(response.data));
				// We're done; hide
				$("#files").modal("hide");
			});
			return false; // would prevent the form from making us go anywhere if .preventDefault() fails
		});
	});

	// Cleanup time; prepare the file dialog for another time
	$('#files').on('hidden', function() {
		$("#files tbody").html('<tr><td>Loading ...</td></tr>');
		var inputfield = $("#files .modal-footer input");
		// clear entered text
		inputfield[0].value = '';
		// clear autocomplete
		inputfield.typeahead({
			source: []
		});
		$("#files .height-transition-box").removeClass("visible");
	});

	// List files from server.
	var fTable;
	$('#files').on('show', function(event) {
		// Request stuff from server and define what happens next
		synthbio.requests.listFiles(function(response) {
			// problemu technicznego
			if(response instanceof String) {
				$('#list-files tbody td').html(response);
				return;
			}

			// Save the list of files for later
			synthbio.gui.recentFilesList = response;

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

			// convert the new content into a DataTable; clear the variable if it was used before
			if (fTable) { fTable.fnClearTable(false); }
			fTable = $('#files table').dataTable(dtOptions);

			// Make each row respond to selection
			$("#files tbody tr").each(function(index, element) {
				element = $(element); // extend to provide the .on() function
				element.on("click", function() {

					switch(synthbio.gui.fileModalDesignation) {
						case "open":
							console.log("select for open");
							break;
						case "save-as":
							console.log("select for save overwrite");
							break;
					}
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
	//synthbio.gui.pingServer();
	
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
		//display the define-inputs modal for quicker development.
		//$('#define-inputs').modal();
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
 * Function for animating in an alert at the bottom of a modal.
 *
 * @param modal the ID of the modal in the HTML, without the hash character
 * @param alertClass One of the Bootstrap alert classes, like "alert-error" or "alert-success".
 * See http://twitter.github.com/bootstrap/components.html#alerts
 * @param innerHTML The HTML for inside the alert div
 */
synthbio.gui.showAdModalAlert = function(modal, alertClass, innerHTML) {
	$("#files .height-transition-box .modal-footer").html('<div class="alert ' + alertClass +
		'" style="margin-bottom: 0;">' + innerHTML + '</div>');
	$("#files .height-transition-box").addClass("visible");
}
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
