$(document).ready(function () {
    $('#products').on('click', '#linkAddProduct', function (e) {
        e.preventDefault();
        let link = $(this);
        let url = link.attr("href");

        $('#addProductModal').on('shown.bs.modal', function () {
            $(this).find("iframe").attr("src", url);
        });

        $('#addProductModal').modal();
    });
});

function getProductInfo(productId, shippingCost) {
    let requestURL = contextPath + "products/get/" + productId;
    $.get(requestURL, function (productJSON) {
        let productName = productJSON.name;
        let mainImagePathURL = productJSON.imagePath;
        let mainImagePath =(contextPath.substring(0, contextPath.length-1)+mainImagePathURL)
        let productCost = $.number(productJSON.cost, 2);
        let productPrice = $.number(productJSON.price, 2);

        let htmlCode = generateProductCode(productId, productName, mainImagePath, productCost, productPrice, shippingCost);
        $('#productList').append(htmlCode);
    }).fail(function (err) {
        showWarningModal(err.responseJSON.message);
    })
}

function generateProductCode(productId, productName, mainImagePath,
                             productCost, productPrice, shippingCost) {
    let nextCount = $('.hiddenProductId').length + 1;
    let quantityId = "quantity" + nextCount;
    let priceId = "price" + nextCount;
    let subtotalId = "subtotal" + nextCount;
    let hmtlCode = `
   <div class="border rounded p-1" >
                <input type="hidden" name="productId" value="${productId}" class="hiddenProductId" />
                <div class="row">
                    <div class="col-1">
                        <div>${nextCount}</div>
                    </div>
                    <div class="col-3">
                        <img src="${mainImagePath}" class="img-fluid">
                    </div>
                </div>

                <div class="row m-2">
                    <b>${productName}</b>
                </div>

                <div class="row m-2">
                    <table>
                        <tr>
                            <td>Product Cost: </td>
                            <td>
                                <input type="text"
                                       required
                                       class="form-control m-1 cost-input"
                                       rowNumber="${nextCount}"
                                       value="${productCost}"
                                       style="max-width: 140px" />
                            </td>
                        </tr>

                        <tr>
                            <td>Quantity:</td>
                            <td>
                                <input type="number"
                                       class="form-control m-1 quantity-input"
                                       id="${quantityId}"
                                       rowNumber="${nextCount}"
                                       value="1"
                                       style="max-width: 140px" />
                            </td>
                        </tr>

                        <tr>
                            <td>Unit Price :</td>
                            <td>
                                <input type="text" class="form-control m-1 price-input"
                                       id="${priceId}"
                                       rowNumber="${nextCount}"
                                       value="${productPrice}" style="max-width: 140px" />
                            </td>
                        </tr>

                        <tr>
                            <td>Subtotal :</td>
                            <td>
                                <input type="text"
                                       readonly="readonly"
                                       class="form-control m-1 subtotal-output"
                                       id="${subtotalId}"
                                       value="${productPrice}" style="max-wid 140px" />
                            </td>
                        </tr>

                        <tr>
                            <td>Shipping Cost:</td>
                            <td>
                                <input type="text" class="form-control m-1 ship-input"
                                       value="${shippingCost}" style="max-wid 140px" />
                            </td>
                        </tr>



                    </table>
                </div>

            </div>
            <div class="row">&nbsp;</div>
`;
    return hmtlCode;
}

function isProductAlreadyAdded(productId) {
    let productExists = false;
    $('.hiddenProductId').each(function (e) {
        let aProductId = $(this).val();
        if (aProductId === productId) {
            productExists = true;

        }
    });
    return productExists;
}

function addProduct(productId, productName) {
    getShippingCost(productId);
}

function getShippingCost(productId) {
    let selectedCountry = $("#country option:selected");
    let countryId = selectedCountry.val();

    let state = $('#state').val();
    if (state.length === 0) {
        state = $('#city').val();
    }

    let requestUrl = contextPath + "get_shipping_cost";
    let params = {productId: productId, countryId: countryId, state: state};

    $.ajax({
        type: 'POST',
        url: requestUrl,
        beforeSend: function (xhr) {
            xhr.setRequestHeader(csrfHeaderName, csrfValue);
        },
        data: params,
        contentType: 'application/json'
    }).done(function (shippingCost) {
        getProductInfo(productId, shippingCost);
    }).fail(function (err) {
        showWarningModal(err.responseJSON.message);
        let shippingCost = 0.0;
        getProductInfo(productId, shippingCost);
    }).always(function () {
        $('#addProductModal').modal("hide");
    });
}