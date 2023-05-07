var loadCountries_state;
var loadCountriesListDropDown;
var listStateViewPanels;
var stateNameLabel;
var fieldStateName;
var addStateButton;
var updateStateButton;
var deleteStateButton;
$(document).ready(function () {
    loadCountries_state = $('#loadCountriesForStateBtn');
    loadCountriesListDropDown = $('#dropDownCountriesList');
    listStateViewPanels = $('#listStateViewPanels');
    stateNameLabel = $('#stateNameLabel');
    fieldStateName = $('#fieldStateName');
    addStateButton = $('#addStateButton');
    updateStateButton = $('#updateStateButton');
    deleteStateButton = $('#deleteStateButton');

    // Load Countries into the Country DropDown List
    loadCountries_state.click(function () {
        loadCountries_statePgn();
    });

    // On DropDown Country Change, load States for the selected Country
    loadCountriesListDropDown.on('change', function () {
        loadStatesForSelectedCountry();
    });

    // on State Selected, load the State into the form.
    listStateViewPanels.on('change', function () {
        changeFormStateToSelectedState();
    });

    //For Add State to Country
    addStateButton.click(function () {
        if (addStateButton.val() === "Add") {
            addState();
        } else {
            changeFormStateToNewForState();
        }
    });

    updateStateButton.click(function () {
        updateState();
    });

    deleteStateButton.click(function () {
        deleteState();
    });
});

function deleteState() {
    let selectedState = $('#listStateViewPanels option:selected');
    let stateId = selectedState.val();
    let url = contextPath + "states/delete/" + stateId;

    $.ajax({
        type: 'GET',
        url: url,
        beforeSend: function (xhr) {
            xhr.setRequestHeader(csrfHeaderName, csrfValue);
        },
        contentType: 'application/json'
    }).done(function (message) {
        changeFormStateToNewForState();
        loadStatesForSelectedCountry();
        showToastMessage("The state has been Deleted.");
    }).fail(function () {
        showToastMessage("ERROR: Could not connect to server or server encountered an error");
    });
}

function updateState() {
    if (!checkFormValidity("statesForm")) return;
    let selectedState = $('#listStateViewPanels option:selected');
    let url = contextPath + "states/save";
    let stateId = selectedState.val();
    let stateName = fieldStateName.val();
    let countryId = loadCountriesListDropDown.val().split("-")[0];
    let countryName = loadCountriesListDropDown.val().split("-")[1];

    let jsonData = {
        id: stateId,
        name: stateName,
        country: {
            id: countryId,
            name: countryName
        }
    };

    $.ajax({
        type: 'POST',
        url: url,
        beforeSend: function (xhr) {
            xhr.setRequestHeader(csrfHeaderName, csrfValue);
        },
        data: JSON.stringify(jsonData),
        contentType: 'application/json'
    }).done(function () {
        showToastMessage("The State has been updated ");
        selectNewlyAddedState(stateId);
    }).fail(function () {
        showToastMessage("ERROR: Could not connect to server or server encountered an error");
    });

}

function changeFormStateToNewForState() {
    addStateButton.val("Add");
    labelCountryName.text("State/Province Name: ");

    updateStateButton.prop('disabled', true);
    deleteStateButton.prop('disabled', true);

    fieldStateName.val("").focus();

}

function addState() {
    if (!checkFormValidity("statesForm")) return;
    let url = contextPath + "states/save";
    let stateName = fieldStateName.val();
    let countryId = loadCountriesListDropDown.val().split("-")[0];
    let countryName = loadCountriesListDropDown.val().split("-")[1];


    let jsonData = {
        name: stateName,
        country: {
            id: countryId,
            name: countryName
        }
    };


    $.ajax({
        type: 'POST',
        url: url,
        beforeSend: function (xhr) {
            xhr.setRequestHeader(csrfHeaderName, csrfValue);
        },
        data: JSON.stringify(jsonData),
        contentType: 'application/json'
    }).done(function (stateId) {
        showToastMessage("The new State has been added to the list of States available in " + countryName);
        selectNewlyAddedState(stateId);
    }).fail(function () {
        showToastMessage("ERROR: Could not connect to server or server encountered an error");
    });
}

function selectNewlyAddedState(stateId) {
    let stateName = fieldStateName.val();
    fieldStateName.val("").focus();
    loadStatesForSelectedCountry();
    $("#listStateViewPanels option[value='" + stateId + "']").prop("selected", true);

}


function changeFormStateToSelectedState() {
    addStateButton.prop("value", "New");
    updateStateButton.prop("disabled", false);
    deleteStateButton.prop("disabled", false);

    stateNameLabel.text("Selected State : ");
    let selectedStateName = $('#listStateViewPanels option:selected');
    fieldStateName.val(selectedStateName.text());

}

function loadStatesForSelectedCountry() {
    let countryId = loadCountriesListDropDown.val().split("-")[0];
    let countryName = loadCountriesListDropDown.val().split("-")[1];
    let url = contextPath + "states/list_by_country/" + countryId;

    $.ajax({
        type: 'GET',
        url: url,
        beforeSend: function (xhr) {
            xhr.setRequestHeader(csrfHeaderName, csrfValue);
        },
        contentType: 'application/json'
    }).done(function (states) {
        listStateViewPanels.empty();
        $.each(states, function (index, state) {
            listStateViewPanels.append("<option value='" + state.id + "'>" + state.name + "</option>");
        });
        showToastMessage("All States Belong to the " + countryName + " have been loaded.");
    }).fail(function () {
        showToastMessage("ERROR: Could not connect to server or server encountered an error");
    });

}

function loadCountries_statePgn() {
    let url = contextPath + "countries/list";
    $.ajax({
        type: 'GET',
        url: url,
        beforeSend: function (xhr) {
            xhr.setRequestHeader(csrfHeaderName, csrfValue);
        },
        contentType: 'application/json'
    }).done(function (countries) {

        loadCountriesListDropDown.empty();
        $.each(countries, function (index, country) {
            loadCountriesListDropDown.append("<option value='" + country.id + "-" + country.name + "'>" + country.name + "</option>");
        });
        loadCountries_state.val("Refresh Country List");
        listStateViewPanels.empty();
        changeFormStateToNewForState();
        showToastMessage("All Countries have been loaded.");
    }).fail(function () {
        showToastMessage("ERROR: Could not connect to server or server encountered an error");
    });
}
