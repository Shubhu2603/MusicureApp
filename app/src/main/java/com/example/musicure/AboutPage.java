package com.example.musicure;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import java.lang.String;

public class AboutPage extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    TextView log;
    String  Cure;
    private Spinner spinner_raga;
    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_page);


        getWindow().setStatusBarColor(ContextCompat.getColor(AboutPage.this,R.color.red));
        getWindow().setNavigationBarColor(ContextCompat.getColor(AboutPage.this,R.color.darkred));

        fAuth=FirebaseAuth.getInstance();
        if(fAuth.getCurrentUser()!=null)
        {
            fAuth.signOut();
        }


        Button play=findViewById(R.id.Play);
        spinner_raga=findViewById(R.id.Spinner_raga);
        log=findViewById(R.id.LOG);
        Button questionnaire=findViewById(R.id.questionnaire);



        String[] cure=getResources().getStringArray(R.array.cure);
        ArrayAdapter adapter=ArrayAdapter.createFromResource(this,R.array.cure,R.layout.spinner_dropdown_layout);
        spinner_raga.setAdapter(adapter);

        //   adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //spinner_raga.setAdapter(adapter);

        spinner_raga.setOnItemSelectedListener(this);

        log.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
            }
        });

        questionnaire.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),Questionnaire.class));
            }
        });

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Cure=spinner_raga.getSelectedItem().toString();
                System.out.println("Here");
                startActivity(new Intent(getApplicationContext(),Music_player.class).putExtra("raga",Cure));
            }
        });

    }


    @Override
    public void onBackPressed(){
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}