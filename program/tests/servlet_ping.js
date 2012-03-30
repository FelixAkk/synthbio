/**
 * Synthetic Biology project (Biobrick Modeller/Simulator)
 * https://github.com/FelixAkk/synthbio
 *
 * This test checks whether the /Ping servlet is active and returns "Pong".
 */

$.ajax("../Ping")
	.done(function(data) { $('body').html(data); })
	.fail(function(data) { $('body').html('Failed: ' + data); });