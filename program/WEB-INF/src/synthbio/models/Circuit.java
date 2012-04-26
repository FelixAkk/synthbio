/**
 * Synthetic Biology project (Biobrick Modeller/Simulator)
 * https://github.com/FelixAkk/synthbio
 */

package synthbio.models;


/**
 * Circuit representation
 * @author jieter
 */
public class Circuit{
	public String name;
	public String description;
	public Gate[] nodes;
	public Signal[] signals;


	public Circuit(String name, String description, Gate[] nodes, Signal[] signals){
		this.name=name;
		this.description=description;
		this.nodes=nodes;
		this.signals=signals;
	}
	
	public Circuit(String name){
		this(name, "", null, null);
	}
	
}
