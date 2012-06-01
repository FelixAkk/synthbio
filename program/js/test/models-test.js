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
 * These tests are designed for models.js
 * 
  * @author Thomas van Helden & Jan Pieter Waagmeester & Felix Akkermans
 */
 

/**
 * Testable objects model
 */
var point = new synthbio.Point(10, 20);
var gate = new synthbio.Gate("and", point);
var notgate= new synthbio.Gate("not", [10, 22]);
var signal = new synthbio.Signal("A", 1, 2);
var cds1 = new synthbio.CDS("cds1", "k2", "d1", "d2");
var cds2 = new synthbio.CDS("cds2", "k2", "d1", "d2");


var gateJSON='{"kind":"and","position":{"x":10,"y":20}}';
var signalJSON='{"protein":"A","from":1,"to":2}';

var circuitName="testCircuit";
var circuitDescription="testDescription";
var circuit;

var	simulation;
var simulationJSON = '{"length":40,"tickWidth":1,"lowLevel":0,"highLevel":600,"testValue":20,"values":{}}';
/**
 * Function called before each test for setup
 */
QUnit.testStart = function(testBatchName) {
	// ensure a clean circuit slate every test
	circuit = new synthbio.Circuit(circuitName, circuitDescription, [gate, gate], [signal, signal], []);
	
	simulation = new synthbio.SimulationInputs({"testValue":20});
};

var circuitJSON=
	'{"name":"'+circuitName+'",'+
	'"description":"'+circuitDescription+'",'+
	'"gates":['+gateJSON+','+gateJSON+'],'+
	'"signals":['+signalJSON+','+signalJSON+'],'+
	'"groups":[],'+	'"inputs":{"length":40,\"tickWidth\":1,\"lowLevel\":0,\"highLevel\":600,\"values":{}}'+
	'}';

var exampleJSON='{ '+
'  "name": "example.syn",'+
'  "description": "Logic for this circuit: D = ~(A^B)",'+
'  "gates": [ '+
'    { "kind": "and", "position": {"x": 2,"y": 2}},'+
'    { "kind": "not", "position": {"x": 2,"y": 4}}'+
'  ],'+
'  "signals": ['+
'    { "from": "input", "to": 0, "protein": "A"},'+
'    { "from": "input", "to": 0, "protein": "B"},'+
'    { "from": 0, "to": 1, "protein": "C"},'+
'    { "from": 1, "to": "output", "protein": "D"}'+
'  ],'+
'  "grouping": [],'+
'  "inputs": {'+
'    "length": 42,'+
'    "values": {'+
'      "A": "H",'+
'      "B": "LLLLLLLLLL LLLLLLLLLLH"'+
'    }'+
'  }'+
'}';

	
/**
 * Tests
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
				raises(function() {
					var foo=new synthbio.Gate("and", 5);
				},
				"Invalid position parameter is given.");
			}
		);
		
		/**
		 * Gate properties
		 */
		test("Gates should have the right properties", function() {
			equal(gate.getKind(), "and", "Gates store types");
			equal(gate.getX(), 10, "Gates store X coordinates");
			equal(gate.getY(), 20, "Gates store Y coordinates");
		});

		/**
		 * Gate properties
		 */
		test("Gates should have the right properties", function() {
			equal(notgate.getKind(), "not", "Gates store types");
			equal(notgate.getX(), 10, "Gates store X coordinates");
			equal(notgate.getY(), 22, "Gates store Y coordinates");
		});
		
		/**
		 * Gate toString
		 */
		test("Gates should have a working toString method", function() {
			equal(gate.toString(), gate.getKind() + ": (X = " + gate.getX() + ", Y = " + gate.getY() +")" ,"The toString method works");
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
			equal(signal.getProtein(), "A", "type of protein");
			equal(signal.getFrom(), "1", "Origin of signal");
			equal(signal.getTo(), "2", "Destination of signal");
		});
		
		/**
		 * Signal toString
		 */
		test("Signals should have a toString method", function() {
			equal(signal.toString(), signal.getProtein() + " links " + signal.getFrom() + " with " + signal.getTo());
		});
		
		/**
		 * From Signal to JSON
		 */
		test("Signal should be able to be constructed from JSON", function() {
			equal(JSON.stringify(signal.toJSON()), signalJSON, "Signals can be translated to JSON");
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
			equal(circuit.getName(), circuitName, "Circuit has a name");
			equal(circuit.getDescription(), circuitDescription, "Circuit has a description");
			deepEqual(circuit.getGates(), [gate, gate], "Circuit has a list of gates");
			deepEqual(circuit.getSignals(), [signal, signal], "Circuit has a list of signals");
			deepEqual(circuit.getGroups(), [], "Circuit has groupings of gates");
		});

		/**
		 * Adding of gates
		 */
		test("Circuits should add and store gates through a method call", function() {
			circuit.addGate(notgate, [1,2]); // add gate on index [2].
			raises(function() {circuit.addGate(notGate);} , "Position is not defined");
			deepEqual(circuit.getGates(), [gate, gate, notgate]);
		});
	
		/**
		 * Get gates
		 */
		test("Circuits should add and get gates through a method call", function() {
			circuit.addGate(notgate); // add gate on index [2].
			deepEqual(circuit.getGate(2), notgate);
			raises(function() { circuit.getGate(3); }, "Empty index is given.");
		});

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
		 * Circuit.getInputs()
		 */
		test("Circuit.getInputs() should yield the circuit's inputs", function() {
			var c=synthbio.Circuit.fromJSON(exampleJSON);
			deepEqual(
				c.getInputSignals(),
				["A", "B"]
			);
		});

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
	
	module("SimulationInputs");
		test("Simulation Inputs should have all base, testing all getters", function(){
			equal(simulation.options.testValue, 20, "TestValue is set");
			equal(simulation.getLength(), 40, "getLength works");
			equal(simulation.getTickWidth(), 1, "getTickWidth works");
			equal(simulation.getLowLevel(), 0, "getLowLevel works");
			equal(simulation.getHighLevel(), 600, "getHighLevel works");
			equal(simulation.getCircuit(), undefined, "bound circuit should start as undefined");	
			raises(function(){simulation.getValues();} , "Can only get values after circuit is bound to simulation");
		});
		
		test("Simulations should be able to bind to a circuit", function(){
			simulation.bindCircuit(circuit);
			deepEqual(simulation.getCircuit(), circuit, "is able to bind to a circuit");
			deepEqual(simulation.getValues(), {}, "Can get values after circuit is bound to simulation");
		});
		
		test("Simulations have a toString method", function(){
			simulation.bindCircuit(circuit);
			equal(simulation.toString(), "Simulation bound to testCircuit has options { length: 40 , tick width: 1 , low level: 0 , high level: 600}");
		});
		
		test("Simulations should be able to convert to JSON", function(){
			simulation.bindCircuit(circuit);
			equal(JSON.stringify(simulation.toJSON()), simulationJSON, "Converting to JSON works");
		});
});