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
 * Functionality for dropdown menus on wires.
 *
 * @author Felix Akkermans & Thomas van Helden
 */

/*jslint devel: true, browser: true, vars: true, plusplus: true, sloppy: true, white: true, maxerr: 50, indent: 4 */
/*global $, synthbio, jsPlumb */

// TODO: wires work when proteins not loaded
// TODO: reset protein after removal of wire


/**
 * in the syntbio package namespace.
 */
var synthbio = synthbio || {};

/**
 * is part of the gui package
 */
synthbio.gui = synthbio.gui || {};
/**
 * Map of proteins (a char) to boolean values signifying whether they have been assigned yet or not
 * (in use/avaiable for select).
 */
synthbio.proteins = {};

/**
 * String to signify that a signal/wire has no protein assigned. Check for equality to this to evaluate if the signal is
 * clear/empty/had no protein assigned.
 */
synthbio.gui.emptyString = "none";

/**
 * Reset proteins so everything is available. Done after loading circuits or restarting page.
 * Proteins have a value of true or false, meaning if they are used or not.
 */
synthbio.resetProteins = function() {
		synthbio.proteins = {};
		// Fetch proteins from server
		synthbio.requests.getCDSs(function(response) {
		$.each(response, function(i, cds) {
			synthbio.proteins[cds.name] = false; 
		});
	});
};

/**
 * Closes all dropdown menus which are still open.
 * Should be done when selecting a new wire or clicking outside of a dropdown
 */
synthbio.gui.closeProteinDropdowns = function() {
	$.each($('.protein-selector'), function(i, menu){
		$('.protein-selector').parent().parent().css('z-index', "1");
		// Change this wires currentProtein to Choose protein and set the old protein value to false in synthbio.proteins
		$('.protein-selector').parent().html("Choose protein");
	});
	jsPlumb.repaintEverything();
};

/**
 * Defines what should happen when a overlay that shows which protein is assigned is clicked. This will show a dropdown
 * for selecting a protein, with all available proteins
 *
 * @param event The original DOM event
 * @param jsPlumbConnectionOverlay The jsPlumb connection overlay object from it's model.
 */
synthbio.gui.clickWire = function(event, jsPlumbConnectionOverlay) {
	// I'll close all other protein selection dropdowns so you don't have to bother.
	synthbio.gui.closeProteinDropdowns();
	var overlayContainer = $(event.srcElement);
	var signalID = overlayContainer.attr("id");
	var prots = "";
	// Provide all available proteins + the currently selected one
	$.each(synthbio.proteins, function(i,cds) {
		if(!(synthbio.proteins[i]) || i === overlayContainer.html()) {
			prots += '<option val="' + i + '">' + i + '</option>';
		}
	});
	// Draw dropdown list on the wire. Opened by default by setting the size attribute.
	overlayContainer.html(
		'<select class="protein-selector" id="protein-select-' + signalID +'" size="' + ($(prots).size()+1) + '">' +
		'<option value="' + synthbio.gui.emptyString + '">' + synthbio.gui.emptyString +'</option>' + prots + '</select>'
	);
	
	// Bring overlay to the front so options can't get occluded by other GUI elements
	overlayContainer.parent().css('z-index', 99)

	// Reinitialize menu location in the model so it gets centered vertically properly
	jsPlumbConnectionOverlay.setLocation(jsPlumbConnectionOverlay.getLocation());
};

/**
 * Defines what should happen when a wires' select changes value
 * @param event The original DOM event
 * @param signal The signal object from the synthbio.model
 * @param jsPlumbConnectionOverlay The jsPlumb connection overlay object from it's model.
 */
synthbio.gui.changeWire = function(event, signal, jsPlumbConnectionOverlay) {
	var previousProtein = signal.getProtein();
	var overlayContainer = $(event.srcElement).parent();
	var signalID = overlayContainer.attr("id");
	// Get the selected value
	var selectedProtein = $('#protein-select-' + signalID).val().trim();
	console.log("old: " + previousProtein + ", new: " + selectedProtein);
	// If the new one is not the clear/empty value (i.e. it is existent), set it to used/unavailable.
	if(selectedProtein !== synthbio.gui.emptyString) {
		// Set the new one to true
		synthbio.proteins[selectedProtein] = true;
	}
	// If the old one was not the clear/empty value, it exists in the map, and mark it as available agian.
	if(previousProtein !== synthbio.gui.emptyString) {
		synthbio.proteins[previousProtein] = false;
	}
	// Set new protein value in GUI; if it's none, show the choose protein string instead, else just leave it be.
	overlayContainer.html(selectedProtein === synthbio.gui.emptyString ? "Choose protein" : selectedProtein);

	// Reset the z-Index of wire to push it down
	overlayContainer.parent().css('z-index', "1");
	
	// Update signal in the model.
	signal.setProtein(selectedProtein);

	// Reinitialize menu location in the model so it gets centered vertically properly
	jsPlumbConnectionOverlay.setLocation(jsPlumbConnectionOverlay.getLocation());
};
 
$(document).ready(function() {

	// Beginning of the page, so reset all proteins
	synthbio.resetProteins();	

	// Make sure dropdowns close when user clicks outside of menu
	$('#grid-container').on("click", function(event){
		// But don't when the user interacts with the/a dropdown itself. In case it's another dropdown, that one will
		// take care of closing itself. See synthbio.gui.closeProteinDropdowns().
		if(!$(event.srcElement).parent().hasClass("popover-inner")) {
			synthbio.gui.closeProteinDropdowns();
		}
	});
});