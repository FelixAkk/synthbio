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
 * GUI JavaScript Document, concerns all basic GUI matters.
 */

/*jslint devel: true, browser: true, forin: true, vars: true, plusplus: true, sloppy: true, white: true, maxerr: 50, indent: 4 */
/*global $, synthbio, jsPlumb, self */

/**
 * syntbio package.
 */
var synthbio = synthbio || {};

/**
 * GUI package
 */
synthbio.gui = synthbio.gui || {};


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
 * Function for animating in an alert at the bottom of a modal. Don't forget to hide it again at some point! See
 * synthbio.gui.hideAdModelAlert to hide a specific alert manually or use the optional timer parameter to hide it
 * automatically after a given numer of milliseconds.
 *
 * @param modal the ID of the modal in the HTML, without the hash character
 * @param alertClass One of the Bootstrap alert classes, like "alert-error" or "alert-success".
 * See http://twitter.github.com/bootstrap/components.html#alerts
 * @param innerHTML The HTML for inside the alert div
 * @param autoHideMs Optional; automatically hide the alert again after a hiven number of milliseconds.
 */
synthbio.gui.showAdModalAlert = function(modal, alertClass, innerHTML, autoHideMs) {
	var fader = $("#"+modal+" .modal-alert-fader");
	// Place content in the footer
	$(".modal-footer", fader).html('<div class="alert ' + alertClass +
		'" style="margin-bottom: 0;">' + innerHTML + '</div>');

	// Get the real/actual/computed height of what the fader whould be if we let it set it's own hieght (height: auto;).
	// This is a bit of non-semantic jiggery-pokery because animating from height: 0-auto; is not supported in browsers.
	fader.css("height", $(".modal-footer", fader).outerHeight() + "px");

	// Optionally; auto hide using a timer
	if(autoHideMs !== undefined) {
		window.setTimeout(function() {
			fader.css("height", "0px");
		}, autoHideMs);
	}
};

/**
 * Hide a specific ad-modal alert manually.
 *
 * @param modal the ID of the modal in the HTML, without the hash character
 */
synthbio.gui.hideAdModalAlert = function(modal) {
	$("#"+modal+" .modal-alert-fader").css("height", "0px");
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

/**
 * Generic options used for the DataTables
 */
synthbio.gui.dataTableOptions = {
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

$(document).ready(function() {
	// Activate zhe Dropdowns Herr Doktor!
	$('.dropdown-toggle').dropdown();

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

	// Hook click listener for editing the circuit details
	$("#circuit-details button").on("click", synthbio.gui.editCircuitDetails);

	// Set behaviour for application exit
	$("#exit").on("click", function() {
		//Try window.open followed by window.close
		window.open('', '_self', ''); 
		window.close(); 

		//Alert if browser does not support closing
		if (!self.closed && !window.closed) {
			alert("Your browser does not allow this application to close itself, please close the screen yourself.");
		}
	});

	//Ask the user for confirmation if the application is closed
	//$(window).bind("beforeunload", function(event) {
	//	return "Caution: this will close the application. Unsaved work will be lost!";
	//});

	// Allow resizing of the simulation tabs
	var startDragPosition = {x: undefined, y: undefined};
	$("#simulation-tab .navbar").draggable({
		axis: "y",
		distance: 10,
		start: function(event, ui) {

			console.log(ui);
		},
		drag: function(event) {
			var block = $(this).parent();

			console.log("event: " + event.pageY, ", height: " + block.css("height"));
		},
		stop: function() {
			console.log("end drag");
		}
	});
});
