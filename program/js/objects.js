/**
 * Synthetic Biology project (Biobrick Modeller/Simulator)
 * https://github.com/FelixAkk/synthbio
 * Definition of objects
 */

/**Testable objects*/
var gate1 = new gate("","x","y");
var signal1 = new signal("input1","input2","output1","output2");
var protein1 = new protein("protein 1", "k2", "d1", "d2");
var protein2 = new protein("protein 2", "k2", "d1", "d2");

/**Gate Object*/
function gate(n,x,y) {
	this.name=n;
	this.Xcoord=x;
	this.Ycoord=y;
};

/**Signal Object*/
function signal(i1, i2, o1, o2) {
	this.input1 = i1;
	this.input2 = i2;
	this.output1 = o1;
	this.output2 = o2;
};

/**Protein Object*/
function protein(n, k, dOne, dTwo) {
	this.name = n;
	this.k2 = k;
	this.d1 = dOne;
	this.d2 = dTwo;
};

