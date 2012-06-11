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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Scanner;

import synthbio.models.CDS;
import synthbio.models.AndPromotor;
import synthbio.models.NotPromotor;

/**
 * Read BioBricks from the provided csv files.
 *
 * For each kind of BioBrick, two methods are provided. One to fetch a
 * collection containing al known bricks, and one to find one Brick by
 * its name or transcription factor(s).
 *
 * @author jieter
 */
public class BioBrickRepository{

	private Collection<CDS> CDSs=new ArrayList<CDS>();
	private Collection<NotPromotor> notPromotors=new ArrayList<NotPromotor>();
	private Collection<AndPromotor> andPromotors=new ArrayList<AndPromotor>();

	public BioBrickRepository() throws Exception{
		this("data/biobricks/2012-06-07/");
	}
	
	/**
	 * loads BioBrick information from the location specified.
	 *
	 * Expects three files in path: and.csv, cds.csv and not.csv
	 *
	 * @param  path
	 * @throws FileNotFoundException 
	 */
	public BioBrickRepository(String path) throws Exception{

		for(String row: this.getDataRows(path+"and.csv")){
			if(row!=""){
				this.andPromotors.add(AndPromotor.fromCSV(row));
			}
		}
		
		for(String row: this.getDataRows(path+"cds.csv")){
			if(row!=""){
				this.CDSs.add(CDS.fromCSV(row));
			}
		}
		
		for(String row: this.getDataRows(path+"not.csv")){
			if(row!=""){
				this.notPromotors.add(NotPromotor.fromCSV(row));
			}
		}
	}

	/**
	 * Get all rows from a file into an ArrayList<String>
	 */
	private Collection<String> getDataRows(String filename) throws Exception{
		ArrayList<String> ret=new ArrayList<String>();
		Scanner scan=new Scanner(new File(filename));

		scan.nextLine();
		while(scan.hasNextLine()){
			ret.add(scan.nextLine());
		}
		return ret;
	}

	/**
	 * Collection of al known CDSs
	 */
	public Collection<CDS> getCDSs(){
		return this.CDSs;
	}

	/**
	 * Return the CDS for the protein name.
	 *
	 * If no such CDS exists, return null.
	 *
	 * @param name The protein name to check.
	 * @return The CDS, or null if none exist.
	 */
	public CDS getCDS(String name){
		for(CDS c: this.CDSs){
			if(c.getName().equals(name)){
				return c;
			}
		}
		return null;
	}

	/**
	 * Collection of all known NotPromotors
	 */
	public Collection<NotPromotor> getNotPromotors(){
		return this.notPromotors;
	}

	/**
	 * Return the NotPromotor for the transcription factors tf.
	 *
	 * If no such NotPromotor exists, return null.
	 *
	 * @param tf The transcription factor to check.
	 * @return The NotPromotor, or null if none exist.
	 */
	public NotPromotor getNotPromotor(String tf){
		for(NotPromotor p: this.notPromotors){
			if(p.getTf().equals(tf)){
				return p;
			}
		}
		return null;
	}

	/**
	 * Collection of all known AndPromotors
	 */
	public Collection<AndPromotor> getAndPromotors(){
		return this.andPromotors;
	}

	/**
	 * Return the AndPromotor for the transcription factors tf1 and tf2.
	 *
	 * If no such AndPromotor exists, return null.
	 *
	 * @param tf1 First transcription factor to check.
	 * @param tf2 Second transcription factor to check.
	 * @return The AndPromotor, or null if none exist.
	 */
	public AndPromotor getAndPromotor(String tf1, String tf2){
		for(AndPromotor p: this.andPromotors){
			if(p.hasTfs(tf1, tf2)){
				return p;
			}
		}
		return null;
	}

}
