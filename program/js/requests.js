/**
 * 		Project Zelula:
 * Synthetic biology modeller/simulator
 * https://github.com/FelixAkk/synthbio
 * by Group E, TU Delft
 * @author	Thomas van Helden, jieter
 *
 * Definition of requests. All requests made from the client to the server
 * should be defined in this file.
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
	}
	//merge provided options with the default options.
	$.extend(options, provided);

	//prepend url
	options.url=synthbio.requests.baseURL+options.url;
	
	return $.ajax(options);
}

/**
 * listFiles
 * Returns a list of all files
 */
synthbio.requests.listFiles = function(callback){
	if(!callback instanceof Function){
		return ("callback function for listFiles is not a function");
	}
	synthbio.requests.baseXHR({
		url: "/ListFiles",
		success: function(response){
			if(!response.success){
				return console.log(response.message);
			}
			return callback(response.data);
		},
		error: function(){
			alert("Error has occured. Cannot get list of files from server");
		},
		always: function(){
			console.log("listFiles called");
		}
		
	});
	
};

/**
 * getFile method.
 * Return a file called "fileName"
 */
synthbio.requests.getFile = function(callback, name){

	synthbio.requests.baseXHR({
		url: "/getFile",
		data: {fileName: name},
		success: function(response){
			callback(response);
		},
		error: function() { 
			alert("Error has occured. Cannot get file from server"); 
		},
		always: function(){
			console.log("getFile called");
		}
	});
};

/**
 * putFile
 * Store a circuit, "circ", on the server called "fileName"
 */
synthbio.requests.putFile = function(name, circ){
	
	synthbio.requests.baseXHR({
		url: "/SaveFiles",
		type: "POST",
		data: {fileName: name, circuit: JSON.stringify(circ)},
		success: function(response){
			if(!response.success){
				console.log(response.message);
			}
			console.log("Storage of "+fileName+" successful");
		},
		error: function(){
			console.log("Error has occured. Cannot save file on server");
		},
		always: function(){
			console.log("putFile called");
		}
		
	});
};

/**
 * getCDSs
 * Return a list of all available proteins
 */
synthbio.requests.getCDSs = function(callback){
	
	synthbio.requests.baseXHR({
		url: "/ListProteinsServlet",
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
			alert("Error has occured. Cannot get proteins from server");
		},
		always: function(){
			console.log("getCDS called");
		}
		
	});
};

/**
 * ListCircuit
 * Return a list of all circuits
 */
synthbio.requests.listCircuits = function(callback){
	
	synthbio.requests.baseXHR({
		url: "/ListCircuits",
		//parse list of cdses and call the callback with that list.
		success: function(response){
			if(!response.success){
				console.log(response.message);
			}
			var list=[];
			$.each(response.data, function(i, elem){
				list[i]=synthbio.Circuit.fromMap(response.data[i]);
			});
			callback(list);
		},
		error: function(){
			alert("Error has occured. Cannot get circuits from server");
		},
		always: function(){
			console.log("listCircuit called");
		}
		
	});
};


/**
 * Simulate
 * Request to simulate a circuit "fileName" with "input" proteins
 */
synthbio.requests.simulate = function(callback, name, input){
	
	new synthbio.requests.callback();
	
	synthbio.requests.baseXHR({
		url: "/simulate",
		data: {fileName: name, cds: JSON.stringify(input)},
		success: function(response){
			callback(response);
		},
		error: function(){
			alert("Error has occured. Cannot simulate this circuit");
		},
		always: function(){
			console.log("simulate called");
		}
		
	});
};

/**
 * Validate
 * Checks if a circuit 
 */
synthbio.requests.validate = function(callback,circuit){
	
	new synthbio.requests.callback();
	
	synthbio.requests.baseXHR({
		url: "/validate",
		data: JSON.stringify(circuit),
		success: function(response){
			callback(response);
		},
		error: alert("Error has occured. Cannot validate this circuit. Please check input"),
		always: console.log("validate called")
		
	});
};
