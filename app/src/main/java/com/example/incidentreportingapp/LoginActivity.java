package com.example.incidentreportingapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    private EditText editLoginEmail;
    private EditText editLoginPassword;
    private Button btnLogin;
    private FirebaseAuth auth;
    private boolean isPasswordVisible;
    public String activeUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initViews();
        clearViews();
        handleEvents();
    }

    private void handleEvents() {
        btnLogin.setOnClickListener(view -> {
            String mail = editLoginEmail.getText().toString();
            String pass = editLoginPassword.getText().toString();
            if (mail.endsWith("com") || mail.contains("@")) {
                Toast.makeText(this, "Enter valid email please.", Toast.LENGTH_SHORT).show();
                editLoginEmail.requestFocus();
            }
            if (TextUtils.isEmpty(mail) || TextUtils.isEmpty(pass))
                Toast.makeText(LoginActivity.this, "Enter mail or password.", Toast.LENGTH_SHORT).show();
            else {
                activeUser = mail;
                login(mail, pass);
            }
//            startActivity(new Intent(LoginActivity.this, MainActivity.class)
//                    .putExtra("Author", editLoginEmail.getText().toString())
//                    .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK));
        });

        editLoginPassword.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                final int right = 2;
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    if (motionEvent.getRawX() >= editLoginPassword.getRight() - editLoginPassword
                            .getCompoundDrawables()[right].getBounds().width()) {
                        int selection = editLoginPassword.getSelectionEnd();
                        if (isPasswordVisible) {
                            //Set invisibility drawable image
                            editLoginPassword.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0,
                                    R.drawable.visible, 0);
                            //Hide password
                            editLoginPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                            isPasswordVisible = false;
                        } else {
                            //Set visibility image
                            editLoginPassword.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0,
                                    R.drawable.invisible, 0);
                            editLoginPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                            isPasswordVisible = true;
                        }
                        editLoginPassword.setSelection(selection);
                        return true;
                    }
                }
                return false;
            }
        });

    }

    private void login(String email, String password) {

        if (CommonMethods.isNetworkAvailable(getApplicationContext())) {
            auth.signInWithEmailAndPassword(email, password).addOnSuccessListener(authResult -> {
                Toast.makeText(LoginActivity.this, "Login successful.", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(LoginActivity.this, MainActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK));
            });
        } else {
            Toast.makeText(this, "Please enable internet connection.", Toast.LENGTH_SHORT).show();
        }
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
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
    }

    private void clearViews() {
        editLoginEmail.setText("");
        editLoginPassword.setText("");
    }
    public String getActiveUser() {
        return activeUser;
    }

}