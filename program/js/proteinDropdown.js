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

/*jslint devel: true, browser: true, vars: true, plusplus: true, sloppy: true, white: true, maxerr: 50, indent: 4 */
/*global $, synthbio, jsPlumb */

/**
 * Dropdown menus on wires.
 * 
 * @author Felix Akkermans & Thomas van Helden
 */

$(document).ready(function() {

	//Set proteins list which will be checked for available proteins
	synthbio.proteins = {};
	
	/**
	* Reset proteins so everything is available. Done after loading circuits or restarting page.
	*/
	synthbio.resetProteins = function() {
		synthbio.requests.getCDSs(function(response) {
			$.each(response, function(i, cds) {
				synthbio.proteins[cds.name] = { "used": false}; 
			});
		});
	};
	
	//Beginning of the page, so reset all proteins
	synthbio.resetProteins();
		
	/**
	* Defines what should happen when a wire is clicked
	* Required the DOM element of the wire, the connectionCount of that wire and the currentProtein selected on that wire
	*/
	synthbio.clickWire = function(wire, wireID, currentProtein) {
		
		var prots = "";
		// Only proceed and display the dropdown if it doesn't already contain the dropdown
		if(wire.children().length === 0) {
			// Provide all available proteins + the currently selected one
			$.each(synthbio.proteins, function(i,cds) {
				if(!(synthbio.proteins[i].used) || i === currentProtein) {
					prots += '<option val="' + i + '">' + i + '</option>';
				}
			});
			//Draw dropdown list on the wire
			wire.html('<select id="protein-select-' + wireID +'">' +
			'<option value="none">Choose protein</option>' +
			prots + 
			'</select>');
		}
	};
	
	/**
	* Defines what should happen when a wires' select changes value
	* Required the DOM element of the wire, the connectionCount of that wire, the currentProtein selected on that wire and the connection signal
	*/
	synthbio.changeWire = function(wire, wireID, currentProtein, signal) {
	
		//Get the selected value
		var selectedProtein = $('#protein-select-' + wireID).val();
		//Update which proteins are used
		if(!(synthbio.proteins[selectedProtein].used)) {
			synthbio.proteins[selectedProtein].used = true;
			if(currentProtein !== "Choose protein") {
				synthbio.proteins[currentProtein].used = false;
			}
			currentProtein = selectedProtein;
		}
		//Set new protein value in GUI
		wire.html(selectedProtein);

		//update signal in model.
		signal.setProtein(selectedProtein);
	};
});