let returnModal;
let modalTitle;
let fieldNote;
let orderId;
let divReason;
let divMessage;
let firstButton;
let secondButton;

$(document).ready(() => {
    returnModal = $('#returnOrderModal');
    modalTitle = $('#returnOrderModalTitle');
    fieldNote = $('#returnNote');
    divReason = $('#divReason');
    divMessage = $('#divMessage');
    firstButton = $('#firstButton');
    secondButton = $('#secondButton');
    handleReturnOrderLink();


});

function showReturnModalDialog(link) {
    divMessage.hide();
    divReason.show();
    firstButton.show();
    secondButton.prop('value', 'Cancel');
    fieldNote.val("");

    orderId = link.attr("orderId");
    returnModal.modal("show");
    modalTitle.text("Return Order ID #" + orderId);
}

function showMessageModal(message) {
    divReason.hide();
    firstButton.hide();
    secondButton.prop('value', "Close");
    divMessage.text(message);

    divMessage.show();
}

function handleReturnOrderLink() {
    $('.linkReturnOrder').on('click', function (e) {
        e.preventDefault();
        showReturnModalDialog($(this));
    });
}

function submitReturnOrderForm() {
    let reason = $("input[name='returnReason']:checked").val();
    let note = fieldNote.val();

    sendReturnOrderRequest(reason, note);
    return false;
}

function sendReturnOrderRequest(reason, note) {
    let requestURL = contextPath + "orders/return";
    let requestBody = {orderId: orderId, reason: reason, note: note};
    $.ajax({
        type: 'POST',
        url: requestURL,
        data: JSON.stringify(requestBody),
        contentType: "application/json",
        beforeSend: function (xhr) {
            xhr.setRequestHeader(csrfHeaderName, csrfValue);
        }
    }).done((returnResponse) => {
        showMessageModal("Return Request has been sent");
    }).fail((error) => {
        showMessageModal(error.responseText);
        updateStatusTextAndHideReturnButton(orderId);
    });


}

function updateStatusTextAndHideReturnButton(orderId) {
    $('.textOrderStatus' + orderId).each(function (index) {
        $(this).text("RETURN_REQUESTED");
    });

    $('.linkReturn' + orderId).each(function (index) {
        $(this).hide();
    });

}