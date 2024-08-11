package com.shopme.checkout.paypal;

import org.junit.jupiter.api.Test;
import org.springframework.http.*;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;


public class PayPalApiTests {
    private static final String BASE_URL = "https://api-m.sandbox.paypal.com";
    private static final String GET_ORDER_API = "/v2/checkout/orders/";
    private static final String CLIENT_ID = "AWFpcybbz3_lDW4iVYwKgGSTa9kpCKw1TSnfHz_y3w2F6E9a7pXWXY6YQ1Y9Q7PyCM78hmTNE_Iy9t1T";
    private static final String CLIENT_SECRET = "ECLEeJuYKYvBY09nNNbD_nTwUMGEYcjqdqh5UZJrO2yz7dk1Zc-2tU_TKJiyQ9ajdG3ujj2B2Sd6KvJg";

    @Test
    public void testGetOrderDetails(){
        String orderId = "3VH37296617428340";
        String requestURL = BASE_URL + GET_ORDER_API + orderId;

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.add("Accept-Language", "en_US");
        headers.setBasicAuth(CLIENT_ID, CLIENT_SECRET);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(headers);
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<PayPalOrderResponse> response = restTemplate.exchange(requestURL, HttpMethod.GET, request, PayPalOrderResponse.class);
        PayPalOrderResponse orderResponse = response.getBody();

        System.out.println("Order ID " + orderResponse.getId());
        System.out.println("Validate:  " + orderResponse.getStatus());

        System.out.println(response);
    }
}
