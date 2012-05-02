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

package synthbio.files;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Scanner;


/**
 * Read Synfiles from filesystem.
 * 
 * @author jieter
 */
public class SynRepository{

	private Collection<String> files=new ArrayList<String>();
	
	public SynRepository() throws Exception{
		this("biobricks/");
	}
	
	/**
	 * loads .syn files from the location specified.
	 *
	 * @param  path
	 * @throws FileNotFoundException 
	 */
	public SynRepository(String path) throws Exception{

	}

	/**
	 * Return a list of available files.
	 */
	public Collection<String> getFileList(){
		return this.files;
	}

	/**
	 * Return the contents of filename.
	 *
	 * @param filename The file to load.
	 */
	public String getFile(String filename){
		return "{}";
	}

}
