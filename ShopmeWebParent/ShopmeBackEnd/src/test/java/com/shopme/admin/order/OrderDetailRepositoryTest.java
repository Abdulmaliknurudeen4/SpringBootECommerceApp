package com.shopme.admin.order;

import com.shopme.entity.order.Order;
import com.shopme.entity.order.OrderDetail;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
public class OrderDetailRepositoryTest {

    @Autowired private OrderDetailRepository repository;

    @Test
    public void testFindWithCategoryAndTimeBetween() throws ParseException{
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        Date startTime = dateFormatter.parse("2021-08-01");
        Date endTime = dateFormatter.parse("2021-08-31");

        List<OrderDetail> listOrderDetails = repository.findWithCategoryAndTimeBetween(startTime, endTime);

        assertThat(listOrderDetails.size()).isGreaterThan(0);

        for(OrderDetail order: listOrderDetails){
            System.out.printf("%-30s | %d | %10.2f \t | %10.2f | %10.2f \n",
                    order.getProduct().getCategory().getName(),
                    order.getQuality(),
                    order.getProductCost(),
                    order.getShippingCost(), order.getSubtotal());
        }
    }

    @Test
    public void testFindWithProductAndTimeBetween() throws ParseException{
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        Date startTime = dateFormatter.parse("2021-10-01");
        Date endTime = dateFormatter.parse("2021-10-31");

        List<OrderDetail> listOrderDetails = repository.findWithProductAndTimeBetween(startTime, endTime);

        assertThat(listOrderDetails.size()).isGreaterThan(0);

        for(OrderDetail order: listOrderDetails){
            System.out.printf("%-30s | %d | %10.2f \t | %10.2f | %10.2f \n",
                    order.getProduct().getName(),
                    order.getQuality(),
                    order.getProductCost(),
                    order.getShippingCost(), order.getSubtotal());
        }
    }
}
