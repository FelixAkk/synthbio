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

$(document).ready(function() {
	// Activate zhe Dropdowns Herr Doktor!
	$('.dropdown-toggle').dropdown();
	
	// Load proteins from server.
	$('#list-proteins').on('show', function(){
		synthbio.requests.getCDSs(function(response){
			if(response instanceof String){
				$('#list-proteins tbody td').html(data.response);
				return;
			}
			var html='';
			$.each(response, function(i, cds){
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
		start: function(event){
			// Prepare transport layer
			$("#gates-transport").css('display', 'block');
		},
		drag: function(event, ui) {
			// Display gate border if dragging in grid (and gate can be dropped)
			var dragInGrid = event.pageX > synthbio.gui.gatesTabWidth;
			$(ui.helper).toggleClass("gate-border", dragInGrid);
		},
		stop: function(event, ui){
			// Add new gate to circuit
			var newGate = synthbio.model.addGate(
				$(this).attr('class').split(' ')[1], // type of gate (second word in class of the element)
				[event.pageX - synthbio.gui.gatesTabWidth, event.pageY  - $(this).height()] // position
			);

			// Display gate in grid
			synthbio.gui.displayGateModel(newGate);

			// Clean up transport layer
			$("#gates-transport .gate").remove();
			$("#gates-transport").css('display', 'none');
		}
	});

	// Start pinging
	synthbio.gui.pingServer();
});

/**
 * Add specified number of JSPlumb anchors to a gate
 *
 * @param toId ID of the gate element. UUID of the anchors will be *id*_input*number* (and id_outputNUMBER)
 * @param inputAnchors Number of input anchors
 * @param outputAnchors Number of output anchors
 * @return Object with inputEndpoints and outputEndpoints (both arrays)
 */
synthbio.gui.addPlumbAnchors = function(toId, inputAnchors, outputAnchors) {
	var res = {
		inputEndpoints: [],
		outputEndpoints: []
	}
	inputAnchors--;
	outputAnchors--;
	
	// Function to calculate the placement (1/2 if there's only one to place, else 1/total)
	var placement = function(num, total) {
		return (total < 1) ? 0.5 : (num / total);
	}

	for (var j = 0; j <= inputAnchors; j++) {
		var inputUUID = toId + "_input" + j;
		res.inputEndpoints.push(jsPlumb.addEndpoint(toId, synthbio.gui.inputEndpoint, { 
			anchor:[0, placement(j, inputAnchors), -1, 0], 
			uuid:inputUUID 
		}));
	} 
	for (var i = 0; i <= outputAnchors; i++) {
		var outputUUID = toId + "_output" + i;
		res.outputEndpoints.push(jsPlumb.addEndpoint(toId, synthbio.gui.outputEndpoint, { 
			anchor:[1, placement(i, outputAnchors), 1, 0], 
			uuid:outputUUID 
		}));
	}
	
	return res;
}

/**
 * Add the correct number of anchors to a gate
 *
 * @param gateModel Target element (object or selector)	
 * @return Returns the model, with anchors properties added
 */
synthbio.gui.addGateAnchors = function(gateModel) {
	var anchors = synthbio.gui.addPlumbAnchors(
		gateModel.element.attr("id"),
		gateModel.model.getInputCount(),
		gateModel.model.getOutputCount()
	);

	// Extend gateModel with anchors and return
	return $.extend(true, anchors, gateModel);
}

/**
 * Maps an (display) element ID to the proper gate object
 */
synthbio.gui.displayGateIdMap = {/*id12: object12, id34: object34*/};

/**
 * Returns gate object by GUI id
 * @param id string
 * @return Object with element, model and anchors (exception if not found)
 */
synthbio.gui.getGateById = function(id) {
	if (id == "gate-input")
		return "input";
	else if (id == "gate-output")
		return "output";
	else if (synthbio.gui.displayGateIdMap[id])
		return synthbio.gui.displayGateIdMap[id];
	else
		throw "Cannot map id to gate";
}

/**
 * Returns gate index by GUI id
 * @param id string
 * @return index (exception if not found)
 */
synthbio.gui.getGateIndexById = function(id) {
	var gate = synthbio.gui.getGateById(id);
	if (gate.model)
		gate = synthbio.model.indexOfGate(gate.model);
	return gate;
}

/**
 * Adds a new gate DOM element to be used within the modelling grid.
 *
 * @param gateModel synthbio.Gate
 * @return Object with element, model and anchors.
 */
synthbio.gui.displayGateModel = function(gateModel) {
	if(!gateModel)
		return;
	
	// Create new display element
	var element = $("<div class=\"gate " + gateModel.getType() + "\">"
		+ gateModel.getImage(true)
		+ "<div class=\"mask\"></div>"
		+ "</div>");

	// Place new element in grid
	$('#grid-container').append(element);
	element.css("left", gateModel.getX());
	element.css("top", gateModel.getY());

	// Make the gate draggable
	jsPlumb.draggable(element, {
		stop: function(event, ui){
			gateModel.setPosition([
				element.css("left"), 
				element.css("top")
			]);
		}
	});

	// Delete on double click
	element.dblclick(function() {
		if (!confirm("Delete " + gateModel.toString() + "?"))
			return;

		jsPlumb.removeAllEndpoints(element);
		element.remove();
	});

	// Add anchors
	var res = synthbio.gui.addGateAnchors({element: element, model: gateModel});

	// Add to ID map
	synthbio.gui.displayGateIdMap[element.attr("id")] = res;

	return res;
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
