package helpers;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.helloworld.R;

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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row, null);
        return new PostHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostHolder holder, int position) {
        holder.title.setText(posts.get(position).getUser());
        holder.desc.setText(posts.get(position).getContent());
        if (TextUtils.isEmpty(posts.get(position).getDp())) {
            Glide.with(holder.dp.getContext())
                    .load(R.drawable.ic_photo_user)
                    .apply(RequestOptions.circleCropTransform())
                    .into(holder.dp);
        } else {
            Glide.with(holder.dp.getContext())
                    .load(posts.get(position).getDp())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .apply(RequestOptions.circleCropTransform())
                    .into(holder.dp);
        }
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

}
