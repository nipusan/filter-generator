package com.nipusan.app.filtergenerator.ui.collection;

import android.content.Intent;
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
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.nipusan.app.filtergenerator.R;
import com.nipusan.app.filtergenerator.databinding.FragmentCollectionBinding;
import com.nipusan.app.filtergenerator.databinding.FragmentFormCollectionBinding;

public class CollectionFragment extends Fragment {

    private CollectionViewModel collectionViewModel;
    private FragmentCollectionBinding binding;
    private FragmentFormCollectionBinding formCollectionBinding;

    private FloatingActionButton btnAddCollection;
    private RecyclerView rvCollections;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        collectionViewModel =
                new ViewModelProvider(this).get(CollectionViewModel.class);

        binding = FragmentCollectionBinding.inflate(inflater, container, false);
        formCollectionBinding = FragmentFormCollectionBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        btnAddCollection = binding.btnAddCollection;
        rvCollections = binding.rvCollections;


        btnAddCollection.setOnClickListener(new View.OnClickListener() {
            /**
             * Called when a view has been clicked.
             *
             * @param v The view that was clicked.
             */
            @Override
            public void onClick(View v) {
                try {
                    FormCollectionFragment nextFragment = new FormCollectionFragment();
                    getActivity().getSupportFragmentManager().beginTransaction().
                            replace(R.id.nav_host_fragment_content_main, nextFragment, "Add Collection")
                            .addToBackStack(null)
                            .commit();

                } catch (Exception e) {
                    Log.e("Exception", e.getMessage());
                }
                Log.i("CollectionFragment", "start onClick!");
            }
        });

        collectionViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                //textView.setText(s);
            }
        });
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}