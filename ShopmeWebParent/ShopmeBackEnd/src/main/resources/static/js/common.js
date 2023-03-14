$(document).ready(function(){
    $('#logoutLink').on('click', function(e){
        e.preventDefault();
        document.logoutForm.submit();
    });
    customizeDropdownHover();
});
function customizeDropdownHover() {
    $('.navbar .dropdown').hover(
        function(){
            $(this).find('.dropdown-menu').first().stop(true,true).delay(250).slideDown();
        }, function () {
            $(this).find('.dropdown-menu').first().stop(true,true).delay(100).slideUp();
    });
   /* $('#userDetail').click(function (){
        location.href = this.href;
    });*/
    // JavaScript Code for All Nav Element click
    $('.navdrop-element').click(function (){
        location.href = this.href;
    });
}
function checkPasswordMatch(confirmPassword){
    if(confirmPassword.value != $('#password').val()){
        confirmPassword.setCustomValidity("Passwords do not match!");
    }else{
        confirmPassword.setCustomValidity("");
    }
}