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
 *
 * @param message
 */
synthbio.util.AssertException = function(message) {
    this.message = message;
}

synthbio.util.AssertException.prototype.toString = function () {
    return 'AssertException: ' + this.message;
};

/**
 *
 * @param exp
 * @param message
 */
synthbio.util.assert = function(exp, message) {
    if (!exp) {
        throw new AssertException(message);
    }
}