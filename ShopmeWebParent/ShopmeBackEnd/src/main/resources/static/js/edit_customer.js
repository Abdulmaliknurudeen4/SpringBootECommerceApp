var dropDownCountry;
var dataListState;
var fieldState;
$(document).ready(function () {
    dropDownCountry = $('#dropDownCountry');
    dataListState = $('#listState');
    fieldState = $('#fieldState');

    dropDownCountry.on('change', () => {
        fetchStateIntoSuggestion();
        fieldState.val("").focus();
    });
});

//Fetch into State not working...
function fetchStateIntoSuggestion() {
    var selectedCountry = $('#dropDownCountry option:selected');
    var url = moduleURL + "/list_state_by_country/" + selectedCountry.val();
    console.log(url);
    $.get(url, (responseJson) => {
        dataListState.empty();
        $.each(responseJson, (index, state) => {
            $("<option>").val(state.name).text(state.name)
                .appendTo(dataListState);
        });
    });
}

function checkEmailUnique(form) {

    var url = moduleURL + "/check_email";
    var customerEmail = $("#email").val();
    var csrfValue = $("input[name='_csrf']").val();
    var customerId = $("#id").val();
    params = {
        id: customerId,
        email: customerEmail,
        _csrf: csrfValue
    };

    $.post(
        url,
        params,
        function (response) {
            if (response === "OK") {
                form.submit();
            } else if (response === "Duplicated") {
                showWarningModal("There is another Customer having the Email"
                    + customerEmail)
            } else {
                showErrorModal("Unknown response from Server")
            }

        }).fail(function () {
        showErrorModal("Could not Connect to Server.");
    });

    return false;
}