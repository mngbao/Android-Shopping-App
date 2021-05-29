package com.example.shopi.views;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.shopi.R;
import com.example.shopi.databinding.FragmentAddProductBinding;
import com.example.shopi.models.Product;
import com.example.shopi.viewmodels.ShopViewModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.UUID;


public class AddProductFragment extends Fragment {

    private static final String TAG = "addProductFragment";

    FragmentAddProductBinding fragmentAddProductBinding;
    ShopViewModel shopViewModel;
    NavController navController;
    private EditText name, price, description;
    private ImageView image;
    private Button button;

    private static final int CAMERA_RESQUEST_CODE = 200;
    private static final int STORAGE_RESQUEST_CODE = 300;

    private static final int IMAGE_PICK_GALLLERY_CODE = 400;
    private static final int IMAGE_PICK_CAMERA_CODE = 500;

    private String[] cameraPermission;
    private String[] storagePersmission;
    private Uri image_uri;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference reference;
    private StorageReference storageReference;
    private ProgressDialog progressDialog;
    private String randomKey, productName, productDescription;
    private float productPrice;

    public AddProductFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentAddProductBinding = FragmentAddProductBinding.inflate(inflater, container, false);
        return fragmentAddProductBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);
        shopViewModel = new ViewModelProvider(requireActivity()).get(ShopViewModel.class);

        name = (EditText) getView().findViewById(R.id.Add_Product_Name);
        price = (EditText) getView().findViewById(R.id.Add_Product_Price);
        description = (EditText) getView().findViewById(R.id.Add_Product_Description);
        image = (ImageView) getView().findViewById(R.id.imageView);

        Product product = new Product();
        reference = FirebaseDatabase.getInstance().getReference().child("Product");

        button = (Button) getView().findViewById(R.id.button);


//        progressDialog = new ProgressDialog(getContext());
//        progressDialog.setTitle("Please wait");
//        progressDialog.setCanceledOnTouchOutside(false);

        cameraPermission = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePersmission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        image.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, 2);
            }
        });

        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                inputData();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2 && resultCode == Activity.RESULT_OK && data != null) {
            image_uri = data.getData();
            image.setImageURI(image_uri);

        }
    }

    private void inputData() {
        final String randomKey = UUID.randomUUID().toString();
        String productName = name.getText().toString().trim();
        Float productPrice = Float.parseFloat((price.getText().toString().trim()));
        String productDescription = description.getText().toString().trim();

        if(TextUtils.isEmpty(productName)){
            Toast.makeText(getContext(),"Product title is required", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(price.getText().toString().trim())){
            Toast.makeText(getContext(),"Product title is required", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(productDescription)){
            Toast.makeText(getContext(),"Product title is required", Toast.LENGTH_SHORT).show();
            return;
        }
        addData();
    }
    private void addData(){
        storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference filePath = storageReference.child("image/" + randomKey);
        filePath.putFile(image_uri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isSuccessful()) ;
                        Uri downloadImageUri = uriTask.getResult();
                        if (uriTask.isSuccessful()) {
                            productName = name.getText().toString().trim();
                            productPrice = Float.parseFloat((price.getText().toString().trim()));
                            productDescription = description.getText().toString().trim();

                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("productName", "" + productName);
                            hashMap.put("productPrice", "" + productPrice);
                            hashMap.put("productDescription", "" + productDescription);
                            hashMap.put("productImage", "" + downloadImageUri);

                            reference.push().setValue(hashMap);
                            Toast.makeText(getContext(), "Your product has been successfully added", Toast.LENGTH_SHORT).show();
                            name.setText("");
                            price.setText("");
                            description.setText("");
                            image.setImageDrawable(getResources().getDrawable(R.drawable.drop_image));
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Failed to upload", Toast.LENGTH_SHORT).show();
                    }
                });
    }




    private void pickFromGallery(){

        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent,IMAGE_PICK_GALLLERY_CODE);
    }

    private void pickFromCamera(){
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.TITLE, "Temp_Image_Title");
        contentValues.put(MediaStore.Images.Media.DESCRIPTION, "Temp_Image_Description");

        image_uri = getContext().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(intent,IMAGE_PICK_CAMERA_CODE);
    }

    private boolean checkStoragePermission(){
        boolean result = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result;

    }
    private boolean checkCameraPermission(){
        boolean result = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);

        boolean result1 = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        return result && result1;
    }

    private void requestCameraPermission(){
        ActivityCompat.requestPermissions(getActivity(),cameraPermission,CAMERA_RESQUEST_CODE);
    }

    private void requestStoragePermission(){
        ActivityCompat.requestPermissions(getActivity(),storagePersmission,STORAGE_RESQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[]grantResults){
        switch (requestCode){
            case CAMERA_RESQUEST_CODE: {
                if (grantResults.length > 0) {
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean storageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted && storageAccepted){
                        pickFromCamera();
                    }
                    else {
                        Toast.makeText(getActivity(),"Camera & Storage Permission are required...", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            case STORAGE_RESQUEST_CODE: {
                if (grantResults.length>0){
                    boolean storageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (storageAccepted) {
                        pickFromGallery();
                    }
                    else {
                        Toast.makeText(getActivity(),"Storage permission is required...",Toast.LENGTH_SHORT).show();
                    }
                }

            }
        }
        super.onRequestPermissionsResult(requestCode,permissions,grantResults);
    }

}
