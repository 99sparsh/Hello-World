package com.example.helloworld;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import helpers.Post;
import helpers.PostAdapter;

public class PostsActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    PostAdapter postAdapter;
    private FirebaseFirestore db;
    private final String TAG = "PostsActivity";
    ArrayList<Post> gposts= new ArrayList<Post>();
    private ProgressBar progressBar;
    private EditText post;
    private FirebaseUser fuser;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posts);
        progressBar = findViewById(R.id.progress_loader);
        db = FirebaseFirestore.getInstance();
        recyclerView = findViewById(R.id.recyclerView);
        post = findViewById(R.id.editText8);
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        fetchPosts();
    }

    @Override
    public void onResume() {
        super.onResume();
        fuser = auth.getCurrentUser();
    }
    private void fetchPosts(){

        db.collection("posts")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        ArrayList<Post> posts= new ArrayList<Post>();
                        if (task.isSuccessful()) {

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Post post = document.toObject(Post.class);
                                posts.add(post);
                            }
                            getPostList(posts);
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    private void getPostList(ArrayList<Post> p){
        progressBar.setVisibility(View.INVISIBLE);
        gposts = (ArrayList<Post>)p.clone();
        postAdapter = new PostAdapter(this,gposts);
        recyclerView.setAdapter(postAdapter);
        postAdapter.notifyDataSetChanged();
    }

    public void makePost(View view) {
        HashMap<String,Object> postData = new HashMap<String,Object>();
        String content = post.getText().toString();
        Pattern hashtag = Pattern.compile("#(\\S+)");
        Matcher mat = hashtag.matcher(content);
        ArrayList<String> interests = new ArrayList<String>();
        while(mat.find())
            interests.add(mat.group(1));
        Date date = new Date();
        postData.put("user",fuser.getDisplayName());
        postData.put("uid",fuser.getUid());
        postData.put("content",content);
        postData.put("interests",interests);
        postData.put("latitude", new DashboardActivity().getLatitude());
        postData.put("longitude", new DashboardActivity().getLongitude());
        postData.put("dp", fuser.getPhotoUrl().toString());
        postData.put("timestamp",(new Timestamp(date.getTime())).toString());
        Log.d(TAG,Arrays.asList(postData).toString());
        progressBar.setVisibility(View.VISIBLE);
        makePost(postData);
        post.setText("");
        Post newPost = new Post(fuser.getDisplayName(), fuser.getPhotoUrl().toString(), content, postData.get("timestamp").toString(),
                fuser.getUid(), (Double)postData.get("latitude"), (Double)postData.get("longitude"),interests);
        gposts.add(0,newPost);
        postAdapter.notifyItemChanged(0);
        progressBar.setVisibility(View.INVISIBLE);
    }

        public void makePost(HashMap post) {
            final boolean[] success = new boolean[1];
            db.collection("posts")
                    .add(post)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Toast.makeText(getApplication(), "Post added!", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "Post written with ID: " + documentReference.getId());
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(),"Post failed!", Toast.LENGTH_SHORT).show();
                            Log.w(TAG, "Error adding post", e);
                        }
                    });
        }
}
