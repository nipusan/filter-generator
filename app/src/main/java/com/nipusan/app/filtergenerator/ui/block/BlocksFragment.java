package com.nipusan.app.filtergenerator.ui.block;

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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
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
import com.nipusan.app.filtergenerator.adapter.BlockAdapter;
import com.nipusan.app.filtergenerator.databinding.FragmentBlockBinding;
import com.nipusan.app.filtergenerator.entity.BlockEntity;
import com.nipusan.app.filtergenerator.entity.CollectionEntity;
import com.nipusan.app.filtergenerator.utils.Constants;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * logic for the crud of the collection entity
 *
 * @author nipusan
 * @version 0.0.1
 * @apiNote Fragment
 */
public class BlocksFragment extends Fragment implements Constants {

    private FragmentBlockBinding binding;
    private FloatingActionButton btnAddBlock;
    private RecyclerView rvBlocks;
    private SharedPreferences preferences;

    BlockAdapter adapter;
    DatabaseReference database = FirebaseDatabase.getInstance().getReference();
    ArrayList<BlockEntity> listBlock;
    List<CollectionEntity> listCollection;
    ArrayList<String> collectionsName;

    Integer blockTypy = null;
    String collection = null;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentBlockBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        btnAddBlock = binding.btnAddBlock;

        try {
            preferences = getContext().getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        } catch (Exception e) {
            Log.e(TAG_EXCEPTION, e.getMessage());
        }

        btnAddBlock.setOnClickListener(new View.OnClickListener() {
            /**
             * Called when a view has been clicked.
             *
             * @param v The view that was clicked.
             */
            @Override
            public void onClick(View v) {
                try {

                    TextView tname, tdesc;
                    CheckBox isGlobal;
                    AutoCompleteTextView actType, actCollection;

                    String[] blockType = getResources().getStringArray(R.array.bock_type);
                    ArrayAdapter<String> adapterBlockType = new ArrayAdapter<String>(
                            getContext(),
                            R.layout.block_list,
                            blockType
                    );

                    Button btnAction;
                    LayoutInflater inflater = LayoutInflater.from(getActivity());
                    final View view = inflater.inflate(R.layout.activity_form_block, null);

                    AlertDialog.Builder form = new AlertDialog.Builder(getActivity());
                    form.setTitle("Saved");
                    form.setMessage("Saved Block");

                    tname = view.findViewById(R.id.etName);
                    tdesc = view.findViewById(R.id.etDesc);
                    isGlobal = view.findViewById(R.id.cbGlobalBlock);
                    btnAction = view.findViewById(R.id.btnSave);
                    btnAction.setVisibility(View.GONE);

                    actType = view.findViewById(R.id.actType);
                    actType.setAdapter(adapterBlockType);
                    actType.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            String selection = (String)parent.getItemAtPosition(position);
                            blockTypy = position;
                            Log.println(Log.INFO, TAG_EVENT_CLICK, selection);
                            Log.println(Log.INFO, TAG_EVENT_CLICK, "selection [" + position + "]");
                        }
                    });

                    actCollection = view.findViewById(R.id.actCollection);

                    getCollectionByOwner(actCollection);
                    
                    form.setView(view);

                    form.setPositiveButton("Saved", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int i) {

                            dialog.dismiss();
                            String name = tname.getText().toString();
                            String desc = tdesc.getText().toString();
                            Boolean isGlobalBlock = isGlobal.isChecked();

                            String userUid = preferences.getString(USER_UID, "");


                            if (TextUtils.isEmpty(name)) {
                                Toast.makeText(getActivity(), "Name is Empty!", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            if (TextUtils.isEmpty(desc)) {
                                Toast.makeText(getActivity(), "Description is Empty!", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            if (TextUtils.isEmpty(collection)) {
                                Toast.makeText(getActivity(), "Collection is Empty!", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            if (blockTypy == null) {
                                Toast.makeText(getActivity(), "Block Type is Empty!", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            database.child(ENTITY_BLOCK)
                                    .push()//FIXME collection;
                                    .setValue(new BlockEntity(blockTypy, isGlobalBlock ? "1" : "0", name, desc, collection, userUid))
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Toast.makeText(view.getContext(), "Saved Block!", Toast.LENGTH_SHORT).show();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull @NotNull Exception e) {
                                    Toast.makeText(view.getContext(), "Block could not be saved!", Toast.LENGTH_SHORT).show();
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
                Log.i("BlockFragment", "start onClick!");
            }
        });

        rvBlocks = binding.rvBlocks;
        rvBlocks.setAdapter(new BlockAdapter(new ArrayList<>(), getActivity()));

        RecyclerView.LayoutManager mLayout = new LinearLayoutManager(getActivity());

        rvBlocks.setLayoutManager(mLayout);
        rvBlocks.setItemAnimator(new DefaultItemAnimator());

        loadData();


        return root;
    }


    /**
     * find All blocks by owner
     */
    private void loadData() {
        String userUid = preferences.getString(USER_UID, "");
        database.child(ENTITY_BLOCK)
                .orderByChild(BLOCK_FIELD_OWNER)
                .equalTo(userUid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        listBlock = new ArrayList<>();
                        for (DataSnapshot item : snapshot.getChildren()) {
                            BlockEntity entity = item.getValue(BlockEntity.class);
                            entity.setKey(item.getKey());
                            listBlock.add(entity);
                        }
                        adapter = new BlockAdapter(listBlock, getActivity());
                        rvBlocks.setAdapter(adapter);
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                });
    }


    /**
     * find All collections by owner
     *
     */
    private void getCollectionByOwner(AutoCompleteTextView actCollection) {
        String userUid = preferences.getString(USER_UID, "");
        database.child(ENTITY_COLLECTION)
                .orderByChild(COLLECTION_FIELD_OWNER)
                .equalTo(userUid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        listCollection = new ArrayList<>();
                        collectionsName = new ArrayList<String>();
                        for (DataSnapshot item : snapshot.getChildren()) {
                            CollectionEntity entity = item.getValue(CollectionEntity.class);
                            entity.setKey(item.getKey());
                            listCollection.add(entity);
                            collectionsName.add(entity.getName());
                            Log.println(Log.INFO, TAG_LIST, item.toString());
                        }

                        if(listCollection != null && listCollection.size() >= 1){
                            ArrayAdapter adapterCollection = new ArrayAdapter(
                                    getContext(),
                                    R.layout.block_list,
                                    collectionsName
                            );
                            actCollection.setAdapter(adapterCollection);
                            actCollection.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    String selection = (String)parent.getItemAtPosition(position);
                                    Log.println(Log.INFO, TAG_EVENT_CLICK, selection);
                                    Log.println(Log.INFO, TAG_EVENT_CLICK, listCollection.get(position).toString());

                                    collection = listCollection.get(position).getKey();
                                }
                            });
                        } else {
                            Toast.makeText(getContext(), "Not Found Collections", Toast.LENGTH_SHORT).show();
                        }
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