/**
 * Synthetic Biology project (Biobrick Modeller/Simulator)
 * https://github.com/FelixAkk/synthbio
 */

package synthbio.models.test;

import synthbio.models.CDS;

import org.junit.*;
import org.junit.Test;
import static org.junit.Assert.*;


public class TestCDS{
	double delta=0.0001;
	
	/**
	 * Test if what comes out equals what we put in.
	 */
	@Test
	public void testConstructor(){
		String name="A";
		double k2=4.6337;
		double d1=0.0240;
		double d2=0.8466;
		
		CDS a=new CDS(name, k2, d1, d2);

		assertEquals(name, a.name);
		assertEquals(k2, a.k2, delta);
		assertEquals(d1, a.d1, delta);
		assertEquals(d2, a.d2, delta);
	}

	/**
	 * Test if object produces sane JSON string.
	 */
	@Test
	public void testToJSONString(){
		String name="A";
		double k2=4.6337;
		double d1=0.0240;
		double d2=0.8466;
		
		CDS a=new CDS(name, k2, d1, d2);

		String expect=
			"{"+
			"\"d1\":"+d1+","+
			"\"d2\":"+d2+","+
			"\"k2\":"+k2+","+
			"\"name\":\""+name+"\""+
			"}";

		assertEquals(expect, a.toJSONString());
	}

	/**
	 * Test fromCSV
	 */
	@Test
	public void testFromCSV(){
		String name="A";
		double k2=4.6337;
		double d1=0.0240;
		double d2=0.8466;

		String csv=name+","+k2+","+d1+","+d2;

		CDS a=CDS.fromCSV(csv);

		assertEquals(name, a.name);
		assertEquals(k2, a.k2, delta);
		assertEquals(d1, a.d1, delta);
		assertEquals(d2, a.d2, delta);
	}
}