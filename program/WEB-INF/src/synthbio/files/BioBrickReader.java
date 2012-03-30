/**
 * Synthetic Biology project (Biobrick Modeller/Simulator)
 * https://github.com/FelixAkk/synthbio
 */

package synthbio.models;

public class BioBrickReader{

	private Protein[] proteins;

	private NotPromotor[] notPromotors;

	private AndPromotor[] andPromotors;
	/**
	 * loads BioBrick information from the location specified.
	 */
	public BioBrickReader(){

	}

	public Protein[] getProteins(){
		return this.proteins;
	}
	public NotPromotor[] getNotPromotors(){
		return this.notPromotors;
	}
	public AndPromotor[] getAndPromotors(){
		return this.andPromotors;
	}
}