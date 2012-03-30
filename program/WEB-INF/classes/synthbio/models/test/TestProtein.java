/**
 * Synthetic Biology project (Biobrick Modeller/Simulator)
 * https://github.com/FelixAkk/synthbio
 */

package synthbio.models.test;

import synthbio.models.Protein;

import junit.framework.TestCase;

public class TestProtein extends TestCase{

	/**
	 * Test if what comes out equals what we put in.
	 */
	public void testConstructorGet(){
		String name="A";
		double k2=4.6337;
		double d1=0.0240;
		double d2=0.8466;
		
		Protein a=new Protein(name, k2, d1, d2);

		assertEquals(name, a.name);
		assertEquals(k2, a.k2);
		assertEquals(d1, a.d1);
		assertEquals(d2, a.d2);
	}

	/**
	 * Test if object produces sane JSON string.
	 */
	public void testToJSONString(){
		String name="A";
		double k2=4.6337;
		double d1=0.0240;
		double d2=0.8466;
		
		Protein a=new Protein(name, k2, d1, d2);

		String expect=
			"{\"name\":\""+name+"\","+
			"\"k2\":"+k2+","+
			"\"d1\":"+d1+","+
			"\"d2\":"+d2+"}";
		
		assertEquals(expect, a.toJSONString());
	}
}