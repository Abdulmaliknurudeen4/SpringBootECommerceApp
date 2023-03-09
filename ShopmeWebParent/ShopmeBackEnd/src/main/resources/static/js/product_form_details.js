function addNextDetailSection() {
    let allDivDetails = $("[id^='divDetail']");
    let divDetailsCount = allDivDetails.length;
    let previousDivDetailSection = allDivDetails.last();
    let previousDivDetailID = previousDivDetailSection.attr('id');
    let htmlLinkRemove = `
    <a class="btn fas fa-times-circle fa-4x icon-dark float-right" 
    title="Remove this Detail" href="javascript:removeDetailsSectionById(${previousDivDetailID})" onclick="removeDetailsSectionById(${previousDivDetailID})"></a>
    `;
    let html = `
 <div class="form-inline" id="divDetail${divDetailsCount}">
        <label class="m-3">Name</label>
        <input type="text" name="detailNames" maxlength="255" class="form-control w-25">
        <label class="m-3">Value</label>
        <input type="text" name="detailValues" maxlength="255" class="form-control w-25">
    </div>
 
 `
    $('#divProductDetails').append(html);
    previousDivDetailSection.append(htmlLinkRemove);
}

function removeDetailsSectionById(id) {
    $(id).remove();
}

$(document).ready(function () {
    $('.linkRemoveExtraDetails').each(function (index) {
        $(this).click(() => {
            removeDetailsSectionById('#divDetail' + index);
        });
    });
});

