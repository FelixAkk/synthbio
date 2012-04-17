// GUI JavaScript Document

/**
 * Ping server to check for connection 'vitals'. Shown a warning if things go really bad.
 */
var fCount = 0; // Failure count: The amount of times that connection attempts have failed. Resets to 0 on success.
var limit = 3; // Amount of times after which a dialog should prompt the user about the failures.
var d = new Date();
function pingServer() {
	var t = d.getTime();
	$.ajax("/Ping")
		.done(function(data) {
			$('#ping').html('Server status: <b>Connected to server <em class="icon-connected"></em> [latency: ' + (d.getTime() - t) + 'ms]</b>');
			fCount = 0;
		})
		.fail(function(data) {
			$('#ping').html('Server status: <b>Warning: not connected to server! <em class="icon-failed"></em></b>');
			$('#ping').attr('class', 'failed');
			if(fCount  <= limit+1) fCount++; // Keep counting untill the dialog was shown
			if(fCount == limit) $('#connection-modal').modal(); // Show the dialog once
		})
		.always(function() { setTimeout(pingServer, 3000); });
}

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


	setTimeout(pingServer, 500);
});
