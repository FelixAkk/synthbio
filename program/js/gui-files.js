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

/**
 * Check if we have a file with 'filename' in the list of recent files.
 */
synthbio.gui.hasRecentFile = function(filename) {
	return $.inArray(filename, synthbio.gui.getRecentFilesList()) !== -1
		|| $.inArray(filename + ".syn", synthbio.gui.getRecentFilesList()) !== -1;
};

/**
 * return a list of recent file names.
 */
synthbio.gui.getRecentFilesList = function() {
	var ret=[];
	$.each(synthbio.gui.recentFilesList, function(index, element){
		ret[index]=element.filename;
	});
	return ret;
};

/**
 * Handles the "save" action, which is issues through for example clicking save in the GUI, or pressing Ctrl+S. This
 * tries to save the file straight away if it already has a filename, else it prompts the user with the Save As file
 * dialog to specify the file further.
 */
synthbio.gui.saveHandler = function() {
	if(synthbio.model.getName() !== "") {
		synthbio.requests.putFile(synthbio.model.getName(), "", synthbio.model, function(response) {
			if(response.success === false) {
				console.error(response.message);
				return false;
			}
		});
	} else {
		synthbio.gui.fileSaveDialog();
	}
};

synthbio.gui.fileSaveDialog = function() {
	$("#files .modal-header h3").html("Save As…");
	$("#files .modal-footer .btn-primary").html("Save As…");
	$("#input-filename").attr("placeholder", "Filename...");
	$("#input-filename").val(synthbio.model.getName());
	synthbio.gui.prepareFileDialog();

	// (Re)set to false. Represents whether we have prompted the user for confirmation once before
	var confirmation = false;
	// Used for comparing to the recently submitted input, to detect selection of a different file to overwrite
	var previousInput = "";
	$("#files form").on("submit", function(event) {
		// Surpress default redirection due to <form action="destination.html"> action
		event.stopPropagation();
		event.preventDefault();
		// get the filename
		var input = $("#input-filename").val().trim();
		
		//Check if circuits should be saved as a compound or not
		var folderName = "";
		if($("#compoundToggle").is(":checked")) {
			folderName = "compound/";
		}

		// Allow prompting for confirmation again if a different filename has been entered this time.
		if(input !== previousInput) {
			confirmation = false;
			previousInput = input;
		}
		// Check if the user is about to overwrite an existing file and hasn't confirmed yet
		if(synthbio.gui.hasRecentFile(input) && !confirmation) {
			// But exclude the case of overwriting the file that the current circuit is named to
			if(input === synthbio.model.getName()) {
				return false;
			}
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
		synthbio.requests.putFile(input, folderName, synthbio.model, function(response) {
			if(response.success === false) {
				synthbio.gui.showAdModalAlert('files', 'alert-error',
					'<strong>File was not saved.</strong> ' + response.message + '</div>');
				synthbio.gui.showAdModalAlert('files', 'alert-error',
					"<strong>Server error:</strong> ' + response.error + '.", 10000);
				console.error(response.message);
				return false;
			}
			// We're done; hide
			$("#files").modal("hide");
		});
		return false; // would prevent the form from making us go anywhere if .preventDefault() fails
	});
};

/**
 * Opens a file from the server.
 */
synthbio.gui.fileOpenDialog = function() {
	$("#files .modal-header h3").html("Open…");
	$("#files .modal-footer .btn-primary").html("Open…");
	$("#input-filename").attr("placeholder", "Search...");
	synthbio.gui.prepareFileDialog();

	$("#files form").on("submit", function(event) {
		// Surpress default redirection due to <form action="destination.html"> action
		event.stopPropagation();
		event.preventDefault();
		// Get the filename
		var input = $("#input-filename").val().trim();

		// Check if filename is empty
		if(input === "") {
			// Show alert
			synthbio.gui.showAdModalAlert('files', 'alert-error',
				'<strong>Incorrect filename:</strong> Filename may not be empty or consist of spaces/tabs.');
			return false;
		}

		// Load the file, let's see if it works
		synthbio.requests.getFile(input, "", function(response) {
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
	synthbio.requests.listFiles("", function(response) {
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
			source: synthbio.gui.getRecentFilesList()
		});
		var html='';
		$.each(synthbio.gui.recentFilesList, function(i, file) {
			var date = new Date(file.modified);
			var datetime =
				date.getFullYear() + '-' + date.getMonth() + '-' + date.getDate() + ' ' +
				date.getHours() + ':' + date.getMinutes();
			html+='<tr><td class="filename">'+file.filename+'</td><td>x</td><td>'+datetime+'</td></tr>';
		});

		// Initialize DataTable, before adding rows to it.
		if(synthbio.gui.fTable !== undefined) {
			// If we run this for the second time and fTable is defined, clear the old table
			synthbio.gui.fTable.fnClearTable();
		}
		$('#files tbody').html(html);

		synthbio.gui.fTable = $('#files table').dataTable(synthbio.gui.dataTableOptions);

		// Make each row respond to selection
		// (Jieter) @FelixAkk: could be done more concice with one onclick handler
		// using the event object passed to the callback to determine the target. 
		$("#files tbody tr").each(function(index, element) {
			element = $(element); // extend to provide the .on() function
			element.on("click", function() {
				$("#input-filename").val($(this).find("td.filename").html());
				// Trigger submit
				$("#files form").submit();
			});
		});
		// When all is done we can finally show the modal
		$("#files").modal("show");
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
	 * Save file if it already has a filename, else prompt the user with the file dialog
	 */
	$("#save").on("click", synthbio.gui.saveHandler);
	/**
	 * Setup/rig file operation dialog when the `Save As` menu item is clicked.
	 */
	$("#save-as").on("click", synthbio.gui.fileSaveDialog);
	/**
	 * Setup/rig file operation dialog when the `Open` menu item is clicked.
	 */
	$("#open").on("click", synthbio.gui.fileOpenDialog);

	// Cleanup time; prepare the file dialog for another time
	$('#files').on('hidden', synthbio.gui.resetFileDialog);

	// List files from server.
	$('#files').on('show', synthbio.gui.prepareFileDialog);

});
