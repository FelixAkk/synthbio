/**
 * Project Zelula
 *
 * Contextproject TI2800
 * TU Delft - University of Technology
 *
 * Authors:
 * 	Felix Akkermans, Niels Doekemeijer, Thomas van Helden
 * 	Albert ten Napel, Jan Pieter Waagmeester
 *
 * https://github.com/FelixAkk/synthbio
 *
 * @author	Thomas van Helden & Jan Pieter Waagmeester & Felix Akkermans
 *
 * This document contains tests for the JavaScript clientside
 * The JSON coming from the Java server will be translated to circuits and UnitTested here
 */
 
/**
 * Testable objects
 */
var point = new synthbio.Point(10, 20);
var gate = new synthbio.Gate("and", point);
var notgate= new synthbio.Gate("not", [10, 22]);
var signal = new synthbio.Signal("A", 1, 2);
var cds1 = new synthbio.CDS("cds1", "k2", "d1", "d2");
var cds2 = new synthbio.CDS("cds2", "k2", "d1", "d2");


var gateJSON='{"type":"and","position":{"x":10,"y":20}}';
var signalJSON='{"protein":"A","from":1,"to":2}';

var circuitName="testCircuit";
var circuitDescription="testDescription";
var circuit;

/**
 * Function called before each test for setup
 */
QUnit.testStart = function(testBatchName) {
	// ensure a clean circuit slate every test
	circuit = new synthbio.Circuit(circuitName, circuitDescription, [gate, gate], [signal, signal], []);
}

var circuitJSON=
	'{"name":"'+circuitName+'",'+
	'"description":"'+circuitDescription+'",'+
	'"gates":['+gateJSON+','+gateJSON+'],'+
	'"signals":['+signalJSON+','+signalJSON+'],'+
	'"groups":[]}';
	
/**
 * The actual tests
 */
