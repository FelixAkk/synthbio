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
 * @author Felix Akkermans & Jan-Pieter Waagmeester
 *
 * Utility functions for global useage.
 */

/*jslint devel: true, browser: true, vars: true, plusplus: true, sloppy: true, white: true, maxerr: 50, indent: 4 */
/*global $, synthbio */
/**
 * Add roundTo method to numbers, rounds with precision
 */
Number.prototype.roundTo = function(to) {
	var p = Math.pow(10,to);
	return Math.round(this*p)/p;
};

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
 * Round data to synthbio.gui.plotPrecision
 */
synthbio.util.roundSeries = function(series) {
	return $.map(series, function(val) { 
		return val.roundTo(synthbio.gui.plotPrecision);
	});
};

/**
 * Calculate the sum series of the input
 * @param series Array of objects with data property
 */
synthbio.util.calculateSumSeries = function(series) {
	var res = [];
	$.each(series, function(i, serie) {
		$.each(serie.data, function(idx, val) {
			res[idx] = res[idx] + val || val;
		});
	});
	return res;
};

/**
 * Appends a trailing path delimiter to the folder name (if not empty)
 */
synthbio.util.appendTrailingDelimiter = function(folder) {
	if (!folder) {
		return folder;
	}

	var last = folder[folder.length - 1];
	if (last !== '\\' && last !== '/') {
		folder += '/';
	}

	return folder;
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

/**
 * Format a date to a standard datetime format (without seconds).
 *
 * example: 2012-06-11 16:36
 *
 * depends on jqueryUI
 */
synthbio.util.formatDatetime = function(date) {
	if(!(date instanceof Date)) {
		date = new Date(date);
	}
	var datetime = $.datepicker.formatDate("yy-mm-dd", date) + " ";
	if(date.getHours() <= 9) {
		datetime += "0";
	}
	datetime += date.getHours();
	datetime += ":";
	if(date.getMinutes() <= 9) {
		datetime += "0";
	}
	datetime += date.getMinutes();
	return datetime;
};
 