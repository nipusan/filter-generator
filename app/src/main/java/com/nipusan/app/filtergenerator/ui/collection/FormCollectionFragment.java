package com.nipusan.app.filtergenerator.ui.collection;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.nipusan.app.filtergenerator.R;
import com.nipusan.app.filtergenerator.databinding.FragmentCollectionBinding;
import com.nipusan.app.filtergenerator.databinding.FragmentFormCollectionBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FormCollectionFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FormCollectionFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private FloatingActionButton btnBackCollection;
    private FragmentFormCollectionBinding binding;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public FormCollectionFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FormCollectionFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FormCollectionFragment newInstance(String param1, String param2) {
        FormCollectionFragment fragment = new FormCollectionFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentFormCollectionBinding.inflate(inflater, container, false);
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_form_collection, container, false);
    }
}