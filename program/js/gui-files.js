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
 * @author Felix Akkermans
 *
 * GUI JavaScript Document, concerns circuit loading/saving.
 */

/*jslint devel: true, browser: true, forin: true, vars: true, plusplus: true, sloppy: true, white: true, maxerr: 50, indent: 4 */
/*global $, synthbio, jsPlumb */

/**
 * syntbio package.
 */
var synthbio = synthbio || {};
synthbio.gui = synthbio.gui || {};

/**
 * This variable holds the array of files coming from the last synthbio.requests.getFiles() call.
 * These are reused later when for example checking if the entered file name is existent when overwriting a file.
 */
synthbio.gui.recentFilesList = [];

/**
 * The variable that will hold the DataTables (library (for fancy sorting, searching, pagebreaking features et al)
 * object for the table in the files dialog.
 */
synthbio.gui.fTable = undefined;

/**
 * Extend the filename with our ".syn" filename extension if it wasn't yet. Also trim spaces of it first.
 * @param filename
 */
synthbio.gui.filenameExtension = function(filename) {
	filename = filename.trim();
	synthbio.util.assert(filename.length > 0, "Provided filename is empty/has no length. When extending a filename, " +
		"one should be provided at least.");
	if (filename.substring(filename.length - 4) !== ".syn") {
		filename = filename + ".syn";
	}
	return filename;
};

