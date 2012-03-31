/**
 * Synthetic Biology project (Biobrick Modeller/Simulator)
 * https://github.com/FelixAkk/synthbio
 */

package synthbio.files.test;

import org.junit.*;
import org.junit.Test;
import static org.junit.Assert.*;

import synthbio.files.BioBrickReader;

public class TestBioBrickReader{

	String biobrickPath="biobricks/";

	public static BioBrickReader bbr;
	/**
	 * Verify that no exceptions are thrown.
	 */
	@Test
	public void testLoading() throws Exception{
		bbr=new BioBrickReader(this.biobrickPath);
	}

	/**
	 * Check if reader returns the right amount of biobricks
	 * per kind.
	 */
	@Test
	public void testBioBrickCounts(){
		assertEquals(45, bbr.getAndPromotors().length);
		assertEquals(10, bbr.getCDSs().length);
		assertEquals(10, bbr.getNotPromotors().length);
	}
}