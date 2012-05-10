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
 *
 * @author Felix Akkermans & Jan-Pieter Waagmeester
 *
 * Utility functions for global useage.
 */

/**
 * syntbio package.
 */
var synthbio = synthbio || {};

/**
 * utilities package
 */
synthbio.util = synthbio.util || {};

/**
 * AssertException class.
 *
 * @param message Debugging message.
 */
synthbio.util.AssertException = function(message) {
    this.message = message;
}

synthbio.util.AssertException.prototype.toString = function () {
    return 'AssertException: ' + this.message;
};

/**
 * Ensure that an expression evaluates to true during execution. if the expression evaluates to false, this indicates a
 * possible bug in code.
 *
 * @param exp Expression yielding a boolean (or truthy, which is not recommended) value.
 * @param message Debugging message.
 */
synthbio.util.assert = function(exp, message) {
    if (!exp) {
        throw new AssertException(message);
    }
}