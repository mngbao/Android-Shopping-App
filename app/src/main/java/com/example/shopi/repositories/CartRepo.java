package com.example.shopi.repositories;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.shopi.models.CartItems;
import com.example.shopi.models.Product;

import java.util.ArrayList;
import java.util.List;

public class CartRepo {

    private MutableLiveData<List<CartItems>> mutableCart = new MutableLiveData<>();
    private MutableLiveData<Double> mutableTotalPrice = new MutableLiveData<>();

    public LiveData<List<CartItems>> getCart() {
        if (mutableCart.getValue() == null) {
            initCart();
        }
        return mutableCart;
    }

    public void initCart() {
        mutableCart.setValue(new ArrayList<CartItems>());
        calculateCartTotal();
    }

    public boolean addItemToCart(Product product) {
        if (mutableCart.getValue() == null) {
            initCart();
        }
        List<CartItems> cartItemList = new ArrayList<>(mutableCart.getValue());
        for (CartItems cartItem: cartItemList) {
            if (cartItem.getProduct().getId().equals(product.getId())) {
                if (cartItem.getQuantity() == 5) {
                    return false;
                }

                int index = cartItemList.indexOf(cartItem);
                cartItem.setQuantity(cartItem.getQuantity() + 1);
                cartItemList.set(index, cartItem);

                mutableCart.setValue(cartItemList);
                calculateCartTotal();
                return true;
            }
        }
        CartItems cartItem = new CartItems(product, 1);
        cartItemList.add(cartItem);
        mutableCart.setValue(cartItemList);
        calculateCartTotal();
        return true;
    }

    public void removeItemFromCart(CartItems cartItem) {
        if (mutableCart.getValue() == null) {
            return;
        }
        List<CartItems> cartItemList = new ArrayList<>(mutableCart.getValue());
        cartItemList.remove(cartItem);
        mutableCart.setValue(cartItemList);
        calculateCartTotal();
    }

    public  void changeQuantity(CartItems cartItem, int quantity) {
        if (mutableCart.getValue() == null) return;

        List<CartItems> cartItemList = new ArrayList<>(mutableCart.getValue());

        CartItems updatedItem = new CartItems(cartItem.getProduct(), quantity);
        cartItemList.set(cartItemList.indexOf(cartItem), updatedItem);

        mutableCart.setValue(cartItemList);
        calculateCartTotal();
    }

    private void calculateCartTotal() {
        if (mutableCart.getValue() == null) return;
        double total = 0.0;
        List<CartItems> cartItemList = mutableCart.getValue();
        for (CartItems cartItem: cartItemList) {
            total += cartItem.getProduct().getPrice() * cartItem.getQuantity();
        }
        mutableTotalPrice.setValue(total);
    }

    public LiveData<Double> getTotalPrice() {
        if (mutableTotalPrice.getValue() == null) {
            mutableTotalPrice.setValue(0.0);
        }
        return mutableTotalPrice;
    }

}
