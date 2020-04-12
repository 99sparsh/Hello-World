package com.example.helloworld;

import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
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

import helpers.Coordinates;
import helpers.Post;
import helpers.PostAdapter;
import helpers.Stemmer;

public class PostsActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    PostAdapter postAdapter;
    private FirebaseFirestore db;
    private final String TAG = "PostsActivity";
    ArrayList<Post> gposts = new ArrayList<Post>();
    private ProgressBar progressBar;
    private EditText post;
    private FirebaseUser fuser;
    private FirebaseAuth auth;
    private Stemmer s;
    final String[] ownToken = {""};
    DashboardActivity loc;
    Coordinates ob;

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
        ob = new Coordinates();
        s = new Stemmer();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        fetchInterestsAndPost();
    }

    @Override
    public void onResume() {
        super.onResume();
        fuser = auth.getCurrentUser();

    }

    private void fetchInterestsAndPost() {
        final ArrayList[] userInterests = new ArrayList[]{new ArrayList<String>()};
        fuser = auth.getCurrentUser();
        db.collection("users").document(fuser.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        Map<String, Object> user = task.getResult().getData();
                        userInterests[0] = (ArrayList<String>) user.get("interests");
                        ownToken[0] = (String) user.get("FCM_Token");
                        fetchPosts(userInterests[0]);
                    }
                });
    }

    private void fetchPosts(ArrayList<String> userInterests) {
        loc = new DashboardActivity();
        Log.e("int", Arrays.toString(userInterests.toArray()));
        db.collection("posts")
                .whereArrayContainsAny("interests", userInterests)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        ArrayList<Post> posts = new ArrayList<Post>();
                        if (task.isSuccessful()) {

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Post post = document.toObject(Post.class);
                                if (ob.checkRange(loc.getLatitude(), loc.getLongitude(), post.getLatitude(), post.getLongitude()))
                                    posts.add(post);
                            }
                            getPostList(posts);
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    public String findRoot(String input) {
        int n = input.length();
        if (input.charAt(n - 1) == input.charAt(n - 2))
            return input;
        for (int i = 0; i < n; i++)
            s.add(input.charAt(i));
        s.stem();
        return s.toString();
    }

    private void getPostList(ArrayList<Post> p) {
        progressBar.setVisibility(View.INVISIBLE);
        gposts = (ArrayList<Post>) p.clone();
        postAdapter = new PostAdapter(this, gposts);
        recyclerView.setAdapter(postAdapter);
        postAdapter.notifyDataSetChanged();
    }

    public void makePost(View view) {
        HashMap<String, Object> postData = new HashMap<String, Object>();
        String content = post.getText().toString();
        if (TextUtils.isEmpty(content)) {
            Toast.makeText(this, "Post can't be empty!", Toast.LENGTH_SHORT).show();
            return;
        }
        Pattern hashtag = Pattern.compile("#(\\S+)");
        Matcher mat = hashtag.matcher(content);
        ArrayList<String> interests = new ArrayList<String>();
        while (mat.find())
            interests.add(findRoot(mat.group(1)));
        Date date = new Date();
        Uri url = fuser.getPhotoUrl();
        String urlString;
        if (url == null)
            urlString = "";
        else
            urlString = url.toString();
        loc = new DashboardActivity();
        Post newPost = new Post(fuser.getDisplayName(), urlString, content, (new Timestamp(date.getTime())).toString(),
                fuser.getUid(), loc.getLatitude(), loc.getLongitude(), interests);
        progressBar.setVisibility(View.VISIBLE);
        makePost(newPost);
        post.setText("");
        gposts.add(0, newPost);
        postAdapter.notifyItemChanged(0);
        progressBar.setVisibility(View.INVISIBLE);
    }

    public void makePost(Post post) {
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
                        Toast.makeText(getApplicationContext(), "Post failed!", Toast.LENGTH_SHORT).show();
                        Log.w(TAG, "Error adding post", e);
                    }
                });
        ArrayList<String> userdetails = new ArrayList<String>();
        userdetails.add(fuser.getDisplayName());
        userdetails.add(ownToken[0]);
        userdetails.add(Double.toString(post.getLatitude()));
        userdetails.add(Double.toString(post.getLongitude()));
        new SendNotifications().execute(post.getInterests(), userdetails);
    }
}
