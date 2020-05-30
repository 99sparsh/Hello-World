package utils;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.helloworld.R;

public class PostHolder extends RecyclerView.ViewHolder {
    ImageView dp;
    TextView title, desc;

    public PostHolder(@NonNull View itemView) {
        super(itemView);
        this.dp = itemView.findViewById(R.id.dpIV);
        this.title = itemView.findViewById(R.id.titleTV);
        this.desc = itemView.findViewById(R.id.descTV);
    }
}
