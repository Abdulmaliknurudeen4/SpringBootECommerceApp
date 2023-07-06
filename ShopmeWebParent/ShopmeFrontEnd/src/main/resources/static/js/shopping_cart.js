$(document).ready(function () {

    $(".linkMinus").on('click', function (event) {
        event.preventDefault($(this));
        decreaseQuantity($(this));
    });

    $(".linkPlus").on('click', function (event) {
        event.preventDefault($(this));
        increaseQuantity($(this));
    });
});

function decreaseQuantity(link) {
    // get product Id
    productId = link.attr("pid");
    quantityInput = $("#quantity" + productId);
    newQuantity = parseInt(quantityInput.val()) - 1;
    // check if new quantity is less than 1
    if (newQuantity > 0) {
        quantityInput.val(newQuantity);
        updateQuantity(productId, newQuantity);
    } else {
        showWarningModal("Mininum Quantity is 1");
    }
}

function increaseQuantity(link) {
    // get product Id
    productId = link.attr("pid");
    quantityInput = $("#quantity" + productId);
    newQuantity = parseInt(quantityInput.val()) + 1;
    // check if new quantity is less than 1
    if (newQuantity <= 5) {
        quantityInput.val(newQuantity);
        updateQuantity(productId, newQuantity);
    } else {
        showWarningModal("Maximum Quantity is 5");
    }
}

function updateQuantity(productId, quantity) {
    quantity = $('#quantity' + productId).val();
    url = contextPath + "cart/update/" + productId + "/" + quantity;

    $.ajax({
        type: 'POST',
        url: url,
        beforeSend: function (xhr) {
            xhr.setRequestHeader(csrfHeaderName, csrfValue);
        }
    }).done((updatedSubtotal) => {
        updateSubtotal(updatedSubtotal, productId);
    }).fail((response) => {
        showErrorModal("Error While updating product to shopping cart.");
    });
}

function updateSubtotal(updatedSubtotal, productId) {

    $('#subtotal' + productId).text(formatNumbertoX(updatedSubtotal, 2));

    updateTotal();
}


// Select all spans with class "h4" and id starting with "subtotal"
var subtotalSpans = $("span.h4[id^='subtotal']");

// Loop through the selected spans
subtotalSpans.each(function () {
    // Do something with each span
    console.log($(this).text());
});


function updateTotal() {
    total = 0.0;
    $('span.h4[id^=\'subtotal\']').each((index, element) => {
        total += parseFloat(element.innerHTML.replaceAll(",", ""));
    });
    $("#estimatedTotal").text(formatNumbertoX(total, 2));

}

function formatNumbertoX(text, places) {
    const updatedSub = parseFloat(text);
    let formattedNumber = updatedSub.toFixed(places);
    formattedNumber = formattedNumber.replace(/\.?0+$/, '');
    return formattedNumber;
}