$(document).ready(function() {
	module("Points");
		test('Construct a Point', function() {
			equal(10, point.getX(), 'check X coordinate');
			equal(20, point.getY(), 'check Y coordinate');
		});
		test('Distance', function() {
			equal(0, point.distanceTo(point), 'Distance to self is 0');

			var xplusone=new synthbio.Point(point.getX()+1, point.getY());
			equal(1, point.distanceTo(xplusone), 'Distance to self.x+1, self.y is 1');

			var yplusone=new synthbio.Point(point.getX(), point.getY()+1);
			equal(1, point.distanceTo(xplusone), 'Distance to self.x, self.y+1 is 1');
		});

	module("Gates");
		/**
		 * Gate construction
		 */
		test("Gates should only be able to be constructed with certain parameters provided with the constructor call.",
			function() {
			raises(function() {new synthbio.Gate("and", 5)}, "Invalid position parameter is given.");
		})
		
		/**
		 * Gate properties
		 */
		test("Gates should have the right properties", function() {
			equal(gate.type, "and", "Gates store types");
			equal(gate.getX(), 10, "Gates store X coordinates");
			equal(gate.getY(), 20, "Gates store Y coordinates");
		});

		/**
		 * Gate properties
		 */
		test("Gates should have the right properties", function() {
			equal(notgate.type, "not", "Gates store types");
			equal(notgate.getX(), 10, "Gates store X coordinates");
			equal(notgate.getY(), 22, "Gates store Y coordinates");
		});
		
		/**
		 * Gate toString
		 */
		test("Gates should have a working toString method", function() {
			equal(gate.toString(), gate.type + ": X = " + gate.getX() + ", Y = "+ gate.getY() ,"The toString method works");
		});
		
		/**
		 * From Gate to JSON
		 */
		test("Gates should be able to be constructed from JSON", function() {
			equal(JSON.stringify(gate), gateJSON, "Gates can be converted to JSON");
		});
		
		/**
		 * From JSON to Gate
		 */
		test("Gates should be able to be constructed from JSON", function() {
			deepEqual(synthbio.Gate.fromJSON(gateJSON), gate, "JSON can be converted to Gates");
		});
		
	module("Signals");
		
		/**
		 * Signal properties
		 */
		test("Signals should store the right properties", function() {
			equal(signal.protein, "A", "type of protein");
			equal(signal.from, "1", "Origin of signal");
			equal(signal.to, "2", "Destination of signal");
		});
		
		/**
		 * Signal toString
		 */
		test("Signals should have a toString method", function() {
			equal(signal.toString(), signal.protein + " links " + signal.from + " with " + signal.to);
		});
		
		/**
		 * From Signal to JSON
		 */
		test("Signal should be able to be constructed from JSON", function() {
			equal(JSON.stringify(signal), signalJSON, "Signals can be translated to JSON");
		});
		
		/**
		 * From JSON to Signal
		 */
		test("Signal should be able to be constructed from JSON", function() {
			deepEqual(signal, synthbio.Signal.fromJSON(signalJSON), "JSON can be converted to Signals");
		});
		

		
	module("Circuits");
		/**
		 * Properties of Circuits
		 */
		test("Circuits should have the correct properties", function() {
			equal(circuit.name, circuitName, "Circuit has a name");
			equal(circuit.description, circuitDescription, "Circuit has a description");
			deepEqual(circuit.gates, [gate, gate], "Circuit has a list of gates");
			deepEqual(circuit.signals, [signal, signal], "Circuit has a list of signals");
			deepEqual(circuit.groups, [], "Circuit has groupings of gates");
		});

		/**
		 * Adding of gates
		 */
		test("Circuits should add and store gates through a method call", function() {
			circuit.add(notgate); // add gate on index [2].
			deepEqual(circuit.getGates(), [gate,gate,notgate])
		})
	
		test("Circuits should add and store gates through a method call", function() {
			circuit.add(notgate); // add gate on index [2].
			deepEqual(circuit.getGate(2), notgate)
			raises(function() {circuit.getGate(3)}, "Empty index is given.");
		})

		/**
		 * Circuit toString
		 */
		test("Circuit toString method is displayed", function() {
			ok(true, circuit.toString());
		});
		
		/**
		 * Circuit to JSON
		 */
		test("Circuits should be able to be converted to JSON", function() {
			equal(
				JSON.stringify(circuit),
				circuitJSON,
				"Circuits can be converted to JSON"
			);
		});
		
		/**
		 * JSON to Circuit
		 */
		test("Circuits should be able to be generated from JSON", function() {
			deepEqual(
				synthbio.Circuit.fromJSON(circuitJSON),
				circuit,
				"JSON can be converted to Circuits"
			);
		});
		
		/**
		 * Circuit evaluation
		 */
		//~ test("Circuits should be evaluated propperly", function() {
			//~ ok(circuit.eval(), "circuit evaluation not tested yet");
		//~ });

		
	/**
	 * CDS
	 */
	module("CDS");

		/**
		 * CDS list
		 */
		test("All CDSs should be listed", function() {
			equal( cds1.name , "cds1", "cds1" );
			equal( cds2.name , "cds2", "cds2" );
		});

		/**
		 * CDS properties
		 */
		test("First part of CDS has values", function() {
			equal( cds1.name , "cds1", "cds1" );
			equal( cds1.k2 , "k2", "K2" );
			equal( cds1.d1 , "d1", "D1" );
			equal( cds1.d2 , "d2", "D2" );
		});
		
		/**
		 * CDS toString
		 */
		test("toString method test of CDS", function() {
			deepEqual(cds1.toString(), "CDS - name = " + cds1.name + ", k2 = "+ cds1.k2+ ", d1 = " + cds1.d1+ ", d2 = " + cds1.d2, "The list of properties is returned");
		});
		
		/**
		 * From CDS to JSON
		 */
		test("CDS can be converted to JSON", function() {
			equal(JSON.stringify(cds1), "{\"name\":\"cds1\",\"k2\":\"k2\",\"d1\":\"d1\",\"d2\":\"d2\"}", "converting CDS to JSON");
		});
		
		/**
		 * From JSON to CDS
		 */
		test("Needs to be able to parse JSON and convert to CDS", function() {
			deepEqual(synthbio.CDS.fromJSON("{\"name\":\"cds1\",\"k2\":\"k2\",\"d1\":\"d1\",\"d2\":\"d2\"}"), cds1, "parsing JSON object of CDS");
		});
	
	module('HTTP Requests');
		/**
		 * Get CDS from server
		 */
		//~ test("testing getCDS function", function() {
			//~ ok(false, "needs testing");
		//~ });
		//~
				/**
		 * Get Signal from server
		 */
		//~ test("Request Signal from server and converting it to Signal object", function() {
			//~ ok(false, "needs testing");
		//~ });
				/**
		 * Get Gate from server
		 */
		//~ test("Request Gate from server and converting it to Gate object", function() {
			//~ ok(false, "needs testing");
		//~ });
		//~
});