/**
 * Synthetic Biology project (Biobrick Modeller/Simulator)
 * https://github.com/FelixAkk/synthbio
 */

package synthbio.models.test;

import synthbio.models.AndPromotor;

import org.junit.*;
import org.junit.Test;
import static org.junit.Assert.*;


/**
 * Testing the AndPromotor
 *
 * @author jieter
 */
public class TestAndPromotor{
	double delta=0.0001;
	
	/**
	 * Test if what comes out equals what we put in.
	 */
	@Test
	public void testConstructor(){
		String tf1="A";
		String tf2="B";
		double k1=4.5272;
		double km=238.9569;
		int n=3;
		
		AndPromotor AND_ab=new AndPromotor(tf1, tf2, k1, km, n);

		assertEquals(tf1, AND_ab.tf1);
		assertEquals(tf2, AND_ab.tf2);
		assertEquals(k1, AND_ab.k1, delta);
		assertEquals(km, AND_ab.km, delta);
		assertEquals(n, AND_ab.n);
	
	}

	/**
	 * Test if object produces sane JSON string.
	 */
	@Test
	public void testToJSONString(){
		String tf1="A";
		String tf2="B";
		double k1=4.5272;
		double km=238.9569;
		int n=3;
		
		AndPromotor AND_ab=new AndPromotor(tf1, tf2, k1, km, n);

		/*
		 * properties are not in order. We should not care about that,
		 * and have a JSON assertion which does not care, but for the
		 * time being, this works.
		 */
		String expect=
			"{\"k1\":"+k1+","+
			"\"km\":"+km+","+
			"\"n\":"+n+","+
			"\"tf1\":\""+tf1+"\","+
			"\"tf2\":\""+tf2+"\""+
			"}";

		assertEquals(expect, AND_ab.toJSONString());
		
	}

	@Test
	public void testHasTfs(){
		String tf1="A";
		String tf2="B";
		double k1=4.5272;
		double km=238.9569;
		int n=3;
		
		AndPromotor AND_ab=new AndPromotor(tf1, tf2, k1, km, n);

		assertTrue(AND_ab.hasTfs("A", "B"));
		assertTrue(AND_ab.hasTfs("B", "A"));
		
		assertFalse(AND_ab.hasTfs("B", "C"));
		assertFalse(AND_ab.hasTfs("B", "B"));
		assertFalse(AND_ab.hasTfs("A", "A"));
		
		
	}
	/**
	 * Test fromCSV
	 */
	@Test
	public void testFromCSV(){
		String tf1="A";
		String tf2="B";
		double k1=4.5272;
		double km=238.9569;
		int n=3;
		
		String csv=tf1+","+tf2+","+k1+","+km+","+n;

		AndPromotor AND_ab=AndPromotor.fromCSV(csv);

		assertEquals(tf1, AND_ab.tf1);
		assertEquals(tf2, AND_ab.tf2);
		assertEquals(k1, AND_ab.k1, delta);
		assertEquals(km, AND_ab.km, delta);
		assertEquals(n, AND_ab.n);
	}
}