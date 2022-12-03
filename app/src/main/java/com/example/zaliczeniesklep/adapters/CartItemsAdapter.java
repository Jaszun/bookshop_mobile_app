package com.example.zaliczeniesklep.adapters;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.zaliczeniesklep.MainActivity;
import com.example.zaliczeniesklep.R;
import com.example.zaliczeniesklep.database_entity.CartItem;
import com.example.zaliczeniesklep.database_entity.Product;
import com.example.zaliczeniesklep.fragments.CartFragment;
import com.example.zaliczeniesklep.helper.DatabaseHelper;

import java.util.List;

public class CartItemsAdapter extends RecyclerView.Adapter<CartItemsAdapter.ItemViewHolder> {
    private MainActivity activity;
    private List<CartItem> cartItems;
    private List<Product> products;
    private CartFragment cartFragment;
    private DatabaseHelper helper;

    public CartItemsAdapter(MainActivity activity, List<CartItem> cartItems, CartFragment cartFragment) {
        this.activity = activity;
        this.cartItems = cartItems;
        this.cartFragment = cartFragment;

        helper = new DatabaseHelper(activity);
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.item_recycler_view_cart_item, parent, false);

        ItemViewHolder holder = new ItemViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        CartItem cartItem = cartItems.get(position);

        Product product = helper.getProductByIdFromDB(cartItem.getProduct_id());

        holder.title.setText(product.getName());
        holder.author.setText(product.getAuthor());
        holder.imageView.setImageDrawable(activity.getDrawable(product.getDrawableImageId()));
        holder.quantity.setText(cartItem.getQuantity() + "");

        if (cartItem.getQuantity() == product.getCount()){
            holder.incrementQuantityButton.setEnabled(false);
        }

        float price = product.getPrice() * cartItem.getQuantity();

        holder.price.setText(String.format("%.02f", price) + "PLN");

        cartFragment.incrementFinalPrice(price);

        holder.decrementQuantityButton.setOnClickListener(v -> decrementQuantity(cartItem, holder));
        holder.incrementQuantityButton.setOnClickListener(v -> incrementQuantity(cartItem, holder));
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder{
        TextView title;
        TextView author;
        ImageView imageView;
        TextView quantity;
        TextView price;

        Button incrementQuantityButton;
        Button decrementQuantityButton;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.order_item_title);
            author = itemView.findViewById(R.id.order_item_author);
            imageView = itemView.findViewById(R.id.order_item_cover_img);
            price = itemView.findViewById(R.id.order_item_price);
            quantity = itemView.findViewById(R.id.cart_item_quantity);

            incrementQuantityButton = itemView.findViewById(R.id.cart_item_increment_quantity);
            decrementQuantityButton = itemView.findViewById(R.id.cart_item_decrement_quantity);
        }
    }

    private void decrementQuantity(CartItem item, ItemViewHolder holder){
        Product product = helper.getProductByIdFromDB(item.getProduct_id());

        int quantity = Integer.valueOf(holder.quantity.getText().toString());

        if (quantity - 1 > 0){
            if (quantity > product.getCount()){
                showAlertDialogForMissingItems(product, quantity);

                quantity = product.getCount();

                holder.incrementQuantityButton.setEnabled(false);
            }

            else {
                quantity--;

                holder.incrementQuantityButton.setEnabled(true);
            }

            holder.quantity.setText(quantity + "");

            float price = product.getPrice() * quantity;

            holder.price.setText(String.format("%.02f", price) + "PLN");

            cartFragment.decrementFinalPrice(product.getPrice());

            activity.addProductToCart(product, quantity, true);
        }

        else if (quantity - 1 == 0){
            showAlertDialog(item, product);
        }
    }

    private void incrementQuantity(CartItem item, ItemViewHolder holder){
        Product product = helper.getProductByIdFromDB(item.getProduct_id());

        int quantity = Integer.valueOf(holder.quantity.getText().toString());

        if (quantity + 1 <= product.getCount()){
            holder.quantity.setText(quantity + "");

            float price = product.getPrice() * quantity;

            holder.price.setText(String.format("%.02f", price) + "PLN");

            cartFragment.incrementFinalPrice(product.getPrice());

            activity.addProductToCart(product, quantity, true);
        }

        else if (quantity > product.getCount()){
            showAlertDialogForMissingItems(product, quantity);

            quantity = product.getCount();

            holder.incrementQuantityButton.setEnabled(false);

            holder.quantity.setText(quantity + "");

            float price = product.getPrice() * quantity;

            holder.price.setText(String.format("%.02f", price) + "PLN");

            cartFragment.incrementFinalPrice(product.getPrice());

            activity.addProductToCart(product, quantity, true);
        }

        else {
            quantity++;

            holder.incrementQuantityButton.setEnabled(true);
        }
    }

    private void removeItemFromCart(CartItem item){
        activity.getCart().remove(item);

        if (activity.getUser() == null){
            activity.saveCartInSharedPreferences();
        }

        else{
            DatabaseHelper helper = new DatabaseHelper(activity);
            helper.deleteRowById(item, item.getId());
        }
    }

    private void showAlertDialog(CartItem item, Product product){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage(activity.getResources().getString(R.string.remove_from_cart_message));
        builder.setTitle(activity.getResources().getString(R.string.remove_from_cart_title));
        builder.setCancelable(false);
        builder.setNegativeButton(activity.getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.setPositiveButton(activity.getResources().getString(R.string.remove), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                removeItemFromCart(item);
                cartFragment.decrementFinalPrice(product.getPrice());
                cartFragment.updateRecyclerView();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    private void showAlertDialogForMissingItems(Product product, int quantity){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage(activity.getResources().getString(R.string.products_missing_message) + "\n" + product.getName() + " -> " + (quantity - product.getCount()));
        builder.setTitle(activity.getResources().getString(R.string.products_missing_title));
        builder.setCancelable(false);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }
}
