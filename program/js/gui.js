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
	
	// Start pinging
	synthbio.gui.pingServer();
	
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

	//Initialize gate-dragging
	$('#gates-tab .gate').draggable({ 
		appendTo: "#gates-transport",
		containment: 'window',
		scroll: false,
		helper: 'clone',
		start: function(event){
			$("#gates-transport").css('display', 'block');
		},
        drag: function(event, ui) {
            $(ui.helper).toggleClass("gate-border", event.pageX > synthbio.gui.gatesTabWidth);
        },
		stop: function(event, ui){
			synthbio.gui.createGateElement(
				$(this).attr('class').split(' ')[1],
				[event.pageX - synthbio.gui.gatesTabWidth, event.pageY  - $(this).height()]
			);

			$("#gates-transport .gate").remove();
			$("#gates-transport").css('display', 'none');
		}
	});
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
 * @param elem Target element (object or selector)	
 * @param amount Amount of anchors [input, output]. If null, the class attribute will be used to determine these numbers.
 * @return Returns the result of synthbio.gui.addPlumbAnchors
 */
synthbio.gui.addGateAnchors = function(elem, amount) {
	elem = $(elem, 0);
	var gateID = elem.attr("id");

	//TODO: Better way to determine number of inputs/outputs (also for compound gates).
	if (!amount || !amount[0] || !amount[1]) {
		var gateClass = elem.attr('class').split(' ')[1];
		if(gateClass == "not")
			amount = [1, 1]
		else if (gateClass == "and")
			amount = [2, 1]
		//else TODO: implement exception throwing using synthbio.util
	}

	synthbio.gui.addPlumbAnchors(gateID, amount[0], amount[1])
}

/**
 * Create a new gate DOM element to be used within the modelling grid.
 *
 * @param gateClass The class/type of gate to be created. Can be either the string 'not', 'and' or 'compound'.
 * @param gatePos Position of the new gate [left, top]. If left < 0 or top < 0, the gate will not be shown
 */
synthbio.gui.createGateElement = function(gateClass, gatePos) {
    // TODO: use models.js -> synthbio.Gate
    if(gateClass !== "not" && gateClass !== "and" && gateClass !== "compound") {
        // TODO: implement exception throwing using synthbio.util
        return;
    }
	if(!gatePos || gatePos[0] < 0 || gatePos[1] < 0)
		return;

	synthbio.gui.gateCounter++;
	var id = "gate" + synthbio.gui.gateCounter;

    var res = {
		idx: synthbio.gui.gateCounter,
		element: $("<div íd=\""+id+"\" class=\"gate " + gateClass + "\">"
			+ "<embed src=\"img/gates/" + gateClass + ".svg\" type=\"image/svg+xml\" />"
			+ "<div class=\"mask\"></div>"
			+ "</div>")
	}

	jsPlumb.draggable(res.element);
	res.element.dblclick(function() {
		alert("removing " + id);
		jsPlumb.removeAllEndpoints(id);
	});

	$('#grid-container').append(res.element);
	res.element.css("left", parseInt(gatePos[0]));
	res.element.css("top", parseInt(gatePos[1]));

    return jQuery.extend(true, synthbio.gui.addGateAnchors(res.element), res);
}

/**
 * Ping server to check for connection 'vitals'. Shown a warning if things go really bad. Declared as a closure to keep
 * variables local.
 */
synthbio.gui.pingServer = function() {
	var date = new Date();
	var fCount = 0; // Failure count: The amount of times that connection attempts have failed. Resets to 0 on success.
	var limit = 3; // Amount of times after which a dialog should prompt the user about the failures.
	var frequency = 500; // The delay between ping calls in milliseconds.
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
			//.always(function() { setTimeout(synthbio.gui.pingServer, frequency); });
	};
}();
