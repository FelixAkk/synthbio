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
 * Define the listFiles method.
 */
synthbio.requests.listFiles = function(){
	
	function callback {};
	
	synthbio.requests.baseXHR({
		url: "/ListFiles",
		success: function(response){
			if(!response.success){
				return callback(response.message);
			}
			return callback(response.data);
		},
		error: function(){
			return callback("Error has occured. Cannot get list of files from server")
		},
		always: callback("listFiles called")
		
	});
	
};

/**
 * Define the getFile method.
 */
synthbio.requests.getFile = function(fileName){

	function callback {};
	
	synthbio.requests.baseXHR({
		url: "/FolderWhereStuffIsSaved/"+ fileName,
		success: function(response){
			return callback(response);
		},
		error: callback("Error has occured. Cannot get file from server"),
		always: callback("getFile called")
		
	});
};

/**
 * Define the putFile method.
 */
synthbio.requests.putFile = function(fileName, circ){
	
	function callback {};
	
	synthbio.requests.baseXHR({
		url: "/SaveFiles",
		type: "POST",
		data: [name: fileName, circuit:JSON.stringify(circ)],
		success: function(response){
			if(!response.success){
				return callback(response.message);
			}
			return callback("Storage of "+fileName+" successful");
		},
		error: callback("Error has occured. Cannot save file on server"),
		always: callback("putFile called")
		
	});
};

/**
 * Define the getCDSs request.
 */
synthbio.requests.getCDSs = function(){
	//if(!(callback instanceof Function)){
	//	throw "Callback function should be supplied";
	//}
	function callback {};
	
	synthbio.requests.baseXHR({
		url: "/ListProteinsServlet",
		//parse list of cdses and call the callback with that list.
		success: function(response){
			if(!response.success){
				return response.message;
			}
			var list=[];
			$.each(response.data, function(i, elem){
				list[i]=synthbio.CDS.fromMap(response.data[i]);
			});
			return callback(list);
		},
		error: callback("Error has occured. Cannot get proteins from server"),
		always: callback("getCDS called")
		
	});
};

/**
 * Define the listCircuits method.
 */
synthbio.requests.listCircuits = function(){
	
	function callback {};
	
	synthbio.requests.baseXHR({
		url: "/ListCircuits",
		//parse list of cdses and call the callback with that list.
		success: function(response){
			if(!response.success){
				return response.message;
			}
			var list=[];
			$.each(response.data, function(i, elem){
				list[i]=synthbio.Circuit.fromMap(response.data[i]);
			});
			return callback(list);
		},
		error: callback("Error has occured. Cannot get circuits from server"),
		always: callback("listCircuit called")
		
	});
};


/**
 * Define the simulate method.
 */
synthbio.requests.simulate = function(fileName, input){
	
	function callback {};
	
	synthbio.requests.baseXHR({
		url: "/simulate",
		data: [fileName, JSON.stringify(input)]
		success: function(response){
			return callback(response);
		},
		error: callback("Error has occured. Cannot simulate this circuit"),
		always: callback("simulate called")
		
	});
};

/**
 * Define the validate method.
 */
synthbio.requests.validate = function(fileName, input){
	
	function callback {};
	
	synthbio.requests.baseXHR({
		url: "/validate",
		data: [fileName, JSON.stringify(input)]
		success: function(response){
			return callback(response);
		},
		error: callback("Error has occured. Cannot validate this circuit. Please check input"),
		always: callback("simulate called")
		
	});
};
