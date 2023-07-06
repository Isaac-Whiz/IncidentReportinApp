package com.example.incidentreportingapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.core.view.View;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private MaterialToolbar toolbar;
    private Button btnChooseImages, btnSend;
    private static final int REQUEST_PERMISSION = 1;
    private static final int REQUEST_PICK_IMAGE = 2;
    private static final int REQUEST_CAMERA = 3;
    private ImagePagerAdapter pagerAdapter;
    private List<String> imagePaths;
    private FragmentTransaction fragmentTransaction;
    private FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        handleToggleEvents();
        checkAndRequestPermissionsAtFirst();
        handleVideoFragment();
        handleNavigationViewEvents();
        handleButtonEvents();
    }

    private void handleToggleEvents() {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.open_drawer, R.string.drawer_closed);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
    }

    private void handleNavigationViewEvents() {
        navigationView.setNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.userPolicy) {
                startActivity(new Intent(MainActivity.this, UserPolicyActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));

            } else if (item.getItemId() == R.id.terms) {
                startActivity(new Intent(MainActivity.this, TermsActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));

            } else if (item.getItemId() == R.id.aboutUs) {
                startActivity(new Intent(Intent.ACTION_VIEW)
                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        .setData(Uri.parse("https://github.com/Isaac-Whiz")));
            } else if (item.getItemId() == R.id.reports) {
                Toast.makeText(this, "Will handle this in a minute", Toast.LENGTH_SHORT).show();

            } else if (item.getItemId() == R.id.logout) {
                auth.signOut();
                startActivity(new Intent(MainActivity.this, LoginActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
            }
            return false;
        });
    }

    private void handleButtonEvents() {
        btnChooseImages.setOnClickListener(view -> {
            checkPermissionsAndOpenGallery();
        });

        btnChooseImages.setOnLongClickListener(view -> {
            checkPermissionsAndOpenCamera();
            return true;
        });
    }

    private void checkAndRequestPermissionsAtFirst() {
        checkCameraPermissions();
        requestCamera();
        checkGalleryPermissions();
        requestGallery();
    }

    private boolean checkGalleryPermissions() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED;
    }

    private boolean checkCameraPermissions() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED;
    }

    private void initViews() {
        drawer = findViewById(R.id.drawer);
        navigationView = findViewById(R.id.navigationView);
        toolbar = findViewById(R.id.toolbar);
        ViewPager viewPager = findViewById(R.id.viewPager);
        btnChooseImages = findViewById(R.id.btnChooseImages);
        imagePaths = new ArrayList<>();
        pagerAdapter = new ImagePagerAdapter(this, imagePaths);
        viewPager.setAdapter(pagerAdapter);
        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        auth = FirebaseAuth.getInstance();
        btnSend = findViewById(R.id.btnSend);
    }

    private void handleVideoFragment() {
        fragmentTransaction.add(R.id.fragmentContainer, new VideoFragment());
        fragmentTransaction.commit();
    }

    private void requestCamera() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                REQUEST_PERMISSION);
    }

    private void requestGallery() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                REQUEST_PERMISSION);
    }

    private void checkPermissionsAndOpenGallery() {
        if (checkGalleryPermissions())
            requestGallery();
        openGallery();
    }

    private void checkPermissionsAndOpenCamera() {
        if (checkCameraPermissions())
            requestCamera();
        openCamera();
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Pictures"), REQUEST_PICK_IMAGE);
    }

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_PICK_IMAGE && resultCode == RESULT_OK) {
            if (data != null) {
                if (data.getClipData() != null) {
                    // Multiple images selected from gallery
                    int count = data.getClipData().getItemCount();
                    for (int i = 0; i < count; i++) {
                        Uri imageUri = data.getClipData().getItemAt(i).getUri();
                        String imagePath = getRealPathFromUri(imageUri);
                        imagePaths.add(imagePath);
                    }
                } else if (data.getData() != null) {
                    // Single image selected from gallery
                    Uri imageUri = data.getData();
                    String imagePath = getRealPathFromUri(imageUri);
                    imagePaths.add(imagePath);
                }

                pagerAdapter.notifyDataSetChanged();
            }
        } else if (requestCode == REQUEST_CAMERA && resultCode == RESULT_OK) {
            if (data != null && data.getExtras() != null) {
                Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");
                String imagePath = saveImageToStorage(imageBitmap);
                if (imagePath != null) {
                    imagePaths.add(imagePath);
                    pagerAdapter.notifyDataSetChanged();
                }
                Toast.makeText(this, "Failed to save image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String getRealPathFromUri(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String imagePath = cursor.getString(columnIndex);
            cursor.close();
            return imagePath;
        }
        return uri.getPath();
    }

    private String saveImageToStorage(Bitmap bitmap) {
        File imagesDir = getExternalFilesDir("Images");
        if (imagesDir != null) {
            if (!imagesDir.exists()) {
                imagesDir.mkdirs();
            }

            File imageFile = new File(imagesDir, System.currentTimeMillis() + ".jpg");
            try (FileOutputStream fos = new FileOutputStream(imageFile)) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                return imageFile.getAbsolutePath();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(Intent.ACTION_MAIN)
                .addCategory(Intent.CATEGORY_HOME)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }
}
