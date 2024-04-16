package com.example.assignment4.login;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.assignment4.R;
import com.example.assignment4.entity.FirebaseSingleton;
import com.example.assignment4.entity.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.List;

public class Login extends AppCompatActivity {

    DatabaseReference databaseReference = FirebaseSingleton.getDatabaseReference();
    FirebaseAuth mAuth;
    Button buttonLogin;
    EditText email, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        buttonLogin = findViewById(R.id.buttonLogin);
        email = findViewById(R.id.editTextEmail);
        password = findViewById(R.id.editTextPassword);

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ProgressBar progressBar = findViewById(R.id.progressBar);
                progressBar.setVisibility(View.VISIBLE);
                String emailValue = email.getText().toString().trim();
                String passwordValue = password.getText().toString().trim();

                DatabaseReference usersRef = databaseReference.child("users").child(emailValue.replace(".", "-"));
                usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            boolean isAdmin = dataSnapshot.child("admin").getValue(Boolean.class);
                            if (isAdmin) {
                                AuthenticationStrategy adminAuth = new AdminAuthentication(Login.this, mAuth);
                                adminAuth.authenticate(emailValue, passwordValue);
                            } else {
                                AuthenticationStrategy customerAuth = new CustomerAuthentication(Login.this, mAuth);
                                customerAuth.authenticate(emailValue, passwordValue);
                            }
                        } else {
                            Toast.makeText(Login.this, "Error User not found - please check both fields and try again", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
            }
        });

        TextView signUpText = findViewById(R.id.textViewSignUp);
            signUpText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showSignUpDialog();
                }
            });
    }

    //Creates and Adds Sign Up Functionality
    private void showSignUpDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialog_sign_up, null);
        dialogBuilder.setView(dialogView);

        mAuth = FirebaseAuth.getInstance();

        EditText fullName = dialogView.findViewById(R.id.fullName);
        EditText password = dialogView.findViewById(R.id.password);
        EditText confirmPassword = dialogView.findViewById(R.id.confirmPassword);
        EditText emailAddress = dialogView.findViewById(R.id.email);
        EditText shippingAddress = dialogView.findViewById(R.id.shippingAddress);
        Spinner spinnerPaymentMethod = dialogView.findViewById(R.id.paymentMethod);
        CheckBox checkBoxAdmin = dialogView.findViewById(R.id.checkBoxAdmin);

        List<String> paymentMethods = Arrays.asList("Visa", "Mastercard");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, paymentMethods);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPaymentMethod.setAdapter(adapter);

        dialogBuilder.setPositiveButton("Sign Up", (dialog, whichButton) -> {
            String name = fullName.getText().toString().trim();
            String passwordValue = password.getText().toString().trim();
            String confirmPasswordValue = confirmPassword.getText().toString().trim();
            String email = emailAddress.getText().toString().trim().replace(".", "-");
            String address = shippingAddress.getText().toString().trim().replace(" ", "/");
            String paymentMethod = spinnerPaymentMethod.getSelectedItem().toString();
            boolean isAdmin = checkBoxAdmin.isChecked();

            if (TextUtils.isEmpty(name) || TextUtils.isEmpty(passwordValue) || TextUtils.isEmpty(confirmPasswordValue)
                    || TextUtils.isEmpty(email) || TextUtils.isEmpty(address) || !passwordValue.equals(confirmPasswordValue) || !passwordContainsNumber(passwordValue)) {
                Toast.makeText(Login.this, "Error, please check all fields and try again", Toast.LENGTH_SHORT).show();
            } else {
                databaseReference.child("users").child(email).setValue(new User(name, email, address, paymentMethod, isAdmin));
                mAuth.createUserWithEmailAndPassword(email.replace("-", "."), passwordValue)
                        .addOnCompleteListener(Login.this, task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(Login.this, "Authentication Successful", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(Login.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        dialogBuilder.setNegativeButton("Cancel", (dialog, whichButton) -> dialog.dismiss());

        AlertDialog signUpDialog = dialogBuilder.create();
        signUpDialog.show();
    }
    //Method to Check for Password Number Compliance
    private boolean passwordContainsNumber(String password) {
        return password.matches(".*\\d.*");
    }
}