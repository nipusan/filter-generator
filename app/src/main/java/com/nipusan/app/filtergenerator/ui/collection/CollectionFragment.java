package com.nipusan.app.filtergenerator.ui.collection;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nipusan.app.filtergenerator.FormCollectionsActivity;
import com.nipusan.app.filtergenerator.MainActivity;
import com.nipusan.app.filtergenerator.R;
import com.nipusan.app.filtergenerator.adapter.CollectionAdapter;
import com.nipusan.app.filtergenerator.databinding.FragmentCollectionBinding;
import com.nipusan.app.filtergenerator.databinding.FragmentFormCollectionBinding;
import com.nipusan.app.filtergenerator.entity.CollectionEntity;
import com.nipusan.app.filtergenerator.utils.Constants;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class CollectionFragment extends Fragment implements Constants {

    private CollectionViewModel collectionViewModel;
    private FragmentCollectionBinding binding;
    private FragmentFormCollectionBinding formCollectionBinding;

    private FloatingActionButton btnAddCollection;
    private RecyclerView rvCollections;
    private SharedPreferences preferences;

    CollectionAdapter collectionAdapter;
    DatabaseReference database = FirebaseDatabase.getInstance().getReference();
    ArrayList<CollectionEntity> listCollection;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        collectionViewModel =
                new ViewModelProvider(this).get(CollectionViewModel.class);

        binding = FragmentCollectionBinding.inflate(inflater, container, false);
        formCollectionBinding = FragmentFormCollectionBinding.inflate(inflater, container, false);
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

                    startActivity(new Intent(getContext(), FormCollectionsActivity.class));

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

    private void loadData() {
        String userUid = preferences.getString(USER_UID, "");
        database.child(ENTITY_COLLECTION)
                .orderByChild(COLLECTION_FIELD_OWNER)
                .equalTo(userUid)
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                listCollection = new ArrayList<>();
                for (DataSnapshot item : snapshot.getChildren()){
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



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}