package com.example.planer;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseAuth;

public class SecondActivity extends AppCompatActivity {

    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        user = FirebaseAuth.getInstance().getCurrentUser();
        TextView currUser = (TextView)findViewById(R.id.userID);
        String email = user.getEmail();
        String uid = user.getUid();
        String result = "Email:\n\t" + email + "\n\n" + "UserID:\n\t" + uid + "\n\n";
        currUser.setText(result);
    }

    public void logout(View view) {
        FirebaseAuth.getInstance().signOut();
        finish();
    }
}