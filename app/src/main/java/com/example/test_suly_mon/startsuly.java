package com.example.test_suly_mon;



import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

public class startsuly extends AppCompatActivity {

    private static final String LOG_TAG = startsuly.class.getName();

    // Firebase Authentication objektum inicializálása
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    // Firebase Firestore objektum inicializálása
    private FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();

    private TextView mLinearLayout;
    private String adat;

    @SuppressLint({"WrongViewCast", "MissingInflatedId"})
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startsuly);

        // Felületi elemek inicializálása
        mLinearLayout = findViewById(R.id.suly);

        // Az aktuálisan bejelentkezett felhasználó UID-jének lekérdezése
        String currentUserId = mAuth.getCurrentUser().getUid();

        // Firestore lekérdezés a users kollekcióban tárolt adatok lekérdezésére
        mFirestore.collection("Users").document(currentUserId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // A dokumentum tartalmának lekérése egy Map objektumba
                        Map<String, Object> userMap = document.getData();

                        // A kg mező tartalmának lekérése a Map objektumból
                        Object kgObject = userMap.get("kg");

                        if (kgObject instanceof String) {
                            // A kg mező tartalma egyetlen String érték
                            String kgString = (String) kgObject;
                            TextView textView = new TextView(getApplicationContext());
                            textView.setText(kgString);
                            mLinearLayout.setText(kgString);
                        } else if (kgObject instanceof ArrayList<?>) {
                            // A kg mező tartalma egy Stringeket tartalmazó ArrayList
                            ArrayList<String> kgList = (ArrayList<String>) kgObject;
                            StringBuilder sb = new StringBuilder();

                            for (int i = 0; i < kgList.size(); i++) {
                                int szam=i+1;
                                sb.append(kgList.get(i)).append(" kg "+ szam+"feljegyezés száma \n");
                            }

                            adat = sb.toString();
                            TextView textView = new TextView(getApplicationContext());
                            textView.setText(adat);
                            mLinearLayout.setText(adat);
                        } else {
                            Log.d(LOG_TAG, "Invalid kg field type");
                        }
                    } else {
                        Log.d(LOG_TAG, "No such document");
                    }
                } else {
                    Log.d(LOG_TAG, "get failed with ", task.getException());
                }
            }
        });
    }





    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.log_out:
                Log.d(LOG_TAG, "Logout clicked!");
                FirebaseAuth.getInstance().signOut();
                finish();
                return true;
            case R.id.profil:
                Intent intent =new Intent(this, Profil.class);
                startActivity(intent);
                return true;
            case R.id.alap:
                Intent intent2 =new Intent(this, startsuly.class);
                startActivity(intent2);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }


}
