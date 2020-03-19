package com.example.helloworld;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompatSideChannelService;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.webkit.WebChromeClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.module.AppGlideModule;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalField;

public class ProfileUpdateActivity extends AppCompatActivity {
    private static final int PERMISSION_CODE = 100;
    Button gallery,camera;
    ImageView img;
    Uri imguri;
    private StorageReference mStorageRef;
    private FirebaseUser fUser;
    int TAKE_IMAGE_CODE = 10001;
    int SELECT_IMAGE_CODE=10002;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_update);
        img=(ImageView)findViewById(R.id.imgview) ;
        mStorageRef = FirebaseStorage.getInstance().getReference("images/profilepictures");

        FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null)
        {
            if(user.getPhotoUrl()!=null)
            {
                Glide.with(this).load(user.getPhotoUrl()).into(img);
            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //Toast.makeText(this, "Hi", Toast.LENGTH_SHORT).show();
        Log.d("result Code",""+resultCode);
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==TAKE_IMAGE_CODE)
        {
            switch(resultCode) {
                case RESULT_OK:
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                img.setImageBitmap(bitmap);
                Fileuploader(bitmap);

            }
        }
        else if(requestCode==SELECT_IMAGE_CODE)
        {
            Toast.makeText(this, "Atleast here", Toast.LENGTH_SHORT).show();
            switch(resultCode) {
                case RESULT_OK:
                    img.setImageURI(data.getData());
                    Uri imageUri = data.getData();
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                        Fileuploader(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

            }
        }
    }
    private void Fileuploader(Bitmap bitmap)
    {
        String uid=FirebaseAuth.getInstance().getCurrentUser().getUid();
        ByteArrayOutputStream baos =new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,baos);
        final StorageReference Reference=FirebaseStorage.getInstance().getReference().child("profilepictures").child(uid+".jpeg");
        Reference.putBytes(baos.toByteArray()).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                getDownloadUrl(Reference);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }
    private void getDownloadUrl(StorageReference Reference)
    {
        Reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                setUserProfileUri(uri);
            }
        });
    }
    private void setUserProfileUri(Uri uri)
    {
        FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
        UserProfileChangeRequest request=new UserProfileChangeRequest.Builder().setPhotoUri(uri).build();
        user.updateProfile(request).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(ProfileUpdateActivity.this, "Profile picture Updated successfully", Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void Choose(View v)
    {
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M)
        {
            if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_DENIED)
            {
                String [] permissions={Manifest.permission.READ_EXTERNAL_STORAGE};
                requestPermissions(permissions,PERMISSION_CODE);
            }
            else
            {
                Filechooser();
            }
        }
        else
        {
            Filechooser();
        }
    }
    public void Filechooser()
    {
        Intent intent=new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        //intent.setAction(intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,SELECT_IMAGE_CODE);
    }

    public void camera(View v)
    {
        Intent intent =new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(intent.resolveActivity(getPackageManager())!=null)
        {
            startActivityForResult(intent,TAKE_IMAGE_CODE);
        }
     }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onResume()
    {
        super.onResume();
    }
    public void onRequestPermissionResult(int requestCode, @NonNull String [] permissions, @NonNull int [] grantResults){
        switch(requestCode){
            case PERMISSION_CODE:
                if(grantResults.length >0&&grantResults[0]==PackageManager.PERMISSION_GRANTED)
                {
                    Filechooser();
                }
                else
                {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                }
        }
    }

 }


