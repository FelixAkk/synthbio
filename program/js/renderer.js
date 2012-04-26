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


		jsPlumb.Defaults.Container = $("#grid-container");
		jsPlumb.setRenderMode(jsPlumb.SVG);

		//document.onselectstart = function(){return false;};	
		

		jsPlumb.draggable($("#grid-container .gate"));

		var fillColor = "gray"; 
		jsPlumb.Defaults.Connector = [ "Bezier", { curviness:50 } ];
		jsPlumb.Defaults.DragOptions = { cursor: "pointer", zIndex:2000 };
		jsPlumb.Defaults.PaintStyle = { strokeStyle:"gray", lineWidth:2 };
		jsPlumb.Defaults.EndpointStyle = { radius:9, fillStyle:"gray" };
		jsPlumb.Defaults.Anchors =  [ "BottomCenter", "TopCenter" ];

		// declare some common values:
		var arrowCommon = { foldback:0.7, fillStyle:fillColor, width:14 };
		// use three-arg spec to create two different arrows with the common values:
		var overlays = [
			[ "Arrow", { location:0.5 }, arrowCommon ]
		];

		jsPlumb.connect({source:"hoi1", target:"hoi2", overlays:overlays});
	});
});