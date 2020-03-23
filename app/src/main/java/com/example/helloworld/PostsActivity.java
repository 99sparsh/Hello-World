package com.example.helloworld;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posts);
        progressBar = findViewById(R.id.progress_loader);
        db = FirebaseFirestore.getInstance();
        recyclerView = findViewById(R.id.recyclerView);
        post = findViewById(R.id.editText8);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        fetchPosts();
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
                            Log.e(TAG+"pSize", posts.size()+"");
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
        Log.e(TAG+"gposts ",gposts.size()+"");
    }

    public void makePost(View view) {
        String content = post.getText().toString();

    }
}
