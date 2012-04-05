/**
 * Info from http://docs.jquery.com/QUnit
 * Synthetic Biology project (Biobrick Modeller/Simulator)
 * https://github.com/FelixAkk/synthbio
 
 * This document contains tests for the JavaScript clientside
 * The JSON coming from the Java server will be translated to circuits and UnitTested here
 */
 $(document).ready(function(){

	/**AND Promoters*/ 
	module("ANDPromoters");

		test("AndPromoters should contain", function() {
		  equal( and.tf1 , "tf1", "TranscriptionFactor1" );
		  equal( and.tf2 , "tf2", "TranscriptionFactor2" );
		  equal( and.k1 , "k1", "K1" );
		  equal( and.km , "km", "Km" );
		  equal( and.n , "n", "the Hill Coeffectient" );
		});
		
		test("toString method test of AndPromoter", function(){
			equal(and.toString(), [and.tf1, and.tf2, and.k1, and.km, and.n], "The list of properties is returned");
		});
 
	/**NOT Promoters*/ 
	module("NotPromoters");

		test("NotPromoters should contain", function() {
		  equal( not.tf , "tf", "TranscriptionFactor" );
		  equal( not.k1 , "k1", "K1" );
		  equal( not.km , "km", "Km" );
		  equal( not.n , "n", "the Hill Coeffectient" );
		});
		
		test("toString method test of NotPromoter", function(){
			equal(not.toString(), [not.tf1, not.k1, not.km, not.n], "The list of properties is returned");
		});
		
	/**What data should be stored in CDS?*/
	module("Reading JSON - CDS");

		test("All CDSs should be listed", function() {
		  equal( cds1.name , "cds1", "cds1" );
		  equal( cds2.name , "cds2", "cds2" );
		});

		test("First part of CDS has values", function() {
		  equal( cds1.name , "cds1", "protein 1" );
		  equal( cds1.k2 , "k2", "K2" );
		  equal( cds1.d1 , "d1", "D1" );
		  equal( cds1.d2 , "d2", "D2" );
		});
		
		test("toString method test of NotPromoter", function(){
			equal(CDS.toString(), [cds1.name, cds1.k2, cds1.d1, cds1.d2], "The list of properties is returned");
		});
		
	/**What is the connection info?*/
	module("Connection info & data transfer");
	
		test("connection has a name", function(){
			ok(true, "needs testing");
		});
		
		test("See Ping Pong test", function(){
			ok(true, "needs testing");
		});
		
	/**Does the circuit export work?*/
	module("Export");
	
		test("An object can be converted to JSON", function(){
			ok(true, "needs testing");
		});
		
		test("An object can be sent to the server as JSON", function(){
			ok(true, "needs testing");
		});
		
		
	/**Can we import data via JSON from Java?*/
	module("Import");
		
		test("Be able to receive JSON from Java server", function(){
			ok(true, "needs testing");
		});
		
		test("Needs to be able to parse JSON", function(){
			ok(true, "needs testing");
		});
		
		test("Needs to be able to convert JSON to gates", function(){
			ok(true, "needs testing");
		});
		
		test("Needs to be able to convert JSON to signals", function(){
			ok(true, "needs testing");
		});
		
		test("Needs to be able to convert JSON to proteins", function(){
			ok(true, "needs testing");
		});
		
		
	/**Calculating output*/
	module("Calculate output");
		
		test("setInput", function(){
			ok(true,"needs testing");
		});
		
		test("setOutput", function(){
			ok(true,"needs testing");
		});
		
		test("getOutput", function(){
			ok(true,"needs testing");
		});
		
	
	/**Validating proteins*/
	module("Validate proteins");
		
		
	/**Validating circuits*/
	module("Validate circuit");
		
		
});
 