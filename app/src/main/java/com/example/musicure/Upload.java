package com.example.musicure;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;

public class Upload extends AppCompatActivity {
    private static final int RESULT_LOAD_FILE = 1;
    FirebaseAuth fAuth;
FirebaseFirestore fStore;
    private String UID;
    private String name;
    private String mail;
    private RecyclerView mUploadList;

    private List<String> fileNameList;
    private List<String> fileDoneList;

    private UploadListAdapter uploadListAdapter;

    private StorageReference mStorage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        getWindow().setStatusBarColor(ContextCompat.getColor(Upload.this,R.color.red));
        getWindow().setNavigationBarColor(ContextCompat.getColor(Upload.this,R.color.darkred));


        setContentView(R.layout.activity_upload);

        Button select=findViewById(R.id.button2);
        mUploadList=findViewById(R.id.recycler);

        fileNameList=new ArrayList<>();
        fileDoneList=new ArrayList<>();

        uploadListAdapter=new UploadListAdapter(fileNameList,fileDoneList);

        mStorage= FirebaseStorage.getInstance().getReference();

        mUploadList.setLayoutManager(new LinearLayoutManager(this));
        mUploadList.setHasFixedSize(true);
        mUploadList.setAdapter(uploadListAdapter);

        fAuth=FirebaseAuth.getInstance();
        fStore=FirebaseFirestore.getInstance();
        UID = fAuth.getCurrentUser().getUid();

        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent();
                intent.setType("audio/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select file"),RESULT_LOAD_FILE);
            }
        });




}

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==RESULT_LOAD_FILE && resultCode==RESULT_OK )
        {
            if(data.getClipData()!=null)
            {
                int totalItemsSelected=data.getClipData().getItemCount();

                for(int i=0;i<totalItemsSelected;i++)
                {
                    Uri fileUri=data.getClipData().getItemAt(i).getUri();

                    String filename=getFileName(fileUri);

                    fileNameList.add(filename);
                    fileDoneList.add("Uploading");
                    uploadListAdapter.notifyDataSetChanged();

                    StorageReference fileToUpload=mStorage.child("Music").child(filename);

                    int finalI = i;
                    fileToUpload.putFile(fileUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            fileDoneList.remove(finalI);
                            fileDoneList.add(finalI,"done");

                            uploadListAdapter.notifyDataSetChanged();
                        }
                    });
                }


                Toast.makeText(Upload.this, "Files Selected", Toast.LENGTH_SHORT).show();
            }
            else if(data.getData()!=null)
            {
                Toast.makeText(Upload.this, "Files Selected", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override

    public void onBackPressed()
    {
        Toast.makeText(Upload.this, "Please Logout to Exit !", Toast.LENGTH_SHORT).show();
    }

    public void logout(View view) {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getApplicationContext(),AboutPage.class));
        finish();
    }

    @SuppressLint("Range")
    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

}