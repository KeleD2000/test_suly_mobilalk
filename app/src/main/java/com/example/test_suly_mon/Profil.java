package com.example.test_suly_mon;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Profil extends AppCompatActivity {
    private FirebaseUser user;
    private FirebaseFirestore mFirestore;
    private DocumentReference mItems;
    private ArrayList<String> mItemList;
    private EditText mSulyEditText;
    private ArrayList <User> user2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil);

        user = FirebaseAuth.getInstance().getCurrentUser();
        mFirestore = FirebaseFirestore.getInstance();
        mItems = mFirestore.collection("Users").document(user.getUid());
        mItemList = new ArrayList<>();
        mSulyEditText = findViewById(R.id.sulyEditText);
        user2=new ArrayList<>();

    }

    // a menü inicializálása
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    // a menü elemek kezelése
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.log_out:
                FirebaseAuth.getInstance().signOut();
                finish();
                return true;
            case R.id.profil:
                Intent intent = new Intent(this, Profil.class);
                startActivity(intent);
                return true;
            case R.id.alap:
                Intent intent2 = new Intent(this, startsuly.class);
                startActivity(intent2);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    public void send(View view) {
        String suly = mSulyEditText.getText().toString();

        if (suly.isEmpty()) {
            Toast.makeText(this, "Kérlek add meg a súlyod!", Toast.LENGTH_SHORT).show();
        } else {
            Map<String, Object> felhasznalo = new HashMap<>();

            felhasznalo.put("kg", FieldValue.arrayUnion(suly));
            felhasznalo.put("email", user.getEmail());

            mItems.update(felhasznalo)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(Profil.this, "Súly mentve!", Toast.LENGTH_SHORT).show();
                                mSulyEditText.setText("");

                                new android.os.Handler().postDelayed(
                                        new Runnable() {
                                            public void run() {
                                                Intent intent = new Intent(Profil.this, startsuly.class);
                                                startActivity(intent);
                                                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                            }
                                        },
                                        2000);
                            } else {
                                Toast.makeText(Profil.this, "Hiba történt a súly mentése során.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

}
