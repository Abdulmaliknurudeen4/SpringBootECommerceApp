$(document).ready(()=>{
    $('#buttonAdd2Cart').on('click', addToCart);
});

function addToCart(){

    quantity = $('#quantity'+productId).val();
    url = contextPath + "cart/add/" + productId + "/" + quantity;

    $.ajax({
        type: 'POST',
        url: url,
        beforeSend: function(xhr) {
            xhr.setRequestHeader(csrfHeaderName, csrfValue);
        }
    }).done((response)=>{
        showModalDialog("Shopping Cart", response);
    }).fail((response)=>{
        showErrorModal(response.responseText);
    });
}