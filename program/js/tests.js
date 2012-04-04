/**
 * Synthetic Biology project (Biobrick Modeller/Simulator)
 * https://github.com/FelixAkk/synthbio
 
 * This document contains tests for the JavaScript clientside
 * The JSON coming from the Java server will be translated to circuits and UnitTested here
 */
 $(document).ready(function(){

	/**What data should be stored in gates?*/ 
	module("Reading JSON - Gates");

		test("Gates should contain", function() {
		  equal( gate1.name , "", "a name" );
		  equal( gate1.Xcoord , "x", "an X-coord" );
		  equal( gate1.Ycoord , "y", "an Y-coord" );
		});

		test("Gates shouldn't contain", function() {
		  equal( gate1.nothing , undefined, "undefined attribute" );
		  equal( gate1.somethingElse , undefined, "undefined attribute" );
		  equal( gate1.undefined , undefined, "undefined attribute" );
		});
		
	/**What data should be stored in signals?*/
	module("Reading JSON - Signals");

		test("Signals should contain", function() {
		  equal( signal1.input1 , "input1" , "A");
		  equal( signal1.input2 , "input2" , "B" );
		  equal( signal1.output1 , "output1" , "C" );
		  equal( signal1.output2 , "output2" , "D" );
		});

		test("Signals shouldn't contain", function() {
		  notEqual( signal1.input1 , "" , "empty input");
		  notEqual( signal1.input2 , "" , "empty input2" );
		  notEqual( signal1.output1 , "random" , "random output" );
		  notEqual( signal1.output2 , "" , "empty output" );
		});
		
	/**What data should be stored in proteins?*/
	module("Reading JSON - Proteins");

		test("All proteins should be listed", function() {
		  equal( protein1.name , "protein 1", "protein 1" );
		  equal( protein2.name , "protein 2", "protein 2" );
		});

		test("First protein has values", function() {
		  equal( protein1.name , "protein 1", "protein 1" );
		  equal( protein1.k2 , "k2", "K2" );
		  equal( protein1.d1 , "d1", "D1" );
		  equal( protein1.d2 , "d2", "D2" );
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
 