package com.nipusan.app.filtergenerator.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.nipusan.app.filtergenerator.R;
import com.nipusan.app.filtergenerator.entity.FieldEntity;
import com.nipusan.app.filtergenerator.utils.Constants;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class FieldAdapter extends RecyclerView.Adapter<FieldAdapter.MyViewHolder> implements Constants {

    private List<FieldEntity> cList;
    private Activity activity;

    DatabaseReference database = FirebaseDatabase.getInstance().getReference();

    public FieldAdapter(List<FieldEntity> cList, Activity activity) {
        this.cList = cList;
        this.activity = activity;
    }

    @NonNull
    @NotNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.layout_field_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull MyViewHolder holder, int position) {
        final FieldEntity entity = cList.get(position);
        holder.tvName.setText(entity.getName());
        holder.tvDescription.setText(entity.getDescription());
        String type = "Not Found";
        try {
            type = ARRAY_LIST_BLOCK_TYPE[entity.getType()];
        } catch (Exception e){
            Log.println(Log.ERROR, TAG_EXCEPTION, e.getMessage());
        }
        holder.tvTypeField.setText(type);
        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        database.child(ENTITY_FIELD).child(entity.getKey()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(activity, "Deleted collection!", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull @NotNull Exception e) {
                                Toast.makeText(activity, "Field could not be deleted!", Toast.LENGTH_SHORT).show();
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

        holder.cardField.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                TextView tname, tdesc, tvalue;
                AutoCompleteTextView stype, sblock;
                Button btnAction;
                LayoutInflater inflater = LayoutInflater.from(activity);
                final View view = inflater.inflate(R.layout.activity_form_field, null);

                AlertDialog.Builder form = new AlertDialog.Builder(activity);
                form.setTitle("Update");
                form.setMessage("The type and Block fields cannot be edited after being created");

                stype = view.findViewById(R.id.actType);
                sblock = view.findViewById(R.id.actBlock);
                tname = view.findViewById(R.id.etName);
                tdesc = view.findViewById(R.id.etDesc);
                tvalue = view.findViewById(R.id.etValue);

                btnAction = view.findViewById(R.id.btnSave);
                tname.setText(entity.getName());
                tdesc.setText(entity.getDescription());
                tvalue.setText(entity.getValue());

                /**
                 * TODO: these fields will not be editable for now
                 */
                btnAction.setVisibility(View.INVISIBLE);
                stype.setVisibility(View.INVISIBLE);
                sblock.setVisibility(View.INVISIBLE);


                form.setView(view);


                form.setPositiveButton("Update", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {

                        dialog.dismiss();
                        String name = tname.getText().toString();
                        String desc = tdesc.getText().toString();
                        String value = tvalue.getText().toString();

                        if (TextUtils.isEmpty(name)) {
                            Toast.makeText(activity, "Name is Empty!", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (TextUtils.isEmpty(desc)) {
                            Toast.makeText(activity, "Description is Empty!", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        database.child(ENTITY_FIELD)
                                .child(entity.getKey())
                                .setValue(new FieldEntity(entity.getType(), name, desc, entity.getIdBlock(), value, entity.getOwner()))
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(view.getContext(), "Updated Field!", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull @NotNull Exception e) {
                                Toast.makeText(view.getContext(), "Field could not be updated!", Toast.LENGTH_SHORT).show();
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

    }

    @Override
    public int getItemCount() {
        return cList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvDescription, tvTypeField;
        CardView cardField;
        ImageView btnDelete;


        public MyViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.cardNameField);
            tvDescription = itemView.findViewById(R.id.cardDescField);
            tvTypeField = itemView.findViewById(R.id.cardTypeField);
            cardField = itemView.findViewById(R.id.cardField);
            btnDelete = itemView.findViewById(R.id.btnDeleteField);
        }
    }
}
