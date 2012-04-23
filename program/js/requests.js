/**
 * Synthetic Biology project (Biobrick Modeller/Simulator)
 * https://github.com/FelixAkk/synthbio
 * @author	jieter
 *
 * Definition of requests. All requests made from the client to the server
 * should be defined in this file.
 */
 
/*jslint devel: true, browser: true, sloppy: true, stupid: false, white: true, maxerr: 50, indent: 4 */
/*global $ */

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
		dataType: "json"
	}
	//merge provided options with the default options.
	$.extend(options, provided);

	//prepend url
	options.url=synthbio.requests.baseURL+options.url;
	
	return $.ajax(options);
}

/**
 * Define the getCDSs request.
 */
synthbio.requests.getCDSs = function(callback){
	if(!(callback instanceof Function)){
		throw "Callback function should be supplied";
	}
	
	synthbio.requests.baseXHR({
		url: "/ListProteins",
		//parse list of cdses and call the callback with that list.
		success: function(response){
			if(!response.success){
				return callback(response.message);
			}
			var list=[];
			$.each(response.data, function(i, elem){
				list[i]=synthbio.CDS.fromMap(response.data[i]);
			});
			return callback(list);
		}
	});
};

