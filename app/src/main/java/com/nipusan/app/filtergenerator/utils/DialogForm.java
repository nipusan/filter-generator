package com.nipusan.app.filtergenerator.utils;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.nipusan.app.filtergenerator.R;
import com.nipusan.app.filtergenerator.entity.CollectionEntity;

import org.jetbrains.annotations.NotNull;

public class DialogForm extends DialogFragment implements Constants {

    String name, desc, key, operation;
    DatabaseReference database = FirebaseDatabase.getInstance().getReference();

    public DialogForm(String name, String desc, String key, String operation) {
        this.name = name;
        this.desc = desc;
        this.key = key;
        this.operation = operation;
    }

    TextView tname, tdesc;
    Button btnAction;

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.activity_form_collections, container, false);
        tname = view.findViewById(R.id.etName);
        tdesc = view.findViewById(R.id.etDesc);
        btnAction = view.findViewById(R.id.btnSave);
        tname.setText(name);
        tdesc.setText(desc);
        btnAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = tname.getText().toString();
                String desc = tdesc.getText().toString();
                if(operation.equals(OPERATION_EDIT)){
                    database.child(ENTITY_COLLECTION)
                            .child(key)
                            .setValue(new CollectionEntity(name, desc))
                            .addOnSuccessListener(new OnSuccessListener<Void>(){
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(view.getContext(), "Updated Collection!", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull @NotNull Exception e) {
                            Toast.makeText(view.getContext(), "Collection could not be updated!", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if(dialog != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }
}
