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
/*global $, synthbio, QUnit, module, test, equal, deepEqual, raises, ok */

/**
 * This document contains tests for the JavaScript clientside
 * These tests are designed for util.js
 * 
 * @author Thomas van Helden
 */
 
 /**
 * Testable objects
 */
 var roundableNum1 = 0.636;
 var roundableNum2 = 3.492;

 
 var circuit = new synthbio.Circuit("Name", "Dexc", [], [], []);
 /**
  * Tests
  */
 $(document).ready(function() {
	module("Utility tests");
		/**
		 * Rounding numbers
		 */
		test('Round numbers', function() {
			equal(roundableNum1.roundTo(1), 0.6, 'Rounded up to 1');
			equal(roundableNum1.roundTo(2), 0.64, 'Rounded up to 1');
			equal(roundableNum1.roundTo(3), 0.636, 'Rounded up to 3');
			equal(roundableNum2.roundTo(1), 3.5, 'Rounded down to 1');
			equal(roundableNum2.roundTo(2), 3.49, 'Rounded down to 2');
		});
	
		test('form2Object method', function(){
			deepEqual(synthbio.util.form2object(circuit, [{ selector: '#testableOption', setter: 'length' }]), {}, "Can alter target Object with form data");
		});
 });