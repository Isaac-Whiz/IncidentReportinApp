package com.example.incidentreportingapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    private EditText editLoginEmail;
    private EditText editLoginPassword;
    private Button btnLogin;
    private FirebaseAuth auth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initViews();
        clearViews();
        btnLogin.setOnClickListener(view -> {
            String mail = editLoginEmail.getText().toString();
            String pass = editLoginPassword.getText().toString();
            if (TextUtils.isEmpty(mail) || TextUtils.isEmpty(pass))
                Toast.makeText(LoginActivity.this, "Enter mail or password.", Toast.LENGTH_SHORT).show();
            login(mail, pass);
        });

    }
    private void login(String email, String password) {
        auth.signInWithEmailAndPassword(email, password).addOnSuccessListener(authResult -> {
            Toast.makeText(LoginActivity.this, "Login successful.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(LoginActivity.this, MainActivity.class)
                    .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK));
        });
    }

    private void initViews() {
        editLoginEmail = findViewById(R.id.editLoginEmail);
        editLoginPassword = findViewById(R.id.editLoginPassword);
        btnLogin = findViewById(R.id.btnLoginLogin);
        auth = FirebaseAuth.getInstance();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        clearViews();
        startActivity(new Intent(LoginActivity.this, RegisterActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK));
    }
    private void clearViews(){
        editLoginEmail.setText("");
        editLoginPassword.setText("");
    }
}