function showImageThumbnail(fileInput) {
    var file = fileInput.files[0];
    var reader = new FileReader();
    reader.onload = function (e) {
        $('#imageView').attr("src", e.target.result);
    };

    reader.readAsDataURL(file);
}

$(document).ready(function (event) {

    $("#buttonCancel").on('click', function () {
        window.location = moduleURL;
    });

    $('#imageInput').on(
        'change',
        function (event) {
            var fileSize = this.files[0].size;
            if (!checkFileSize(this)) {
                return;
            }
            showImageThumbnail(this);

        });

});

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

function checkFileSize(fileInput) {
    fileSize = fileInput.files[0].size;
    if (fileSize > MAX_FILE_SIZE) {
        fileInput.setCustomValidity("You must choose an image less than 1MB!");
        fileInput.reportValidity();
        return false;
    } else {
        fileInput.setCustomValidity("");
        return true;
    }
}