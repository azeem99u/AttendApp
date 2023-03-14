package com.example.androidthings.attendapp;

import static com.example.androidthings.attendapp.App.sharedPreferences;
import static com.example.androidthings.attendapp.Constants.BATCH_NAME;
import static com.example.androidthings.attendapp.Constants.BATCH_PREF;
import static com.example.androidthings.attendapp.Constants.DATE;
import static com.example.androidthings.attendapp.Constants.DURATION;
import static com.example.androidthings.attendapp.Constants.DURATION_PREF;
import static com.example.androidthings.attendapp.Constants.MAX_NUMBERS;
import static com.example.androidthings.attendapp.Constants.MAX_PREF;
import static com.example.androidthings.attendapp.Constants.MIN_NUMBERS;
import static com.example.androidthings.attendapp.Constants.MIN_PREF;
import static com.example.androidthings.attendapp.Constants.NAME_PREF;
import static com.example.androidthings.attendapp.Constants.SKIP_NUMBERS;
import static com.example.androidthings.attendapp.Constants.SKIP_PREF;
import static com.example.androidthings.attendapp.Constants.SUBJECT;
import static com.example.androidthings.attendapp.Constants.SUBJECT_PREF;
import static com.example.androidthings.attendapp.Constants.TEACHER_NAME;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.androidthings.attendapp.activities.AttendanceActivity;
import com.example.androidthings.attendapp.databinding.FragmentCreateAttendanceSheetBinding;
import com.example.androidthings.attendapp.models.DialogEvent;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.card.MaterialCardView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Calendar;
import java.util.Objects;


public class CreateAttBottomSheet extends BottomSheetDialogFragment {

    public CreateAttBottomSheet() {}

