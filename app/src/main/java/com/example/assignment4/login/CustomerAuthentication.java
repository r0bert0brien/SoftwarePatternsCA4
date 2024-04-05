package com.example.assignment4.login;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.assignment4.R;
import com.example.assignment4.customer.BottomNavigationCustomer;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

public class CustomerAuthentication implements AuthenticationStrategy {

    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;
    private Context context;

    public CustomerAuthentication(Context context, FirebaseAuth mAuth, DatabaseReference databaseReference) {
        this.context = context;
        this.mAuth = mAuth;
        this.databaseReference = databaseReference;
    }

    @Override
    public void authenticate(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(context, "Customer has Logged In", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(context, BottomNavigationCustomer.class);
                            intent.putExtra("EMAIL", email.replace(".", "-"));
                            context.startActivity(intent);
                        } else {
                            ProgressBar progressBar = ((Activity) context).findViewById(R.id.progressBar);
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(context, "Error, please check password", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
