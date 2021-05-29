package com.example.shopi.repositories;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.shopi.models.Product;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ShopRepo {

    private MutableLiveData<List<Product>> mutableProductList;
    DatabaseReference productDbRef;
    public LiveData<List<Product>> getProducts() {
        if (mutableProductList == null) {
            mutableProductList = new MutableLiveData<>();
            loadProducts();
        }
        return mutableProductList;
    }

    private void loadProducts() {

        productDbRef = FirebaseDatabase.getInstance().getReference("Product");
        List<Product> productList = new ArrayList<>();

        productDbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                productList.clear();
                for (DataSnapshot productDatasnap : snapshot.getChildren()){
                    String description = productDatasnap.child("productDescription").getValue(String.class);
                    String imageURL = productDatasnap.child("productImage").getValue(String.class);
                    String name = productDatasnap.child("productName").getValue(String.class);
                    Double price = Double.parseDouble(productDatasnap.child("productPrice").getValue(String.class));
                    productList.add(new Product(UUID.randomUUID().toString(),description,imageURL,name,price));
                }
                mutableProductList.setValue(productList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
          //productList.add(new Product("Likely new from March 2021","https://firebasestorage.googleapis.com/v0/b/notes-16738.appspot.com/o/products%2Fimac21.jpeg?alt=media&token=e1cf1537-ab30-44a3-91f1-4d6284e79540", "iMac 21", 1299.0 ));
//        productList.add(new Product(UUID.randomUUID().toString(), "iPad Air", 799, "https://firebasestorage.googleapis.com/v0/b/notes-16738.appspot.com/o/products%2Fipadair.jpeg?alt=media&token=da387155-bd8f-4343-954b-e46da7d252ae"));
//        productList.add(new Product(UUID.randomUUID().toString(), "iPad Pro", 999, "https://firebasestorage.googleapis.com/v0/b/notes-16738.appspot.com/o/products%2Fipadpro.jpeg?alt=media&token=5d433343-f3b3-43eb-8bf2-5298eb5bf11c"));
//        productList.add(new Product(UUID.randomUUID().toString(), "iPhone 11", 699, "https://firebasestorage.googleapis.com/v0/b/notes-16738.appspot.com/o/products%2Fiphone11.jpeg?alt=media&token=c6874af2-c81e-48eb-96e9-2f1f3fad617f"));
//        productList.add(new Product(UUID.randomUUID().toString(), "iPhone 11 Pro", 999, "https://firebasestorage.googleapis.com/v0/b/notes-16738.appspot.com/o/products%2Fiphone11pro.jpg?alt=media&token=c4547c4f-7a46-483d-80e5-8f1a93d96a03"));
//        productList.add(new Product(UUID.randomUUID().toString(), "iPhone 11 Pro Max", 1099, "https://firebasestorage.googleapis.com/v0/b/notes-16738.appspot.com/o/products%2Fiphone11promax.png?alt=media&token=109a89bd-e52b-4b76-91d4-5175aa516a23"));
//        productList.add(new Product(UUID.randomUUID().toString(), "iPhone SE", 399, "https://firebasestorage.googleapis.com/v0/b/notes-16738.appspot.com/o/products%2Fiphonese.jpeg?alt=media&token=8a3a144d-0cd8-4f6d-94cb-0d81634ea5d0"));
//        productList.add(new Product(UUID.randomUUID().toString(), "MacBook Air", 999, "https://firebasestorage.googleapis.com/v0/b/notes-16738.appspot.com/o/products%2Fmacbookair.jpeg?alt=media&token=aae96a4a-e86a-4a15-825a-3da9851330c8"));
//        productList.add(new Product(UUID.randomUUID().toString(), "MacBook Pro 13", 1299, "https://firebasestorage.googleapis.com/v0/b/notes-16738.appspot.com/o/products%2Fmbp13touch.jpeg?alt=media&token=88c2bf8e-e72d-4243-a9ab-4cc32e3aff18"));
//        productList.add(new Product(UUID.randomUUID().toString(), "MacBook Pro 16", 2399, "https://firebasestorage.googleapis.com/v0/b/notes-16738.appspot.com/o/products%2Fmbp16touch.jpeg?alt=media&token=24498b7f-09b8-42ea-9edb-1bad649902d4"));

    }
}
