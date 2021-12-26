package com.example.musicure;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Log extends AppCompatActivity {

    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setStatusBarColor(ContextCompat.getColor(Log.this,R.color.red));
        getWindow().setNavigationBarColor(ContextCompat.getColor(Log.this,R.color.darkred));


        setContentView(R.layout.activity_log);
        TextView login;
        EditText fullname,email,pass1,cpass;
        Button Register;
        ImageButton back;
        FirebaseAuth fAuth;
        FirebaseFirestore fStore ;
        ProgressBar progressBar;


        fullname=findViewById(R.id.name1);
        pass1=findViewById(R.id.pass1);
        cpass=findViewById(R.id.cpass);
        email=findViewById(R.id.email);
        Register=findViewById(R.id.register);
        login=findViewById(R.id.logpage);
        back=findViewById(R.id.BACK4);

        fAuth= FirebaseAuth.getInstance();
        fStore=FirebaseFirestore.getInstance();
        progressBar=findViewById(R.id.progressBar);

        progressBar.setVisibility(View.GONE);

        if(fAuth.getCurrentUser()!=null)
        {
            Toast.makeText(Log.this, "Logout to Sign up wih another account !", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getApplicationContext(),AboutPage.class));
            finish();
        }


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Log.this,MainActivity.class));
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Log.this,MainActivity.class));
            }
        });

        Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String Email=email.getText().toString().trim();
                String password=pass1.getText().toString().trim();
                String name=fullname.getText().toString();


                if(TextUtils.isEmpty(Email)){
                    email.setError("Email is required");
                    return;
                }
                if(TextUtils.isEmpty(password)){
                    pass1.setError("Password is required");
                    return;
                }
                if(password.length() < 6){
                    pass1.setError("Password should be greater than 6 characters");
                    return;
                }
                if(!pass1.getText().toString().equals(cpass.getText().toString()))
                {
                    cpass.setError("Password does not match");
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                fAuth.createUserWithEmailAndPassword(Email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){

                            userID=fAuth.getCurrentUser().getUid();
                            DocumentReference documentReference=fStore.collection("Users").document(userID);
                            Map<String,Object> user=new HashMap<>();
                            user.put("fname",name);
                            user.put("email",Email);
                            documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(Log.this, "User Created", Toast.LENGTH_SHORT).show();
                                }
                            });
                            progressBar.setVisibility(View.GONE);
                            startActivity(new Intent(getApplicationContext(),MainActivity.class));
                        }
                        else
                            {
                                Toast.makeText(Log.this, "Error!"+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                        }
                    }
                });
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(Log.this,MainActivity.class));
    }
}