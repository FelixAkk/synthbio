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
import java.util.HashMap;

import synthbio.Util;

/**
 * Read Synfiles from filesystem.
 * 
 * @author jieter
 */
public class SynRepository{

	/**
	 * Path to the location we store .syn files in.
	 */
	private String path;

	/**
	 * Collection of .syn files.
	 */
	private Collection<String> files=new ArrayList<String>();

	private HashMap<String, Long> modified=new HashMap<String, Long>();

	public SynRepository() throws Exception{
		this("data/synstore/");
	}
	
	/**
	 * Loads the list of .syn files from the location specified.
	 *
	 * @param  path
	 * @throws FileNotFoundException 
	 */
	public SynRepository(String path) throws Exception{
		this.path=path;

		this.loadFiles();
	}

	/**
	 * Load the list of files from this.path.
	 */
	public void loadFiles() {
		File folder = new File(this.path);
		for(File file: folder.listFiles()) {
			if(file.isFile() && file.getName().endsWith(".syn")) {
				this.files.add(file.getName());
				this.modified.put(file.getName(), file.lastModified());
			}
		}
	}

	/**
	 * Return a list of available files.
	 */
	public Collection<String> getFileList() {
		return this.files;
	}

	/**
	 * Does the .syn store contain the filename?
	 */
	public boolean hasFile(String filename) {
		return this.files.contains(filename);
	}
	
	/**
	 * Return the contents of filename.
	 *
	 * @param filename The file to load.
	 */
	public String getFile(String filename) throws Exception{
		assert this.hasFile(filename);

		return Util.fileToString(this.path+filename);
	}

	/**
	 * Save the content to file
	 *
	 * @param filename The file to save to.
	 * @param content The content of the file.
	 */
	public void putFile(String filename, String content) throws Exception{
		assert filename.endsWith(".syn");
		Util.stringToFile(this.path+filename, content);

		this.files.add(filename);
	}
	
	/**
	 * return the last modification time for the file specified.
	 */
	public Long lastModified(String filename) {
		assert this.hasFile(filename) : "No such file: "+filename;

		return this.modified.get(filename);
	}
}
