var buttonLoad;
var dropDownCountry;
var toastMessenger;
var addButton;
var updateButton;
var deleteButton;
var labelCountryName;
var fieldCountryName;
var fieldCountryCode;
$(document).ready(function () {
    buttonLoad = $('#buttonLoadCountries');
    dropDownCountry = $('#dropDownCountries');
    toastMessenger = $('#toastMessage');
    addButton = $('#addButton');
    updateButton = $('#updateButton');
    deleteButton = $('#deleteButton');
    labelCountryName = $('#labelCountryName');
    fieldCountryName = $('#fieldCountryName');
    fieldCountryCode = $('#fieldCountryCode');

    buttonLoad.click(function () {
        loadCountries();
    });

    dropDownCountry.on('change', function () {
        changeFormStateToSelectedCountry();
    });

    addButton.click(function () {
        if (addButton.val() == "Add") {
            addCountry();
        } else {
            changeFormStateToNew();
        }
    });

    updateButton.click(function () {
        updateCountry();
    });

    deleteButton.click(function () {
        deleteCountry();
    });
});

function deleteCountry() {
    let countryId = dropDownCountry.val().split("-")[0];
    let url = contextPath + "countries/delete/" + countryId;
    $.ajax({
        type: 'GET',
        url: url,
        beforeSend: function (xhr) {
            xhr.setRequestHeader(csrfHeaderName, csrfValue);
        },
        contentType: 'application/json'
    }).done(function (countryID) {
        changeFormStateToNew();
        loadCountries();
        showToastMessage("The country has been Deleted.");
    }).fail(function () {
        showToastMessage("ERROR: Could not connect to server or server encountered an error");
    });
}

function updateCountry() {
    let url = contextPath + "countries/save";
    let countryName = fieldCountryName.val();
    let countryCode = fieldCountryCode.val();
    let countryId = dropDownCountry.val().split("-")[0];
    let jsonData = {id: countryId, name: countryName, code: countryCode};

    $.ajax({
        type: 'POST',
        url: url,
        beforeSend: function (xhr) {
            xhr.setRequestHeader(csrfHeaderName, csrfValue);
        },
        data: JSON.stringify(jsonData),
        contentType: 'application/json'
    }).done(function (countryID) {
        $('#dropDownCountries option:selected').val(countryId + "-" + countryCode);
        $('#dropDownCountries option:selected').text(countryName);
        changeFormStateToNew();
        showToastMessage("The country has been Updated.");
    }).fail(function () {
        showToastMessage("ERROR: Could not connect to server or server encountered an error");
    });
}

function addCountry() {
    let url = contextPath + "countries/save";
    let countryName = fieldCountryName.val();
    let countryCode = fieldCountryCode.val();
    let jsonData = {name: countryName, code: countryCode};

    $.ajax({
        type: 'POST',
        url: url,
        beforeSend: function (xhr) {
            xhr.setRequestHeader(csrfHeaderName, csrfValue);
        },
        data: JSON.stringify(jsonData),
        contentType: 'application/json'
    }).done(function (countryID) {
        showToastMessage("The new country has beeen added.");
        selectNewlyAddedCountry(countryID, countryCode, countryName);
    }).fail(function () {
        showToastMessage("ERROR: Could not connect to server or server encountered an error");
    });
}

function selectNewlyAddedCountry(countryId, countryCode, CountryName) {
    loadCountries();
    let optionValue = countryid + "-" + countryCode;
    $("<option>").val(optionValue).text(CountryName).appendTo(dropDownCountry);

    $("#dropDownCountries option[value='" + optionValue + "']").prop("selected", true);

    fieldCountryCode.val("");
    fieldCountryName.val().focus();
}

function changeFormStateToNew() {
    addButton.val("Add");
    labelCountryName.text("Country Name: ");

    updateButton.prop('disabled', true);
    deleteButton.prop('disabled', true);

    fieldCountryCode.val("");
    fieldCountryName.val("").focus();
}

function loadCountries() {
    let url = contextPath + "countries/list";
    $.get(url, function (responseJSON) {
        dropDownCountry.empty();

        $.each(responseJSON, (index, country) => {
            optionValue = country.id + "-" + country.code;
            $("<option>").val(optionValue).text(country.name).appendTo(dropDownCountry);
        });
    }).done(function () {
        buttonLoad.val("Refresh Country List");
        showToastMessage("All Countries have been loaded.");
    }).fail(function () {
        showToastMessage("ERROR: Could not connect to server or server encountered an error");
    });
}

function showToastMessage(message) {
    toastMessenger.text(message);
    // $('.toast').toast('show');
    $('.toast').prop('toast', show);
}

function changeFormStateToSelectedCountry() {
    addButton.prop("value", "New");
    deleteButton.prop("disabled", false);
    updateButton.prop("disabled", false);

    labelCountryName.text("Selected Country: ");
    let selectedCountryName = $('#dropDownCountries option:selected').text();
    fieldCountryName.val(selectedCountryName);

    let countryCode = dropDownCountry.val().split("-")[1];
    fieldCountryCode.val(countryCode);


}