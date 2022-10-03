package com.example.planer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            Intent intent = new Intent(this, SecondActivity.class);
            startActivity(intent);

        }
    }

    //
    public void signUp(View view) {
        EditText username = findViewById(R.id.username_input);
        String user = username.getText().toString();
        EditText password = findViewById(R.id.password_input);
        String pass = password.getText().toString();

        mAuth.createUserWithEmailAndPassword(user, pass)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(MainActivity.this,
                                "Account created. Please log in.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this,
                                "Authentication failed.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void signIn(View view) {
        EditText username = findViewById(R.id.username_input);
        String user = username.getText().toString();
        EditText password = findViewById(R.id.password_input);
        String pass = password.getText().toString();

        if (!validateForm(user, pass)) {
            Toast.makeText(MainActivity.this,
                    "Please enter email and password", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(user, pass).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                Intent intent = new Intent(MainActivity.this, SecondActivity.class);
                startActivity(intent);

            } else {
                Toast.makeText(MainActivity.this,
                        "Incorrect email / password.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public boolean validateForm(String username, String password) {
        return !username.isEmpty() && !password.isEmpty();
    }
}