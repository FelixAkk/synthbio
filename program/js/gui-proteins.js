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
 */

/**
 * Dropdown menus on wires.
 * 
 * @author Felix Akkermans & Thomas van Helden
 */

/*jslint devel: true, browser: true, vars: true, plusplus: true, sloppy: true, white: true, maxerr: 50, indent: 4 */
/*global $, synthbio, jsPlumb */

//TODO: Click anywhere to make wires dissapear, adjust currentProtein when you do, wires work when proteins not loaded, reset protein after removal of wire

/**
 * syntbio package.
 */
var synthbio = synthbio || {};
synthbio.gui = synthbio.gui || {};

/**
 * Reset proteins so everything is available. Done after loading circuits or restarting page.
 * Proteins have a value of true or false, meaning if they are used or not.
 */
synthbio.resetProteins = function() {
	synthbio.proteins = {};
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
synthbio.closeProteinDropdown = function(){
	$.each($('.protein-selector'), function(i, menu){
		$('.protein-selector').parent().parent().css('z-index', "1");
		//Change this wires currentProtein to Choose protein and set the old protein value to false in synthbio.proteins
		$('.protein-selector').parent().html("Choose protein");
		jsPlumb.repaintEverything();
	});
};

/**
 * Defines what should happen when a wire is clicked
 * Required the DOM element of the wire, the connectionCount of that wire and the currentProtein selected on that wire
 */
synthbio.clickWire = function(wire, wireID) {
	
	synthbio.closeProteinDropdown();
	
	var prots = "";
	// Provide all available proteins + the currently selected one
	$.each(synthbio.proteins, function(i,cds) {
		if(!(synthbio.proteins[i]) || i === wire.html()) {
			prots += '<option val="' + i + '">' + i + '</option>';
		}
	});
	
	//Draw dropdown list on the wire
	wire.html('<select class="protein-selector" id="protein-select-' + wireID +'">' +
	'<option value="none">Choose protein</option>' +
	prots + 
	'</select>');
	
	//Open them by default
	$('#protein-select-' + wireID).attr('size', ($(prots).size()+1));
	$('#protein-select-' + wireID).width("auto");
	
	//Bring selected wire to the front
	wire.parent().css('z-index', 99);
};

/**
 * Defines what should happen when a wires' select changes value
 * Required the DOM element of the wire, the connectionCount of that wire, the currentProtein selected on that wire and the connection signal
 */
synthbio.changeWire = function(wire, wireID, currentProtein, signal) {

	//Get the selected value
	var selectedProtein = $('#protein-select-' + wireID).val();
	//Update which proteins are used
	if(!(synthbio.proteins[selectedProtein])) {
		synthbio.proteins[selectedProtein] = true;
		if(currentProtein !== "Choose protein") {
			synthbio.proteins[currentProtein] = false;
		}
		currentProtein = selectedProtein;
	}
	//Set new protein value in GUI
	wire.html(selectedProtein);

	//Reset the z-Index of wire
	wire.parent().css('z-index', "1");
	
	//Update signal in model.
	signal.setProtein(selectedProtein);
	
	//return new currentProtein to update this in the wire
	return currentProtein;
};
 
$(document).ready(function() {

	//Beginning of the page, so reset all proteins
	synthbio.resetProteins();	

	//Make sure dropdowns close when user clicks outside of menu
	$('body').on("click", function(event){
		if($($(event.srcElement).children()[0]).hasClass("protein-selector")){
			return;
		}
		synthbio.closeProteinDropdown();
	});
});