package com.example.doggywalkerapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference userRef;
    private SharedPreferences sharedPreferences;
    private StorageReference storageReference;
    private StorageTask uploadStorageTask;
    private ActivityResultLauncher<String> getContent;
    private ActivityResultLauncher<Intent> startCamera;
    private final static String TAG = "MAIN";

    private final static int MY_CAMERA_PERMISSION_CODE = 100;
    private static final String[] PERMISSIONS = new String[]{
            android.Manifest.permission.CAMERA
    };
    private Uri imageUri;
    private Button registerBt;
    private UserClass user;
    private TextView closeTxt;
    private ImageButton cameraButton, galleryButton;
    private Dialog dialog;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        firebaseAuth.signOut();
    }


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        imageUri = null;
        progressDialog = new ProgressDialog(this);
        firebaseAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference("Uploads");

        final EditText passEt = (EditText) findViewById(R.id.userPassword);
        final EditText phoneEt = (EditText) findViewById(R.id.phoneNumber);
        final EditText userEt = (EditText) findViewById(R.id.userName);
        final EditText raceEt = (EditText) findViewById(R.id.dogRace);
        final EditText locationEt = (EditText) findViewById(R.id.location);
        final EditText emailEt = (EditText) findViewById(R.id.userEmail);


        registerBt = (Button) findViewById(R.id.registerBt);
        final TextView clear = (TextView) findViewById(R.id.clearRemarks);
        final TextView loginText = (TextView) findViewById(R.id.loginTextBt);
        final ImageView uploadImage = (ImageView) findViewById(R.id.userIcon);


        clear.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                emailEt.setTextSize(20);
                emailEt.setHint(getResources().getString(R.string.email));
                emailEt.setHintTextColor(getResources().getColor(R.color.white));
                emailEt.setEnabled(true);


                passEt.setTextSize(20);
                passEt.setHint(getResources().getString(R.string.password));
                passEt.setHintTextColor(getResources().getColor(R.color.white));
                passEt.setEnabled(true);

                phoneEt.setTextSize(20);
                phoneEt.setHint(getResources().getString(R.string.phone));
                phoneEt.setHintTextColor(getResources().getColor(R.color.white));
                phoneEt.setEnabled(true);


                userEt.setTextSize(20);
                userEt.setHint(getResources().getString(R.string.userName));
                userEt.setHintTextColor(getResources().getColor(R.color.white));
                userEt.setEnabled(true);

                locationEt.setTextSize(20);
                locationEt.setHint(getResources().getString(R.string.location));
                locationEt.setHintTextColor(getResources().getColor(R.color.white));
                locationEt.setEnabled(true);

                raceEt.setTextSize(20);
                raceEt.setHint(getResources().getString(R.string.dogRace));
                raceEt.setHintTextColor(getResources().getColor(R.color.white));
                raceEt.setEnabled(true);

                return false;
            }
        });


        //Upload Image with dialog
        //set dialog
        dialog = new Dialog(RegisterActivity.this);
        dialog.setContentView(R.layout.custom_gallery_dialog);
        Objects.requireNonNull(dialog.getWindow()).setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);

        galleryButton = (ImageButton) dialog.findViewById(R.id.GalleryButton);
        cameraButton = (ImageButton) dialog.findViewById(R.id.cameraButton);
        closeTxt = (TextView) dialog.findViewById(R.id.cancelText);
        closeTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        galleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getContent.launch("image/*");
                dialog.dismiss();
            }
        });
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    pickCamera();
                }
            }
        });
        uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
            }
        });

        getContent = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri o) {
                imageUri = o;
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), imageUri);
                    uploadImage.setImageBitmap(bitmap);
                    Log.e("TTT", o.toString());
                } catch (Exception e) {
//                    showToast(e.getMessage());
                }

            }
        });

        startCamera = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == RESULT_OK) {
                            // There are no request codes
                            Log.e(TAG, imageUri.toString());
                            uploadImage.setImageURI(imageUri);
                        }
                    }
                }
        );


        registerBt.setOnClickListener(new View.OnClickListener() {
            @Override
            @SuppressLint("ResourceAsColor")
            public void onClick(View v) {
                final boolean passFlag = Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$").matcher(passEt.getText()).matches();
                final boolean emailFlag = Pattern.compile("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$").matcher(emailEt.getText()).matches();
                final boolean phoneFlag = Pattern.compile("^05\\d{8}$").matcher(phoneEt.getText()).matches();
                final boolean userFlag = Pattern.compile("^[a-zA-Z]{3,8}$").matcher(userEt.getText()).matches();

                if (passEt != null && passFlag && phoneEt != null && phoneFlag && emailEt != null && emailFlag && locationEt != null
                        && !locationEt.getText().toString().equals("") && raceEt != null &&
                        !raceEt.getText().toString().equals("") && userEt != null && userFlag) {
                    String userName = userEt.getText().toString();
                    String userPassword = passEt.getText().toString();
                    String phoneNumber = phoneEt.getText().toString();
                    String race = raceEt.getText().toString();
                    String finalLocation = locationEt.getText().toString();
                    String userEmail = emailEt.getText().toString();

                    user = new UserClass(userEmail,
                            userName,
                            phoneNumber,
                            race,
                            finalLocation,
                            "");

                    regUser(userEmail,userPassword);
                    Log.d("afterCreate", user.getUid());
                    Log.d("afterCreate2", user.getUid());


                }

                if (emailEt == null || !emailFlag) {
                    emailEt.setTextSize(13);
                    emailEt.setEnabled(false);
                    emailEt.setHintTextColor(getResources().getColor(R.color.red));
                    emailEt.setHint("Enter your email correctly");
                    emailEt.setText("");
                }
                if (passEt == null || !passFlag) {
                    passEt.setTextSize(13);
                    passEt.setEnabled(false);
                    passEt.setHintTextColor(getResources().getColor(R.color.red));
                    passEt.setHint("Include at least: 8 characters, one letter and one number");
                    passEt.setText("");
                }
                if (!phoneFlag || phoneEt == null) {
                    phoneEt.setText("");
                    phoneEt.setEnabled(false);
                    phoneEt.setHint("Phone number must start with 05");
                    phoneEt.setTextSize(13);
                    phoneEt.setHintTextColor(getResources().getColor(R.color.red));
                }
                if (!userFlag || userEt == null) {
                    userEt.setText("");
                    userEt.setEnabled(false);
                    userEt.setHint("One word which should include alphabets and length of 3-8");
                    userEt.setTextSize(13);
                    userEt.setHintTextColor(getResources().getColor(R.color.red));
                }
                if (raceEt == null || raceEt.getText().toString().equals("")) {
                    raceEt.setText("");
                    raceEt.setEnabled(false);
                    raceEt.setHint("Cannot be empty");
                    raceEt.setTextSize(13);
                    raceEt.setHintTextColor(getResources().getColor(R.color.red));
                }
                if (locationEt == null || locationEt.getText().toString().equals("")) {

                    locationEt.setText("");
                    locationEt.setEnabled(false);
                    locationEt.setHint("Cannot be empty");
                    locationEt.setTextSize(13);
                    locationEt.setHintTextColor(getResources().getColor(R.color.red));
                }

            }
        });


        phoneEt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phoneEt.setTextSize(20);
                phoneEt.setHint(getResources().getString(R.string.phone));
                phoneEt.setHintTextColor(getResources().getColor(R.color.white));
            }
        });

        passEt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                passEt.setTextSize(20);
                passEt.setHint(getResources().getString(R.string.password));
                passEt.setHintTextColor(getResources().getColor(R.color.white));
            }
        });


        locationEt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locationEt.setTextSize(20);
                locationEt.setHint(getResources().getString(R.string.location));
                locationEt.setHintTextColor(getResources().getColor(R.color.white));
            }
        });

        raceEt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                raceEt.setTextSize(20);
                raceEt.setHint(getResources().getString(R.string.dogRace));
                raceEt.setHintTextColor(getResources().getColor(R.color.white));
            }
        });
        userEt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userEt.setTextSize(20);
                userEt.setHint(getResources().getString(R.string.userName));
                userEt.setHintTextColor(getResources().getColor(R.color.white));
            }
        });

        emailEt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emailEt.setTextSize(20);
                emailEt.setHint(getResources().getString(R.string.email));
                emailEt.setHintTextColor(getResources().getColor(R.color.white));
            }
        });
        loginText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent go = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(go);

            }
        });
    }

    final Runnable r = new Runnable() {
        public void run() {
            Intent go = new Intent(RegisterActivity.this, UserPageActivity.class);
            startActivity(go);
            finish();

        }
    };

    private void uploadFile(String uidForUpload) {

        if (imageUri != null) {
            imageUri = reduceImageSize(RegisterActivity.this, imageUri);
            StorageReference fileReference = storageReference.child(uidForUpload);
            Log.d("uidSagivGugu56: ", uidForUpload);
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


    @Override
    protected void onResume() {
        super.onResume();
        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(PERMISSIONS, MY_CAMERA_PERMISSION_CODE);
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_PERMISSION_CODE && grantResults.length > 0) {
            showToast("Camera granted");
        } else {
            showToast("Camera not granted");

        }
    }

    public void pickCamera() {
        dialog.dismiss();
        Log.e(TAG, "pick Camera");
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Picture");
        values.put(MediaStore.Images.Media.DESCRIPTION, "From Camera");
        imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);

        startCamera.launch(cameraIntent);
        Log.e(TAG, "pick Camera X");
    }

    private void regUser(String email, String password) {
        firebaseAuth.createUserWithEmailAndPassword(email,password).
                addOnCompleteListener(this,
                        new OnCompleteListener<AuthResult>() {
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    registerBt.setEnabled(false);
                                    progressDialog.setMessage("Registration is in progress");
                                    progressDialog.show();
                                    String userId = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();
                                    user.setUid(userId);

                                    Log.d("uidsagivtry", user.getUid());
                                    uploadFile(userId);
                                    saveUserDB(user);


                                    final Handler handler = new Handler();
                                    handler.postDelayed(r, 4000);
                                } else {
                                    showToast("Email is already registered");
                                }
                            }
                        });
    }

    private void saveUserDB(UserClass user) {
        userRef = FirebaseDatabase.getInstance().getReference("Users");
        userRef.child(user.getUid()).setValue(user);
        sharedPreferences = getSharedPreferences("User", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(user);
        editor.putString("User", json);
        editor.apply();
        Log.d("REG", "createUserWithEmailAndPassword:success");
        showToast("Successfully registered");
    }


    private void showToast(String text) {
        LayoutInflater inflater = getLayoutInflater();
        @SuppressLint({"MissingInflatedId", "LocalSuppress"})
        View my_toast = inflater.inflate(R.layout.my_toast, findViewById(R.id.my_toast));
        TextView tv = my_toast.findViewById(R.id.tv_my_toast);
        tv.setText(text);
        Toast toast = new Toast(RegisterActivity.this);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(my_toast);
        toast.show();
    }

    public static Uri reduceImageSize(Context context, Uri uri) {
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
}