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
 * These tests are designed for gui-simulate.js
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
	module("GUI Simulate tests");
		test("Round series", function(){
			ok(false, "needs testing");
		});
	
	
});