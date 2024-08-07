package com.shopme.cart;

import com.shopme.entity.CartItem;
import com.shopme.entity.Customer;
import com.shopme.entity.product.Product;
import com.shopme.product.ProductRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class ShoppingCartService {
    @Autowired
    private CartItemRepository cartItemRepository;
    @Autowired
    private ProductRepository productRepository;

    public Integer addProduct(Integer productId, Integer quantity, Customer customer) throws ShoppingCartException {
        Integer updatedQuantity = quantity;
        CartItem cartItem = cartItemRepository.findByCustomerAndProduct(customer, new Product(productId));
        if(cartItem != null){
            updatedQuantity = cartItem.getQuantity() + quantity;
            if(updatedQuantity > 5){
                throw new ShoppingCartException("You can not add more than 5 items of this product");
            }
        }else{
            cartItem = new CartItem();
            cartItem.setCustomer(customer);
            cartItem.setQuantity(quantity);
            cartItem.setProduct(new Product(productId));
        }
        cartItem.setQuantity(updatedQuantity);
        cartItemRepository.save(cartItem);
        return quantity;
    }


    public List<CartItem> listCartItems(Customer customer){
        return cartItemRepository.findByCustomer(customer);
    }

    public float updateQuantity(Integer productId, Integer quantity, Customer customer){
        cartItemRepository.updateQuantity(quantity,  productId, customer.getId());
        Product product = productRepository.findById(productId);
        if(product!=null){
            return product.getDiscountPrice() * quantity;
        }
        return 0;
    }

    public void removeProduct(Integer productId, Customer customer){
        cartItemRepository.deleteByCustomerAndProduct(productId,customer.getId() );
    }
}
