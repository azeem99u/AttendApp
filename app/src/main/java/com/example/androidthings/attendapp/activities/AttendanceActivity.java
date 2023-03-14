package com.example.androidthings.attendapp.activities;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.os.Build.VERSION.SDK_INT;
import static com.example.androidthings.attendapp.Constants.BATCH_NAME;
import static com.example.androidthings.attendapp.Constants.DATE;
import static com.example.androidthings.attendapp.Constants.DURATION;
import static com.example.androidthings.attendapp.Constants.MAX_NUMBERS;
import static com.example.androidthings.attendapp.Constants.MIN_NUMBERS;
import static com.example.androidthings.attendapp.Constants.SKIP_NUMBERS;
import static com.example.androidthings.attendapp.Constants.SUBJECT;
import static com.example.androidthings.attendapp.Constants.TEACHER_NAME;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.androidthings.attendapp.CustomTimePickerDialog;
import com.example.androidthings.attendapp.HelperFunctions;
import com.example.androidthings.attendapp.adapters.MyAdapter;
import com.example.androidthings.attendapp.PDFConverter;
import com.example.androidthings.attendapp.models.PdfDetails;
import com.example.androidthings.attendapp.R;
import com.example.androidthings.attendapp.models.RollNumber;
import com.example.androidthings.attendapp.databinding.ActivityAttendanceBinding;
import com.google.android.material.textfield.TextInputEditText;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class AttendanceActivity extends AppCompatActivity implements CustomTimePickerDialog.DurationPickerListener {

    private ActivityAttendanceBinding binding;
    TextWatcher mTextWatcher;
    String batchName;
    String minNumber;
    String maxNumbers;
    String skipNumbers;
    ArrayList<Integer> skipNumbersList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAttendanceBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        handleValidation();
        binding.saveBtn.setOnClickListener(view -> {
            String teacherName = binding.nameEt.getText().toString().trim();
            String subject = binding.subjectEt.getText().toString().trim();
            String time = binding.timeEt.getText().toString().trim();
            String date = binding.dateEt.getText().toString().trim();
            if (teacherName.isEmpty()|subject.isEmpty()){
                if (teacherName.isEmpty()){
                    binding.nameEt.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_error_24, 0);
                }
                if (subject.isEmpty()){
                    binding.subjectEt.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_error_24, 0);
                }
                Toast.makeText(this, "Full fill top requirement", Toast.LENGTH_SHORT).show();
                return;
            }
            if (checkPermission()) {
                PdfDetails pdfDetails = new PdfDetails(teacherName,batchName, subject, time, date);
                Executor mExecutor = Executors.newSingleThreadExecutor();
                mExecutor.execute(() -> {
                    createPdfAndFile(pdfDetails, MyAdapter.getRollNumbers());
                    new Handler(Looper.getMainLooper()).post(this::showProgressDialog);
                    new Handler(Looper.getMainLooper()).postDelayed(this::finish,2000);
                });
            } else {
                showPermissionDialog();
            }
        });


        if (getIntent().getExtras() != null) {
            batchName = getIntent().getStringExtra(BATCH_NAME);
            minNumber = getIntent().getStringExtra(MIN_NUMBERS);
            maxNumbers = getIntent().getStringExtra(MAX_NUMBERS);
            skipNumbers = getIntent().getStringExtra(SKIP_NUMBERS);
            binding.nameEt.setText(getIntent().getStringExtra(TEACHER_NAME));
            binding.timeEt.setText(getIntent().getStringExtra(DURATION));
            binding.dateEt.setText(getIntent().getStringExtra(DATE));
            binding.subjectEt.setText(getIntent().getStringExtra(SUBJECT));
            String[] split = skipNumbers.split(",");
            for (String s : split) {
                skipNumbersList.add(Integer.parseInt(s));
            }
            Log.d("mytag", "onCreate: " + skipNumbersList.toString());
            StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL);
            binding.recyclerView.setLayoutManager(staggeredGridLayoutManager);
            MyAdapter myAdapter = new MyAdapter(getRollNumbers(Integer.parseInt(minNumber), Integer.parseInt(maxNumbers), batchName+" "));
            myAdapter.setHasStableIds(true);
            binding.recyclerView.setAdapter(myAdapter);
            binding.dateEt.setOnClickListener(v -> getDate());
            binding.timeEt.setOnClickListener(view -> {
                new CustomTimePickerDialog().show(getSupportFragmentManager(), CustomTimePickerDialog.TIME_PICKER_TAG);
            });
        }
    }

    private void handleValidation() {

        binding.nameEt.setOnFocusChangeListener((v, hasFocus) -> {
            if(hasFocus) {
                binding.nameEt.addTextChangedListener(getWatcher(binding.nameEt));
            }
            else{
                binding.nameEt.removeTextChangedListener(mTextWatcher);
            }
        });

        binding.subjectEt.setOnFocusChangeListener((v, hasFocus) -> {
            if(hasFocus) {
                binding.subjectEt.addTextChangedListener(getWatcher(binding.subjectEt));
            }
            else{
                binding.subjectEt.removeTextChangedListener(mTextWatcher);
            }
        });

    }

    @NonNull
    private TextWatcher getWatcher(TextInputEditText et) {

        mTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!et.getText().toString().trim().isEmpty()) {
                    et.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                } else {
                    et.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_error_24, 0);
                }
            }
        };
        return mTextWatcher;
    }

    private void createPdfAndFile(PdfDetails pdfDetails, ArrayList<RollNumber> rollNumbers) {
        PdfDocument document = new PdfDocument();


        int totalSize = rollNumbers.size();

        if (totalSize<=60 | totalSize<60){
            PDFConverter pdfConverter = new PDFConverter();
            PdfDocument.Page page = pdfConverter.createPdf(pdfDetails, rollNumbers, document, 1, 1);
            document.finishPage(page);
        }
        if (totalSize>=61 && totalSize<=60*2){
            ArrayList<RollNumber> pageList1 = new ArrayList<>();
            for (int i = 0; i < 60; i++) {
                pageList1.add(rollNumbers.get(i));
            }
            PDFConverter pdfConverter = new PDFConverter();
            PdfDocument.Page page = pdfConverter.createPdf(pdfDetails, pageList1, document, 1, 1);
            document.finishPage(page);

            ArrayList<RollNumber> pageList2 = new ArrayList<>();
            for (int i = 60; i < rollNumbers.size(); i++) {
                pageList2.add(rollNumbers.get(i));
            }
            PdfDocument.Page page2 = pdfConverter.createPdf(pdfDetails, pageList2, document, 2, 61);
            document.finishPage(page2);
        }

        if (totalSize>=121 && totalSize<=60*3){
            ArrayList<RollNumber> pageList1 = new ArrayList<>();
            for (int i = 0; i < 60; i++) {
                pageList1.add(rollNumbers.get(i));
            }
            PDFConverter pdfConverter = new PDFConverter();
            PdfDocument.Page page = pdfConverter.createPdf(pdfDetails, pageList1, document, 1, 1);
            document.finishPage(page);

            ArrayList<RollNumber> pageList2 = new ArrayList<>();
            for (int i = 60; i < 120; i++) {
                pageList2.add(rollNumbers.get(i));
            }
            PdfDocument.Page page2 = pdfConverter.createPdf(pdfDetails, pageList2, document, 2, 61);
            document.finishPage(page2);

            ArrayList<RollNumber> pageList3 = new ArrayList<>();
            for (int i = 120; i < rollNumbers.size(); i++) {
                pageList3.add(rollNumbers.get(i));
            }
            PdfDocument.Page page3 = pdfConverter.createPdf(pdfDetails, pageList3, document, 3, 121);
            document.finishPage(page3);
        }

        try {
            File filePath = HelperFunctions.getFile(System.currentTimeMillis()+".pdf");
            document.writeTo(new FileOutputStream(filePath));
            document.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void getDate() {
        final Calendar cldr = Calendar.getInstance();
        int day = cldr.get(Calendar.DAY_OF_MONTH);
        int month = cldr.get(Calendar.MONTH);
        int year = cldr.get(Calendar.YEAR);
        @SuppressLint("SetTextI18n") DatePickerDialog picker = new DatePickerDialog(AttendanceActivity.this,
                (view, year1, monthOfYear, dayOfMonth) -> binding.dateEt.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year1), year, month, day);
        picker.show();
    }


    ArrayList<RollNumber> getRollNumbers(int minSize, int maxSize, String style) {

        ArrayList<RollNumber> rollNumbers = new ArrayList<>();
        for (int i = minSize; i <= maxSize; i++) {
            if (skipNumbersList.contains(i)) {
                continue;
            }
            rollNumbers.add(new RollNumber(style + i, "Present"));
        }
        return rollNumbers;
    }

    @Override
    public void dataMessage(String duration) {
        binding.timeEt.setText(duration);
    }






    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Closing Activity")
                .setMessage("Are you sure you want to close this activity?")
                .setPositiveButton("Yes", (dialog, which) -> finish())
                .setNegativeButton("No", null)
                .show();
    }


    public void showProgressDialog() {

        int llPadding = 30;
        LinearLayout ll = new LinearLayout(this);
        ll.setOrientation(LinearLayout.HORIZONTAL);
        ll.setPadding(llPadding, llPadding, llPadding, llPadding);
        ll.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams llParam = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        llParam.gravity = Gravity.CENTER;
        ll.setLayoutParams(llParam);

        ProgressBar progressBar = new ProgressBar(this);
        progressBar.setIndeterminate(true);
        progressBar.setPadding(0, 0, llPadding, 0);
        progressBar.setLayoutParams(llParam);

        llParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        llParam.gravity = Gravity.CENTER;
        TextView tvText = new TextView(this);
        tvText.setText("Loading ...");
        tvText.setTextColor(Color.parseColor("#000000"));
        tvText.setTextSize(20);
        tvText.setLayoutParams(llParam);

        ll.addView(progressBar);
        ll.addView(tvText);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setView(ll);

        AlertDialog dialog = builder.create();
        dialog.show();
        Window window = dialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.copyFrom(dialog.getWindow().getAttributes());
            layoutParams.width = LinearLayout.LayoutParams.WRAP_CONTENT;
            layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
            dialog.getWindow().setAttributes(layoutParams);
        }
    }



    private void showPermissionDialog() {
        if (SDK_INT >= Build.VERSION_CODES.R) {
            try {
                Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                intent.addCategory("android.intent.category.DEFAULT");
                intent.setData(Uri.parse(String.format("package:%s", getApplicationContext().getPackageName())));
                startActivityForResult(intent, 2000);
            } catch (Exception e) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                startActivityForResult(intent, 2000);

            }

        } else
            ActivityCompat.requestPermissions(this, new String[]{WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE}, 333);
    }

    private boolean checkPermission() {
        if (SDK_INT >= Build.VERSION_CODES.R) {
            return Environment.isExternalStorageManager();
        } else {
            int write = ContextCompat.checkSelfPermission(getApplicationContext(),
                    WRITE_EXTERNAL_STORAGE);
            int read = ContextCompat.checkSelfPermission(getApplicationContext(),
                    READ_EXTERNAL_STORAGE);
            return write == PackageManager.PERMISSION_GRANTED && read == PackageManager.PERMISSION_GRANTED;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull @org.jetbrains.annotations.NotNull String[] permissions, @NonNull @org.jetbrains.annotations.NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 333) {
            if (grantResults.length > 0) {
                boolean write = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                boolean read = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                if (read && write) {
                } else {

                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2000) {
            if (SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {
                } else {

                }
            }
        }
    }

}