package com.shopme.admin.order;

import com.shopme.admin.paging.PagingAndSortingHelper;
import com.shopme.admin.setting.country.CountryRepository;
import com.shopme.entity.Country;
import com.shopme.entity.order.Order;
import com.shopme.entity.order.OrderNotFoundException;
import com.shopme.entity.order.OrderStatus;
import com.shopme.entity.order.OrderTrack;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class OrderService {
    private static final int ORDERS_PER_PAGE = 10;

    @Autowired
    private OrderRepository repo;
    @Autowired
    private CountryRepository countryRepository;
    @Autowired
    private OrderRepository orderRepository;

    public void listByPage(int pageNum, PagingAndSortingHelper helper) {
        String sortField = helper.getSortField();
        String sortDir = helper.getSortDir();
        String keyword = helper.getKeyword();

        Sort sort = null;

        if ("destination".equals(sortField)) {
            sort = Sort.by("country").and(Sort.by("state")).and(Sort.by("city"));
        } else {
            sort = Sort.by(sortField);
        }

        sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();
        Pageable pageable = PageRequest.of(pageNum - 1, ORDERS_PER_PAGE, sort);

        Page<Order> page = null;

        if (keyword != null) {
            page = repo.findAll(keyword, pageable);
        } else {
            page = repo.findAll(pageable);
        }

        helper.udpateModelAttributes(pageNum, page);

    }

    public Order get(Integer id) throws OrderNotFoundException {
        try {
            Order order = repo.findById(id).get();
            return order;
        } catch (NoSuchElementException e) {
            throw new OrderNotFoundException("could not find any orders with ID " + id);
        }
    }

    public void delete(Integer id) throws OrderNotFoundException {
        Long count = repo.countById(id);
        if (count == null || count == 0) {
            throw new OrderNotFoundException("Could not find any orders with ID: " + id);
        }
        repo.deleteById(id);
    }

    public List<Country> listAllCountries(){
        return countryRepository.findAllByOrderByNameAsc();
    }

    public void save(Order orderInForm) {
        Order orderInDB = orderRepository.findById(orderInForm.getId()).get();
        orderInForm.setOrderTime(orderInDB.getOrderTime());
        orderInForm.setCustomer(orderInDB.getCustomer());

        orderRepository.save(orderInForm);
    }

    public void updateStatus(Integer orderId, String status){
        Order orderInDB = orderRepository.findById(orderId).get();
        OrderStatus statusToUpdate = OrderStatus.valueOf(status);

        if(!orderInDB.hasStatus(statusToUpdate)){
            List<OrderTrack> orderTracks = orderInDB.getOrderTracks();

            OrderTrack track = new OrderTrack();
            track.setOrder(orderInDB);
            track.setStatus(statusToUpdate);
            track.setUpdatedTime(new Date());
            track.setNotes(statusToUpdate.defaultDescription());

            orderTracks.add(track);
            orderInDB.setStatus(statusToUpdate);
            orderRepository.save(orderInDB);
        }
    }
}
