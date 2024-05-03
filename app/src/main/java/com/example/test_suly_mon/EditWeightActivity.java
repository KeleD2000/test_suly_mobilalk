package com.example.test_suly_mon;

import static android.content.ContentValues.TAG;

import android.app.AlertDialog;
import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.view.inputmethod.EditorInfo;
import android.text.InputType;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class EditWeightActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Felhasználói felület megjelenítése AlertDialog segítségével
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Súly módosítása");

        // Felhasználói felület összeállítása
        EditText editTextWeight = new EditText(this);
        editTextWeight.setHint("Új súly");
        editTextWeight.setInputType(InputType.TYPE_CLASS_NUMBER); // Csak számokat fogad
        editTextWeight.setImeOptions(EditorInfo.IME_ACTION_DONE); // Befejezés jelzése
        editTextWeight.requestFocus();
        builder.setView(editTextWeight);

        // Mégse gomb
        builder.setNegativeButton("Mégse", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Ha a felhasználó mégsem akar menteni, egyszerűen bezárjuk az ablakot
                dialog.dismiss();
                // Visszatérés a startsuly tevékenységhez
                finish();
            }
        });

        // Mentés gomb
        builder.setPositiveButton("Mentés", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Új súly lekérése az EditTextből
                String newWeight = editTextWeight.getText().toString();

                // Sorszám lekérése az intentből
                int rowIndex = getIntent().getIntExtra("rowIndex", -1);

                if (rowIndex != -1) {
                    // Firebase adatbázis frissítése az új súllyal
                    updateWeightInFirebase(rowIndex, newWeight);
                }

                // Dialógus bezárása
                dialog.dismiss();

                // Visszatérés a startsuly tevékenységhez
                finish();
            }
        });


        // A dialógus létrehozása és megjelenítése
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void updateWeightInFirebase(int rowIndex, String newWeight) {
        // Az üres stringek kezelése
        if (newWeight.isEmpty()) {
            // Üres string esetén a frissítés kihagyása vagy kezelése
            // Pl.: Logolás vagy hibaüzenet megjelenítése
            Log.d(TAG, "Új súly üres, frissítés kihagyva.");
            return;
        }

        // Az aktuális felhasználó UID-jének lekérése
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // A Firestore adatbázis referenciájának lekérése
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // A megfelelő dokumentum referenciájának meghatározása
        DocumentReference userRef = db.collection("Users").document(currentUserId);

        // A dokumentum lekérése és frissítése a kg tömb megfelelő elemének módosításával
        userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        List<String> kgList = (List<String>) document.get("kg");

                        // Ellenőrizzük, hogy a rowIndex érvényes-e
                        if (rowIndex >= 0 && rowIndex < kgList.size()) {
                            // Az adott sorban lévő súly frissítése az új súllyal
                            kgList.set(rowIndex, newWeight);

                            // A dokumentum frissítése a módosított kg tömbbel
                            userRef.update("kg", kgList)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d(TAG, "Súly sikeresen frissítve!");
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w(TAG, "Hiba történt a súly frissítése közben", e);
                                        }
                                    });
                        }
                    } else {
                        Log.d(TAG, "Nincs ilyen dokumentum");
                    }
                } else {
                    Log.d(TAG, "Sikertelen lekérés ", task.getException());
                }
            }
        });
    }
}
