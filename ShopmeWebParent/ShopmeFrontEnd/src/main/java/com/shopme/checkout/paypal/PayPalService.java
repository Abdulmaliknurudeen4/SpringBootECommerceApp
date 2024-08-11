package com.shopme.checkout.paypal;

import com.shopme.settings.PaymentSettingBag;
import com.shopme.settings.SettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@Component
public class PayPalService {

    @Autowired
    private SettingService settingService;
    private static final String GET_ORDER_API = "/v2/checkout/orders/";


    public boolean validateOrder(String orderId) throws PayPalApiException{
        PayPalOrderResponse orderResponse = getOrderDetails(orderId);
        assert orderResponse != null;
        return orderResponse.validate(orderId);
    }

    private PayPalOrderResponse getOrderDetails(String orderId) throws PayPalApiException {
        ResponseEntity<PayPalOrderResponse> response = makeRequest(orderId);
        HttpStatusCode statusCode = response.getStatusCode();

        if(!statusCode.equals(HttpStatus.OK)){
            throwExceptionForNonOkResponse(statusCode);
        }
        return response.getBody();
    }

    private ResponseEntity<PayPalOrderResponse> makeRequest(String orderId) {
        PaymentSettingBag paymentSetting = settingService.getPaymentSettings();
        String baseURL = paymentSetting.getURL();
        String requestURL = baseURL + GET_ORDER_API + orderId;
        String CLIENT_ID = paymentSetting.getClientID();
        String CLIENT_SECRET = paymentSetting.getClientSecret();

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.add("Accept-Language", "en_US");
        headers.setBasicAuth(CLIENT_ID, CLIENT_SECRET);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(headers);
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.exchange(requestURL, HttpMethod.GET,
                request, PayPalOrderResponse.class);
    }

    private static void throwExceptionForNonOkResponse(HttpStatusCode statusCode) throws PayPalApiException {
        String message = null;
        switch(HttpStatus.valueOf(statusCode.value())){
            case NOT_FOUND:
                message = "Order ID not found";
            case BAD_REQUEST:
                message ="Bad Request to PayPal Checkout API";
            case INTERNAL_SERVER_ERROR:
                message = "PayPal server error";
            default:
                message = "PayPal returned non-OK status Code";
        }
        throw new PayPalApiException(message);
    }
}
