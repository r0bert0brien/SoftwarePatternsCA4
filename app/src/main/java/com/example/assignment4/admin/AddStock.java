    package com.example.assignment4.admin;

    import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

    import android.app.Activity;
    import android.app.Dialog;
    import android.content.Intent;
    import android.net.Uri;
    import android.os.Bundle;

    import androidx.annotation.NonNull;
    import androidx.annotation.Nullable;
    import androidx.fragment.app.Fragment;

    import android.util.Log;
    import android.view.LayoutInflater;
    import android.view.View;
    import android.view.ViewGroup;
    import android.widget.ArrayAdapter;
    import android.widget.AutoCompleteTextView;
    import android.widget.Button;
    import android.widget.EditText;
    import android.widget.ImageButton;
    import android.widget.ImageView;

    import com.example.assignment4.R;
    import com.example.assignment4.entity.Product;
    import com.google.firebase.database.DataSnapshot;
    import com.google.firebase.database.DatabaseError;
    import com.google.firebase.database.DatabaseReference;
    import com.google.firebase.database.FirebaseDatabase;
    import com.google.firebase.database.ValueEventListener;
    import com.google.firebase.storage.FirebaseStorage;
    import com.google.firebase.storage.StorageReference;
    import com.squareup.picasso.Picasso;

    import java.util.ArrayList;
    public class AddStock extends Fragment {

        private static final int PICK_IMAGE_REQUEST = 0;
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://rob-ca2-default-rtdb.europe-west1.firebasedatabase.app/");
        String email, downloadUrl;
        EditText title, manufacturer, price;
        AutoCompleteTextView category;
        ImageView imageView;
        Button chooseImage, addStock;
        ArrayAdapter<String> arrayAdapter;
        ArrayList<String> productCategories = new ArrayList<>();
        public AddStock(String email){
            this.email = email;
        }
        public AddStock() {
            // Required empty public constructor
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_add_stock, container, false);
            populateCategorySuggestions();
            imageView = view.findViewById(R.id.imageView);
            title = view.findViewById(R.id.title);
            manufacturer = view.findViewById(R.id.manufacturer);
            price = view.findViewById(R.id.price);
            category = view.findViewById(R.id.category);
            arrayAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, productCategories);
            category.setAdapter(arrayAdapter);
            chooseImage = view.findViewById(R.id.chooseImageButton);
            addStock = view.findViewById(R.id.addStock);

            addStock.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DatabaseReference newProductRef = databaseReference.child("product").push();
                    newProductRef.setValue(new Product(newProductRef.getKey(),title.getText().toString().trim(),
                            manufacturer.getText().toString().trim(), price.getText().toString().trim(), category.getText().toString().trim(), String.valueOf(downloadUrl), 0));
                    clearAllFields();
                }
            });

            chooseImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openPictureChooser();
                }
            });

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Dialog dialog = new Dialog(getContext());
                    dialog.setContentView(R.layout.dialog_image_preview);
                    ImageView imageViewPreview = dialog.findViewById(R.id.imageViewPreview);
                    Picasso.get().load(downloadUrl).into(imageViewPreview);
                    dialog.show();
                }
            });

            return view;
        }

        public void clearAllFields(){
            downloadUrl = "";
            title.setText("");
            manufacturer.setText("");
            price.setText("");
            category.setText("");
        }

        private void populateCategorySuggestions(){
            databaseReference.child("product").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                            String potentialCategory = dataSnapshot.child("category").getValue(String.class);
                            Log.d(TAG, potentialCategory);
                            if (potentialCategory != null && !productCategories.contains(potentialCategory)) {
                                productCategories.add(potentialCategory);
                            }
                        }
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }
        private void openPictureChooser() {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
        }
        @Override
        public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
                Uri selectedImageUri = data.getData();
                StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("productImages");
                StorageReference imageRef = storageRef.child("image" + System.currentTimeMillis() + ".jpg");
                imageRef.putFile(selectedImageUri)
                        .addOnSuccessListener(taskSnapshot -> {
                            imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                                downloadUrl = uri.toString();
                                Log.d(TAG, "Download URL: " + downloadUrl);
                                Picasso.get().load(downloadUrl).into(imageView);
                            });
                        })
                        .addOnFailureListener(e -> {
                            Log.e(TAG, "Image upload failed.", e);
                        });
            }
        }

    }