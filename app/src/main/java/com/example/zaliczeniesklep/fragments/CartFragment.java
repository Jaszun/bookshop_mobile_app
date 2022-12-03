package com.example.zaliczeniesklep.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import com.example.zaliczeniesklep.MainActivity;
import com.example.zaliczeniesklep.R;
import com.example.zaliczeniesklep.database_entity.CartItem;
import com.example.zaliczeniesklep.database_entity.Product;
import com.example.zaliczeniesklep.helper.DatabaseHelper;
import com.example.zaliczeniesklep.adapters.CartItemsAdapter;
import com.example.zaliczeniesklep.schema.Schema;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CartFragment extends Fragment {
    private MainActivity activity;

    private RecyclerView recyclerView;
    private RelativeLayout emptyCartLayout;

    private LinearLayout finalPriceContainer;
    private LinearLayout buttonsContainer;

    private TextView findSomething;
    private TextView finalPriceTextView;

    private Button orderButton;
    private ImageButton shareButton;

    private List<Product> products;
    private List<CartItem> cartItems;

    private float finalPrice = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart, container, false);

        activity = (MainActivity) getActivity();

        recyclerView = view.findViewById(R.id.order_recycler_view_products);
        emptyCartLayout = view.findViewById(R.id.no_items_in_cart_container);
        findSomething = view.findViewById(R.id.find_something);
        orderButton = view.findViewById(R.id.order_button);
        shareButton = view.findViewById(R.id.share_button);
        finalPriceTextView = view.findViewById(R.id.final_price);
        buttonsContainer = view.findViewById(R.id.buttons_container);
        finalPriceContainer = view.findViewById(R.id.final_price_container);

        orderButton.setOnClickListener(v -> makeAnOrder());
        shareButton.setOnClickListener(v -> shareCart());

        findSomething.setOnClickListener(v -> runSearchFragment());

        if (activity.getProducts().size() == 0){
            DatabaseHelper helper = new DatabaseHelper(getActivity().getApplicationContext());

            products = helper.getProductsFromDB();
        }

        else{
            products = activity.getProducts();
        }

        cartItems = activity.getCart();

        CartItemsAdapter adapter = new CartItemsAdapter(activity, cartItems, this);

        DividerItemDecoration decoration = new DividerItemDecoration(activity, DividerItemDecoration.VERTICAL);
        decoration.setDrawable(ContextCompat.getDrawable(activity, R.drawable.s_divider));

        recyclerView.addItemDecoration(decoration);
        recyclerView.setAdapter(adapter);

        if (cartItems.size() == 0){
            emptyCartLayout.setVisibility(View.VISIBLE);
            orderButton.setVisibility(View.GONE);
            finalPriceContainer.setVisibility(View.GONE);
            buttonsContainer.setVisibility(View.GONE);
        }

        else {
            emptyCartLayout.setVisibility(View.GONE);
            orderButton.setVisibility(View.VISIBLE);
            finalPriceContainer.setVisibility(View.VISIBLE);
            buttonsContainer.setVisibility(View.VISIBLE);
        }

        return view;
    }

    public void decrementFinalPrice(float price){
        finalPrice -= price;

        finalPriceTextView.setText(String.format("%.02f", finalPrice) + "PLN");
    }

    public void incrementFinalPrice(float price){
        finalPrice += price;

        finalPriceTextView.setText(String.format("%.02f", finalPrice) + "PLN");
    }

    private void runSearchFragment(){
        activity.moveToFragment(1);
    }

    public void updateRecyclerView(){
        finalPrice = 0;

        cartItems = activity.getCart();

        CartItemsAdapter adapter = new CartItemsAdapter(activity, cartItems, this);

        recyclerView.setAdapter(adapter);

        if (cartItems.size() == 0){
            emptyCartLayout.setVisibility(View.VISIBLE);
            orderButton.setVisibility(View.GONE);
            finalPriceContainer.setVisibility(View.GONE);
            buttonsContainer.setVisibility(View.GONE);
        }

        else {
            emptyCartLayout.setVisibility(View.GONE);
            orderButton.setVisibility(View.VISIBLE);
            finalPriceContainer.setVisibility(View.VISIBLE);
            buttonsContainer.setVisibility(View.VISIBLE);
        }
    }

    private void shareCart(){
        String value = getString(R.string.app_name) + " " + getString(R.string.nav_cart) + "\n\n";

        for (int i = 0; i < cartItems.size(); i++){
            CartItem item = cartItems.get(i);

            value += (i + 1) + ". " + products.get(item.getProduct_id() - 1).getName() + ", " + products.get(item.getProduct_id() - 1).getAuthor() + ", " + getString(R.string.quantity) + " " + item.getQuantity() + ";\n";
        }

        value += "\n" + getString(R.string.final_price) + " " + String.format("%.02f", finalPrice) + "PLN";

        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, value);
        sendIntent.setType("text/plain");

        Intent shareIntent = Intent.createChooser(sendIntent, null);
        startActivity(shareIntent);
    }

    private void makeAnOrder(){
        if (activity.getUser() == null){
            showAlertDialogForGuest();
        }

        else {
            startOrderFragment();
        }
    }

    private void showAlertDialogForGuest(){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage(activity.getResources().getString(R.string.buying_as_guest_message));
        builder.setTitle(activity.getResources().getString(R.string.buying_as_guest_title));
        builder.setCancelable(false);
        builder.setNegativeButton(activity.getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.setPositiveButton(activity.getResources().getString(R.string.continue_string), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                startOrderFragment();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    private void showAlertDialogForMissingItems(String listOfItems){
        Log.i("orderTest", listOfItems);

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage(activity.getResources().getString(R.string.products_missing_message) + listOfItems);
        builder.setTitle(activity.getResources().getString(R.string.products_missing_title));
        builder.setCancelable(false);
        builder.setNegativeButton(activity.getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.setPositiveButton(activity.getResources().getString(R.string.continue_string), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                startOrderFragment();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    private void startOrderFragment(){
        DatabaseHelper helper = new DatabaseHelper(activity);

        List<String> missingItems = new ArrayList<>();

        int numOfMissingProducts;

        for (CartItem item : cartItems){
            Product product = helper.getProductByIdFromDB(item.getProduct_id());

            if (product.getCount() < item.getQuantity()){
                numOfMissingProducts = item.getQuantity() - product.getCount();

                missingItems.add("\n- " + product.getName() + " -> " + numOfMissingProducts);

                if (activity.getUser() != null) {
                    activity.getCart().get(activity.getCart().indexOf(item)).setQuantity(item.getQuantity() - numOfMissingProducts);
                    helper.updateRowById(item, item.getId(), Schema.CartSchema.QUANTITY_COLUMN, String.valueOf(item.getQuantity() - numOfMissingProducts));
                }

                else {
                    activity.getCart().get(activity.getCart().indexOf(item)).setQuantity(item.getQuantity() - numOfMissingProducts);
                }
            }
        }

        if (activity.getUser() == null){
            activity.saveCartInSharedPreferences();
        }

        if (missingItems.size() > 0){
            showAlertDialogForMissingItems(missingItems.stream().map(s -> String.valueOf(s)).collect(Collectors.joining()));
            updateRecyclerView();
        }

        else{
            Bundle bundle = new Bundle();
            bundle.putFloat("price", finalPrice);

            OrderFragment fragment = new OrderFragment();
            fragment.setArguments(bundle);

            getChildFragmentManager().beginTransaction().setCustomAnimations(R.anim.fragment_slide_in_top, R.anim.fragment_slide_out_top).replace(R.id.cart_fragment_layout, fragment).commit();
        }
    }
}
