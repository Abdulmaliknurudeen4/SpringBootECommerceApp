let confirmText;
let confirmModalDialog;
let yesButton;
let noButton;
let iconNames={
    'PICKED':'fa-people-carry',
    'SHIPPING':'fa-shipping-fast',
    'DELIVERED':'fa-box-open',
    'RETURNED':'fa-undo'
}

$(document).ready(function () {
    confirmText = $('#confirmText');
    confirmModalDialog = $('#confirmModal');
    yesButton = $('#yesButton');
    noButton = $('#noButton');
    $('.linkUpdateStatus').on('click', function (e) {
        e.preventDefault();
        let link = $(this);
        showUpdateConfirmModal(link);
    });

    addEventHandlerForYesButton();

});

function addEventHandlerForYesButton() {
    yesButton.on('click', function (e) {
        e.preventDefault();
        sendRequestToUpdateOrderStatus($(this));
        confirmModalDialog.modal('hide');
    });
}

function showUpdateConfirmModal(link) {
    noButton.text("NO");
    yesButton.show();
    let orderId = link.attr("orderId");
    let status = link.attr("status");
    yesButton.attr("href", link.attr("href"));

    confirmText.text("Are you sure you want to update status of the order ID #"
        + orderId + "  to " + status + "?");
    confirmModalDialog.modal();

}

function sendRequestToUpdateOrderStatus(button) {
    let requestUrl = button.attr("href");
    $.ajax({
        type: 'POST',
        url: requestUrl,
        beforeSend: function (xhr) {
            xhr.setRequestHeader(csrfHeaderName, csrfValue);
        }
    }).done(function (response) {
        showMessageModal("Order updated successfully");
        updateStatusIconColor(response.orderId, response.status);
    }).fail(function (err) {
        showMessageModal("Error updating order status");
    });
}

function showMessageModal(message) {
    noButton.text("Close");
    yesButton.hide();
    confirmText.text(message);
}
function updateStatusIconColor(orderId, status){
    link = $('#link'+status+orderId);
    link.replaceWith("<i class='fa "+iconNames[status] +" fa-2x icon-green'></i>");
}