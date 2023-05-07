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

function fetchStateIntoSuggestion() {
    var selectedCountry = $('#dropDownCountry option:selected');
    var url = "settings/list_state_by_country/" + selectedCountry.val();
    $.get(url, (responseJson) => {
        dataListState.empty();
        $.each(responseJson, (index, state) => {
            $("<option>").val(state.name).text(state.name)
                .appendTo(dataListState);
        });
    });
}

function checkEmailUnique(form) {
    var url = contextPath + "settings/check_customer_email";
    var customerEmail = $("#email").val();
    var csrfValue = $("input[name='_csrf']").val();
    params = {
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
                showWarningModal("There is another User having the Email"
                    + customerEmail)
            } else {
                showErrorModal("Unknown response from Server")
            }

        }).fail(function () {
        showErrorModal("Could not Connect to Server.");
    });

    return false;
}