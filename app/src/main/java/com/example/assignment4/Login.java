package com.example.assignment4;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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

import com.example.assignment4.admin.BottomNavigationAdmin;
import com.example.assignment4.entity.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Login extends AppCompatActivity {

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://rob-ca2-default-rtdb.europe-west1.firebasedatabase.app/");
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
                if (TextUtils.isEmpty(emailValue) || TextUtils.isEmpty(passwordValue)) {
                    Toast.makeText(Login.this, "Please enter both Email and Password", Toast.LENGTH_SHORT).show();
                }
                mAuth.signInWithEmailAndPassword(emailValue, passwordValue)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressBar.setVisibility(View.GONE);
                                if (task.isSuccessful()) {
                                    fetchUserByEmail(emailValue);
                                } else {
                                    Toast.makeText(Login.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                                }
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
    private void fetchUserByEmail(String userEmail) {
        userEmail = userEmail.replace(".", "-");
        DatabaseReference usersRef = databaseReference.child("users").child(userEmail);
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    boolean isAdmin = false;
                    DataSnapshot adminSnapshot = dataSnapshot.child("admin");
                    if (adminSnapshot.exists()) {
                        isAdmin = adminSnapshot.getValue(Boolean.class);
                        if (isAdmin) {
                            Toast.makeText(Login.this, "Admin.", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), BottomNavigationAdmin.class);
                        intent.putExtra("EMAIL", dataSnapshot.getKey().toString());
                        startActivity(intent);
                        finish();
                        return;
                        } else {
                            Toast.makeText(Login.this, "Not Admin.", Toast.LENGTH_SHORT).show();
                        /*Intent intent = new Intent(getApplicationContext(), FragmentHome.class);
                        intent.putExtra("PHONE_NUMBER", phoneNumber);
                        startActivity(intent);
                        finish();
                        return;*/
                        }
                    }
                } else {
                    Toast.makeText(Login.this, "User not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Error fetching user by email", error.toException());
            }
        });
    }
    private void showSignUpDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
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

        List<String> paymentMethods = new ArrayList<>();
        paymentMethods.add("Visa");
        paymentMethods.add("Mastercard");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, paymentMethods);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPaymentMethod.setAdapter(adapter);

        dialogBuilder.setPositiveButton("Sign Up", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String name = fullName.getText().toString().trim();
                String passwordValue = password.getText().toString().trim();
                String confirmPasswordValue = confirmPassword.getText().toString().trim();
                String email = emailAddress.getText().toString().trim().replace(".", "-");
                String address = shippingAddress.getText().toString().trim().replace(" ", "/");
                String paymentMethod = spinnerPaymentMethod.getSelectedItem().toString();
                Boolean isAdmin = false;
                if (checkBoxAdmin.isChecked()) {
                    isAdmin = true;
                }
                //TODO: Refactor these alerts to be more specific
                if (TextUtils.isEmpty(name) || TextUtils.isEmpty(passwordValue) || TextUtils.isEmpty(passwordValue)
                        || TextUtils.isEmpty(email) || TextUtils.isEmpty(address) || !passwordValue.equals(confirmPasswordValue) || !passwordContainsNumber(passwordValue)) {
                    Toast.makeText(Login.this, "Error, please check all fields and try again", Toast.LENGTH_SHORT).show();
                } else {


                    databaseReference.child("users").child(email).setValue(new User(name, email, address, paymentMethod, isAdmin));
                    mAuth.createUserWithEmailAndPassword(email.replace("-", "."), passwordValue)
                            .addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(Login.this, "Authentication Successful", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(Login.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.dismiss();
            }
        });

        AlertDialog signUpDialog = dialogBuilder.create();
        signUpDialog.show();
    }

    //Method for Checking the Password Contains a Number
    private boolean passwordContainsNumber(String password) {
        for (char c : password.toCharArray()) {
            if (Character.isDigit(c)) {
                return true;
            }
        }
        return false;
    }
}