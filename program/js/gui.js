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
				var newGate = $(synthbio.gui.createGateElement("not"/*this.className.substring(5)*/));
				$('#grid-container').append(newGate);

				newGate.css("top", parseInt($(this).offset().top));
				newGate.css("left", parseInt(event.pageX - synthbio.gui.gatesTabWidth));
				jsPlumb.draggable(newGate);
			}

			$("#gates-transport .gate").remove();
			$("#gates-transport").css('display', 'none');
		}
	});
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