    public static final String BOTTOM_SHEET_PICKER_TAG = "CreateAttendanceSheet";
    private FragmentCreateAttendanceSheetBinding binding;
    TextWatcher mTextWatcher;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCreateAttendanceSheetBinding.inflate(inflater);
        BottomSheetDialog dialog = (BottomSheetDialog) getDialog();
        Objects.requireNonNull(dialog).getBehavior().setState(BottomSheetBehavior.STATE_EXPANDED);
        return binding.getRoot();
    }


    @SuppressLint("ResourceAsColor")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        manageValidation();
        binding.etDate.setOnClickListener(view1 -> {
            getDate();
        });
        binding.etDuration.setOnClickListener(view12 -> {
            new CustomTimePickerDialog().show(getChildFragmentManager(), CustomTimePickerDialog.TIME_PICKER_TAG);
        });
        binding.cancelBottomSheetBtn.setOnClickListener(view13 -> {
            dismiss();
        });
        binding.closeBottomSheetBtn.setOnClickListener(view15 -> {
            dismiss();
        });

        binding.continueBtn.setOnClickListener(view14 -> {
            String batchName = binding.etBatchName.getText().toString().trim();
            String maxNumbers = binding.etMaxNumber.getText().toString().trim();
            String minNumber = binding.etMinNumber.getText().toString().trim();
            String skipNumbers = binding.etSkipNumber.getText().toString().trim();
            String teacherName = binding.etTeacherName.getText().toString().trim();
            String subjectName = binding.etSubject.getText().toString().trim();
            String duration = binding.etDuration.getText().toString().trim();
            String date = binding.etDate.getText().toString().trim();

            if (skipNumbers.isEmpty()){
                skipNumbers = "0";
            }

            if (subjectName.isEmpty() | teacherName.isEmpty() |minNumber.isEmpty() | maxNumbers.isEmpty() | date.isEmpty()){
                if (subjectName.isEmpty()){
                    binding.subjectCard.setStrokeColor(ContextCompat.getColor(requireContext(),R.color.red));
                }
                if (teacherName.isEmpty()){
                    binding.nameCard.setStrokeColor(ContextCompat.getColor(requireContext(),R.color.red));
                }
                if (minNumber.isEmpty()){
                    binding.minCard.setStrokeColor(ContextCompat.getColor(requireContext(),R.color.red));
                }
                if (maxNumbers.isEmpty()){
                    binding.maxCard.setStrokeColor(ContextCompat.getColor(requireContext(),R.color.red));
                }
                if (date.isEmpty()){
                    binding.dateCard.setStrokeColor(ContextCompat.getColor(requireContext(),R.color.red));
                }
                return;
            }

            Intent intent = new Intent(requireActivity(), AttendanceActivity.class);
            intent.putExtra(BATCH_NAME, batchName);
            intent.putExtra(MAX_NUMBERS, maxNumbers);
            intent.putExtra(MIN_NUMBERS, minNumber);
            intent.putExtra(SKIP_NUMBERS, skipNumbers);
            intent.putExtra(TEACHER_NAME, teacherName);
            intent.putExtra(SUBJECT, subjectName);
            intent.putExtra(DURATION, duration);
            intent.putExtra(DATE, date);
            startActivity(intent);
            dismiss();
        });
    }

    private void manageValidation() {

        binding.etMinNumber.setText(sharedPreferences.getString(MIN_PREF,"1"));
        binding.etMaxNumber.setText(sharedPreferences.getString(MAX_PREF,""));
        binding.etTeacherName.setText(sharedPreferences.getString(NAME_PREF,""));
        binding.etSubject.setText(sharedPreferences.getString(SUBJECT_PREF,""));
        binding.etBatchName.setText(sharedPreferences.getString(BATCH_PREF,""));
        binding.etSkipNumber.setText(sharedPreferences.getString(SKIP_PREF,""));
        binding.etDuration.setText(sharedPreferences.getString(DURATION_PREF,""));


        binding.etMaxNumber.setFilters(new InputFilter[]{
                new MinMaxFilter(1, 180)
        });
        binding.etMinNumber.setFilters(new InputFilter[]{
                new MinMaxFilter(1, 170)
        });

        binding.etBatchName.setOnFocusChangeListener((v, hasFocus) -> {
            if(hasFocus) {
                binding.etBatchName.addTextChangedListener(getWatcher(BATCH_PREF));
            }
            else{
                binding.etBatchName.removeTextChangedListener(mTextWatcher);
            }
        });

        binding.etMinNumber.setOnFocusChangeListener((v, hasFocus) -> {
            if(hasFocus) {
                binding.etMinNumber.addTextChangedListener(getWatcher(binding.minCard,MIN_PREF));
            }
            else{
                binding.etMaxNumber.removeTextChangedListener(mTextWatcher);
            }
        });

        binding.etMaxNumber.setOnFocusChangeListener((v, hasFocus) -> {
            if(hasFocus) {
                binding.etMaxNumber.addTextChangedListener(getWatcher(binding.maxCard,MAX_PREF));
            }
            else{
                binding.etMaxNumber.removeTextChangedListener(mTextWatcher);
            }
        });

        binding.etSkipNumber.setOnFocusChangeListener((v, hasFocus) -> {
            if(hasFocus) {
                binding.etSkipNumber.addTextChangedListener(getWatcher(SKIP_PREF));
            }
            else{
                binding.etSkipNumber.removeTextChangedListener(mTextWatcher);
            }
        });

        binding.etTeacherName.setOnFocusChangeListener((v, hasFocus) -> {
            if(hasFocus) {
                binding.etTeacherName.addTextChangedListener(getWatcher(binding.nameCard,NAME_PREF));
            }
            else{
                binding.etTeacherName.removeTextChangedListener(mTextWatcher);
            }
        });

        binding.etSubject.setOnFocusChangeListener((v, hasFocus) -> {
            if(hasFocus) {
                binding.etSubject.addTextChangedListener(getWatcher(binding.subjectCard,SUBJECT_PREF));
            }
            else{
                binding.etSubject.removeTextChangedListener(mTextWatcher);
            }
        });
        binding.etDuration.addTextChangedListener(getWatcher(DURATION_PREF));
        binding.etDate.addTextChangedListener(getWatcher(binding.dateCard,null));

    }



    private void getDate() {
        final Calendar cldr = Calendar.getInstance();
        int day = cldr.get(Calendar.DAY_OF_MONTH);
        int month = cldr.get(Calendar.MONTH);
        int year = cldr.get(Calendar.YEAR);
        @SuppressLint("SetTextI18n") DatePickerDialog picker = new DatePickerDialog(requireContext(),
                (view, year1, monthOfYear, dayOfMonth) -> binding.etDate.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year1), year, month, day);
        picker.show();
    }


    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onCategoryChangeEvent(DialogEvent event) {
        DialogEvent stickyEvent = EventBus.getDefault().removeStickyEvent(DialogEvent.class);
        if (stickyEvent != null) {
            binding.etDuration.setText(event.getDuration());
        }
    }

    @Override
    public void onStart() {
        EventBus.getDefault().register(this);
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @NonNull
    private TextWatcher getWatcher(MaterialCardView cardView, String prefKey) {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String s = editable.toString().trim();
                if (s.isEmpty()){
                    cardView.setStrokeColor(ContextCompat.getColor(requireContext(),R.color.red));
                }else {
                    cardView.setStrokeColor(ContextCompat.getColor(requireContext(),R.color.card_color));
                }
                if (prefKey != null){
                    sharedPreferences.edit().putString(prefKey,s).apply();
                }

            }
        };
    }

    @NonNull
    private TextWatcher getWatcher(String prefKey) {

        mTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String s = editable.toString().trim();
                sharedPreferences.edit().putString(prefKey, s).apply();
            }
        };
        return mTextWatcher;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mTextWatcher != null){
            binding.etDuration.removeTextChangedListener(mTextWatcher);
            binding.etDate.removeTextChangedListener(mTextWatcher);
        }
    }
}