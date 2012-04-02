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

	public String biobrickPath="biobricks/";

	public BioBrickReader bbr;

	 @Before
	public void setUp() throws Exception {
		this.bbr=new BioBrickReader(this.biobrickPath);
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