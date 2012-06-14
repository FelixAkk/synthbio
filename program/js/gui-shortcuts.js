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
 * GUI JavaScript Document, concerns with the definition of the capturing and handling of
 * shortcut keys and mnemonics of menu items and workspace actions.
 */

/*jslint devel: true, browser: true, forin: true, vars: true, plusplus: true, sloppy: true, white: true, maxerr: 50, indent: 4 */
/*global $, synthbio */

/**
 * syntbio package.
 */
var synthbio = synthbio || {};
synthbio.gui = synthbio.gui || {};

/**
 * Handler keypresses
 */
synthbio.gui.handleKeystroke = function(event) {
	if(event.ctrlKey === true) {
		switch(event.which) {
			case 69: // E key
				synthbio.gui.exportHandler();
				event.preventDefault();
			break;
			case 83: // S key
				if(event.shiftKey === true) {
					// the Ctrl+Shift+S case
					synthbio.gui.fileSaveDialog();
				} else {
					// the Ctrl+S case
					synthbio.gui.saveHandler();
				}
				event.preventDefault();
				break;
			// S key
			case 79:
				// the Ctrl+O case
				synthbio.gui.fileOpenDialog();
				event.preventDefault();
				break;
			default:
				break;
		}
	} else {
		switch(event.which) {
			case 118: // F7 key
				$('#define-inputs').modal('show');
				break
			case 119: // F8
				synthbio.gui.validateHandler();
				break;
			case 120: // F9
				synthbio.gui.simulateHandler();
				break;
			case 121: // F10
				synthbio.gui.showSimulationTabs();
				break;
		}
	}
};

$(document).ready(function() {
	$(window).keydown(synthbio.gui.handleKeystroke);
});
