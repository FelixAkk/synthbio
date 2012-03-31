/**
 * Synthetic Biology project (Biobrick Modeller/Simulator)
 * https://github.com/FelixAkk/synthbio
 */

package synthbio.models;

import java.io.File;
import java.util.HashMap;
import java.util.Scanner;
import java.io.FileNotFoundException;

public class BioBrickReader{

	private CDS[] CDSs;

	private NotPromotor[] notPromotors;

	private AndPromotor[] andPromotors;

	
	/**
	 * loads BioBrick information from the location specified.
	 *
	 * Expects three files in path: and.csv, cds.
	 *
	 * @param 	path
	 * @throws FileNotFoundException 
	 */
	public BioBrickReader(String path){
		
		
		
	}
	private String[] getDataRows(String filename) throws Exception{
		Scanner scan=new Scanner(new File(filename));
		
		return null;
		
	}

	public CDS[] getCDSs(){
		return this.CDSs;
	}
	public NotPromotor[] getNotPromotors(){
		return this.notPromotors;
	}
	public AndPromotor[] getAndPromotors(){
		return this.andPromotors;
	}
}