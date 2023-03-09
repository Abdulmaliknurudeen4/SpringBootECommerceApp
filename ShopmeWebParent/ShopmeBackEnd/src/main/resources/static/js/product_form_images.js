var extraImagesCount = 0;

$(document).ready(function () {
    $("input[name='extraImage']").each(function (index) {
        extraImagesCount++;
        $(this).change(function () {
            if (!checkFileSize(this)) {
                return;
            }
            showExtraImageThumbnail(this, index);
        });
    });

    $(".linkRemoveExtraImage").each(function (index) {
        $(this).click(() => {
            removeExtraImage(index);
        });
    });

});

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

function showExtraImageThumbnail(fileInput, index) {
    var file = fileInput.files[0];
    var reader = new FileReader();
    reader.onload = function (e) {
        $('#extraThumbnail' + index).attr("src", e.target.result);
    };
    reader.readAsDataURL(file);
    if (index >= extraImagesCount - 1) {
        addNextExtraImageSection(index + 1);
    }
}

function addNextExtraImageSection(index) {
    htmlExtraImage = `
    <div class="col border m-3 p-2" id="divExtraImage${index}">
            <div id="extraImageHeader${index}"><label >Extra Image #${index + 1}: </label></div>
            <div class="m-2"><img id="extraThumbnail${index}" alt="Extra Image #${index + 1} preview"
                                  class="img-fluid" src="${defaultImageThumbnailSrc}"></div>
            <div><input type="file" name="extraImage"
                        accept="image/png, image/jpeg"
                        onchange="showExtraImageThumbnail(this, ${index})"
                        id="extraImage">
            </div>
        </div>
    `;
    htmlLinkRemove = `
    <a class="btn fas fa-times-circle fa-4x icon-dark float-right" 
    title="Remove this image" href="javascript:void(0)" onclick="removeExtraImage(${index - 1})"></a>
    `;
    $("#divProductImages").append(htmlExtraImage);
    $("#extraImageHeader" + (index - 1)).append(htmlLinkRemove);
    extraImagesCount++;
}

function removeExtraImage(index) {
    $("#divExtraImage" + index).remove();
}

var longDesc = new RichTextEditor('#longDesc');
var shortDesc = new RichTextEditor('#shortDesc');