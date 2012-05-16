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
/*global $, synthbio */

/**
 * Put all the requests to the server in one place...
 * 
 * @author Thomas van Helden, Jan Pieter Waagmeester, Felix Akkermans, Niels Doekemeijer
 */

/**
 * syntbio package.
 */
var synthbio = synthbio || {};

/**
 * requests package
 */
synthbio.requests = synthbio.requests || {};

/**
 * define the base url for the server
 */
synthbio.requests.baseURL = "";

/**
 * define the base XHR call which is used by alle remote requests.
 */
synthbio.requests.baseXHR = function(provided){
	var options={
		dataType: "json",
		error: function(){},
		always: function(){}
	};
	//merge provided options with the default options.
	$.extend(options, provided);

	//prepend url
	options.url=synthbio.requests.baseURL+options.url;
	
	return $.ajax(options);
};

/**
 * listFiles
 * Returns a list of all files
 * Callback will be done on the result
 * Other messages will be shown in console.log
 */
synthbio.requests.listFiles = function(callback){
	if(!callback instanceof Function){
		return ("callback function for listFiles is not a function");
	}
	synthbio.requests.baseXHR({
		url: "/Circuit?action=list",
		success: function(response){
			if(!response.success){
				return console.log(response.message);
			}
			callback(response.data);
		},
		error: function(){
			console.log("Error has occured. Cannot get list of files from server");
		},
		always: function(){
			console.log("listFiles called");
		}
	});
};

/**
 * getFile method.
 * Return a file called "fileName"
 * Callback function will be applied to the file that is returned
 * Other info is shown in the console.log
 */
synthbio.requests.getFile = function(callback, name){

	synthbio.requests.baseXHR({
		url: "/Circuit?action=load",
		data: {
			"filename": name
		},
		success: function(response){
			callback(response);
		},
		error: function() { 
			console.log("Error has occured. Cannot get file from server"); 
		},
		always: function(){
			console.log("getFile called");
		}
	});
};

/**
 * putFile
 * Store a circuit, "circ", on the server called "fileName"
 * Callback function will be the return info/errors
 */
synthbio.requests.putFile = function(callback, fileName, circ){
	
	synthbio.requests.baseXHR({
		url: "/Circuit?action=save",
		type: "POST",
		data: {
			"filename": fileName,
			"circuit": JSON.stringify(circ)
		},
		success: function(response){
			if(!response.success){
				callback(response.message);
			}
			callback("Storage of "+fileName+" successful");
		},
		error: function(){
			callback("Error has occured. Cannot save file on server");
		},
		always: function(){
			callback("putFile called");
		}
		
	});
};

/**
 * getCDSs
 * Return a list of all available proteins
 * Callback will be applied to the returned list
 * Other messages are shown in console.log
 */
synthbio.requests.getCDSs = function(callback){
	
	synthbio.requests.baseXHR({
		url: "/ListProteins",
		//parse list of cdses and call the callback with that list.
		success: function(response){
			if(!response.success){
				console.log(response.message);
			}
			var list=[];
			$.each(response.data, function(i, elem){
				list[i]=synthbio.CDS.fromMap(response.data[i]);
			});
			callback(list);
		},
		error: function(){
			console.log("Error has occured. Cannot get proteins from server");
		},
		always: function(){
			console.log("getCDS called");
		}
		
	});
};

/**
 * Circuit to SBML
 * Save a circuit, "circ", as SBML with a "name"
 * Callback will be applied on the return messages
 */
synthbio.requests.circuitToSBML = function(callback, name, circ){
	
	synthbio.requests.baseXHR({
		url: "/Circuit?action=exportSBML",
		type: "POST",
		data: {
			"filename": name,
			"circuit": JSON.stringify(circ)
		},
		success: function(response){
			if(!response.success){
				callback(response.message);
			}
			callback(response);
		},
		error: function(){
			callback("Error has occured. Cannot save circuit");
		},
		always: function(){
			callback("circuitToSBML called");
		}
		
	});
};

/**
 * Simulate
 * Request to simulate a circuit "fileName" with "input" proteins
 * Callback will be applied on the returned data
 * Other messages are shown in console.log
 */
synthbio.requests.simulate = function(callback, name, input){
	
	synthbio.requests.baseXHR({
		url: "/Circuit?action=simulate",
		data: {
			"filename": name,
			"inputs": JSON.stringify(input)
		},
		success: function(response){
			callback(response);
		},
		error: function(){
			console.log("Error has occured. Cannot simulate this circuit");
		},
		always: function(){
			console.log("simulate called");
		}
		
	});
};

/**
 * Validate
 * Checks if a circuit is ready to be simulated
 * Callback will be applied on the return messages
 */
synthbio.requests.validate = function(callback,circuit){
	
	synthbio.requests.baseXHR({
		url: "/Circuit?action=validate",
		data: JSON.stringify(circuit),
		success: function(response){
			callback(response);
		},
		error: function(){
			callback("Error has occured. Cannot validate this circuit. Please check input");
		},
		always: function(){
			callback("validate called");
		}
		
	});
};
