package com.nipusan.app.filtergenerator.ui.home;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nipusan.app.filtergenerator.R;
import com.nipusan.app.filtergenerator.adapter.BlockAdapter;
import com.nipusan.app.filtergenerator.databinding.FragmentHomeBinding;
import com.nipusan.app.filtergenerator.entity.BlockEntity;
import com.nipusan.app.filtergenerator.entity.CollectionEntity;
import com.nipusan.app.filtergenerator.entity.FieldEntity;
import com.nipusan.app.filtergenerator.utils.Constants;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment implements Constants {

    private FragmentHomeBinding binding;
    private SharedPreferences preferences;
    private CollectionEntity tempCollection;
    private BlockEntity tempBlock;
    private FieldEntity tempField;

    private TextView collectionName, infoFields;
    private LinearLayout llActHomeBlock, llActHomeField;
    private AutoCompleteTextView actBlock, actField;

    BlockAdapter adapter;
    DatabaseReference database = FirebaseDatabase.getInstance().getReference();
    List<BlockEntity> listBlock;
    List<FieldEntity> listField;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        collectionName = binding.tvCollectionActive;
        actBlock = binding.actHomeBlock;
        actField = binding.actHomeField;
        infoFields = binding.tvInfoFields;
        llActHomeBlock = binding.llActHomeBlock;
        llActHomeField = binding.llActHomeField;

        try {
            preferences = getContext().getSharedPreferences("MyPref", Context.MODE_PRIVATE);
            String key = preferences.getString(COLLECTION_UID, "");
            String name = preferences.getString(COLLECTION_NAME, "");
            tempCollection = new CollectionEntity();
            tempCollection.setKey(key);
            tempCollection.setName(name);
            if (!name.isEmpty()) {
                collectionName.setText("Active collection: " + name);
                llActHomeField.setVisibility(View.VISIBLE);
                llActHomeBlock.setVisibility(View.VISIBLE);
                loadBlock();
            } else {
                collectionName.setText("No Active collection!");
                llActHomeField.setVisibility(View.GONE);
                llActHomeBlock.setVisibility(View.GONE);
            }


        } catch (Exception e) {
            Log.e(TAG_EXCEPTION, e.getMessage());
        }

        return root;
    }


    /**
     * find All blocks by Collection
     */
    private void loadBlock() {
        try {
            Log.println(Log.INFO, TAG_EVENT_CLICK, tempCollection.toString());
            database.child(ENTITY_BLOCK)
                    .orderByChild(BLOCK_FIELD_ID_PROJECT)
                    .equalTo(tempCollection.getKey())
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                            listBlock = new ArrayList<>();
                            for (DataSnapshot item : snapshot.getChildren()) {
                                BlockEntity entity = item.getValue(BlockEntity.class);
                                entity.setKey(item.getKey());
                                listBlock.add(entity);
                            }
                            Log.println(Log.INFO, TAG_LIST, "list Bock: " + listBlock.size());

                            if (listBlock.size() <= 0) {
                                llActHomeBlock.setVisibility(View.GONE);
                                llActHomeField.setVisibility(View.GONE);
                            }

                            ArrayAdapter adapterBlockType = new ArrayAdapter(
                                    getContext(),
                                    R.layout.block_list,
                                    listBlock
                            );

                            actBlock.setAdapter(adapterBlockType);
                            actBlock.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    Log.println(Log.INFO, TAG_EVENT_CLICK, listBlock.get(position).toString());
                                    tempBlock = listBlock.get(position);
                                    loadField();
                                }
                            });
                        }

                        @Override
                        public void onCancelled(@NonNull @NotNull DatabaseError error) {

                        }
                    });
        } catch (Exception e) {
            Log.e(TAG_EXCEPTION, e.getMessage());
        }
    }

    /**
     * find All fields by Block
     */
    private void loadField() {
        String userUid = preferences.getString(USER_UID, "");
        database.child(ENTITY_FIELD)
                .orderByChild(FIELD_FIELD_ID_BLOCK)
                .equalTo(tempBlock.getKey())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        listField = new ArrayList<>();
                        String textInfo = "";
                        for (DataSnapshot item : snapshot.getChildren()) {
                            FieldEntity entity = item.getValue(FieldEntity.class);
                            entity.setKey(item.getKey());
                            listField.add(entity);
                            textInfo += "Field " + entity.getName() + "\n";
                            textInfo += "value " + entity.getValue() + "\n\n";
                            Log.println(Log.INFO, TAG_LIST, entity.toString());
                        }
                        Log.println(Log.INFO, TAG_LIST, "list Field: " + listField.size());

                        if (listField.size() <= 0) {
                            llActHomeField.setVisibility(View.GONE);
                        }

                        ArrayAdapter adapterFieldType = new ArrayAdapter(
                                getContext(),
                                R.layout.field_list,
                                listField
                        );

                        actField.setAdapter(adapterFieldType);
                        infoFields.setText(textInfo);
                        actField.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                Log.println(Log.INFO, TAG_EVENT_CLICK, listField.get(position).toString());
                                tempField = listField.get(position);
                                String info = "";
                                info += "Field " + listField.get(position).getName() + "\n";
                                info += "value " + listField.get(position).getValue() + "\n\n";
                                infoFields.setText(info);
                                loadFiledData();
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                });
    }

    private void loadFiledData() {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}