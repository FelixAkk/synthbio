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
 
package synthbio.simulator;

import synthbio.models.Circuit;
import synthbio.models.CircuitException;

/**
 * A class for converting Circuit-objects to SBML.
 * @author Albert ten Napel
 */
public class CircuitConverter {

	/**
	 * Converts a Circuit-object to SBML.
	 * @param		circuit	The Circuit-object to convert.
	 * @return					The SBML-formatted string.
	 */
	public String convert(Circuit circuit) {
		try {
			circuit.validate();
		} catch(CircuitException ce) {
			ce.printStackTrace();
		}
		return null;
	}
}
