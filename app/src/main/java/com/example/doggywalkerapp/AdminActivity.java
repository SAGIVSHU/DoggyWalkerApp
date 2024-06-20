package com.example.doggywalkerapp;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class AdminActivity extends AppCompatActivity {
    private StorageTask uploadStorageTask;
    private ActivityResultLauncher<String> getContent;


    //array of week days for easy saving
    private String[] days = new String[]{"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};


    private StorageReference storageReference;
    private Uri imageUri;
    private DogWalkerClass dogWalker;
    private DatabaseReference dbRef;
    private String name, phoneNumber, location, rating, ratedTrips;
    private Button publish, back;
    private ImageView uploadImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        publish = (Button) findViewById(R.id.publish);
        uploadImage = (ImageView) findViewById(R.id.walkerIcon);
        back = findViewById(R.id.backBt);

        storageReference = FirebaseStorage.getInstance().getReference("Uploads");
        imageUri = null;

        uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getContent.launch("image/*");
            }
        });
        getContent = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri o) {
                imageUri = o;
                Log.d("imageAniSagiv", imageUri.toString());
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), imageUri);
                    uploadImage.setImageBitmap(bitmap);
                    Log.e("TTT", o.toString());
                } catch (Exception e) {
                }

            }
        });

        publish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = ((EditText) findViewById(R.id.walkerName)).getText().toString();
                phoneNumber = ((EditText) findViewById(R.id.walkerPhoneNumber)).getText().toString();
                rating = ((EditText) findViewById(R.id.rating)).getText().toString();
                location = ((EditText) findViewById(R.id.wlakerLocation)).getText().toString();
                ratedTrips = ((EditText) findViewById(R.id.numberOfRatedTrips)).getText().toString();

                dogWalker = new DogWalkerClass(name, phoneNumber, rating, location, "none",ratedTrips);

                dbRef = FirebaseDatabase.getInstance().getReference("");
                String walkerUid = dbRef.push().getKey();
                dogWalker.setWalkerId(walkerUid);

                for (String day : days) {
                    saveDogWalkerToDB(day);
                }
                saveDogWalkerToDB("DogWalkers");
                uploadImage(dogWalker.getWalkerId());
                showToast("Data Uploaded");

            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminActivity.this,WelcomeActivity.class));
            }
        });
    }

    private void saveDogWalkerToDB(String path) {
        dbRef = FirebaseDatabase.getInstance().getReference("DogWalkersFolder/"+path);

        //Write into given path
        // Set the dog walker object under the generated unique ID
        if (dogWalker.getWalkerId() != null) {

                dbRef.child(dogWalker.getWalkerId()).setValue(dogWalker)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // Data successfully saved
                                Log.d(TAG, "Dog walker saved successfully!");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Handle error
                                Log.e(TAG, "Error saving dog walker: " + e.getMessage());
                            }
                        });
            }
         else {
            // Handle case where unique ID is null
            Log.e(TAG, "Error generating unique ID for dog walker");
        }
    }


    private void uploadImage(String uidForUpload) {
        if (imageUri != null) {
            imageUri = reduceImageSize(AdminActivity.this, imageUri);
            StorageReference fileReference = storageReference.child(uidForUpload);
            Log.d("uidSagivGugu576: ", dogWalker.getWalkerId());
            uploadStorageTask = fileReference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            showToast("Uploaded image successfully");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            showToast(e.getMessage());
                        }
                    });
        }
    }


    private static Uri reduceImageSize(Context context, Uri uri) {
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeStream(context.getContentResolver().openInputStream(uri));

            // Compress the bitmap
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 10, outputStream); // Adjust compression quality as needed

            // Write compressed bitmap to a file
            File file = new File(context.getCacheDir(), "temp_image.jpg");
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(outputStream.toByteArray());
            fileOutputStream.flush();
            fileOutputStream.close();
            Log.d("resized image sagiv:", "yes");
            return Uri.fromFile(file);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            if (bitmap != null) {
                bitmap.recycle();
            }

        }
    }

    private void showToast(String text) {
        LayoutInflater inflater = getLayoutInflater();
        @SuppressLint({"MissingInflatedId", "LocalSuppress"})
        View my_toast = inflater.inflate(R.layout.my_toast, findViewById(R.id.my_toast));
        TextView tv = my_toast.findViewById(R.id.tv_my_toast);
        tv.setText(text);
        Toast toast = new Toast(AdminActivity.this);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(my_toast);
        toast.show();
    }
}