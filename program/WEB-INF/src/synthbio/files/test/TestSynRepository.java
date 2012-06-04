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

import java.util.Collection;
import java.io.File;


import org.junit.*;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import synthbio.files.SynRepository;
import synthbio.models.*;
import synthbio.Util;


/**
 * Test the Syn repository
 *
 * @author jieter
 */
public class TestSynRepository{
	
	public String synPath="data/synstore/";

	public SynRepository synrep;

	public String testFilename="testfilename.syn";

	
	@Before
	public void setUp() throws Exception {
		this.synrep=new SynRepository(synPath);
	}

	/**
	 * Check that the file list contains at least the example.syn and
	 */
	@Test
	public void testFileList(){
		Collection<String> list=synrep.getFileList();
		assertThat(list.size(), is(greaterThan(0)));
		assertThat(list, hasItem("example.syn"));
	}

	/**
	 * Check that the String returned by getFile is equal to the file
	 * contents.
	 */
	@Test
	public void testGetFile() throws Exception{
		String file=synrep.getFile("example.syn");

		assertEquals(
			Util.fileToString("data/synstore/example.syn"),
			file
		);
	}

	/**
	 * Check if saving a file yields a file with the desired content.
	 */
	@Test
	public void testPutFile() throws Exception{

		String content="testcontent123";
		
		synrep.putFile(this.testFilename, content);

		assertEquals(
			content,
			synrep.getFile(this.testFilename)
		);

		
	}

	/**
	 * Check if the synstore returns a last modified timestamp.
	 */
	@Test
	public void testLastModified() throws Exception{
		String filename="example.syn";
		File file=new File("data/synstore/"+filename);
		assertThat(synrep.lastModified(filename), is(equalTo(file.lastModified())));
	}

	@After
	public void deleteFiles() throws Exception{
		new File("data/synstore/"+this.testFilename).delete();
	}
}
