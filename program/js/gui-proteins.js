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
 * @author Felix Akkermans & Thomas van Helden & Niels Doekemeijer
 *
 * GUI JavaScript Document, concerns all protein GUI matters (showing & selection).
 */

/*jslint devel: true, browser: true, vars: true, plusplus: true, sloppy: true, white: true, maxerr: 50, indent: 4 */
/*global $, synthbio, jsPlumb */

/**
 * syntbio package.
 */
var synthbio = synthbio || {};
synthbio.gui = synthbio.gui || {};

/**
 * String to signify that a signal/wire has no protein assigned to it. Check for equality against this to see if the
 * signal is clear/empty/had no protein assigned.
 */
synthbio.gui.noProtein = "None";

/**
 * Returns the available proteins (loads them from the server at the start of the program)
 * @return Object {protein1: true (used), protein2: false (not used), ..}
 */
synthbio.getProteins = (function() {
	var proteins = {};
	synthbio.requests.getCDSs(function(response) {
		synthbio.util.assert(response.length, "Loading proteins failed");

		//Walk through response and fill the result object
		$.each(response, function(i, cds) {
			proteins[cds.name] = false; 
		});

		//Fill the proteins modal
		synthbio.gui.fillProteins(response);
	});

	return function() {
		//Get used proteins from model and merge with object of all available proteins
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

	// Initialize the DataTable (for sorting/searching)
	var lpTable = $('#list-proteins table').dataTable(synthbio.gui.dataTableOptions);
	
	// Hook up custom search/filter input box for this table. Only the `keyup` event seems give a good result
	$("#list-proteins .modal-footer input").bind("keyup", function(event) {
		lpTable.fnFilter($(this).val());
	});
};

/**
 * Closes all dropdown menus which are still open.
 * Should be done when selecting a new wire or clicking outside of a dropdown
 * @param mtarget Element on which the mouse has clicked (optional)
 */
synthbio.gui.closeProteinDropdowns = function(mtarget){
	var change = false;

	var sel = $('.protein-selector').parent().not(function(){
		//Filter out points not in mouse
		var el = $(this);
		var has = el.has(mtarget);
		return (mtarget === undefined) || el.is(mtarget) || has.length;
	});

	//Check if the selector had a result (if so, there are still dropdowns open)
	if (sel && sel.length) {
		sel.parent().css('z-index', "1");
		change = true;
	}
	
	//If dropdowns were closed, update the labels and do a repaint
	if(change) {
		synthbio.gui.updateConnections();
		jsPlumb.repaintEverything();
	}
};

/**
 * Defines what should happen when a overlay that shows which protein is assigned is clicked. This will show a dropdown
 * for selecting a protein, with all available proteins
 *
 * @param label The DOM element in the overlay, containing the selected protein string or selection dropdown.
 * @param connection The jsPlumb connection overlay object from it's model.
 * @param oldProtein The previously assigned protein string.
 */
synthbio.gui.openProteinDropdown = function(label, connection, oldProtein) {
	// I'll close all other protein selection dropdowns so you don't have to bother.
	synthbio.gui.closeProteinDropdowns();

	// Provide all available proteins + the currently selected one
	var prots = "";
	$.each(synthbio.getUnusedProteins(), function(i, protein) {
		prots += '<option ' +
			'val="' + protein + '"' + 
			((oldProtein === protein) ? ' selected ' : '') + '>' +
			protein + 
			'</option>';
	});

	// Draw dropdown list on the wire. Opened by default by setting the size attribute.
	label.html(
		'<select class="protein-selector" id="protein-select-' + connection.id +'" size="' + ($(prots).size()+1) + '">' +
		'<option value="' + synthbio.gui.noProtein + '">' + synthbio.gui.noProtein +'</option>' + prots + '</select>'
	);

	// Bring overlay to the front so options can't get occluded by other GUI elements
	label.parent().css('z-index', 99);

	// Reinitialize menu location in the model so it gets centered vertically properly
	connection.getOverlay("label").setLocation(connection.getOverlay("label").getLocation());
};

/**
 * Defines what should happen when a wires' select changes value
 *
 * @param label The DOM element in the overlay, containing the selected protein string or selection dropdown.
 * @param connection The jsPlumb connection overlay object from it's model.
 * @param signal The signal object from the synthbio.model
 */
synthbio.gui.selectProtein = function(label, connection, signal) {
	var previousProtein = signal.getProtein();
	//Get the selected value
	var selectedProtein = $('#protein-select-' + connection.id).val();
	// If the selected protein is non-existent
	if(!synthbio.validProtein(selectedProtein)) {
		selectedProtein = "";
	}

	//Update signal in model and update all connections (as there might have occured side effects)
	signal.setProtein(selectedProtein);
	synthbio.gui.updateConnections();

	//Reset the z-Index of wire
	label.parent().css('z-index', "1");
};

/**
 * Updates a label for a specific connection according to signal. Creates a label if none is defined.
 *
 * @param signal synthbio.Signal object with information for connection
 * @param connection The jsPlumb connection object that needs its label updated.
 */
synthbio.gui.setProteinLabel = function(signal, connection) {
	var overlay = connection.getOverlay("label");
	var label = $('#lbl_' + connection.id, 0);

	if (!label || !label.length) {
		overlay.setLabel('<a id="lbl_' + connection.id + '" href="javascript: void(0);" class="popover-content"></a>');
		label = $('#lbl_' + connection.id, 0);

		label.on("click", function(event) {
			var oldProtein = signal.getProtein();
			signal.setProtein("");

			synthbio.gui.openProteinDropdown(label, connection, oldProtein);
		});
		
		label.on("change", function(event) {
			synthbio.gui.selectProtein(label, connection, signal);
		});
	}

	label.html(signal.getProtein() || "Choose protein");
	overlay.setLocation(overlay.getLocation()); //Workaround for proper location
};

/**
 * Updates all the connection labels according to the information in synthbio.model
 */
synthbio.gui.updateConnections = function() {
	$.each(synthbio.gui.displaySignalIdMap, function(conn, obj) {
		synthbio.gui.setProteinLabel(obj.signal, obj.connection);
	});
};

$(document).ready(function() {
	//Bind global mousedown function to close protein selection box(es)
	$(document).mousedown(function(event) {
		synthbio.gui.closeProteinDropdowns(event.target);
	});
});