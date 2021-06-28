package com.nipusan.app.filtergenerator.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nipusan.app.filtergenerator.R;
import com.nipusan.app.filtergenerator.entity.BlockEntity;
import com.nipusan.app.filtergenerator.entity.CollectionEntity;
import com.nipusan.app.filtergenerator.utils.Constants;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CollectionAdapter extends RecyclerView.Adapter<CollectionAdapter.MyViewHolder> implements Constants {

    private List<CollectionEntity> cList;
    private Activity activity;
    private SharedPreferences preferences;

    DatabaseReference database = FirebaseDatabase.getInstance().getReference();

    public CollectionAdapter(List<CollectionEntity> cList, Activity activity) {
        this.cList = cList;
        this.activity = activity;
    }

    @NonNull
    @NotNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.layout_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull MyViewHolder holder, int position) {
        final CollectionEntity entity = cList.get(position);
        holder.tvName.setText(entity.getName());
        holder.tvDescription.setText(entity.getDescription());
        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {

                        database.child(ENTITY_BLOCK)
                                .orderByChild(BLOCK_FIELD_ID_PROJECT)
                                .equalTo(entity.getKey())
                                .addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                        boolean exist = false;
                                        for (DataSnapshot item : snapshot.getChildren()) {
                                            BlockEntity entity = item.getValue(BlockEntity.class);
                                            entity.setKey(item.getKey());
                                            exist = true;
                                            break;
                                        }

                                        if (!exist) {
                                            database.child(ENTITY_COLLECTION).child(entity.getKey()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    Toast.makeText(activity, "Deleted collection!", Toast.LENGTH_SHORT).show();
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull @NotNull Exception e) {
                                                    Toast.makeText(activity, "Collection could not be deleted!", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        } else {
                                            Toast.makeText(activity, "There are references to this object in the blocks entity, remove the relationships before attempting to remove this collection", Toast.LENGTH_LONG).show();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull @NotNull DatabaseError error) {
                                        Toast.makeText(activity, "Error: on cancelled!", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).setMessage("Do you want to delete collection " + entity.getName() + " ?");
                builder.show();
            }
        });

        holder.cardCollection.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                TextView tname, tdesc;
                Button btnAction;
                LayoutInflater inflater = LayoutInflater.from(activity);
                final View view = inflater.inflate(R.layout.activity_form_collections, null);

                AlertDialog.Builder form = new AlertDialog.Builder(activity);
                form.setTitle("Update");
                form.setMessage("Update data");

                tname = view.findViewById(R.id.etName);
                tdesc = view.findViewById(R.id.etDesc);
                btnAction = view.findViewById(R.id.btnSave);
                tname.setText(entity.getName());
                tdesc.setText(entity.getDescription());
                btnAction.setVisibility(View.GONE);

                form.setView(view);


                form.setPositiveButton("Update", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {

                        dialog.dismiss();
                        String name = tname.getText().toString();
                        String desc = tdesc.getText().toString();

                        if (TextUtils.isEmpty(name)) {
                            Toast.makeText(activity, "Name is Empty!", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (TextUtils.isEmpty(desc)) {
                            Toast.makeText(activity, "Description is Empty!", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        database.child(ENTITY_COLLECTION)
                                .child(entity.getKey())
                                .setValue(new CollectionEntity(name, desc, entity.getOwner()))
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
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
                });


                form.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                AlertDialog build = form.create();
                build.show();

                return true;
            }
        });

        holder.cardCollection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    preferences = activity.getSharedPreferences("MyPref", Context.MODE_PRIVATE);
                    preferences.edit().putString(COLLECTION_UID, entity.getKey()).apply();
                    preferences.edit().putString(COLLECTION_NAME, entity.getName()).apply();
                    Toast.makeText(activity, "Active Collection", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Log.e(TAG_EXCEPTION, e.getMessage());
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return cList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvDescription;
        CardView cardCollection;
        ImageView btnDelete;


        public MyViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.cardNameCollection);
            tvDescription = itemView.findViewById(R.id.cardDescCollection);
            cardCollection = itemView.findViewById(R.id.cardCollection);
            btnDelete = itemView.findViewById(R.id.btnDeleteCollection);
        }
    }
}
