package com.nipusan.app.filtergenerator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.nipusan.app.filtergenerator.entity.CollectionEntity;
import com.nipusan.app.filtergenerator.utils.Constants;

import org.jetbrains.annotations.NotNull;

public class FormCollectionsActivity extends AppCompatActivity implements Constants {

    EditText name, description;

    Button btnAddCollection;

    DatabaseReference database = FirebaseDatabase.getInstance().getReference();

    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_collections);

        name = findViewById(R.id.etName);
        description = findViewById(R.id.etDesc);

        btnAddCollection = findViewById(R.id.btnSave);

        preferences = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);


        btnAddCollection.setOnClickListener(new View.OnClickListener() {
            /**
             * Called when a view has been clicked.
             *
             * @param v The view that was clicked.
             */
            @Override
            public void onClick(View v) {

                try {
                    String getName = name.getText().toString();
                    String getDescription = description.getText().toString();
                    String userUid = preferences.getString(USER_UID, "");

                    if (getName.isEmpty()) {
                        name.setError("Name is empty!");
                    } else if (getDescription.isEmpty()) {
                        description.setError("Description is empty!");
                    } else if (userUid.isEmpty()) {
                        description.setError("User Not Found!");
                    } else {
                        database.child(ENTITY_COLLECTION).push().setValue(new CollectionEntity(getName, getDescription, userUid))
                                .addOnSuccessListener(new OnSuccessListener() {
                                    @Override
                                    public void onSuccess(Object o) {
                                        Toast.makeText(FormCollectionsActivity.this, "Saved Collection", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(FormCollectionsActivity.this, MainActivity.class));
                                        finish();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull @NotNull Exception e) {
                                Toast.makeText(FormCollectionsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } catch (Exception e) {
                    Log.e("savedData", e.getMessage());
                    Toast.makeText(FormCollectionsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        });

    }
}