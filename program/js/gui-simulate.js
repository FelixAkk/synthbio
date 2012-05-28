/**
 * Project Zelula
 *
 * Contextproject TI2800
 * TU Delft - University of Technology
 *
 * Authors:
 *  Felix Akkermans, Niels Doekemeijer, Thomas van Helden
 *  Albert ten Napel, Jan Pieter Waagmeester
 *
 * https://github.com/FelixAkk/synthbio
 *
 * @author Felix Akkermans & Jan-Pieter Waagmeester & Niels Doekemeijer
 *
 * GUI JavaScript Document, concerns all simulation GUI matters (validation & plotting).
 */

/*jslint devel: true, browser: true, forin: true, vars: true, plusplus: true, sloppy: true, white: true, maxerr: 50, indent: 4 */
/*global $, synthbio, jsPlumb, Highcharts */

/**
 * syntbio package.
 */
var synthbio = synthbio || {};
synthbio.gui = synthbio.gui || {};

/**
 * Plot simulation output
 * @param series Array of output points
 */
synthbio.gui.plotOutput = function(series) {
	var i;
	var sumSeries = [];
	
	for (i = 0; i < Math.min(series[0].data.length, series[1].data.length); i++) {
		sumSeries[i] = series[0].data[i] + series[1].data[i];
	}
	
	var xAxis = {
		minRange: 3,
		labels: {
			formatter: function() { return this.value + "s"; }
		}
	};
	
	var c = new Highcharts.StockChart({
		chart :  {renderTo: 'grid-container'},
		credits: {enabled: false},
		title :  {text : 'Simulation output'},
		xAxis: xAxis, 
		
		tooltip : {
			formatter: function() {
				var s = '<b>'+ this.x +' seconds</b>';

				$.each(this.points, function(i, point) {
					s += "<br/>" + point.series.name + ": " + this.point.y;
				});
			
				return s;
			}
		},
		
		series : series,
		
		navigator: {     
			series: {data: sumSeries},
			xAxis: xAxis
		},
											 
		rangeSelector: {
			buttons: [{
				type: 'millisecond',
				count: 5,
				text: '5s'
			}, {
				type: 'millisecond',
				count: 10,
				text: '10s'
			}, {
				type: 'millisecond',
				count: 20,
				text: '20s'
			}, {
				type: 'all',
				text: 'All'
			}],
											 
			selected : 3,
			inputEnabled: false                                                
		}                                                                                          
	});

};

$(document).ready(function() {
	// Validate
	$('#validate').on('click', function(){
		console.log('Started validating circuit...');
		synthbio.requests.validate(
			synthbio.model,
			function(response){
				if(response.message !== '') {
					$('#validate-alert p').html(response.message);
					if(!response.success){
						
						$('#validate-alert').addClass("invalid");
					}
					$('#validate-alert').modal();
				}else{
					//call
					alert('simulation valid, display...');
				}
			}
		);

	});
	$("#validate-alert").bind('closed', function(){
		$(this).find("p").html('');
		$('#validate-alert').addClass("invalid");
	});
	
	/**
	 * Dump the circuit to console.
	 */
	$('#dump-circuit').on('click', function() {
		console.log(synthbio.model);
		console.log(JSON.stringify(synthbio.model));
	});

	/**
	 * Run simulation
	 */
	$('#simulate').on('click', function() {
		console.log('Simulate initiated.');
	
		synthbio.requests.simulate(
			synthbio.model,
			function(response){
				console.log("synthbio.request.simulate called response callback");
				if(response.message !== '') {
					$('#validate-alert p').html(response.message);
					if(!response.success){
						
						$('#validate-alert').addClass("invalid");
					}
					
					console.log(synthbio.model);
					$('#validate-alert').modal();
				}else{
					//@todo: implement output visualisation here.
					console.log(response.data);
					var series = response.data.names.map(function(val) {
						return {
							name: val,
							data: response.data.data[val]
						};
					});
					synthbio.gui.plotOutput(series);
				}
			}
		);
		
	});
});
