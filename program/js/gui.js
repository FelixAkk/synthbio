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

	//$('#gates-tab').appendChild(synthbio.gui.createGateElement('and'));
    //$('#gates-tab').appendChild(synthbio.gui.createGateElement('not'));

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
			if(event.pageX > synthbio.gui.gatesTabWidth) {
				var newGate = $(synthbio.gui.createGateElement($(this).attr('class').split(' ')[1]));
				$('#grid-container').append(newGate);

				newGate.css("top", parseInt(event.pageY  - $(this).height()));
				newGate.css("left", parseInt(event.pageX - synthbio.gui.gatesTabWidth));
				synthbio.gui.addGateAnchors(newGate);
			}

			$("#gates-transport .gate").remove();
			$("#gates-transport").css('display', 'none');
		}
	});
});

/**
 * All the JSPlumb source anchors
 */
synthbio.gui.SourceEndpoints = [];

/**
 * All the JSPlumb target anchors
 */
synthbio.gui.TargetEndpoints = [];

/**
 * Add specified number of JSPlumb anchors to a gate
 *
 * @param toId ID of the gate element. UUID of the anchors will be *id*_input*number* (and id_outputNUMBER)
 * @param inputAnchors Number of input anchors
 * @param outputAnchors Number of output anchors
 */
synthbio.gui.addPlumbAnchors = function(toId, inputAnchors, outputAnchors) {
	inputAnchors--;
	outputAnchors--;
	
	var placement = function(num, total) {
		return (total < 1) ? 0.5 : (num / total);
	}

	for (var j = 0; j <= inputAnchors; j++) {
		var targetUUID = toId + "_input" + j;
		synthbio.gui.TargetEndpoints.push(jsPlumb.addEndpoint(toId, synthbio.gui.inputEndpoint, { anchor:[0, placement(j, inputAnchors), -1, 0], uuid:targetUUID }));
	} 
	for (var i = 0; i <= outputAnchors; i++) {
		var sourceUUID = toId + "_output" + i;
		synthbio.gui.SourceEndpoints.push(jsPlumb.addEndpoint(toId, synthbio.gui.outputEndpoint, { anchor:[1, placement(i, outputAnchors), 1, 0], uuid:sourceUUID }));
	}
}

/**
 * Add the correct number of anchors to a gate
 *
 * @param elem Target element. Uses the class attribute to determine the proper number of anchors.
 */
synthbio.gui.addGateAnchors = function(elem) {
	elem = $(elem);
	var gateClass = elem.attr('class').split(' ')[1];
	var gateID = elem.attr("id");

	//TODO: Better way to determine number of inputs/outputs (also for compound gates).
    if(gateClass == "not")
		synthbio.gui.addPlumbAnchors(gateID, 1, 1)
	else if (gateClass == "and")
		synthbio.gui.addPlumbAnchors(gateID, 2, 1)
	//else TODO: implement exception throwing using synthbio.util
}

/**
 * Create a new gate DOM element to be used within the modelling grid.
 *
 * @param gateClass The class/type of gate to be created. Can be either the string 'not', 'and' or 'compound'.
 */
synthbio.gui.createGateElement = function(gateClass) {
    // TODO: use synthbio.util
    if(gateClass !== "not" && gateClass !== "and" && gateClass !== "compound") {
        // TODO: implement exception throwing using synthbio.util
        return;
    }

	synthbio.gui.gateCounter++;
	var id = "gate" + synthbio.gui.gateCounter;

    var element = $("<div íd=\""+id+"\" class=\"gate " + gateClass + "\">"
        + "<embed src=\"../img/gates/" + gateClass + ".svg\" type=\"image/svg+xml\" />"
        + "<div class=\"mask\"></div>"
        + "</div>");

	jsPlumb.draggable(element);
    return element;
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
			.always(function() { setTimeout(synthbio.gui.pingServer, frequency); });
	};
}();
