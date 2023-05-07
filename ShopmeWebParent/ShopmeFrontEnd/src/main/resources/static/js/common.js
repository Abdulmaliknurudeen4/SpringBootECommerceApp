var passwordInput;
$(document).ready(function () {
    passwordInput = $('#password');
});

function checkPasswordMatch(confirmPassword) {
    if (confirmPassword.value !== passwordInput.val()) {
        confirmPassword.setCustomValidity("Passwords do not match!");
    } else {
        confirmPassword.setCustomValidity("");
    }
}

function showModalDialog(title, message) {
    $('#modalTitle').text(title);
    $('#modalBody').text(message);
    $('#modalDialog').modal();
}

function showErrorModal(message) {
    showModalDialog("Error", message);
}

function showWarningModal(message) {
    showModalDialog("Warning", message);
}