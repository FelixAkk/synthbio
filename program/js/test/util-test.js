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

var point = new synthbio.Point(10, 20);
var gate = new synthbio.Gate("and", point);
var circuit = new synthbio.Circuit("Name", "Desc", [gate], [], []);
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
	
		test("Round series", function(){
			deepEqual(synthbio.util.roundSeries([1,2]), [0.64, 3.49, 5.94], "Series can be rounded correctly by plot precision");
		});
		
		test("Sum of series", function(){
			equal(synthbio.util.calculateSumSeries([1,2,3]), 6, "[1, 2, 3] becomes 6");
			equal(synthbio.util.calculateSumSeries([1.5,2,3]), 6.5, "[1.5, 2, 3] becomes 6.5");
			equal(synthbio.util.calculateSumSeries([0,1,0]), 1, "[0, 1, 0] becomes 1");
			equal(synthbio.util.calculateSumSeries([0,0,0]), 0, "[0, 0, 0] becomes 0");
			equal(synthbio.util.calculateSumSeries([]), 0, "[] becomes 0");
			equal(synthbio.util.calculateSumSeries([1,5,7,3,-12]), 4, "[1, 5, 7, 3, -12] becomes 4");
		});
		
		test('form2Object method', function(){
			synthbio.util.form2object(circuit.getSimulationInputs(), [{ selector: '#testableOption', setter: 'setLength' }]);
			
			equal(circuit.getSimulationInputs().getLength() , "20", "Can use form data to alter Object methods");
		});
 });