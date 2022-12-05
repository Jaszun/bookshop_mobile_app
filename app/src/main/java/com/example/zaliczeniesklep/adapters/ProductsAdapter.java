package com.example.zaliczeniesklep.adapters;

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
import com.example.zaliczeniesklep.database_entity.Product;
import com.example.zaliczeniesklep.fragments.HomeFragment;
import com.example.zaliczeniesklep.fragments.ProductPreviewFragment;
import com.example.zaliczeniesklep.fragments.SearchFragment;

import java.util.List;

public class ProductsAdapter extends RecyclerView.Adapter<ProductsAdapter.ItemViewHolder> {
    private MainActivity activity;
    private List<Product> products;

    public ProductsAdapter(MainActivity activity, List<Product> products){
        this.activity = activity;
        this.products = products;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.item_recycler_view_product, parent, false);

        ItemViewHolder holder = new ItemViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        Product product = products.get(position);

        int image = product.getDrawableImageId();

        String title = (product.getName().length() >= 35 ? product.getName().substring(0, 32) + "..." : product.getName());

        holder.title.setText(title);

        holder.imageView.setImageDrawable(activity.getDrawable(image));
        holder.author.setText(product.getAuthor());
        holder.price.setText(product.getPrice() + " PLN");

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ProductPreviewFragment fragment = new ProductPreviewFragment();

                Bundle bundle = new Bundle();
                bundle.putInt("productId", product.getId() - 1);

                fragment.setArguments(bundle);

                if (activity.getSupportFragmentManager().findFragmentById(R.id.fragment_container) instanceof HomeFragment){
                    activity.getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fragment_slide_in_right, R.anim.fragment_slide_out_right).replace(R.id.home_fragment_layout, fragment).commit();
                }

                else if (activity.getSupportFragmentManager().findFragmentById(R.id.fragment_container) instanceof SearchFragment){
                    activity.getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fragment_slide_in_right, R.anim.fragment_slide_out_right).replace(R.id.search_fragment_layout, fragment).commit();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder{
        TextView title;
        TextView author;
        TextView price;
        ImageView imageView;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.title);
            author = itemView.findViewById(R.id.author);
            price = itemView.findViewById(R.id.price);
            imageView = itemView.findViewById(R.id.cover_img);
        }
    }
}
