package helpers;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.collection.LruCache;
import androidx.recyclerview.widget.RecyclerView;

import com.example.helloworld.PostsActivity;
import com.example.helloworld.R;

import java.net.URL;
import java.util.ArrayList;

public class PostAdapter extends RecyclerView.Adapter<PostHolder> {

    Context c;
    ArrayList<Post> posts;

    public PostAdapter(Context c, ArrayList<Post> posts) {
        this.c = c;
        this.posts = posts;

    }

    @NonNull
    @Override
    public PostHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row,null);
        return new PostHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostHolder holder, int position) {
        holder.title.setText(posts.get(position).getTitle());
        holder.desc.setText(posts.get(position).getDescription());
        //loadBitmap(position,holder.dp);

       // new GetImages().execute(posts.get(position).getDp());
        holder.dp.setImageResource(R.drawable.ic_photo_user);
    }

    @Override
    public int getItemCount() {

        Log.e("AdapterSIZE",posts.size()+"");
        return posts.size();
    }


}
