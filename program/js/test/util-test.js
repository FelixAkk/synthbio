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

synthbio.gui = synthbio.gui || {};
synthbio.gui.plotPrecision = synthbio.gui.plotPrecision || 1;

var array1 = {data: [1,5,7,3,0]};
var array2 = {data: [1,1,1,2,1]};
var array3 = {data: [2,4,-2,1,-2]};
var array4 = {data: [0,0,0,0,0,0,0]};
var duoSeries = [array1, array2];
var multiSeries = [array1, array2, array3];
var diffSizeSeries = [array1,array4];

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
	
		/**
		 * Round series
		 */
		test("Round series", function() {
			deepEqual(synthbio.util.roundSeries([0.64, 3.49, 5.94]), [0.6, 3.5, 5.9], "Series can be rounded correctly by plot precision");
		});
		
		/**
		 * Calculating the sums of different series
		 * Returns one array with the index value of arrays added up -> [1,2] + [1,1] = [2,3]
		 */
		test("Sum of series", function() {
			deepEqual(synthbio.util.calculateSumSeries(duoSeries), [2,6,8,5,1], "Can add 2 series");
			deepEqual(synthbio.util.calculateSumSeries(multiSeries), [4,10,6,6,-1], "Can add more than 2 series, including zero and negative values");
			deepEqual(synthbio.util.calculateSumSeries(diffSizeSeries), [1,5,7,3,0,0,0], "Can add series of different sizes");
		});
		
		/**
		 * form2object
		 * Allows you to alter an object by reading selectors and setters from text fields.
		 */
		test('form2Object method', function() {
			synthbio.util.form2object(circuit.getSimulationInputs(), [{ selector: '#testableOption', setter: 'setLength' }]);

			equal(circuit.getSimulationInputs().getLength() , "20", "Can use form data to alter Object methods");
		});
 });