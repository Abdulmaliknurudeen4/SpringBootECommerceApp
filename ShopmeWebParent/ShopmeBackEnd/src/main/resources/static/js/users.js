$(document).ready(function() {
	$('.link-delete')
		.on(
			'click',
			function(event) {
				event.preventDefault();
				link = $(this);
				showModalDialog("Are you Sure you want to Delete the User with the id ?");
				$('.confirm-dialog')
					.on(
						'click',
						function() {
							$(this)
								.attr(
									'href',
									link
										.attr('href'));
						});

			});
});
function showModalDialog(message) {
	$('#modalBody').text(message);
	$('#modalDialog').modal();
}