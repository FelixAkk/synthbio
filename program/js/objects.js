/**
 * Info from http://api.jquery.com/
 * Synthetic Biology project (Biobrick Modeller/Simulator)
 * https://github.com/FelixAkk/synthbio
 *
 * Definition of objects
 */

 /**We have to check which objects we need and which data we wish to keep. It was not totally clear to me which data should be stored client side. At least this contains enough scaffolding for each class*/
 
/**Testable objects*/
var and = new AndPromoter("tf1", "tf2", "k1", "km", "n");
var not = new NotPromoter("tf", "k1", "km", "n");
var cds1 = new CDS("cds1", "k2", "d1", "d2");
var cds2 = new CDS("cds2", "k2", "d1", "d2");
var and2 = jsonToAnd("{\"tf1\":\"tf1\",\"tf2\":\"tf2\",\"k1\":\"k1\",\"km\":\"km\",\"n\":\"n\"}");
var not2 = jsonToNot("{\"tf\":\"tf\",\"k1\":\"k1\",\"km\":\"km\",\"n\":\"n\"}");
var cds3 = jsonToCDS("{\"name\":\"cds1\",\"k2\":\"k2\",\"d1\":\"d1\",\"d2\":\"d2\"}");


/**AndPromoter
 * ToString() returns a list of all properties of AndPromoter
 */
function AndPromoter(transFactor1, transFactor2, kOne, kM, hillCoef){
	this.tf1 = transFactor1;
	this.tf2 = transFactor2;
	this.k1 = kOne;
	this.km = kM;
	this.n = hillCoef;
}
AndPromoter.prototype.toString = function(){
	return [this.tf1, this.tf2, this.k1, this.km, this.n];
}

/**NotPromoter
 * ToString() returns a list of all properties of NotPromoter
 */
function NotPromoter(transFactor, kOne, kM, hillCoef){
	this.tf = transFactor;
	this.k1 = kOne;
	this.km = kM;
	this.n = hillCoef;
}
NotPromoter.prototype.toString = function(){
	return [this.tf1, this.k1, this.km, this.n];
}

/**CDS Object
 * ToString() returns a list of all properties of a CDS
 */
function CDS(n, k, dOne, dTwo) {
	this.name = n;
	this.k2 = k;
	this.d1 = dOne;
	this.d2 = dTwo;
}
CDS.prototype.toString = function(){
	return [this.name, this.k2, this.d1, this.d2];
}

/**
 * Function which will request an AndPromoter from the server and translate it to a AndPromoter object.
 * Parameter: whichPromoter is used to specify which AndPromoter you wish to get.
 */
function getAndPromoter(whichPromoter){
	/**Which data do we want*/
	$.getJSON("URl",
	{
		//whichPromoter
	},
	/**What do we want to do with the data*/
	function(json) {
		//Make an AndPromoter object out of it
		return jsonToAnd(json);
	});
}

/** Method which takes JSON string and converts it into an AndPromoter*/
function jsonToAnd(json){
	var temp = $.parseJSON(json);
	return new AndPromoter(temp.tf1, temp.tf2, temp.k1, temp.km, temp.n);
}

/**
 * Function which will request an NotPromoter from the server and translate it to a NotPromoter object.
 * Parameter: whichPromoter is used to specify which NotPromoter you wish to get.
 */
function getNotPromoter(whichPromoter){
	/**Which data do we want*/
	$.getJSON("URl",
	{
		//whichPromoter
	},
	/**What do we want to do with the data*/
	function(json) {
		//Make an NotPromoter object out of it
		return jsonToNot(json);
	});
}

/** Method which takes JSON string and converts it into an NotPromoter*/
function jsonToNot(json){
	var temp = $.parseJSON(json);
	return new NotPromoter(temp.tf, temp.k1, temp.km, temp.n);
}

/**
 * Function which will request CDS from the server and translate it to a CDS object.
 * Parameter: whichPromoter is used to specify which CDS you wish to get.
 */
function getCDS(whichCDS){
	/**Which data do we want*/
	$.getJSON("URl",
	{
		//whichCDS
	},
	/**What do we want to do with the data*/
	function(json) {
		//Make an CDS object out of it
		return jsonToCDS(json);
	});
}

/** Method which takes JSON string and converts it into CDS*/
function jsonToCDS(json){
	var temp = $.parseJSON(json);
	return new CDS(temp.name, temp.k2, temp.d1, temp.d2);
}

/**
 * Function which will request a calculation from the server.
 * Parameter: the circuit which we want to calculate the output of.
 */
function calcRes(circuit){
	/**Which data do we want*/
	$.getJSON("URl",
	{
		//circuit
	},
	/**What do we want to do with the data*/
	function(json) {
		//Show result somehow
	});
}