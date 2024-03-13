package com.example.incidentreportingapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {
    private EditText editEmail, editPassword, editContact, editSecondName, editFirstName;
    private Button btnLogin, btnRegister, btnEmergency;
    private CheckBox checkBox;
    private FirebaseAuth auth;
    private TextView txtViewTerms;
    private static final int REQUEST_CALL_PERMISSION = 1;
    private boolean isPasswordVisible;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initViews();
        handleEvents();
        setBtnEmergency();
    }
    private void initViews() {
        editFirstName = findViewById(R.id.editFirstName);
        editSecondName = findViewById(R.id.editSecondName);
        editContact = findViewById(R.id.editContact);
        editEmail = findViewById(R.id.editEmail);
        editPassword = findViewById(R.id.editPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);
        auth = FirebaseAuth.getInstance();
        checkBox = findViewById(R.id.checkbox);
        txtViewTerms = findViewById(R.id.txtViewTerms);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Registered User");
        btnEmergency = findViewById(R.id.btnEmergency);
    }

    private void setBtnEmergency() {
        btnEmergency.setOnClickListener(view -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL_PERMISSION);
            } else {
                Intent intent = new Intent(Intent.ACTION_CALL)
                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        .setData(Uri.parse("tel:999"));
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                } else {
                    Toast.makeText(this, "No application to complete the calling event", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
    private void handleEvents() {
        btnRegister.setOnClickListener(view -> {
            checkViewsNullityAndRegister();
        });

        btnLogin.setOnClickListener(view -> {
            navigateToLoginActivity();
        });

        txtViewTerms.setOnClickListener(view -> {
            navigateToTermsActivity();
        });
        editPassword.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                final int right = 2;
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    if (motionEvent.getRawX() >= editPassword.getRight() - editPassword
                            .getCompoundDrawables()[right].getBounds().width()) {
                        int selection = editPassword.getSelectionEnd();
                        if (isPasswordVisible) {
                            editPassword.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0,
                                    R.drawable.visible, 0);
                            editPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                            isPasswordVisible = false;
                        } else {
                            editPassword.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0,
                                    R.drawable.invisible, 0);
                            editPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                            isPasswordVisible = true;
                        }
                        editPassword.setSelection(selection);
                        return true;
                    }
                }
                return false;
            }
        });
    }

    private void checkViewsNullityAndRegister() {
        if (isNetworkAvailable()) {
            int limit = 6;
            String email = editEmail.getText().toString();
            if (TextUtils.isEmpty(email)) {
                Toast.makeText(this, "Enter email.", Toast.LENGTH_SHORT).show();
            } else {
                if (!Utils.isValidEmail(email)) {
                    Toast.makeText(this, "Enter valid email please.", Toast.LENGTH_SHORT).show();
                    editEmail.requestFocus();
                } else {
                    if (checkBox.isChecked()) {
                        String password = editPassword.getText().toString();
                        String fName = editFirstName.getText().toString();
                        String lName = editSecondName.getText().toString();
                        String contact = editContact.getText().toString();
                        if (TextUtils.isEmpty(password)
                                || TextUtils.isEmpty(contact)
                                || TextUtils.isEmpty(fName)
                                || TextUtils.isEmpty(lName)) {
                            Toast.makeText(RegisterActivity.this, "Please fill all fields.", Toast.LENGTH_SHORT).show();
                        } else if (editPassword.length() < limit) {
                            Toast.makeText(RegisterActivity.this, "Enter 6 or more password characters.", Toast.LENGTH_SHORT).show();
                        } else {
                            databaseReference.push().setValue(new RegisteringUser(fName, lName, contact, email));
                            checkUserExistsAndRegister(email, password);
                        }
                    } else {
                        Toast.makeText(this, "Accept the terms first to continue.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        } else {
            Toast.makeText(this, "Please enable internet connection.", Toast.LENGTH_SHORT).show();
        }
    }

    private void navigateToLoginActivity() {
        if (Utils.isNetworkAvailable(getApplicationContext())) {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class)
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
        } else {
            Toast.makeText(this, "Please enable internet connection.", Toast.LENGTH_SHORT).show();
        }
    }

    private void navigateToTermsActivity() {
        startActivity(new Intent(RegisterActivity.this, TermsActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
    }

    private boolean isNetworkAvailable() {
        return Utils.isNetworkAvailable(this);
    }



    private void register(String txtEmail, String txtPassword) {

        auth.createUserWithEmailAndPassword(txtEmail, txtPassword).
                addOnCompleteListener(RegisterActivity.this, task -> {

                    if (task.isSuccessful()) {
                        Toast.makeText(RegisterActivity.this, "Registration successful.", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(RegisterActivity.this, MainActivity.class)
                                .putExtra("Author", txtEmail)
                                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                    } else {
                        Toast.makeText(RegisterActivity.this, "Registration failed.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void checkUserExistsAndRegister(String email, String password) {
        auth.fetchSignInMethodsForEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult() != null && task.getResult().getSignInMethods() != null
                                && task.getResult().getSignInMethods().size() > 0) {
                            Toast.makeText(RegisterActivity.this, "User already exists.", Toast.LENGTH_SHORT).show();
                        } else {
                            register(email, password);
                        }
                    } else {
                        Toast.makeText(RegisterActivity.this, "Error checking user existence.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        System.exit(0);
    }
}