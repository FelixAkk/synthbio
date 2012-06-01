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
 * The big plot object (Highcharts.StockChart)
 */
synthbio.gui.plot = {};

/**
 * Precision of plotting data
 */
synthbio.gui.plotPrecision = 2;

/**
 * Round data to synthbio.gui.plotPrecision
 */
synthbio.gui.roundSeries = function(series) {
	return $.map(series, function(val) { 
		return val.roundTo(synthbio.gui.plotPrecision); a
	});
};

/**
 * Calculate the sum series of the input
 * @param series Array of objects with data property
 */
synthbio.gui.calculateSumSeries = function(series) {
	var res = [];
	$.each(series, function(i, serie) {
		$.each(serie.data, function(idx, val) {
			res[idx] = res[idx] + val || val;
		});
	});
	return res;
};

/**
 * Update the synthbio.gui.plot sum series (after adding/hiding a series)
 * @param val Array of values
 * @param hidden True for hiding values, false for showing 
 */
synthbio.gui.updateSumSeries = function(val, hidden) {
	var sumSeries = synthbio.gui.plot.get("navseries");

	if (sumSeries && sumSeries.yData) {
		var newData = sumSeries.yData;

		if (hidden) {
			$.each(val, function(idx, v) {
				newData[idx] -= v;
			});
		} else {
			$.each(val, function(idx, v) {
				newData[idx] += v;
			});	
		}

		newData = synthbio.gui.roundSeries(newData);
		sumSeries.setData(newData);
	}
};

/**
 * Plot an array of series
 * @param series Array of output points ([{name: "name", data: [1, 2, 3, ..]}, ..])
 * @param timestep Timestep in seconds (defaults to 1).
 */
synthbio.gui.plotSeries = function(series, timestep) {
	timestep = timestep || 1;

	//Update the top-left range buttons to show better ranges
	var rangeButtons = $.map(synthbio.chartOptions.rangeSelector.buttons, function(val) {
		//Make a copy of the button object
		val = $.extend({}, val);

		if (val.count > 0) {
			val.count *= timestep;
			val.text = val.count + 's';
		}
		
		return val;
	});

	//Extend the default options with the series
	var options = $.extend(true, {}, synthbio.chartOptions, {
		series : series,
		navigator: {
			series: { data: synthbio.gui.calculateSumSeries(series) }
		},
		rangeSelector: {
			buttons: rangeButtons
		}
	});

	//Plot!
	synthbio.gui.plot = new Highcharts.StockChart(options);
};

/**
 * Plot simulation output
 * @param response Data object from synthbio.requests.simulate
 */
synthbio.gui.plotOutput = function(response) {
	var timestep = response.step || 1;

	//Map the series from the synthbio.requests.simulate output to Highcharts.Stockchart data
	var series = response.names.map(function(val) {
		return {
			type: 'spline',
			name: val,
			pointInterval: timestep,
			data: synthbio.gui.roundSeries(response.data[val])
		};
	});

	synthbio.gui.plotSeries(series, timestep);
};

/**
 * Setup options for Highcharts.StockChart 
 */
synthbio.chartOptions = {
	chart:   {renderTo: 'output-chart'},
	credits: {enabled: false},
	title:   {text: 'Simulation output'},
	loading: {style: { backgroundColor: 'silver' }},
	series:  [{data: [0, 0, 0, 0, 0]}]
};

//x-axis: Display the x value and add an "s" (data always starts at 0)
synthbio.chartOptions.xAxis = {
	minRange: 3,
	labels: {
		formatter: function() { return this.value + "s"; }
	}
};

//tooltip: Format the output for the tooltip (show time in seconds and all values)
synthbio.chartOptions.tooltip = {
	formatter: function() {
		//Current time
		var s = '<b>'+ this.x +' seconds</b>';

		//Protein values
		$.each(this.points, function(i, point) {
			s += "<br/>" + point.series.name;
			s += ": " + this.point.y.roundTo(synthbio.gui.plotPrecision);
		});
	
		return s;
	}
};

//navigator: Make sure the id is "navseries"
synthbio.chartOptions.navigator = {
	series: { id: "navseries", data: [0, 0, 0, 0, 0] },
	xAxis: synthbio.chartOptions.xAxis,
	top: 340
};

//legend: Show legend at the right
synthbio.chartOptions.legend = {
	enabled: true,
	borderWidth: 0,

	align: 'right',
	layout: 'vertical',
	verticalAlign: 'top',
	y: 75,
	shadow: true
};

//Update the sumseries in the navigator when a series is hidden or shown
synthbio.chartOptions.plotOptions = {
	series: {
		events: {
			show: function() {
				synthbio.gui.updateSumSeries(this.yData, false);
			},
			hide: function() {
				synthbio.gui.updateSumSeries(this.yData, true);
			}
		}
	}
};

//Setup the range selector (5s, 10s, 20s, all)
synthbio.chartOptions.rangeSelector = {
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
};

$(document).ready(function() {
	//Create an initial chart (in a loading state) as a stub
	synthbio.gui.plot = new Highcharts.StockChart(synthbio.chartOptions);
	synthbio.gui.plot.showLoading();

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
	 * Run simulation
	 */
	$('#simulate').on('click', function() {
		console.log('Simulate initiated.');
		synthbio.gui.plot.showLoading();
	
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
					$('#show-output').modal();
					synthbio.gui.plotOutput(response.data);
				}
			}
		);
		
	});
});
