package com.nipusan.app.filtergenerator;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.nipusan.app.filtergenerator.databinding.FragmentBlockBinding;
import com.nipusan.app.filtergenerator.utils.Constants;

public class FormBlockActivity extends AppCompatActivity implements Constants {


    private FragmentBlockBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_form_block);

        binding = FragmentBlockBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String[] blockType = getResources().getStringArray(R.array.bock_type);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this,
                R.layout.block_list,
                blockType
        );

        TextView tvInfo = findViewById(R.id.tvInfo);
        tvInfo.setText("types: " + adapter.getCount());

        Log.println(Log.INFO, TAG_LIST, "types: " + adapter.getCount());

        AutoCompleteTextView actType = findViewById(R.id.actType);
        actType.setAdapter(adapter);

        super.onCreate(savedInstanceState);
    }
}