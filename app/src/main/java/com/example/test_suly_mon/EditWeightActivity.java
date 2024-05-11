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

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Súly módosítása");

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
                dialog.dismiss();
                finish();
            }
        });

        // Mentés gomb
        builder.setPositiveButton("Mentés", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String newWeight = editTextWeight.getText().toString();

                int rowIndex = getIntent().getIntExtra("rowIndex", -1);

                if (rowIndex != -1) {
                    updateWeightInFirebase(rowIndex, newWeight);
                }

                dialog.dismiss();

                finish();
            }
        });


        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void updateWeightInFirebase(int rowIndex, String newWeight) {
        if (newWeight.isEmpty()) {

            Log.d(TAG, "Új súly üres, frissítés kihagyva.");
            return;
        }


        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        DocumentReference userRef = db.collection("Users").document(currentUserId);

        userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        List<String> kgList = (List<String>) document.get("kg");

                        if (rowIndex >= 0 && rowIndex < kgList.size()) {
                            kgList.set(rowIndex, newWeight);

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
