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
import com.example.zaliczeniesklep.helper.DatabaseHelper;

import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ItemViewHolder> {
    private MainActivity activity;
    private Order order;
    private List<CartItem> cart;
    private int numOfItems;

    public OrderAdapter(MainActivity activity, Order order){
        this.activity = activity;
        this.order = order;
        numOfItems = order.decodeItems().length;
    }

    public OrderAdapter(MainActivity activity, List<CartItem> cart){
        this.activity = activity;
        this.cart = cart;
        numOfItems = cart.size();
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.item_recycler_view_order, parent, false);

        ItemViewHolder holder = new ItemViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        Product product;
        int quantity;
        String price;

        DatabaseHelper helper = new DatabaseHelper(activity);

        if (order != null) {
            product = helper.getProductByIdFromDB(order.decodeItems()[position][0]);
            quantity = order.decodeItems()[position][1];
            price = String.format("%.02f", product.getPrice() * quantity) + "PLN";
        }

        else {
            product = helper.getProductByIdFromDB(cart.get(position).getProduct_id());
            quantity = cart.get(position).getQuantity();
            price = String.format("%.02f", product.getPrice() * cart.get(position).getQuantity()) + "PLN";
        }

        holder.title.setText(product.getName());
        holder.author.setText(product.getAuthor());
        holder.quantity.setText(String.valueOf(quantity));
        holder.price.setText(price);

        try{
            int drawableId = Integer.parseInt(product.getImage());

            holder.cover.setImageDrawable(activity.getDrawable(drawableId));
        } catch (Exception e){
            holder.cover.setImageBitmap(activity.getBitmapFromImage(product.getImage()));
        }
    }

    @Override
    public int getItemCount() {
        return numOfItems;
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder{
        TextView title;
        TextView author;
        TextView quantity;
        TextView price;
        ImageView cover;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.order_item_title);
            author = itemView.findViewById(R.id.order_item_author);
            quantity = itemView.findViewById(R.id.order_item_quantity);
            price = itemView.findViewById(R.id.order_item_price);
            cover = itemView.findViewById(R.id.order_item_cover_img);
        }
    }
}
