package org.example.service;


import org.example.domain.checkout.CartItem;

import static org.example.util.CommonUtil.delay;

public class PriceValidatorService {

    public boolean isCartItemInvalid(CartItem cartItem) {
        int cartId = cartItem.getItemId();
        delay(500);
        return cartId == 7 || cartId == 9 || cartId == 11;
    }
}
