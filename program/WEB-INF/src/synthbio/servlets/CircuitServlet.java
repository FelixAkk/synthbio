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

package synthbio.servlets;
 
import synthbio.files.BioBrickRepository;
import synthbio.files.SynRepository;
import synthbio.models.CircuitFactory;

/**
 * CircuitServlet is the super class for all servlets concerning Circuits.
 * 
 * API functions documented at:
 * https://github.com/FelixAkk/synthbio/wiki/Zelula-HTTP-API
 * 
 * @author jieter 
 */
@SuppressWarnings("serial")
public class CircuitServlet extends SynthbioServlet {

	/**
	 * The repository of .syn files.
	 */
	protected SynRepository synRepository;

	/**
	 * The BioBrick repository
	 */
	protected BioBrickRepository biobrickRepository;

	/**
	 * The CircuitFactory
	 */
	protected CircuitFactory circuitFactory;
}
