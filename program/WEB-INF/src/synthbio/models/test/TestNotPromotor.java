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

package synthbio.models.test;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

import synthbio.models.NotPromotor;

/**
 * Testing the NotPromotor.
 * 
 * @author jieter
 */
public class TestNotPromotor{
	double delta=0.0001;
	
	/**
	 * Test if what comes out equals what we put in.
	 */
	@Test
	public void testConstructor(){
		String tf="A";
		double k1=4.5272;
		double km=238.9569;
		int n=3;
		
		NotPromotor NOT_a=new NotPromotor(tf, k1, km, n);

		assertEquals(tf, NOT_a.tf);
		assertEquals(k1, NOT_a.k1, delta);
		assertEquals(km, NOT_a.km, delta);
		assertEquals(n, NOT_a.n);
	
	}

	/**
	 * Test if object produces sane JSON string.
	 */
	@Test
	public void testToJSONString(){
		String tf="A";
		double k1=4.5272;
		double km=238.9569;
		int n=3;
		
		NotPromotor NOT_a=new NotPromotor(tf, k1, km, n);

		String expect=
			"{"+
			"\"k1\":"+k1+","+
			"\"km\":"+km+","+
			"\"n\":"+n+","+
			"\"tf\":\""+tf+"\""+
			"}";
		
		assertEquals(expect, NOT_a.toJSONString());
	}

	/**
	 * Test fromCSV
	 */
	@Test
	public void testFromCSV(){
		String tf="A";
		
		double k1=4.5272;
		double km=238.9569;
		int n=3;
		
		String csv=tf+","+k1+","+km+","+n;

		NotPromotor NOT_a=NotPromotor.fromCSV(csv);

		assertEquals(tf, NOT_a.tf);
		assertEquals(k1, NOT_a.k1, delta);
		assertEquals(km, NOT_a.km, delta);
		assertEquals(n, NOT_a.n);
	}
}