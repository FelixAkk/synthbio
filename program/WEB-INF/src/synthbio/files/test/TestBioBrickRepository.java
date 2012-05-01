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

package synthbio.files.test;

import org.junit.*;
import org.junit.Test;
import static org.junit.Assert.*;

import synthbio.files.BioBrickRepository;
import synthbio.models.*;

/**
 * Test the BioBrick repository
 *
 * @author jieter
 */
public class TestBioBrickRepository{
	double delta=0.0001;
	
	public String biobrickPath="biobricks/";

	public BioBrickRepository bbr;

	@Before
	public void setUp() throws Exception {
		this.bbr=new BioBrickRepository(this.biobrickPath);
	}

	/**
	 * Check if reader returns the right amount of biobricks
	 * per kind.
	 */
	@Test
	public void testBioBrickCounts(){
		assertEquals(45, bbr.getAndPromotors().size());
		assertEquals(10, bbr.getCDSs().size());
		assertEquals(10, bbr.getNotPromotors().size());
	}

	/**
	 * Check getCDS response.
	 */
	@Test
	public void testGetCDS(){
		CDS c=bbr.getCDS("A");

		assertNotNull(c);
		assertEquals("A", c.getName());
		assertEquals(4.6337, c.getK2(), delta);
		assertEquals(0.0240, c.getD1(), delta);
		assertEquals(0.8466, c.getD2(), delta);
	}
	
	/**
	 * Check getCDS response for a non-existant CDS
	 */
	@Test
	public void testGetNonExistantCDS(){
		CDS c=bbr.getCDS("Z");

		assertNull(c);
	}

	/**
	 * Check getNotPromotor response.
	 */
	@Test
	public void testGetNotPromotor(){
		NotPromotor p=bbr.getNotPromotor("B");

		assertNotNull(p);
		assertEquals("B", p.getTf());
		assertEquals(2.8753, p.getK1(), delta);
		assertEquals(281.3545, p.getKm(), delta);
		assertEquals(1, p.getN(), delta);
	}
	
	/**
	 * Check getNotPromotor response for a non-existant NotPromotor
	 */
	@Test
	public void testGetNonExistantNotPromotor(){
		NotPromotor c=bbr.getNotPromotor("Z");

		assertNull(c);
	}
	
	/**
	 * Check getAndPromotor response.
	 */
	@Test
	public void testGetAndPromotor(){
		AndPromotor p=bbr.getAndPromotor("A", "B");

		assertNotNull(p);
		assertEquals("A", p.getTf1());
		assertEquals("B", p.getTf2());
	}
	
	/**
	 * Check getAndPromotor response for a non-existant AndPromotor.
	 */
	@Test
	public void testGetNonExistantAndPromotor(){
		AndPromotor p=bbr.getAndPromotor("A", "A");

		assertNull(p);
	}
}
