package com.example.zaliczeniesklep.adapters;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.zaliczeniesklep.MainActivity;
import com.example.zaliczeniesklep.R;
import com.example.zaliczeniesklep.database_entity.Category;
import com.example.zaliczeniesklep.fragments.SearchFragment;

import java.util.List;

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.ItemViewHolder> {
    private MainActivity activity;
    private Context context;
    private List<Category> categories;

    public CategoriesAdapter (MainActivity activity, List<Category> categories){
        this.activity = activity;
        this.context = activity.getApplicationContext();
        this.categories = categories;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_recycler_view_category, parent, false);

        ItemViewHolder holder = new ItemViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        String name = context.getResources().getStringArray(R.array.categories)[position];
        int image = categories.get(position).getDrawableImageId();
        int categoryId = position + 1;

        holder.imageView.setImageDrawable(context.getDrawable(image));
        holder.textView.setText(name);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putInt("CategoryId", categoryId);

                SearchFragment fragment = new SearchFragment();
                fragment.setArguments(bundle);

                activity.selectNavigationItem(R.id.search);
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder{
        TextView textView;
        ImageView imageView;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            textView = itemView.findViewById(R.id.category_name);
            imageView = itemView.findViewById(R.id.category_icon);
        }
    }
}
