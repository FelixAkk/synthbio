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
			deepEqual(and.toString(), "AND - tf1 = " + and.tf1 + ", tf2 = "+ and.tf2+ ", k1 = " + and.k1+ ", km = " + and.km+ ", n = " + and.n, "The list of properties is returned");
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
			deepEqual(not.toString(),"NOT - tf1 = " + not.tf1 + ", k1 = " + not.k1+ ", km = " + not.km+ ", n = " + not.n, "The list of properties is returned");
		});
		
	/**What data should be stored in CDS?*/
	module("CDS");

		test("All CDSs should be listed", function() {
		  equal( cds1.name , "cds1", "cds1" );
		  equal( cds2.name , "cds2", "cds2" );
		});

		test("First part of CDS has values", function() {
		  equal( cds1.name , "cds1", "cds1" );
		  equal( cds1.k2 , "k2", "K2" );
		  equal( cds1.d1 , "d1", "D1" );
		  equal( cds1.d2 , "d2", "D2" );
		});
		
		test("toString method test of CDS", function(){
			deepEqual(cds1.toString(), "CDS - name = " + cds1.name + ", k2 = "+ cds1.k2+ ", d1 = " + cds1.d1+ ", d2 = " + cds1.d2, "The list of properties is returned");
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
	
		test("A AndPromoter can be converted to JSON", function(){
			deepEqual(JSON.stringify(and), "{\"tf1\":\"tf1\",\"tf2\":\"tf2\",\"k1\":\"k1\",\"km\":\"km\",\"n\":\"n\"}","converting AND to JSON");
		});
		
		test("A NotPromoter can be converted to JSON", function(){
			deepEqual(JSON.stringify(not), "{\"tf\":\"tf\",\"k1\":\"k1\",\"km\":\"km\",\"n\":\"n\"}","converting AND to JSON");
		});
		
		test("CDS can be converted to JSON", function(){
			deepEqual(JSON.stringify(cds1), "{\"name\":\"cds1\",\"k2\":\"k2\",\"d1\":\"d1\",\"d2\":\"d2\"}","converting AND to JSON");
		});
		
		test("An object can be sent to the server as JSON", function(){
			ok(true, "needs testing");
		});
		
		
	/**Can we import data via JSON from Java?*/
	module("Import");
		
		test("Be able to receive JSON from Java server", function(){
			ok(true, "needs testing");
		});
		
		/**Using jsonToAnd method to convert JSON to Javascript Object*/
		test("Needs to be able to parse JSON and convert to AndPromoter, testing jsonToAnd", function(){
			deepEqual(and2, and, "parsing JSON object of AND");
		});
		
		test("testing getAndPromoter function", function(){
			ok(true, "needs testing");
		});
		
		/**Using jsonToNot method to convert JSON to Javascript Object*/
		test("Needs to be able to parse JSON and convert to NotPromoter", function(){
			deepEqual(not2, not, "parsing JSON object of NOT");
		});
		
		test("testing getNotPromoter function", function(){
			ok(true, "needs testing");
		});
		
		/**Using jsonToCDS method to convert JSON to Javascript Object*/
		test("Needs to be able to parse JSON and convert to CDS", function(){
			deepEqual(cds3, cds1, "parsing JSON object of CDS");
		});
		
		test("testing getCDS function", function(){
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
 