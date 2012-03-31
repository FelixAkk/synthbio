/**
 * Synthetic Biology project (Biobrick Modeller/Simulator)
 * https://github.com/FelixAkk/synthbio
 */

package synthbio.files;


import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import synthbio.models.*;

public class BioBrickReader{

	private CDS[] CDSs;

	private NotPromotor[] notPromotors;

	private AndPromotor[] andPromotors;

	
	/**
	 * loads BioBrick information from the location specified.
	 *
	 * Expects three files in path: and.csv, cds.csv and not.csv
	 *
	 * @param 	path
	 * @throws FileNotFoundException 
	 */
	public BioBrickReader(String path) throws Exception{

		String[] rows;

		rows=this.getDataRows(path+"and.csv");
		this.andPromotors=new AndPromotor[rows.length];
		for(int i=0; i<rows.length; i++){
			if(rows[i]!=""){
				this.andPromotors[i]=AndPromotor.fromCSV(rows[i]);
			}
		}
		
		rows=this.getDataRows(path+"cds.csv");
		this.CDSs=new CDS[rows.length];
		for(int i=0; i<rows.length; i++){
			if(rows[i]!=""){
				this.CDSs[i]=CDS.fromCSV(rows[i]);
			}
		}
		
		rows=this.getDataRows(path+"not.csv");
		this.notPromotors=new NotPromotor[rows.length];
		for(int i=0; i<rows.length; i++){
			if(rows[i]!=""){
				this.notPromotors[i]=NotPromotor.fromCSV(rows[i]);
			}
		}
		
		
	}
	private String[] getDataRows(String filename) throws Exception{
		Scanner scan=new Scanner(new File(filename));

		scan.nextLine();
		ArrayList<String> ret=new ArrayList();
		while(scan.hasNextLine()){
			ret.add(scan.nextLine());
		}
		return ret.toArray(new String[ret.size()]);
		
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