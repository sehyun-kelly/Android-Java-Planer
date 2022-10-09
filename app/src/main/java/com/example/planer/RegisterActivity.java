package com.example.planer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

public class RegisterActivity extends AppCompatActivity {

    static final String TAG = "Android";

    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        this.db = FirebaseFirestore.getInstance();
    }


    /**
     * Redirect user to login activity.
     *
     * @param view
     * @author Ravinder Shokar
     */
    public void signIn(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void signUp(View view) {
        EditText username = findViewById(R.id.username_input);
        EditText password = findViewById(R.id.password_input);
        EditText email = findViewById(R.id.email_input);
        EditText confirmPassword = findViewById(R.id.confirm_password_input);


        String userNameInput = username.getText().toString();
        String emailInput = email.getText().toString();
        String passwordInput = password.getText().toString();
        String confirmPasswordInput = confirmPassword.getText().toString();

        if (!validateForm(userNameInput, emailInput, passwordInput, confirmPasswordInput)) {
            Toast.makeText(RegisterActivity.this,
                    "Please enter user name, email, password, and confirm password", Toast.LENGTH_SHORT).show();
            return;
        }


        // This is where we would add the user to the db and then redirect to login activity

        // Add user to db

        // If an error display error as toast

        // Redirect to sign up
        signIn(view);

//        mAuth.signInWithEmailAndPassword(user, pass).addOnCompleteListener(this, task -> {
//            if (task.isSuccessful()) {
//                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
//                startActivity(intent);
//
//            } else {
//                Toast.makeText(MainActivity.this,
//                        "Incorrect email / password.", Toast.LENGTH_SHORT).show();
//            }
//        });
    }

    private boolean validateForm(String userName, String email, String password, String confirmPassword) {
        return !userName.isEmpty() && !email.isEmpty() && !password.isEmpty() && !confirmPassword.isEmpty();
    }
}
