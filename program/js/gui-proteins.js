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
 * Returns the available proteins (loads them from the server at the start of the program)
 * @return Object {protein1: true (used), protein2: false (not used), ..}
 */
synthbio.getProteins = (function() {
	var proteins = {};
	synthbio.requests.getCDSs(function(response) {
		synthbio.util.assert(response.length, "Loading proteins failed");

		$.each(response, function(i, cds) {
			proteins[cds.name] = false; 
		});

		synthbio.gui.fillProteins(response);
	});

	return function() {
		return $.extend({}, proteins, synthbio.model.getUsedProteins());
	};
}());

/**
 * Returns the available unused proteins
 * @return Array [protein1, protein2, ..]
 */
synthbio.getUnusedProteins = function() {
	var res = [];
	$.each(synthbio.getProteins(), function(protein, used){
		if (!used) {
			res.push(protein);
		}
	});
	return res;
};

/**
 * Checks if protein is valid
 * @protein String Name of protein
 * @return boolean
 */
synthbio.validProtein = function(protein) {
	var proteins = synthbio.getProteins();
	return protein && proteins && (proteins[protein] !== undefined);
};

/**
 * Fill the "show proteins" modal with proteins
 * @param response Reponse object for the synthbio.requests.getCDSs
 */
synthbio.gui.fillProteins = function(response) {
	if(response instanceof String) {
		$('#list-proteins tbody td').html(response);
		return;
	}
	// construct table body contents
	var html = '';
	$.each(response, function(i, cds) {
		html += '<tr><td>'+cds.name+'</td><td>'+cds.k2+'</td><td>'+cds.d1+'</td><td>'+cds.d2+'</td></tr>';
	});

	$('#list-proteins tbody').html(html);

	var lpTable = $('#list-proteins table').dataTable(synthbio.gui.dataTableOptions);
	
	// Hook up custom search/filter input box for this table. Only the `keyup` event seems give a good result
	$("#list-proteins .modal-footer input").bind("keyup", function(event) {
		lpTable.fnFilter($(this).val());
	});
};

/**
 * Closes all dropdown menus which are still open.
 * Should be done when selecting a new wire or clicking outside of a dropdown
 */
synthbio.gui.closeProteinDropdowns = function(mtarget){
	var change = false;

	var sel = $('.protein-selector').parent().not(function(){
		//Filter out points not in mouse
		var el = $(this);
		var has = el.has(mtarget);
		return (mtarget === undefined) || el.is(mtarget) || has.length;
	});

	if (sel && sel.length) {
		sel.parent().css('z-index', "1");
		sel.html("Choose protein");
		change = true;
	};
	
	if(change) {
		synthbio.gui.updateConnections();
		jsPlumb.repaintEverything();
	}
};

/**
 * Defines what should happen when a wire is clicked
 * Required the DOM element of the wire, the id of that wire and the currentProtein selected on that wire
 */
synthbio.gui.openProteinDropdown = function(wire, wireID, currentProtein) {
	
	synthbio.gui.closeProteinDropdowns();

	// Provide all available proteins + the currently selected one
	var prots = "";
	$.each(synthbio.getUnusedProteins(), function(i, protein) {
		prots += '<option ' +
			'val="' + protein + '"' + 
			((currentProtein === protein) ? ' selected ' : '') + '>' + 
			protein + 
			'</option>';
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
 * Required the DOM element of the wire, the connectionCount of that wire and the connection signal
 */
synthbio.gui.selectProtein = function(wire, wireID, signal) {

	//Get the selected value
	var selectedProtein = $('#protein-select-' + wireID).val();
	if (!synthbio.validProtein(selectedProtein)) {
		selectedProtein = "";
	}

	//Update signal in model.
	signal.setProtein(selectedProtein);
	synthbio.gui.updateConnections();

	//Reset the z-Index of wire
	wire.parent().css('z-index', "1");
};

synthbio.gui.setProteinLabel = function(signal, connection) {
	var overlay = connection.getOverlay("label");
	var wire = $('#lbl_' + connection.id, 0);

	if (!wire || !wire.length) {
		overlay.setLabel('<a id="lbl_' + connection.id + '" href=#></a>');
		wire = $('#lbl_' + connection.id, 0);

		wire.on("click", function(event) {
			var oldProtein = signal.getProtein();
			signal.setProtein("");

			synthbio.gui.openProteinDropdown(wire, connection.id, oldProtein);
			overlay.setLocation(overlay.getLocation()); //Workaround for proper location
		});
		
		wire.on("change", function(event) {
			synthbio.gui.selectProtein(wire, connection.id, signal);
			overlay.setLocation(overlay.getLocation()); //Workaround for proper location
		});
	}

	wire.html(signal.getProtein() || "Choose protein");
};

synthbio.gui.updateConnections = function() {
	$.each(synthbio.gui.displaySignalIdMap, function(conn, obj) {
		synthbio.gui.setProteinLabel(obj.signal, obj.connection);
	});
};
 
$(document).mousedown(function(event) {
	synthbio.gui.closeProteinDropdowns(event.target);
});