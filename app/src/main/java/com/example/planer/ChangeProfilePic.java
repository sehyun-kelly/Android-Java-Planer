package com.example.planer;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Class for handling profile pic uploads to Firebase Storage
 */
public class ChangeProfilePic extends AppCompatActivity {

    CircleImageView profilePic;
    Uri imageUri;
    Button selectImageBtn;
    Button saveChangesBtn;

    FirebaseUser user;
    FirebaseStorage storage;
    StorageReference storageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_profile_pic);

        user = FirebaseAuth.getInstance().getCurrentUser();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        profilePic = findViewById(R.id.profilePic);
        StorageReference picRef = storageRef.child("ProfilePics/" + user.getUid());
        picRef.getDownloadUrl().addOnSuccessListener(uri -> {
            imageUri = uri;
            Glide.with(ChangeProfilePic.this).load(imageUri)
                    .centerCrop().into(profilePic);
        });

        selectImageBtn = findViewById(R.id.selectImageBtn);
        saveChangesBtn = findViewById(R.id.saveChangesBtn);
        selectImageBtn.setOnClickListener(v -> selectImage());
        saveChangesBtn.setOnClickListener(v -> uploadImage());
    }

    /**
     * Select an image to upload from the user's device
     */
    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, 100);
    }

    /**
     * Update profile pic view to show successful selection
     * @param requestCode arbitrary
     * @param resultCode arbitrary
     * @param data result from selectImage()
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && data != null && data.getData() !=null) {
            imageUri = data.getData();
            profilePic.setImageURI(imageUri);
        }
    }

    /**
     * Upload the selected image to Firebase Storage, finishing the activity.
     */
    private void uploadImage() {
        String filename = user.getUid();
        storageRef = FirebaseStorage.getInstance().getReference("ProfilePics/" + filename);
        if (imageUri != null) {
            storageRef.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        Toast.makeText(getApplicationContext(), "Profile Picture Changed",
                                Toast.LENGTH_SHORT).show();
                        finish();
                    }).addOnFailureListener(e -> Toast.makeText(getApplicationContext(),
                            "Upload Failed", Toast.LENGTH_SHORT).show());
        } else {
            Toast.makeText(this, "Please select an image before saving",
                    Toast.LENGTH_SHORT).show();
        }
    }
}