package com.example.zaliczeniesklep.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.zaliczeniesklep.MainActivity;
import com.example.zaliczeniesklep.R;
import com.example.zaliczeniesklep.database_entity.CartItem;
import com.example.zaliczeniesklep.database_entity.Order;
import com.example.zaliczeniesklep.database_entity.Product;
import com.example.zaliczeniesklep.fragments.AddBookFragment;
import com.example.zaliczeniesklep.helper.DatabaseHelper;

import java.util.List;

public class TagAdapter extends RecyclerView.Adapter<TagAdapter.ItemViewHolder> {
    private MainActivity activity;
    private AddBookFragment parent;
    private List<String> tags;

    public TagAdapter(MainActivity activity, List<String> tags, AddBookFragment parent){
        this.activity = activity;
        this.tags = tags;
        this.parent = parent;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.item_recycler_view_subcategory, parent, false);

        ItemViewHolder holder = new ItemViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        holder.title.setText(tags.get(position));

        holder.cover.setOnClickListener(v -> {
            parent.removeTag(tags.get(position));
            parent.updateRecyclerView();
        });
    }

    @Override
    public int getItemCount() {
        return tags.size();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder{
        TextView title;
        ImageView cover;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.tag_name);
            cover = itemView.findViewById(R.id.remove_tag);
        }
    }
}
