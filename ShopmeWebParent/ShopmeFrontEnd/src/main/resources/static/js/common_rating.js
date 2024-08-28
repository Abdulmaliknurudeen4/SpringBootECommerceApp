//Common code for product reviews/rating
$(document).ready(function(){
    formatRatingNumber();
});
function formatRatingNumber(){
    ratingText = $('#ratingNumber').text();
    formattedRating = $.number(ratingText, 2, decimalSeperator, thousandSeperator);
    $('#ratingNumber').text(formattedRating);

}
$(".product-detail-rating-star").rating({
    displayOnly: true,
    hoverOnClera: false,
    showCaption: false,
    theme: 'krajee-svg'
});