	$(document).ready(function() {

			$("#buttonCancel").on('click', function() {
				window.location = "[[@{/users}]]";
			});

		});

		function checkEmailUnique(form) {

			var url = "[[@{/users/check_email}]]";
			var userEmail = $("#email").val();
			var csrfValue = $("input[name='_csrf']").val();
			var userId = $("#id").val();
			params = {
				id : userId,
				email : userEmail,
				_csrf : csrfValue
			};

			$.post(
					url,
					params,
					function(response) {
						if (response == "OK") {
							form.submit();
						} else if (response == "Duplicated") {
							showModalDialog("Warning!",
									"There is another User having the Email"
											+ userEmail);
						} else {
							showModalDialog("Error",
									"Unknown response from Server");
						}

					}).fail(function() {
				showModalDialog("Error", "Could not Connect to Server.");
			});

			return false;
		}

		function showModalDialog(title, message) {
			$('#modalTitle').text(title);
			$('#modalBody').text(message);
			$('#modalDialog').modal();
		}