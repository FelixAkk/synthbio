// GUI JavaScript Document

$(document).ready(function() {
	
	/**
	 * Activate zhe Dropdowns Herr Doktor!
	 */
	$('.dropdown-toggle').dropdown();
	
	/**
	 * Load proteins from server.
	 */
	$('#list-proteins').on('show', function(){
		synthbio.requests.getCDSs(function(response){
			if(response instanceof String){
				$('#list-proteins tbody td').html(data.response);
				return;
			}
			var html='';
			$.each(response, function(i, cds){
				html+='<tr><td>'+cds.name+'</td><td>'+cds.k2+'</td><td>'+cds.d1+'</td><td>'+cds.d2+'</td></tr>';
			});
			$('#list-proteins tbody').html(html);
			
		});
			
	});

	function pingServer() {
		$.ajax("/Ping")
			.done(function(data) { $('#gatesStatus').html('Connected to server &#10003;'); })
			.fail(function(data) { $('#gatesStatus').html('Warning: not connected to server!'); })
			.always(function() { setTimeout(pingServer, 3000); });
	}

	setTimeout(pingServer, 500);
});
