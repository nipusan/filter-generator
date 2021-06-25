package com.nipusan.app.filtergenerator.ui.block;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class BlocksViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public BlocksViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is block fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}