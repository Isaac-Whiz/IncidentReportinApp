package com.example.incidentreportingapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {
    private EditText editEmail, editPassword, editContact, editSecondName, editFirstName;
    private Button btnLogin, btnRegister;
    private CheckBox checkBox;
    private FirebaseAuth auth;
    private TextView txtViewTerms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initViews();
        buttonClicks();
    }

    private void buttonClicks() {
        btnRegister.setOnClickListener(view -> {
            int limit = 6;
            if (!checkBox.isChecked())
                Toast.makeText(this, "Accept the terms first to continue.", Toast.LENGTH_SHORT).show();
            String email = editEmail.getText().toString();
            String password = editPassword.getText().toString();
            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                Toast.makeText(RegisterActivity.this, "Enter email or password.", Toast.LENGTH_SHORT).show();
            } else if (editPassword.length() < limit) {
                Toast.makeText(RegisterActivity.this, "Enter 6 or more password characters.", Toast.LENGTH_SHORT).show();
            } else
                register(email, password);
        });

        btnLogin.setOnClickListener(view -> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class)
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
        });

        txtViewTerms.setOnClickListener(view ->{
            startActivity(new Intent(RegisterActivity.this, TermsActivity.class)
                    .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
        });
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
    }

    private void register(String txtEmail, String txtPassword) {
        auth.createUserWithEmailAndPassword(txtEmail, txtPassword).
                addOnCompleteListener(RegisterActivity.this, task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(RegisterActivity.this, "Registration successful.", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(RegisterActivity.this, MainActivity.class)
                                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                    }
                    Toast.makeText(RegisterActivity.this, "Registration failed.", Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        System.exit(0);
    }
}