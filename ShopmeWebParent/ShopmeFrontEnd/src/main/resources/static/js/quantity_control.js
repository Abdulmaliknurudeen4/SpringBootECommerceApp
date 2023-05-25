$(document).ready(function() {

    $(".linkMinus").on('click', function(event) {
        event.preventDefault();
        // get product Id
        productId = $(this).attr("pid");
        quantityInput = $("#quantity" + productId);
        newQuantity = parseInt(quantityInput.val()) - 1;
        // check if new quantity is less than 1
        if(newQuantity > 0){
            quantityInput.val(newQuantity);
        }else{
            showErrorModal("Mininum Quantity is 1");
        }
    });

    $(".linkPlus").on('click', function(event) {
        event.preventDefault();
        // get product Id
        productId = $(this).attr("pid");
        quantityInput = $("#quantity" + productId);
        newQuantity = parseInt(quantityInput.val()) + 1;
        // check if new quantity is less than 1
        if(newQuantity <= 5){
            quantityInput.val(newQuantity);
        }else{
            showErrorModal("Maximum Quantity is 5");
        }
    });
})