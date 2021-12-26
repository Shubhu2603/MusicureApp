package com.example.musicure;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;

public class ForgotPass extends AppCompatActivity {

    private Button forgot;
    private EditText email;
    private String mail;
    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_pass);


        getWindow().setStatusBarColor(ContextCompat.getColor(ForgotPass.this,R.color.red));
        getWindow().setNavigationBarColor(ContextCompat.getColor(ForgotPass.this,R.color.darkred));

        ImageButton back=findViewById(R.id.BACK3);

        forgot = findViewById(R.id.forgotpass);
        email = findViewById(R.id.editTextTextPersonName);
        fAuth = FirebaseAuth.getInstance();


        forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateData();
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(getApplicationContext(),MainActivity.class));
    }

    private void validateData() {

        mail = email.getText().toString();
        if (mail.isEmpty()) {
            email.setError("Required");
        } else{
            forgetPass();
    }

}

    private void forgetPass() {
        fAuth.sendPasswordResetEmail(mail).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    Toast.makeText(getApplicationContext(), "Password reset link sent to email address", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(),MainActivity.class));
                    finish();
                }
                else
                {
                    Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        });
        }

}

