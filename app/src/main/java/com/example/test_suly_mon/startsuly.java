package com.example.test_suly_mon;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
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

    private boolean adatKiirt = false;

    private TextView mLinearLayout;
    private String adat;

    private TableLayout mTableLayout;

    @SuppressLint({"WrongViewCast", "MissingInflatedId"})
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startsuly);

        // Felületi elem inicializálása
        mTableLayout = findViewById(R.id.tableLayout);

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

                        if (kgObject instanceof ArrayList<?>) {
                            // A kg mező tartalma egy Stringeket tartalmazó ArrayList
                            ArrayList<String> kgList = (ArrayList<String>) kgObject;

                            for (int i = 0; i < kgList.size(); i++) {
                                String kgString = kgList.get(i);
                                int szam = i + 1;

                                // Új TableRow létrehozása
                                TableRow row = new TableRow(startsuly.this);
                                row.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));

                                // Új TextView létrehozása és konfigurálása
                                TextView textViewKg = new TextView(startsuly.this);
                                textViewKg.setText(kgString);
                                textViewKg.setGravity(Gravity.CENTER);
                                textViewKg.setPadding(10, 10, 10, 10);

                                TextView textViewSzam = new TextView(startsuly.this);
                                textViewSzam.setText(szam + " feljegyezés");
                                textViewSzam.setGravity(Gravity.CENTER);
                                textViewSzam.setPadding(10, 10, 10, 10);

                                // Hozzáadás a TableRow-hoz
                                row.addView(textViewKg);
                                row.addView(textViewSzam);

                                // Új ImageView létrehozása és konfigurálása a módosítás ikonnal
                                ImageView editIcon = new ImageView(startsuly.this);
                                editIcon.setImageResource(R.drawable.ic_edit);
                                editIcon.setPadding(2, 2, 2, 2);

                                editIcon.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        // Ellenőrizd, hogy melyik sorra kattintottak, és szerezzd be a súlyt
                                        TableRow parentRow = (TableRow) v.getParent();
                                        TextView textViewKg = (TextView) parentRow.getChildAt(0); // Feltételezve, hogy az első gyermek a súly
                                        String currentWeight = textViewKg.getText().toString();

                                        // Intent létrehozása az EditWeightActivity-re és a súly átadása
                                        Intent intent = new Intent(startsuly.this, EditWeightActivity.class);
                                        intent.putExtra("currentWeight", currentWeight);
                                        startActivity(intent);
                                    }
                                });


                                // Új ImageView létrehozása és konfigurálása a törlés ikonnal
                                ImageView deleteIcon = new ImageView(startsuly.this);
                                deleteIcon.setImageResource(R.drawable.ic_delete);
                                deleteIcon.setPadding(2, 2, 2, 2);

                                // Hozzáadás a TableRow-hoz
                                row.addView(editIcon);
                                row.addView(deleteIcon);

                                // Hozzáadás a TableLayout-hoz
                                mTableLayout.addView(row);
                            }
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
        switch (item.getItemId()) {
            case R.id.log_out:
                Log.d(LOG_TAG, "Logout clicked!");
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


}
