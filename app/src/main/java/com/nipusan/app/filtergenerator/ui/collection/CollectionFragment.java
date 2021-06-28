package com.nipusan.app.filtergenerator.ui.collection;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nipusan.app.filtergenerator.R;
import com.nipusan.app.filtergenerator.adapter.CollectionAdapter;
import com.nipusan.app.filtergenerator.databinding.FragmentCollectionBinding;
import com.nipusan.app.filtergenerator.entity.BlockEntity;
import com.nipusan.app.filtergenerator.entity.CollectionEntity;
import com.nipusan.app.filtergenerator.utils.Constants;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * logic for the crud of the collection entity
 *
 * @author nipusan
 * @version 0.0.1
 * @apiNote Fragment
 */
public class CollectionFragment extends Fragment implements Constants {

    private FragmentCollectionBinding binding;
    private FloatingActionButton btnAddCollection;
    private RecyclerView rvCollections;
    private SharedPreferences preferences;

    CollectionAdapter collectionAdapter;
    DatabaseReference database = FirebaseDatabase.getInstance().getReference();
    ArrayList<CollectionEntity> listCollection;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentCollectionBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        btnAddCollection = binding.btnAddCollection;

        try {
            preferences = getContext().getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        } catch (Exception e) {
            Log.e(TAG_EXCEPTION, e.getMessage());
        }

        btnAddCollection.setOnClickListener(new View.OnClickListener() {
            /**
             * Called when a view has been clicked.
             *
             * @param v The view that was clicked.
             */
            @Override
            public void onClick(View v) {
                try {

                    TextView tname, tdesc;
                    Button btnAction;
                    LayoutInflater inflater = LayoutInflater.from(getActivity());
                    final View view = inflater.inflate(R.layout.activity_form_collections, null);

                    AlertDialog.Builder form = new AlertDialog.Builder(getActivity());
                    form.setTitle("Saved");
                    form.setMessage("Saved Collection");

                    tname = view.findViewById(R.id.etName);
                    tdesc = view.findViewById(R.id.etDesc);
                    btnAction = view.findViewById(R.id.btnSave);
                    btnAction.setVisibility(View.GONE);
                    form.setView(view);

                    form.setPositiveButton("Saved", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int i) {

                            dialog.dismiss();
                            String name = tname.getText().toString();
                            String desc = tdesc.getText().toString();
                            String userUid = preferences.getString(USER_UID, "");

                            if (TextUtils.isEmpty(name)) {
                                Toast.makeText(getActivity(), "Name is Empty!", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            if (TextUtils.isEmpty(desc)) {
                                Toast.makeText(getActivity(), "Description is Empty!", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            database.child(ENTITY_COLLECTION)
                                    .push()
                                    .setValue(new CollectionEntity(name, desc, userUid))
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Toast.makeText(view.getContext(), "Saved Collection!", Toast.LENGTH_SHORT).show();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull @NotNull Exception e) {
                                    Toast.makeText(view.getContext(), "Collection could not be saved!", Toast.LENGTH_SHORT).show();
                                }
                            });

                        }
                    });

                    form.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    AlertDialog build = form.create();
                    build.show();

                } catch (Exception e) {
                    Log.e("Exception", e.getMessage());
                }
                Log.i("CollectionFragment", "start onClick!");
            }
        });

        rvCollections = binding.rvCollections;
        rvCollections.setAdapter(new CollectionAdapter(new ArrayList<>(), getActivity()));

        RecyclerView.LayoutManager mLayout = new LinearLayoutManager(getActivity());

        rvCollections.setLayoutManager(mLayout);
        rvCollections.setItemAnimator(new DefaultItemAnimator());

        loadData();

        return root;
    }

    /**
     * find All collections by owner
     *
     */
    private void loadData() {
        String userUid = preferences.getString(USER_UID, "");
        database.child(ENTITY_COLLECTION)
                .orderByChild(COLLECTION_FIELD_OWNER)
                .equalTo(userUid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        listCollection = new ArrayList<>();
                        for (DataSnapshot item : snapshot.getChildren()) {
                            CollectionEntity entity = item.getValue(CollectionEntity.class);
                            entity.setKey(item.getKey());
                            listCollection.add(entity);
                        }
                        collectionAdapter = new CollectionAdapter(listCollection, getActivity());
                        rvCollections.setAdapter(collectionAdapter);
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                });
    }

    /**
     * find All blocks by owner
     */
    private Boolean existBlocksByCollection(String idProject) {
        //  String userUid = preferences.getString(USER_UID, "");
        final Boolean[] ret = {false};
        database.child(ENTITY_BLOCK)
                .orderByChild(BLOCK_FIELD_ID_PROJECT)
                .equalTo(idProject)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {

                        for (DataSnapshot item : snapshot.getChildren()) {
                            BlockEntity entity = item.getValue(BlockEntity.class);
                            entity.setKey(item.getKey());
                            ret[0] = true;
                            break;
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                });
        return ret[0];
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}