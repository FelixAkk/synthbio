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
	synthbio.clickWire = function(wire, wireID, currentProtein, signal) {
		
		var prots = "";
		// Only proceed and display the dropdown if it doesn't already contain the dropdown
		if(wire.children().length === 0) {
			// Provide all available proteins + the currently selected one
			$.each(synthbio.proteins, function(i,cds) {
				if(!(synthbio.proteins[i].used) || i === currentProtein) {
					prots += '<li class="proteinItem"><a href="#">' + i + '</a></li>';
				}
			});
			//Draw dropdown list on the wire
			wire.html('<ul class="nav" id="something">' +
					'<li class="dropdown">' +
						'<a href="#" class="dropdown-toggle" data-toggle="dropdown">Choose protein<b class="caret"></b></a>' +
						'<ul class="dropdown-menu" >' + 
							prots + 
						'</ul>' +
					'</li>' +
				'</ul>');
		}
		//Make it dropdownable
		$('.dropdown-toggle').dropdown();
		
		//Bind function to the list items
		$('#something li .proteinItem').on('click', function(e) { 
			e.preventDefault();
			var selectedProtein = ($(this).children()[0]).innerHTML;
			if(!(synthbio.proteins[selectedProtein].used)) {
				synthbio.proteins[selectedProtein].used = true;
				if(currentProtein !== "Choose protein") {
					synthbio.proteins[currentProtein].used = false;
				}
				currentProtein = selectedProtein;
			}
			wire.html('<a id="conn' + wireID + '" href=#>' + selectedProtein + '</a>');

			//update signal in model.
			signal.setProtein(selectedProtein);
		});			
	};	
});