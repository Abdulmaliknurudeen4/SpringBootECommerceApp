decimalSeperator = decimalPointType === 'COMMA' ? ',' : '.';
thousandSeperator = thousandPointType === 'COMMA' ? ',' : '.';
$(document).ready(function () {

    $(".linkMinus").on('click', function (event) {
        event.preventDefault($(this));
        decreaseQuantity($(this));
    });

    $(".linkPlus").on('click', function (event) {
        event.preventDefault($(this));
        increaseQuantity($(this));
    });

    $(".linkRemove").on('click', function (event) {
        event.preventDefault($(this));
        removeProduct($(this));
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

    $('#subtotal' + productId).text(formatCurrency(updatedSubtotal));

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
    productCount = 0;
    $('span.h4[id^=\'subtotal\']').each((index, element) => {
        productCount++;
        total += parseFloat(clearCurrencyFormat(element.innerHTML));
    });
    if(productCount < 1){
        showEmptyShoppingCart();
    }else{
        $("#estimatedTotal").text(formatCurrency(total));
    }

}

function formatNumbertoX(text, places) {
    const updatedSub = parseFloat(text);
    let formattedNumber = updatedSub.toFixed(places);
    formattedNumber = formattedNumber.replace(/\.?0+$/, '');
    return formattedNumber;
}

function removeProduct(link){
    url = link.attr("href");
    rowNumber = link.attr("rowNumber");
    $.ajax({
        type: 'DELETE',
        url: url,
        beforeSend: function (xhr) {
            xhr.setRequestHeader(csrfHeaderName, csrfValue);
        }
    }).done((response) => {
       removeProductHTML(rowNumber);
       updateTotal();
       updateCountNumbers();
       showModalDialog("Shopping Cart", response);
    }).fail((response) => {
        showErrorModal("Error While removing product from shoppping cart.");
    });
}

function removeProductHTML(rowNumber){
    $('#row'+rowNumber).remove();
}
function updateCountNumbers(){
    $(".divCount").each((index,element)=>{
        element.innerHTML = ""+(index+1);
    });
}

function showEmptyShoppingCart(){
    $('#sectionTotal').hide();
    $('#sectionEmptyCartMessage').removeClass('d-none');
}

function formatCurrency(amount){
    return $.number(amount, decimalDigits, decimalSeperator, thousandSeperator);
}
function clearCurrencyFormat(numberString){
    result = numberString.replaceAll(thousandSeperator, "");
    return result.replaceAll(decimalSeperator, ".");
}