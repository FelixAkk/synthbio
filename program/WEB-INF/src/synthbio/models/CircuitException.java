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

package synthbio.models;

/**
 * Exception to be thrown if Circuit creation fails.
 *
 * @author Jieter
 */
public class CircuitException extends Exception{
	public CircuitException(String message){
		super(message);
	}
}
