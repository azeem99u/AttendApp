package com.example.androidthings.attendapp.activities;


import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.os.Build.VERSION.SDK_INT;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.androidthings.attendapp.CreateAttBottomSheet;
import com.example.androidthings.attendapp.CustomTimePickerDialog;
import com.example.androidthings.attendapp.R;
import com.example.androidthings.attendapp.databinding.ActivityMainBinding;
import com.example.androidthings.attendapp.models.DialogEvent;

import org.greenrobot.eventbus.EventBus;

public class MainActivity extends AppCompatActivity  implements CustomTimePickerDialog.DurationPickerListener{
    private ActivityMainBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(R.id.homeFragment, R.id.settingsFragment).build();
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentContainerView);
        assert navHostFragment != null;
        NavController navController = navHostFragment.getNavController();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.bottomNavigationView, navController);
        binding.bottomNavigationView.setBackground(null);
        binding.fab.setOnClickListener(view -> new CreateAttBottomSheet().show(getSupportFragmentManager(), CreateAttBottomSheet.BOTTOM_SHEET_PICKER_TAG));
    }

    @Override
    public void dataMessage(String duration) {
        EventBus.getDefault().postSticky(new DialogEvent(duration));
    }
}