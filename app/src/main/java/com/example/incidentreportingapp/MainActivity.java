package com.example.incidentreportingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private MaterialToolbar toolbar;
    private Button btnChooseImages, btnSend, btnLocateMe;
    private static final int REQUEST_PERMISSION = 1;
    private static final int REQUEST_PICK_IMAGE = 2;
    private static final int REQUEST_CAMERA = 3;
    private MapView mapView;
    private GoogleMap googleMap;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private ViewPager viewPager;
    private ImagePagerAdapter pagerAdapter;
    private ArrayList<String> imagePaths;
    private FragmentTransaction fragmentTransaction;
    private FirebaseAuth auth;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private EditText editDescription, editLocation, editCategory;
    public DatabaseReference databaseReference;
    private StorageReference storageReference;
    private SentReports sentReports;
    private String videoUrl;
    private UploadTask uploadTask;
    private VideoView videoViewMain;
    private MediaController mediaControllerMain;
    private Button btnAddVideoMain, btnRemoveImages, btnRemoveVideo;
    private Uri videoUri;
    private ProgressBar progressBar;
    private static final int REQUEST_PICK_VIDEO = 1;
    private static final int REQUEST_VIDEO_CAPTURE = 2;
    private String author;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
        initViews();

        handleToggleEvents();
        checkAndRequestPermissionsAtFirst();
        handleNavigationViewEvents();
        handleButtonEvents();
        addVideoMain();
        removeImages();
    }

    private void sendEmailWithAttachments(String recipientEmail, String subject, String body, ArrayList<String> imagePaths) {
        Intent intent = new Intent(Intent.ACTION_SEND_MULTIPLE);
        intent.setType("message/rfc822");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{recipientEmail});
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, body);
        intent.setPackage("com.google.android.gm");

        ArrayList<Uri> attachmentUris = new ArrayList<>();

        // Attach images
        if (imagePaths != null && !imagePaths.isEmpty()) {
            for (String imagePath : imagePaths) {
                File imageFile = new File(imagePath);
                Uri imageUri = FileProvider.getUriForFile(this, this.getPackageName() + ".provider", imageFile);
                attachmentUris.add(imageUri);
            }
        }

        if (!attachmentUris.isEmpty()) {
            intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, attachmentUris);
        }

        startActivity(Intent.createChooser(intent, "Send Email"));
    }

    private void handleToggleEvents() {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.open_drawer, R.string.drawer_closed);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Intent intent = getIntent();
        author = intent.getStringExtra("Author");
        mapView.onPause();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        Intent intent = getIntent();
        author = intent.getStringExtra("Author");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    private void loadCurrentLocation() {
        if (googleMap != null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                googleMap.setMyLocationEnabled(true);
                fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, location -> {
                    if (location != null) {
                        LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15));
                    }
                });
            }
        }
    }


    private void validateAuthor() {
        Intent intent = getIntent();
        author = intent.getStringExtra("Author");
        Menu menu = navigationView.getMenu();
        MenuItem menuItem = menu.findItem(R.id.reports);
        if (author != null) {
            if (author.equals(Utils.ADMIN1) || author.equals(Utils.ADMIN2)) {
                menuItem.setVisible(true);
            }
        } else {
            menuItem.setVisible(false);
        }
        invalidateOptionsMenu();
    }

    private void handleNavigationViewEvents() {
        validateAuthor();
        navigationView.setNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.userPolicy) {
                startActivity(new Intent(MainActivity.this, UserPolicyActivity.class)
                        .putExtra("Author", author)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));

            } else if (item.getItemId() == R.id.terms) {
                startActivity(new Intent(MainActivity.this, TermsActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));

            } else if (item.getItemId() == R.id.aboutUs) {
                startActivity(new Intent(Intent.ACTION_VIEW)
                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        .setData(Uri.parse(Utils.GITHUB_URL)));
            } else if (item.getItemId() == R.id.reports) {
                startActivity(new Intent(MainActivity.this, Report.class)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
            } else if (item.getItemId() == R.id.logout) {
                auth.signOut();
                startActivity(new Intent(MainActivity.this, LoginActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
            }
            return false;
        });
    }

    private void handleButtonEvents() {
        btnLocateMe.setOnClickListener(view -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            }
        });

        btnChooseImages.setOnClickListener(view -> {
            pagerAdapter.notifyDataSetChanged();
            checkPermissionsAndOpenGallery();
        });

        btnChooseImages.setOnLongClickListener(view -> {
            checkPermissionsAndOpenCamera();
            return true;
        });

        btnSend.setOnClickListener(view -> {
            if (!Utils.isNetworkAvailable(getApplicationContext())) {
                Toast.makeText(this, "Please enable internet connection.", Toast.LENGTH_SHORT).show();
                return;
            }
            backupVideoAndSendMail();
            //clearViews();
        });
        btnRemoveVideo.setOnClickListener(view -> {
            removeVideo();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        clearViews();
        mapView.onResume();
    }

    private void clearViews() {
        editCategory.setText(null);
        editDescription.setText(null);
        editLocation.setText(null);
    }

    private void composeTextualDataBackupAndSendMail() {
        String recipient = "info@upf.go.ug";
        String subject = "Information regarding reporting an incident.";
        String location = editLocation.getText().toString();
        String description = editDescription.getText().toString();
        String category = editCategory.getText().toString();
        if (TextUtils.isEmpty(category) || TextUtils.isEmpty(location) || TextUtils.isEmpty(description)) {
            Toast.makeText(this, "Fill all the input fields please.", Toast.LENGTH_SHORT).show();
            return;
        }
        backupMetaData();
        String body = "Location: " + location + "\n" + "Category: " +
                category + "\n" + "Description: "
                + description + "\n" + "Video Url: " + videoUrl;
        sendEmailWithAttachments(recipient, subject, body, pagerAdapter.imagePaths);
    }

    private void backupMetaData() {
        Intent intent = getIntent();
        String author = intent.getStringExtra("Author");
        String location = editLocation.getText().toString();
        String description = editDescription.getText().toString();
        String category = editCategory.getText().toString();
        String picPaths = pagerAdapter.imagePaths.toString();
        String dateAndTime = getCurrentDateAndTime();
        sentReports.setAuthor(author);
        sentReports.setCategory(category);
        sentReports.setDescription(description);
        sentReports.setLocation(location);
        sentReports.setImagePaths(picPaths);
        sentReports.setCurrentTimeAndDate(dateAndTime);
        sentReports.setVideoUrl(videoUrl);
        databaseReference.child("Report backups").push().setValue(sentReports);

    }

    private String getCurrentDateAndTime() {
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return dateFormat.format(date);
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
        viewPager = findViewById(R.id.viewPager);
        btnChooseImages = findViewById(R.id.btnChooseImages);
        imagePaths = new ArrayList<>();
        pagerAdapter = new ImagePagerAdapter(this, imagePaths);
        viewPager.setAdapter(pagerAdapter);
        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        auth = FirebaseAuth.getInstance();
        btnSend = findViewById(R.id.btnSend);
        editCategory = findViewById(R.id.editCategory);
        editDescription = findViewById(R.id.editDescription);
        editLocation = findViewById(R.id.editLocation);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference("Videos");
        sentReports = new SentReports();
        progressBar = findViewById(R.id.progressBar);
        mediaControllerMain = findViewById(R.id.mediaControllerMain);
        videoViewMain = findViewById(R.id.videoViewMain);
        btnAddVideoMain = findViewById(R.id.btnAddVideoMain);
        btnRemoveImages = findViewById(R.id.btnRemoveImages);
        btnRemoveVideo = findViewById(R.id.btnRemoveVideoMain);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        btnLocateMe = findViewById(R.id.btnLocateMe);

    }

    private void removeVideo() {
        if (videoViewMain != null) {
            progressBar.setVisibility(View.INVISIBLE);
            videoViewMain.stopPlayback();
            videoViewMain.setVideoURI(null);
            videoViewMain.setVisibility(View.INVISIBLE);
        }
    }

    private void removeImages() {
        btnRemoveImages.setOnClickListener(view -> {
            ArrayList<String> newImageUrls = new ArrayList<>();
            imagePaths = newImageUrls;
            ImagePagerAdapter.imagePaths = newImageUrls;
            viewPager.setAdapter(pagerAdapter);
        });
    }

    private void handleMediaController() {
        mediaControllerMain = new MediaController(this);
        videoViewMain.setMediaController(mediaControllerMain);
        mediaControllerMain.setAnchorView(videoViewMain);
        videoViewMain.setOnPreparedListener(mediaPlayer -> videoViewMain.pause());
    }

    private void addVideoMain() {
        handleMediaController();
        btnAddVideoMain.setOnClickListener(view -> {
            videoViewMain.setVisibility(View.VISIBLE);
            pickVideoFromGallery();
        });

        btnAddVideoMain.setOnLongClickListener(v -> {
            videoViewMain.setVisibility(View.VISIBLE);
            startVideoRecording();
            return true;
        });
    }

    private String getExt(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void backupVideoAndSendMail() {
        if (videoViewMain.getVisibility() == View.INVISIBLE) {
            composeTextualDataBackupAndSendMail();
        } else {
            if (videoUri != null) {
                // Generate a unique filename for the video
                String filename = System.currentTimeMillis() + "." + getExt(videoUri);

                // Create a reference to the "Videos" path with the unique filename
                StorageReference videoReference = storageReference.child(filename);

                uploadTask = videoReference.putFile(videoUri);
                progressBar.setVisibility(View.VISIBLE);

                Task<Uri> urlTask = uploadTask.continueWithTask(task -> {
                    if (task.isSuccessful()) {
                        return videoReference.getDownloadUrl();
                    }
                    throw task.getException();
                }).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        Toast.makeText(MainActivity.this, "Video Saved", Toast.LENGTH_SHORT).show();
                        videoUrl = downloadUri.toString();
                        progressBar.setVisibility(View.INVISIBLE);
                        composeTextualDataBackupAndSendMail();
                        recreate();
                    } else {
                        progressBar.setVisibility(View.INVISIBLE);
                        composeTextualDataBackupAndSendMail();
                        Toast.makeText(MainActivity.this, "Failed backing up video, check internet connection.", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                composeTextualDataBackupAndSendMail();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadCurrentLocation();
            }
        }
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
            }
        }
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_PICK_VIDEO) {
                videoUri = data.getData();
                videoViewMain.setVideoURI(videoUri);

            } else if (requestCode == REQUEST_VIDEO_CAPTURE) {
                videoUri = data.getData();
                videoViewMain.setVideoURI(videoUri);
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

    public void pickVideoFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        intent.setDataAndType(videoUri, "video/*");
        startActivityForResult(intent, REQUEST_PICK_VIDEO);
    }

    private void startVideoRecording() {
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_VIDEO_CAPTURE);
        } else {
            Toast.makeText(this, "No video recording app found", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(Intent.ACTION_MAIN)
                .addCategory(Intent.CATEGORY_HOME)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.googleMap = googleMap;

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            loadCurrentLocation();
        }
    }
}
