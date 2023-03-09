dropDownBrand = $('#brandSelect');
categorySelect = $('#categorySelect');
$(document).ready(function () {
    dropDownBrand.change(function () {
        categorySelect.empty();
        categorySelect.prop('disabled', false);
        getCategories();
    });
    getCategoriesForNewForm();

});
function getCategoriesForNewForm(){
    let catIdField = $("#categoryId");
    let editMode = false;
    if(catIdField.length){
        editMode = true;
    }
    if(!editMode) getCategories();
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
function checkIsProductUnique(form) {
    var name = $("#nameInput").val();
    var csrfValue = $("input[name='_csrf']").val();
    var productId = $("#id").val();
    params = {
        id: productId,
        name: name,
        _csrf: csrfValue,
    };

    $.post(
        checkUniqueURL,
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
