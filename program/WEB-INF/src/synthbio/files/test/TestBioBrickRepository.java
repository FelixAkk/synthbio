/**
 * Synthetic Biology project (Biobrick Modeller/Simulator)
 * https://github.com/FelixAkk/synthbio
 */

package synthbio.files.test;

import org.junit.*;
import org.junit.Test;
import static org.junit.Assert.*;

import synthbio.files.BioBrickRepository;
import synthbio.models.*;

public class TestBioBrickRepository{

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

		assertEquals("A", c.getName());
	}
}
