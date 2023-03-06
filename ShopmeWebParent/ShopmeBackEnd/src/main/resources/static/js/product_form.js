dropDownBrand = $('#brandSelect');
categorySelect = $('#categorySelect');
var extraImagesCount = 0;

$(document).ready(function () {
    dropDownBrand.change(function () {
        categorySelect.empty();
        categorySelect.prop('disabled', false);
        getCategories();
    });
    getCategories();

    $("input[name='extraImage']").each(function (index) {
        extraImagesCount++;
        $(this).change(function () {
            showExtraImageThumbnail(this, index);
        });
    });

});

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

function getCategories() {
    brandId = dropDownBrand.val();
    url = brandModuleURL + "/listCategoriesByBrand/" + brandId

    $.get(url, function (responseJson) {
        $.each(responseJson, function (index, category) {
            $("<option>").val(category.id).text(category.name).appendTo(categorySelect);
        });
    });

}

var longDesc = new RichTextEditor('#longDesc');
var shortDesc = new RichTextEditor('#shortDesc');

function checkIsProductUnique(form) {

    var url = "[[@{/products/check_name}]]";
    var name = $("#nameInput").val();
    var csrfValue = $("input[name='_csrf']").val();
    var productId = $("#id").val();
    params = {
        id: productId,
        name: name,
        _csrf: csrfValue,
    };

    $.post(
        url,
        params,
        function (response) {
            if (response === "OK") {
                form.submit();
            } else if (response === "Duplicated") {
                showErrorModal("There is another Product Having the Same Name: " + name);
            } else {
                showErrorModal("Unknown response from Server");
            }

        }).fail(function () {
        showErrorModal("Could not Connect to Server.");
    });
    return false;
}
