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
import android.widget.CheckBox;
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
import com.nipusan.app.filtergenerator.entity.BlockEntity;
import com.nipusan.app.filtergenerator.utils.Constants;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class BlockAdapter extends RecyclerView.Adapter<BlockAdapter.MyViewHolder> implements Constants {

    private List<BlockEntity> cList;
    private Activity activity;

    DatabaseReference database = FirebaseDatabase.getInstance().getReference();

    public BlockAdapter(List<BlockEntity> cList, Activity activity) {
        this.cList = cList;
        this.activity = activity;
    }

    @NonNull
    @NotNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.layout_block_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull MyViewHolder holder, int position) {
        final BlockEntity entity = cList.get(position);
        holder.tvName.setText(entity.getName());
        holder.tvDescription.setText(entity.getDescription());
        String type = "Not Found";
        try {
            type = ARRAY_LIST_BLOCK_TYPE[entity.getType()];
        } catch (Exception e){
            Log.println(Log.ERROR, TAG_EXCEPTION, e.getMessage());
        }
        holder.tvTypeBlock.setText(type);
        if (!entity.getOverallProject().isEmpty() && entity.getOverallProject().equalsIgnoreCase("1")) {
            holder.global.setVisibility(View.VISIBLE);
        } else {
            holder.global.setVisibility(View.GONE);
        }
        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        database.child(ENTITY_BLOCK).child(entity.getKey()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(activity, "Deleted collection!", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull @NotNull Exception e) {
                                Toast.makeText(activity, "Block could not be deleted!", Toast.LENGTH_SHORT).show();
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

        holder.cardBlock.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                TextView tname, tdesc;
                AutoCompleteTextView stype, scollection;
                CheckBox global;
                Button btnAction;
                LayoutInflater inflater = LayoutInflater.from(activity);
                final View view = inflater.inflate(R.layout.activity_form_block, null);

                AlertDialog.Builder form = new AlertDialog.Builder(activity);
                form.setTitle("Update");
                form.setMessage("The type, collection and global block fields cannot be edited after being created");

                stype = view.findViewById(R.id.actType);
                scollection = view.findViewById(R.id.actCollection);
                tname = view.findViewById(R.id.etName);
                tdesc = view.findViewById(R.id.etDesc);
                global = view.findViewById(R.id.cbGlobalBlock);

                btnAction = view.findViewById(R.id.btnSave);
                tname.setText(entity.getName());
                tdesc.setText(entity.getDescription());

                /**
                 * TODO: these fields will not be editable for now
                 */
                btnAction.setVisibility(View.INVISIBLE);
                global.setVisibility(View.INVISIBLE);
                stype.setVisibility(View.INVISIBLE);
                scollection.setVisibility(View.INVISIBLE);


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

                        database.child(ENTITY_BLOCK)
                                .child(entity.getKey())
                                .setValue(new BlockEntity(entity.getType(), entity.getOverallProject(), name, desc, entity.getIdProject(), entity.getOwner()))
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(view.getContext(), "Updated Block!", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull @NotNull Exception e) {
                                Toast.makeText(view.getContext(), "Block could not be updated!", Toast.LENGTH_SHORT).show();
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
        TextView tvName, tvDescription, tvTypeBlock;
        //Spinner stype, scollection;
        ImageView global;

        CardView cardBlock;
        ImageView btnDelete;


        public MyViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.cardNameBlock);
            tvDescription = itemView.findViewById(R.id.cardDescBlock);
            tvTypeBlock = itemView.findViewById(R.id.cardTypeBlock);
            global = itemView.findViewById(R.id.isGlobal);

            //stype = itemView.findViewById(R.id.spBlockType);
            //stype = itemView.findViewById(R.id.spCollection);

            cardBlock = itemView.findViewById(R.id.cardBlock);
            btnDelete = itemView.findViewById(R.id.btnDeleteBlock);
        }
    }
}
