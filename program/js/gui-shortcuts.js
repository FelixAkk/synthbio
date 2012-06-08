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
			// S key
			case 83:
				// the Ctrl+S case
				synthbio.gui.saveHandler();
				event.preventDefault();
				break;
			// S key
			case 79:
				// the Ctrl+O case
				synthbio.gui.fileOpenDialog();
				event.preventDefault();
				break;
			default:
				console.log(event.which);
				break;
		}
	} else {
		console.log(event.which);
	}
};

$(document).ready(function() {
	$(window).keydown(synthbio.gui.handleKeystroke);
});
