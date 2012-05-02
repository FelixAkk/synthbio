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
 */
 
 
 $(document).ready(function(){
	/**
	 * listFiles(callback)
	 * This should return a list of files which are saved
	 * Requested by GUI and delivered by server
	 */
	module("listFiles");
		test('List all files', function(){
			deepEqual(synthbio.requests.listFiles(console.log),[], "List should be returned containing filenames");
		});
 
	/**
	 * getFile(callback, fileName)
	 * This should return a file with a specific name
	 * Requested by GUI and delivered by server
	 */
	module("getFile");
		test('return files from server', function(){
			deepEqual(synthbio.requests.getFile(console.log, "fileName"), "expected JSON", "Return the requested file from server");
			deepEqual(synthbio.requests.getFile(console.log, "wrongFileName"), "Error has occured. Cannot get file from server", "Should throw error message when filename is incorrect");
			deepEqual(synthbio.requests.getFile(console.log, undefined), "Error has occured. Cannot get file from server", "Should throw error message when there is no filename");
		});
	/**
	 * putFile(callback, fileName, circuit)
	 * This should save a file
	 * Sent by GUI and saved by server
	 */
	module("putFile");
		test('save files', function(){
			deepEqual(synthbio.requests.putFile(console.log, "fileName", "JSON"), "success", "Should be able to store a file on the server");
			deepEqual(synthbio.requests.putFile(console.log, "fileName", "IncorrectCircuit"), "Error has occured. Cannot save file on server", "Should not save incorrect circuits");
			deepEqual(synthbio.requests.putFile(console.log, undefined, undefined), "Error has occured. Cannot save file on server", "Should throw error when parameters are not filled in correctly");
		});
 
	/**
	 * getCDS(callback)
	 * This should return a list of proteins which are available, including meta-data
	 * Requested by GUI and delivered by server
	 */
	module("getCDSs");
		test('List all proteins', function(){
			deepEqual(synthbio.requests.getCDSs(console.log), proteins(true), "Should return a list of available proteins");
		});
	 
	/**
	 * listCircuits
	 * This should return a list of circuits of files which are saved
	 * These circuits will be shown in the compound gate section in the GUI
	 * Requested by GUI and delivered by server
	 */
	module("listCircuits");
		test('List all circuits of saved files', function(){
			deepEqual(synthbio.requests.listCircuits(console.log), {}, "should return a list containing saved circuits");
		});
 
	 /**
	 * circuitToSBML(callback, fileName, circuit)
	 * This should save a circuit as SBML file
	 * Sent by GUI and saved by server
	 */
	 module("circuitToSBML");
		test('save circuit to SBML', function(){
			deepEqual(synthbio.requests.circuitToSBML(console.log, "fileName", "circuit JSON"), "success message", "should be able to save a circuit to an SBML file");
			deepEqual(synthbio.requests.circuitToSBML(console.log, "fileName", "incorrect JSON"), "error message", "should thrown error message when circuit is incorrect");
			deepEqual(synthbio.requests.circuitToSBML(console.log, undefined, undefined), "Error has occured. Cannot get circuits from server", "should return error message when parameters are not filled in correctly");
		});
	
	 /**
	 * simulate(callback, fileName, input)
	 * This should simulate a circuit and send it back to the GUI
	 * Requested by GUI and delivered by server
	 */
	 module("simulate");
		test('simulate a circuit', function(){
			deepEqual(synthbio.requests.simulate(console.log, "fileName", "input"), "expected JSON", "Should be able to get a calculated simulation after sending a circuit to the server");
			deepEqual(synthbio.requests.simulate(console.log, "fileName", "wrong or unavailable input"), "expected JSON", "Should return error message when input is wrong");
			deepEqual(synthbio.requests.simulate(console.log, undefined, undefined), "Error has occured. Cannot simulate this circuit", "Should return error message when parameters are not filled in correctly");
		});
 
	 /**
	 * validate(callback, circuit)
	 * This should return true if a circuit has all values defined to be simulated
	 * Requested by GUI and delivered by server
	 */
	 module("validate");
		test('All attributes for simulation are defined', function(){
			deepEqual(synthbio.requests.validate(console.log, circuit), true, "should be able to validate a circuit");
			deepEqual(synthbio.requests.validate(console.log, circuit), false, "should be able to identify an incorrect circuit");
			deepEqual(synthbio.requests.validate(console.log, undefined), "Error has occured. Cannot validate this circuit. Please check input", "should throw an error when parameters are incorrect");
		});
 });
 