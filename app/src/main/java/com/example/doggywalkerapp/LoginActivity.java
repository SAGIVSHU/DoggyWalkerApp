package com.example.doggywalkerapp;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;


public class LoginActivity extends AppCompatActivity {

    private ProgressDialog progressDialog;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference userRef;
    private SharedPreferences sharedPreferences;
    private Button loginBt;

    final Runnable r = new Runnable() {
        public void run() {
            Intent go = new Intent(LoginActivity.this, UserPage.class);
            startActivity(go);
            finish();

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        progressDialog = new ProgressDialog(this);

        firebaseAuth = firebaseAuth.getInstance();
        loginBt = (Button) findViewById(R.id.loginBt);
        final TextView rgBt = (TextView) findViewById(R.id.registerTextBt);
        final EditText emailEt = (EditText) findViewById(R.id.emailEt);
        final EditText passEt = (EditText) findViewById(R.id.passwordEt);
        final TextView invalidTextLogin = (TextView) findViewById(R.id.invalidEmailTextLogin);

        loginBt.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                invalidTextLogin.setVisibility(View.INVISIBLE);

                if (emailEt.getText().toString().equals("") || passEt.getText().toString().equals("")) {
                    invalidTextLogin.setText("Fields must be written");
                    invalidTextLogin.setVisibility(View.VISIBLE);
                } else {
                    invalidTextLogin.setVisibility(View.INVISIBLE);
                    LoginUser(emailEt.getText().toString(), passEt.getText().toString());

                }
            }
        });
        rgBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent go = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(go);
            }
        });
    }

    public void LoginUser(String email, String password) {
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    progressDialog.setMessage("Logging in...");
                    progressDialog.show();
                    Log.d("LoginActivity1", "Successful login");
                    loginBt.setEnabled(false);
                    Log.d("LoginActivity2", "Button disabled");
                    Log.d("signInWithEmailAndPassword:", "success");
                    showToast("Logged In!");
                    sharedPreferences = getSharedPreferences("User", MODE_PRIVATE);
                    String json = sharedPreferences.getString("User", "");
                    userRef = FirebaseDatabase.getInstance().getReference("User");
                    userRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                User user = postSnapshot.getValue(User.class);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                Gson gson = new Gson();
                                String json = gson.toJson(user);
                                editor.putString("User", json);
                                editor.apply();
                                final Handler handler = new Handler();
                                handler.postDelayed(r, 500);

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.w("loadPost:onCancelled", error.toException());
                        }
                    });

                } else {
                    progressDialog.dismiss();
                    Log.d("signInWithEmailAndPassword:", "failed");
                    showToast("Incorrect login");
                }
            }
        });
    }


    private void showToast(String text) {
        LayoutInflater inflater = getLayoutInflater();
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) View my_toast = inflater.inflate(R.layout.my_toast, findViewById(R.id.my_toast));
        TextView tv = my_toast.findViewById(R.id.tv_my_toast);
        tv.setText(text);
        Toast toast = new Toast(LoginActivity.this);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(my_toast);
        toast.show();
    }


}
