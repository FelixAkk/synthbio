/**
 * Synthetic Biology project (Biobrick Modeller/Simulator)
 * https://github.com/FelixAkk/synthbio
 * Definition of objects
 */

/**Testable objects*/
var gate1 = new Gate("","x","y");
var signal1 = new Signal("input1","input2","output1","output2");
var protein1 = new Protein("protein 1", "k2", "d1", "d2");
var protein2 = new Protein("protein 2", "k2", "d1", "d2");

/**Gate Object*/
function Gate(n,x,y) {
	this.name=n;
	this.Xcoord=x;
	this.Ycoord=y;
};

/**Signal Object*/
function Signal(i1, i2, o1, o2) {
	this.input1 = i1;
	this.input2 = i2;
	this.output1 = o1;
	this.output2 = o2;
};

/**Protein Object*/
function Srotein(n, k, dOne, dTwo) {
	this.name = n;
	this.k2 = k;
	this.d1 = dOne;
	this.d2 = dTwo;
};

