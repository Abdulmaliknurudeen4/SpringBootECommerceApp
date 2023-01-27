function showImageThumbnail(fileInput) {
    var file = fileInput.files[0];
    var reader = new FileReader();
    reader.onload = function (e) {
        $('#imageView').attr("src", e.target.result);
    };

    reader.readAsDataURL(file);
}

$(document).ready(function (event) {

    $("#buttonCancel").on('click', function() {
        window.location = moduleURL;
    });

    $('#imageInput')
        .on(
            'change',
            function (event) {
                var image = this.files[0];
                var fileSize = this.files[0].size;
                if (fileSize > 1048576) {
                    this
                        .setCustomValidity("You must choose an image less than 1MB!");
                    showModalDialog("Error",
                        "You must choose an image less than 1MB!");
                    this.reportValidity();
                } else {
                    this.setCustomValidity("");
                    showImageThumbnail(this);
                }

            });

})