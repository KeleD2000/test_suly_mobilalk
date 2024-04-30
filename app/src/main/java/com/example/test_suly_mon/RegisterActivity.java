package com.example.test_suly_mon;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    private static final String LOG_TAG = RegisterActivity.class.getName();
    private static final String PREF_KEY = RegisterActivity.class.getPackage().toString();
    private static final int SECRET_KEY = 99;

    EditText kg;
    EditText userEmailEditText;
    EditText passwordEditText;
    EditText passwordConfirmEditText;
    EditText magassag;

    private SharedPreferences preferences;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        int secret_key = getIntent().getIntExtra("SECRET_KEY", 0);

        if (secret_key != 99) {
            finish();
        }

        userEmailEditText = findViewById(R.id.userEmailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        passwordConfirmEditText = findViewById(R.id.passwordAgainEditText);
        kg = findViewById(R.id.kg);
//        magassag = findViewById(R.id.magasag);

        preferences = getSharedPreferences(PREF_KEY, MODE_PRIVATE);

        String password = preferences.getString("password", "");

        passwordEditText.setText(password);
        passwordConfirmEditText.setText(password);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        Log.i(LOG_TAG, "onCreate");
    }

    public void register(View view) {

        String email = userEmailEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        String passwordConfirm = passwordConfirmEditText.getText().toString();
        String[] kgArray = kg.getText().toString().split(",");

        if (password.length() < 6) {
            // Ha a jelszó hossza kevesebb, mint 6 karakter
            Log.e(LOG_TAG, "A jelszó legalább 6 karakter hosszúnak kell lennie.");
            Toast.makeText(RegisterActivity.this, "A jelszó legalább 6 karakter hosszúnak kell lennie.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (email.isEmpty() || password.isEmpty()) {
            Log.e(LOG_TAG, "Az e-mail cím vagy a jelszó üres vagy null értékű.");
            Toast.makeText(RegisterActivity.this, "Az e-mail cím vagy a jelszó üres vagy null értékű.", Toast.LENGTH_SHORT).show();
            return;
        }


        if (!password.equals(passwordConfirm)) {
            Log.e(LOG_TAG, "Nem egyenlő a jelszó és a megerősítése.");
            return;
        }


        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Sikeres regisztráció
                    Log.d(LOG_TAG, "User created successfully");
                    String userId = mAuth.getCurrentUser().getUid();
                    Map<String, Object> user = new HashMap<>();
                    user.put("email", email);
                    user.put("kg", Arrays.asList(kgArray));
                    FirebaseFirestore.getInstance()
                            .collection("Users")
                            .document(userId)
                            .set(user)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(LOG_TAG, "User data added to Firestore successfully");
                                    startSuly();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.e(LOG_TAG, "Error adding user data to Firestore: " + e.getMessage());
                                    Toast.makeText(RegisterActivity.this, "Error adding user data to Firestore: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            });
                } else {
                    // Sikertelen regisztráció
                    Log.d(LOG_TAG, "User wasn't created successfully");
                    Exception exception = task.getException();
                    if (exception instanceof FirebaseAuthUserCollisionException) {
                        // Az email cím már foglalt
                        Toast.makeText(RegisterActivity.this, "Az email cím már foglalt. Kérlek, adj meg egy másikat.", Toast.LENGTH_LONG).show();
                    } else {
                        // Egyéb hiba
                        Toast.makeText(RegisterActivity.this, "User wasn't created successfully: " + exception.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }

            }
        });
    }

    public void cancel(View view) {
        finish();
        overridePendingTransition(R.anim.scale_in, R.anim.scale_out);
    }

    private void startSuly(/* registered used class */) {
        Intent intent = new Intent(this, startsuly.class);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(LOG_TAG, "onStart");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(LOG_TAG, "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(LOG_TAG, "onDestroy");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(LOG_TAG, "onPause");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(LOG_TAG, "onResume");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i(LOG_TAG, "onRestart");
    }


}