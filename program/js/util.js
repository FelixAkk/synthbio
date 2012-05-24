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
/*global $, synthbio */

/**
 * @author Felix Akkermans & Jan-Pieter Waagmeester
 *
 * Utility functions for global useage.
 */

/**
 * syntbio package.
 */
var synthbio = synthbio || {};

/**
 * utilities package
 */
synthbio.util = synthbio.util || {};

/**
 * AssertException class.
 *
 * @param message Debugging message.
 */
synthbio.util.AssertException = function(message) {
	this.message = message;
};

synthbio.util.AssertException.prototype.toString = function () {
	return 'AssertException: ' + this.message;
};

/**
 * Ensure that an expression evaluates to true during execution.
 * If the expression evaluates to false, this indicates a possible bug
 * in code.
 *
 * @param exp Expression yielding a boolean
 * (or truthy, which is not recommended) value.
 *
 * @param message Debugging message.
 */
synthbio.util.assert = function(exp, message) {
	if (!exp) {
		throw new synthbio.util.AssertException(message);
	}
};

/**
 * Copy the value of form fields to an object.
 * 
 * For example:
 * form2object(
 *		circuit,
 *		[
 *			{ selector: '#length', setter: 'setLength' },
 *			{ ... }
 *		]
 * 
 * @param object The target object.
 * @param mappings an array of selector-setter mappings
 */
synthbio.util.form2object = function(target, mappings) {
	
	var selector;
	$.each(mappings, function (index, mapping) {
		
		if(mapping.selector.jquery) {
			selector = mapping.selector;
		} else {
			selector = $(mapping.selector);
		}

		if(selector.length && selector.length === 1) {
			synthbio.util.assert(selector.val, "No val() method on selected element " + selector);
			synthbio.util.assert(target[mapping.setter], "No such setter on object: " + mapping.setter);
			target[mapping.setter](selector.val());
		}
	});
};
