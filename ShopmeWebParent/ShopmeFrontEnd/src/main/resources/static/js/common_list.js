function showDeleteConfirmModal(element, path) {
    let productDeletePath = element.attr("href");
    let parts = productDeletePath.split("/");
    let productId = parts[parts.length - 1];
    showModalDialog("Are you Sure you want to Delete the " + path + " with the id " + productId);
    $('.confirm-dialog').attr('href', productDeletePath);

}

function clearFilter() {
    window.location = moduleURL;
}

function showModalDialog(message) {
    $('#modalBody').text(message);
    $('#modalDialog').modal();
}

function handleDetailLinkClick(cssClass, modalId) {
    $(cssClass).on('click', function (e) {
        e.preventDefault();
        let linkDetailURL = $(this).attr('href');
        $(modalId).modal("show").find('.modal-content').load(linkDetailURL);
    });
}