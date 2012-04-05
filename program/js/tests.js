/**
 * Info from http://docs.jquery.com/QUnit
 * Synthetic Biology project (Biobrick Modeller/Simulator)
 * https://github.com/FelixAkk/synthbio
 * @author	Thomas van Helden
 * This document contains tests for the JavaScript clientside
 * The JSON coming from the Java server will be translated to circuits and UnitTested here
 */
 
/**
 * Testable objects
 */
var gate = new Gate("and", 10,20);
var signal = new Signal("A",1,2);
var cds1 = new CDS("cds1", "k2", "d1", "d2");
var cds2 = new CDS("cds2", "k2", "d1", "d2");
var circuit = new Circuit("circuit", "description", [gate, gate], [signal, signal], []);
/**
 * The actual tests
 */
$(document).ready(function(){

	/**
	 * Circuits
	 */
	module("Circuits");
		/**
		 * Properties of Circuits
		 */
		test("Circuits should have properties", function(){
			equal(circuit.name, "circuit", "Circuit has a name");
			equal(circuit.desc, "description", "Circuit has a description");
			deepEqual(circuit.gates, [gate, gate], "Circuit has a list of gates");
			deepEqual(circuit.signals, [signal, signal], "Circuit has a list of signals");
			deepEqual(circuit.groups, [], "Circuit has groupings of gates");
		});
		
		/**
		 * Circuit toString
		 */
		test("Circuit toString method is displayed", function(){
			ok(true, circuit.toString());
		});
		
		/**
		 * Circuit to JSON
		 */
		test("Circuits should be able to be converted to JSON", function(){
			equal(JSON.stringify(circuit), "{\"name\":\"circuit\",\"desc\":\"description\",\"gates\":[{\"type\":\"and\",\"x\":10,\"y\":20},{\"type\":\"and\",\"x\":10,\"y\":20}],\"signals\":[{\"protein\":\"A\",\"from\":1,\"to\":2},{\"protein\":\"A\",\"from\":1,\"to\":2}],\"groups\":[]}" ,"Circuits can be converted to JSON");
		});
		
		/**
		 * JSON to Circuit
		 */
		test("Circuits should be able to be generated from JSON", function(){
			deepEqual(Circuit.fromJSON("{\"name\":\"circuit\",\"desc\":\"description\",\"gates\":[{\"type\":\"and\",\"x\":10,\"y\":20},{\"type\":\"and\",\"x\":10,\"y\":20}],\"signals\":[{\"protein\":\"A\",\"from\":1,\"to\":2},{\"protein\":\"A\",\"from\":1,\"to\":2}],\"groups\":[]}"), circuit, "JSON can be converted to Circuits");
		});
		
		/**
		 * Circuit evaluation
		 */
		test("Circuits should be evaluated propperly", function(){
			ok(circuit.eval(), "circuit evaluation not tested yet");
		});
		
	/**
	 * Gates
	 */
	module("Gates");
	
		/**
		 * Gate properties
		 */
		test("Gates should have the right properties", function(){
			equal(gate.type, "and", "Gates store types");
			equal(gate.x, "10", "Gates store X coordinates");
			equal(gate.y, "20", "Gates store Y coordinates");
		});
		
		/**
		 * Gate toString
		 */
		test("Gates should have a working toString method", function(){
			equal(gate.toString(), gate.type + ": X = " + gate.x + ", Y = "+ gate.y ,"The toString method works");
		});
		
		/**
		 * From Gate to JSON
		 */
		test("Gates should be able to be constructed from JSON", function(){
			equal(JSON.stringify(gate), "{\"type\":\"and\",\"x\":10,\"y\":20}", "Gates can be converted to JSON");
		});
		
		/**
		 * From JSON to Gate
		 */
		test("Gates should be able to be constructed from JSON", function(){
			deepEqual(Gate.fromJSON("{\"type\":\"and\",\"x\":10,\"y\":20}"), gate, "JSON can be converted to Gates");
		});
		
		/**
		 * Get Gate from server
		 */
		test("Request Gate from server and converting it to Gate object", function(){
			ok(false, "needs testing");
		});
		
		
	/**
	 *Signals
	 */
	module("Signals");
		
		/**
		 * Signal properties
		 */
		test("Signals should store the right properties", function(){
			equal(signal.protein, "A", "type of protein");
			equal(signal.from, "1", "Origin of signal");
			equal(signal.to, "2", "Destination of signal");
		});
		
		/**
		 * Signal toString
		 */
		test("Signals should have a toString method", function(){
			equal(signal.toString(), signal.protein + " links " + signal.from + " with " + signal.to);
		});
		
		/**
		 * From Signal to JSON
		 */
		test("Signal should be able to be constructed from JSON", function(){
			equal(JSON.stringify(signal), "{\"protein\":\"A\",\"from\":1,\"to\":2}", "Signals can be translated to JSON");
		});
		
		/**
		 * From JSON to Signal
		 */
		test("Signal should be able to be constructed from JSON", function(){
			deepEqual(Signal.fromJSON("{\"protein\":\"A\",\"from\":1,\"to\":2}"), signal, "JSON can be converted to Signals");
		});
		
		/**
		 * Get Signal from server
		 */
		test("Request Signal from server and converting it to Signal object", function(){
			ok(false, "needs testing");
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
		test("toString method test of CDS", function(){
			deepEqual(cds1.toString(), "CDS - name = " + cds1.name + ", k2 = "+ cds1.k2+ ", d1 = " + cds1.d1+ ", d2 = " + cds1.d2, "The list of properties is returned");
		});
		
		/**
		 * From CDS to JSON
		 */
		test("CDS can be converted to JSON", function(){
			equal(JSON.stringify(cds1), "{\"name\":\"cds1\",\"k2\":\"k2\",\"d1\":\"d1\",\"d2\":\"d2\"}", "converting CDS to JSON");
		});
		
		/**
		 * From JSON to CDS
		 */
		test("Needs to be able to parse JSON and convert to CDS", function(){
			deepEqual(CDS.fromJSON("{\"name\":\"cds1\",\"k2\":\"k2\",\"d1\":\"d1\",\"d2\":\"d2\"}"), cds1, "parsing JSON object of CDS");
		});
		
		/**
		 * Get CDS from server
		 */
		test("testing getCDS function", function(){
			ok(false, "needs testing");
		});
		
});