synthbio.gui.saveFile = function() {
	$("#files .modal-header h3").html("Save As…");
	$("#files .modal-footer .btn-primary").html("Save As…");
	$("#files .modal-footer input").attr("placeholder", "Filename...");

	// (Re)set to false. Represents whether we have prompted the user for confirmation once before
	var confirmation = false;
	// Used for comparing to the recently submitted input, to detect selection of a different file
	var previousInput = "";
	$("#files form").on("submit", function(event) {
		// Surpress default redirection due to <form action="destination.html"> action
		event.stopPropagation();
		event.preventDefault();
		// get the filename
		var input = $("input", this)[0].value.trim();
		// Allow prompting for confirmation again if a different filename has been entered this time.
		if(input !== previousInput) {
			confirmation = false;
			previousInput = input;
		}
		// Check if the user is about to overwrite an existing file and hasn't confirmed yet
		if(
			(
				$.inArray(input, synthbio.gui.recentFilesList) >= 0 ||
					$.inArray(input+".syn", synthbio.gui.recentFilesList) >= 0
				)
				&& !confirmation) {
			synthbio.gui.showAdModalAlert('files', 'alert-error',
				"<strong>Overwrite file?</strong> Press enter again to confirm");
			confirmation = true;
			return false;
		}
		// Check if filename is empty
		if(input === "") {
			// Show alert
			synthbio.gui.showAdModalAlert('files', 'alert-error',
				"<strong>Incorrect filename:</strong> Filename may not be empty or consist of spaces/tabs.", 3000);
			confirmation = false; // Something else was shown, so allow asking for a confirmation again
			return false;
		}

		// If all is good (we end up here), and we check if the filename was suffixed by ".syn" and do so if needed.
		input = synthbio.gui.filenameExtension(input);

		// Save the file, let's see if it works
		synthbio.requests.putFile(input, synthbio.model, function(response) {
			if(response.success === false) {
				synthbio.gui.showAdModalAlert('files', 'alert-error',
					'<strong>File was not saved.</strong> ' + response.message + '</div>');
				console.error(response.message);
				return false;
			}
			// We're done; hide
			$("#files").modal("hide");
		});
		return false; // would prevent the form from making us go anywhere if .preventDefault() fails
	});
};
synthbio.gui.openFile = function() {
	$("#files .modal-header h3").html("Open…");
	$("#files .modal-footer .btn-primary").html("Open…");
	$("#files .modal-footer input").attr("placeholder", "Search...");

	$("#files form").on("submit", function(event) {
		// Surpress default redirection due to <form action="destination.html"> action
		event.stopPropagation();
		event.preventDefault();
		// Get the filename
		var input = $("input", this)[0].value.trim();

		// Check if filename is empty
		if(input === "") {
			// Show alert
			synthbio.gui.showAdModalAlert('files', 'alert-error',
				'<strong>Incorrect filename:</strong> Filename may not be empty or consist of spaces/tabs.');
			return false;
		}

		// Load the file, let's see if it works
		synthbio.requests.getFile(input, function(response) {
			if(response.success === false) {
				console.error(response.message);
				synthbio.gui.showAdModalAlert('files', 'alert-error',
					'<strong>File was not opened.</strong> ' + response.message);
			}
			synthbio.loadCircuit(synthbio.Circuit.fromMap(response.data));
			// We're done; hide
			$("#files").modal("hide");
		});
		return false; // would prevent the form from making us go anywhere if .preventDefault() fails
	});
};
synthbio.gui.resetFileDialog = function() {
	$("#files tbody").html('<tr><td>Loading ...</td></tr>');
	// Remove all preveiously registered event handlers. Critical! Else they stack and on each rigging action.
	$("#files form").off("submit");
	var inputfield = $("#files .modal-footer input");
	// clear entered text
	inputfield[0].value = '';
	// clear autocomplete
	inputfield.typeahead({
		source: []
	});
	$("#files .modal-alert-fader").height("0px");
};
synthbio.gui.prepareFileDialog = function(event) {
	// Request stuff from server and define what happens next
	synthbio.requests.listFiles(function(response) {
		// problemu technicznego
		if(response instanceof String) {
			$('#list-files tbody td').html(response);
			return;
		}

		// Save the list of files for later
		synthbio.gui.recentFilesList = response;

		// Setup the text input box for entering the filename operate on
		$("#files .modal-footer input").typeahead({
			// The possible auto completions, the same as the files listed
			source: response
		});
		var html='';
		$.each(response, function(i, file) {
			html+='<tr><td>'+file+'</td><td>x</td><td>x</td></tr>';
		});


		$('#files tbody').html(html);

		// Initialize DataTable.
		if(synthbio.gui.fTable !== undefined) {
			// If we run this for the second time and fTable is defined, clear the old table
			synthbio.gui.fTable.fnClearTable();
		}
		synthbio.gui.fTable = $('#files table').dataTable(synthbio.gui.dataTableOptions);

		// Make each row respond to selection
		$("#files tbody tr").each(function(index, element) {
			element = $(element); // extend to provide the .on() function
			element.on("click", function() {
				$("#files .modal-footer input").val(synthbio.gui.recentFilesList[index]);
				// Trigger submit
				$("#files form").submit();
			});
		});
	});
};

$(document).ready(function() {

	//new button
	$('#new').on('click', function(){
		if(confirm("Caution: this will delete unsaved work!")){
			synthbio.newCircuit();
		}
	});
	/**
	 * Setup/rig file operation dialog when the `Save As` menu item is clicked.
	 */
	$("#save-as").on("click", synthbio.gui.saveFile);

	/**
	 * Save file if it already has a filename, else prompt the user with the file dialog
	 */
	$("#save").on("click", function() {
		if(synthbio.model.getName() !== "") {
			synthbio.requests.putFile(synthbio.model.getName(), synthbio.model, function(response) {
				if(response.success === false) {
					console.error(response.message);
					return false;
				}
			});
		} else {
			synthbio.gui.saveFile();
		}
	});
	/**
	 * Setup/rig file operation dialog when the `Open` menu item is clicked.
	 */
	$("#open").on("click", synthbio.gui.openFile);

	// Cleanup time; prepare the file dialog for another time
	$('#files').on('hidden', synthbio.gui.resetFileDialog);

	// List files from server.
	$('#files').on('show', synthbio.gui.prepareFileDialog);

});
