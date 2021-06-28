package com.nipusan.app.filtergenerator.ui.field;

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
import com.nipusan.app.filtergenerator.adapter.FieldAdapter;
import com.nipusan.app.filtergenerator.databinding.FragmentFieldBinding;
import com.nipusan.app.filtergenerator.entity.BlockEntity;
import com.nipusan.app.filtergenerator.entity.FieldEntity;
import com.nipusan.app.filtergenerator.utils.Constants;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * logic for the crud of the Field entity
 *
 * @author nipusan
 * @version 0.0.1
 * @apiNote Fragment
 */
public class FieldFragment extends Fragment implements Constants {

    private FragmentFieldBinding binding;
    private FloatingActionButton btnAddField;
    private RecyclerView rvFields;
    private SharedPreferences preferences;

    FieldAdapter adapter;
    DatabaseReference database = FirebaseDatabase.getInstance().getReference();
    ArrayList<FieldEntity> listField;
    List<BlockEntity> listBlock;
    ArrayList<String> blocksName;

    Integer fieldTypy = null;
    String block = null;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentFieldBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        btnAddField = binding.btnAddField;

        try {
            preferences = getContext().getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        } catch (Exception e) {
            Log.e(TAG_EXCEPTION, e.getMessage());
        }

        btnAddField.setOnClickListener(new View.OnClickListener() {
            /**
             * Called when a view has been clicked.
             *
             * @param v The view that was clicked.
             */
            @Override
            public void onClick(View v) {
                try {

                    TextView tname, tdesc, tvalue;
                    AutoCompleteTextView actType, actBlock;

                    String[] fieldType = getResources().getStringArray(R.array.bock_type);
                    ArrayAdapter<String> adapterFieldType = new ArrayAdapter<String>(
                            getContext(),
                            R.layout.field_list,
                            fieldType
                    );

                    Button btnAction;
                    LayoutInflater inflater = LayoutInflater.from(getActivity());
                    final View view = inflater.inflate(R.layout.activity_form_field, null);

                    AlertDialog.Builder form = new AlertDialog.Builder(getActivity());
                    form.setTitle("Saved");
                    form.setMessage("Saved Field");

                    tname = view.findViewById(R.id.etName);
                    tdesc = view.findViewById(R.id.etDesc);
                    tvalue = view.findViewById(R.id.etValue);
                    btnAction = view.findViewById(R.id.btnSave);
                    btnAction.setVisibility(View.GONE);

                    actType = view.findViewById(R.id.actType);
                    actType.setAdapter(adapterFieldType);
                    actType.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            String selection = (String) parent.getItemAtPosition(position);
                            fieldTypy = position;
                            Log.println(Log.INFO, TAG_EVENT_CLICK, selection);
                            Log.println(Log.INFO, TAG_EVENT_CLICK, "selection [" + position + "]");
                        }
                    });

                    actBlock = view.findViewById(R.id.actBlock);

                    getBlockByOwner(actBlock);

                    form.setView(view);

                    form.setPositiveButton("Saved", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int i) {

                            dialog.dismiss();
                            String name = tname.getText().toString();
                            String desc = tdesc.getText().toString();
                            String value = tvalue.getText().toString();

                            String userUid = preferences.getString(USER_UID, "");


                            if (TextUtils.isEmpty(name)) {
                                Toast.makeText(getActivity(), "Name is Empty!", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            if (TextUtils.isEmpty(desc)) {
                                Toast.makeText(getActivity(), "Description is Empty!", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            if (TextUtils.isEmpty(block)) {
                                Toast.makeText(getActivity(), "Block is Empty!", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            if (fieldTypy == null) {
                                Toast.makeText(getActivity(), "Field Type is Empty!", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            database.child(ENTITY_FIELD)
                                    .push()//FIXME block;
                                    .setValue(new FieldEntity(fieldTypy, name, desc, value, block, userUid))
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Toast.makeText(view.getContext(), "Saved Field!", Toast.LENGTH_SHORT).show();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull @NotNull Exception e) {
                                    Toast.makeText(view.getContext(), "Field could not be saved!", Toast.LENGTH_SHORT).show();
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
                Log.i("FieldFragment", "start onClick!");
            }
        });

        rvFields = binding.rvFields;
        rvFields.setAdapter(new FieldAdapter(new ArrayList<>(), getActivity()));

        RecyclerView.LayoutManager mLayout = new LinearLayoutManager(getActivity());

        rvFields.setLayoutManager(mLayout);
        rvFields.setItemAnimator(new DefaultItemAnimator());

        loadData();

        return root;
    }


    /**
     * find All fields by owner
     */
    private void loadData() {
        String userUid = preferences.getString(USER_UID, "");
        database.child(ENTITY_FIELD)
                .orderByChild(BLOCK_FIELD_OWNER)
                .equalTo(userUid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        listField = new ArrayList<>();
                        for (DataSnapshot item : snapshot.getChildren()) {
                            FieldEntity entity = item.getValue(FieldEntity.class);
                            entity.setKey(item.getKey());
                            listField.add(entity);
                        }
                        adapter = new FieldAdapter(listField, getActivity());
                        rvFields.setAdapter(adapter);
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                });
    }


    /**
     * find All Blocks by owner
     */
    private void getBlockByOwner(AutoCompleteTextView actBlock) {
        try {
            String userUid = preferences.getString(USER_UID, "");
            database.child(ENTITY_BLOCK)
                    .orderByChild(BLOCK_FIELD_OWNER)
                    .equalTo(userUid)
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                            listBlock = new ArrayList<>();
                            blocksName = new ArrayList<String>();
                            for (DataSnapshot item : snapshot.getChildren()) {
                                BlockEntity entity = item.getValue(BlockEntity.class);
                                entity.setKey(item.getKey());
                                listBlock.add(entity);
                                blocksName.add(entity.getName());
                                Log.println(Log.INFO, TAG_LIST, item.toString());
                            }

                            if (listBlock != null && listBlock.size() >= 1) {
                                ArrayAdapter adapterBlock = new ArrayAdapter(
                                        getContext(),
                                        R.layout.field_list,
                                        blocksName
                                );
                                actBlock.setAdapter(adapterBlock);
                                actBlock.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        String selection = (String) parent.getItemAtPosition(position);
                                        Log.println(Log.INFO, TAG_EVENT_CLICK, selection);
                                        Log.println(Log.INFO, TAG_EVENT_CLICK, listBlock.get(position).toString());

                                        block = listBlock.get(position).getKey();
                                    }
                                });
                            } else {
                                Toast.makeText(getContext(), "Not Found Blocks", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull @NotNull DatabaseError error) {

                        }
                    });
        } catch (Exception e) {
            Log.println(Log.ERROR, TAG_EXCEPTION, e.getMessage());
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}