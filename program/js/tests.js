/**
 * Info from http://docs.jquery.com/QUnit
 * Synthetic Biology project (Biobrick Modeller/Simulator)
 * https://github.com/FelixAkk/synthbio
 
 * This document contains tests for the JavaScript clientside
 * The JSON coming from the Java server will be translated to circuits and UnitTested here
 */
 
/**
 * Testable objects
 */
var gate = new Gate("and", 10,20);
var signal = new Signal(A,1,2);
var cds1 = new CDS("cds1", "k2", "d1", "d2");
var cds2 = new CDS("cds2", "k2", "d1", "d2");

/**
 * The actual tests
 */
 $(document).ready(function(){

	/**
	 * Gates
	 */
	module("Gates");
	
		/**
		 * Gate properties
		 */
		test("Gates should have the right properties", function(){
			equal(gate.type, "type", "Gates store types");
			equal(gate.x, "x", "Gates store X coordinates");
			equal(gate.y, "y", "Gates store Y coordinates");
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
			equal(gate.toJSON(), "{\"type\":\"and\",\"x\":\"10\",\"y\":\"20\"}", "Gates can be converted to JSON");
		});
		
		/**
		 * From JSON to Gate
		 */
		test("Gates should be able to be constructed from JSON", function(){
			deepEqual(Gate.fromJSON("{\"type\":\"and\",\"x\":\"10\",\"y\":\"20\"}"), gate, "JSON can be converted to Gates");
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
			equal(signal.toJSON(), "{\"protein\":\"A\",\"from\":\"1\",\"to\":\"2\"}", "Signals can be translated to JSON");
		});
		
		/**
		 * From JSON to Signal
		 */
		test("Signal should be able to be constructed from JSON", function(){
			deepEqual(Signal.fromJSON("{\"protein\":\"A\",\"from\":\"1\",\"to\":\"2\"}"), signal, "JSON can be converted to Signals");
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
			deepEqual(cds1.toJSON, "{\"name\":\"cds1\",\"k2\":\"k2\",\"d1\":\"d1\",\"d2\":\"d2\"}","converting CDS to JSON");
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
		
 
}