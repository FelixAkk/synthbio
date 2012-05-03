// GUI JavaScript Document
/**
 * 		Project Zelula:
 * Synthetic biology modeller/simulator
 * https://github.com/FelixAkk/synthbio
 * by Group E, TU Delft
 *
 * @author Felix Akkermans & Jan-Pieter Waagmeester
 */

$(document).ready(function() {

	jsPlumb.ready(function() {

		jsPlumb.setRenderMode(jsPlumb.SVG);
		
		jsPlumb.Defaults.Container = $("#grid-container");
		jsPlumb.draggable($("#grid-container .gate"));

		jsPlumb.importDefaults({
			DragOptions : { cursor: 'pointer', zIndex:2000 },
			ConnectionOverlays : [
				[ "Arrow", { location:0.9 } ],
				[ "Label", { 
					location:0.5,
					id:"label",
					cssClass:"arrowLabel"
				}]
			]
		});			

		// this is the paint style for the connecting lines..
		var pointHoverStyle = {
			lineWidth:3,
			strokeStyle:"purple"
		},
		connectorPaintStyle = {
			lineWidth:3,
			strokeStyle:"#deea18",
			joinstyle:"round"
		},
		// .. and this is the hover style. 
		connectorHoverStyle = {
			lineWidth:5,
			strokeStyle:"#2e2aF8"
		},
		// the definition of input endpoints
		inputEndpoint = {
			endpoint:"Rectangle",					
			paintStyle:{ fillStyle:"#558822",width:10,height:10 },
			hoverPaintStyle:pointHoverStyle,
			isTarget:true,
			dropOptions: {
				activeClass: 'dragActive',
				hoverClass: 'dragHover'
			}
		},
		// the definition of target endpoints
		outputEndpoint = {
			endpoint:"Dot",
			paintStyle:{ fillStyle:"#225588",radius:7 },
			connector:[ "Flowchart", { stub:40 } ],
			connectorStyle:connectorPaintStyle,
			hoverPaintStyle:pointHoverStyle,
			connectorHoverStyle:connectorHoverStyle,
			isSource:true
		};	

		var allSourceEndpoints = [], allTargetEndpoints = [],
		_addEndpoints = function(toId, inputAnchors, outputAnchors) {
			inputAnchors--;
			outputAnchors--;
			
			var placement = function(num, total) {
				return (total < 1) ? 0.5 : (num / total);
			}

			for (var j = 0; j <= inputAnchors; j++) {
				var targetUUID = toId + "_input" + j;
				allTargetEndpoints.push(jsPlumb.addEndpoint(toId, inputEndpoint, { anchor:[0, placement(j, inputAnchors), -1, 0], uuid:targetUUID }));
			}
			for (var i = 0; i <= outputAnchors; i++) {
				var sourceUUID = toId + "_output" + i;
				allSourceEndpoints.push(jsPlumb.addEndpoint(toId, outputEndpoint, { anchor:[1, placement(i, outputAnchors), 1, 0], uuid:sourceUUID }));
			}
		};

		// listen for new connections; initialise them the same way we initialise the connections at startup.
		jsPlumb.bind("jsPlumbConnection", function(connInfo, originalEvent) { 
			connInfo.connection.getOverlay("label").setLabel("I am a signal");
		});

		_addEndpoints("hoi1", 1, 2);
		_addEndpoints("hoi2", 2, 1);

		// connect a few up
		jsPlumb.connect({uuids:["hoi2_output0", "hoi1_input0"]});
		//jsPlumb.connect({uuids:["hoi2BottomRight", "hoi1BottomLeft"]});
	});
});