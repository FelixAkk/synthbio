// GUI JavaScript Document

$(document).ready(function() {
	
	/* Activate zhe Dropdowns Herr Doktor! */
	$('.dropdown-toggle').dropdown();
	
	//~ $('.modal').modal('hide');

	/**
	 * Load proteins from server.
	 */
	$('#list-proteins').on('show', function(){
		$.ajax({
			url: "/ListProteins",
			dataType: "json",
			success: function(response){
				//on failure, display error message
				if(!response.success){
					$('#list-proteins tbody td').html(data.response);
					return;
				}
				
				//clear the table...
				$('#list-proteins tbody').html('');
				
				//...and insert rows.
				for(i in response.data){
					row=response.data[i];
					rowhtml='<tr>'+
						'<td>'+row.name+'</td>'+
						'<td>'+row.k2+'</td>'+
						'<td>'+row.d1+'</td>'+
						'<td>'+row.d2+'</td></tr>';
					
					$('#list-proteins tbody').append(rowhtml);
				}
				
			}
		});
			
	});
});
