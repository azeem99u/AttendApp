package com.example.androidthings.attendapp;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.androidthings.attendapp.databinding.FragmentCustomTimePickerDialogBinding;

import org.greenrobot.eventbus.EventBus;


public class CustomTimePickerDialog extends DialogFragment {
    public static final String TIME_PICKER_TAG = "CustomTimePickerDialog";
    public CustomTimePickerDialog() {}
    private FragmentCustomTimePickerDialogBinding binding;
    DurationPickerListener callBackListener;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCustomTimePickerDialogBinding.inflate(inflater);
        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setCancelable(false);
        binding.minutesNumberPicker.setMaxValue(4);
        binding.minutesNumberPicker.setMinValue(1);
        binding.minutesNumberPicker.setDisplayedValues(new String[] { "00", "15","30","45"});
        binding.minutesNumberPicker.setWrapSelectorWheel(true);
        binding.hoursNumberPicker.setMaxValue(6);
        binding.hoursNumberPicker.setMinValue(0);
        binding.hoursNumberPicker.setWrapSelectorWheel(true);
        binding.buttonOk.setOnClickListener(view1 -> {

            int hours = binding.hoursNumberPicker.getValue();
            int index = binding.minutesNumberPicker.getValue();

            String sHours = "0"+hours+" h";
            String minutes = null;
            switch (index){
                case 1:
                    minutes = "";
                    break;
                case 2:
                    minutes = " 15 minutes";
                    break;
                case 3:
                    minutes = " 30 minutes";
                    break;
                case 4:
                    minutes = " 45 minutes";
                    break;
            }
            callBackListener.dataMessage(sHours+minutes);
            dismiss();
        });


        binding.buttonCancel.setOnClickListener(view1 -> {
            dismiss();
        });
    }

    public interface DurationPickerListener {
        void dataMessage(String duration);
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (requireActivity() instanceof DurationPickerListener){
            callBackListener = (DurationPickerListener) requireActivity();
        }
    }
}