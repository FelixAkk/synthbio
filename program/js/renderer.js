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
			// default drag options
			DragOptions : { cursor: 'pointer', zIndex:2000 },
			// the overlays to decorate each connection with.  note that the label overlay uses a function to generate the label text; in this
			// case it returns the 'labelText' member that we set on each connection in the 'init' method below.
			ConnectionOverlays : [
				[ "Arrow", { location:0.9 } ],
				[ "Label", { 
					location:0.5,
					id:"label"
				}]
			]
		});			

		// this is the paint style for the connecting lines..
		var connectorPaintStyle = {
			lineWidth:3,
			strokeStyle:"#deea18",
			joinstyle:"round"
		},
		// .. and this is the hover style. 
		connectorHoverStyle = {
			lineWidth:5,
			strokeStyle:"#2e2aF8"
		},
		// the definition of source endpoints (the small blue ones)
		sourceEndpoint = {
			endpoint:"Dot",
			paintStyle:{ fillStyle:"#225588",radius:7 },
			isSource:true,
			connector:[ "Flowchart", { stub:40 } ],
			connectorStyle:connectorPaintStyle,
			hoverPaintStyle:connectorHoverStyle,
			connectorHoverStyle:connectorHoverStyle,
			dragOptions:{}
		},
		// the definition of target endpoints (will appear when the user drags a connection) 
		targetEndpoint = {
			endpoint:"Rectangle",					
			paintStyle:{ fillStyle:"#558822",width:10,height:10 },
			hoverPaintStyle:connectorHoverStyle,
			maxConnections:-1,
			dropOptions:{ hoverClass:"hover", activeClass:"active" },
			isTarget:true
		};			

		var allSourceEndpoints = [], allTargetEndpoints = [];
		_addEndpoints = function(toId, sourceAnchors, targetAnchors) {
			for (var i = 0; i < sourceAnchors.length; i++) {
				var sourceUUID = toId + sourceAnchors[i];
				allSourceEndpoints.push(jsPlumb.addEndpoint(toId, sourceEndpoint, { anchor:sourceAnchors[i], uuid:sourceUUID }));
			}
			for (var j = 0; j < targetAnchors.length; j++) {
				var targetUUID = toId + targetAnchors[j];
				allTargetEndpoints.push(jsPlumb.addEndpoint(toId, targetEndpoint, { anchor:targetAnchors[j], uuid:targetUUID }));
			}
		};

		// listen for new connections; initialise them the same way we initialise the connections at startup.
		jsPlumb.bind("jsPlumbConnection", function(connInfo, originalEvent) { 
			connInfo.connection.getOverlay("label").setLabel("I am a banana");
		});

		_addEndpoints("hoi1", ["RightMiddle"], ["TopLeft", "BottomLeft"]);
		_addEndpoints("hoi2", ["TopRight", "BottomRight"], ["LeftMiddle"]);

		// connect a few up
		jsPlumb.connect({uuids:["hoi2TopRight", "hoi1TopLeft"]});
		jsPlumb.connect({uuids:["hoi2BottomRight", "hoi1BottomLeft"]});
	});
});