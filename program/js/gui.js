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
 * @author Felix Akkermans & Jan-Pieter Waagmeester
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
 * Width of the <aside> element with all the gates in pixels.
 */
synthbio.gui.gatesTabWidth = parseFloat($('#gates-tab').css("width"));

$(document).ready(function() {
    // Activate zhe Dropdowns Herr Doktor!
	$('.dropdown-toggle').dropdown();
	
	// Start pinging
	//pingServer();
	
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

	$('#gates-tab .gate').on('mousedown', synthbio.gui.instantiateGate);
});

/**
 * Create a new gate DOM element to be used within the modelling grid.
 *
 * @param class The class/type of gate to be created. Can be either the string 'not', 'and' or 'compound'.
 */
synthbio.gui.createGateElement = function(gateClass) {
	// TODO: use synthbio.util
	if(gateClass !== "not" && gateClass !== "and" && gateClass !== "compound") {
		// TODO: implement exception throwing using synthbio.util
		return;
	}

	var element = $("<div class=\"gate " + gateClass + " draggable\">"
		+ "<embed src=\"../img/gates/" + gateClass + ".svg\" type=\"image/svg+xml\" />"
		+ "<div class=\"mask\"></div>"
		+ "</div>");
	return element;
}

/**
 * Instantiate/copy/duplicate a provided gate HTML element and place it as a draggable in the transport
 *
 * @param event JavaScript event provided by default. Required for getting mouse/pointer position.
 */
synthbio.gui.instantiateGate = function(event) {
	// We need the event object. Without we have no idea where the mouse is and exit the function call.
	if(arguments.length < 1) {
		// TODO: implement exception throwing using
		return;
	}

	/**
	 * Gated used in the transport layer (not the one for in the grid, for that we construct a clone).
	 */
	var	dragGate = synthbio.gui.createGateElement(this.className.substring(5));
	var gatesTransport = $('#gates-transport');

	/**
	 * Designates whether the gate has crossed the border between the gates tab and the grid. Used in optimizations.
	 */
	var cross = false;

	gatesTransport.append(dragGate);
	dragGate.css('position', 'absolute');
	dragGate.css('left', (event.pageX - 50)+'px');
	dragGate.css('top',  (event.pageY - 25 - parseInt(gatesTransport.css('top')))+'px');
	gatesTransport.css('display', 'block');
	dragGate.draggable({
		stop: function(event)
		{
			// The gate must have been moved into the modelling grid (beyond the gates tab) to be added
			if(event.pageX > synthbio.gui.gatesTabWidth) {
				var newGate = $(this).clone();
				// Compensate for the offset of the grid so it ends up where the cursor is
				newGate.css("left", parseInt(newGate.css("left")) - synthbio.gui.gatesTabWidth);
				// Remove the glow
				newGate.removeClass("gate-border");
				// Add it to the modelling grid
				$('#grid-container').append(newGate);
				// Make the new draggable in the jsPlumb system
				jsPlumb.draggable(newGate);
			}

			/**
			 * Cleanup: remove the gate that was in the transport layer, and then hide it again.
			 * Recommended to remove ALL gates in the transport layer. Just removing the currently dragging gate
			 * proves troublesome because it leaves them unremoved in some situations.
			 */
			$("#gates-transport .gate").remove();
			gatesTransport.css('display', 'none');
		},
		drag: function(event) {
			if(!cross && event.pageX > synthbio.gui.gatesTabWidth) {
				// When we first crossed, do once
				$(this).addClass("gate-border");
				cross = true;
			} else if(cross && event.pageX < synthbio.gui.gatesTabWidth) {
				// Else if we crossed back again, do one
				$(this).removeClass("gate-border");
				cross = false;
			}
		}
	});
	dragGate.trigger("mousedown.draggable", [event]);
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