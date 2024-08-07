function showDeleteConfirmModal(element, path) {
    let productDeletePath = element.attr("href");
    let parts = productDeletePath.split("/");
    let productId = parts[parts.length - 1];
    showModalDialog("Are you Sure you want to Delete the Order with the id " + productId);
    $('.confirm-dialog').attr('href', productDeletePath);

}
function showModalDialog(message) {
    $('#modalBody').text(message);
    $('#modalDialog').modal();
}