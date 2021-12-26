package com.example.musicure;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class MainActivity extends AppCompatActivity {



    private FirebaseAuth mAuth;
    private SignInButton signInButton;
    private String TAG="MainActivity";
    private GoogleSignInClient mGoogleSignInClient;
    private int RC_SIGN_IN=123;
    Window window;

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser user=mAuth.getCurrentUser();
        if(user!=null){
            startActivity(new Intent(getApplicationContext(),AboutPage.class));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().setStatusBarColor(ContextCompat.getColor(MainActivity.this,R.color.red));
        getWindow().setNavigationBarColor(ContextCompat.getColor(MainActivity.this,R.color.darkred));

  /*        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if(Build.VERSION.SDK_INT>21){
            window=this.getWindow() ;
            window.setStatusBarColor(this,getResources().getColor(R.color.design_default_color_primary));
        }

   */


        mAuth=FirebaseAuth.getInstance();
        ImageButton back=findViewById(R.id.BACK2);
        Button login=(Button)findViewById(R.id.sign);
        EditText email=(EditText)findViewById(R.id.User);
        EditText pass1=(EditText)findViewById(R.id.Pass);
        Button signup=(Button)findViewById(R.id.button3);
        TextView forgot=(TextView)findViewById(R.id.forgot);
        ProgressBar progressBar=findViewById(R.id.progressBar2);
        ImageButton verify=(findViewById(R.id.google));

        GoogleSignInOptions gso = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient=GoogleSignIn.getClient(this,gso);

        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });


        forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),ForgotPass.class));
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

              // Toast.makeText(getApplicationContext(), "Button", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.VISIBLE);
                Intent in=new Intent(MainActivity.this,Log.class);
                progressBar.setVisibility(View.GONE);
                startActivity(in);
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String Email=email.getText().toString().trim();
                String password=pass1.getText().toString().trim();


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
                progressBar.setVisibility(View.VISIBLE);


                //Sign In
                mAuth.signInWithEmailAndPassword(Email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(MainActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(MainActivity.this,Upload.class));
                        }
                        else{
                            Toast.makeText(MainActivity.this, "Error!"+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });

            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),AboutPage.class));
            }
        });






    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        startActivity(new Intent(getApplicationContext(),AboutPage.class));
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent,RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);

        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {

        try {
            // Google Sign In was successful, authenticate with Firebase
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            Toast.makeText(MainActivity.this, "Signed in Successfully", Toast.LENGTH_SHORT).show();
            FirebaseGoogleAuth(account);
           
        } catch (ApiException e) {
            // Google Sign In failed, update UI appropriately
            Toast.makeText(MainActivity.this, "Sign in Failed", Toast.LENGTH_SHORT).show();
            FirebaseGoogleAuth(null);
        }
    }


    private void FirebaseGoogleAuth(GoogleSignInAccount acct)
    {
        AuthCredential authCredential=GoogleAuthProvider.getCredential(acct.getIdToken(),null);
        mAuth.signInWithCredential(authCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    startActivity(new Intent(getApplicationContext(),Upload.class));

                }
            }
        });
    }

}
