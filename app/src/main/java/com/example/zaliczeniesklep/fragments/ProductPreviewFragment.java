package com.example.zaliczeniesklep.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.zaliczeniesklep.MainActivity;
import com.example.zaliczeniesklep.R;
import com.example.zaliczeniesklep.database_entity.Product;
import com.example.zaliczeniesklep.helper.DatabaseHelper;
import com.example.zaliczeniesklep.schema.Schema;

public class ProductPreviewFragment extends Fragment {
    private MainActivity activity;

    private int quantity = 1;

    private Product chosenProduct;

    private ImageView imageView;
    private TextView titleTextView;
    private TextView authorTextView;
    private TextView priceTextView;
    private TextView quantityTextView;

    private LinearLayout adminPanel;

    private TextView currentQuantity;
    private Button adminIncrementQuantity;
    private Button adminDecrementQuantity;
    private Button applyQuantityChanges;
    private EditText adminQuantity;

    private int changeStockQuantity = 0;

    private Button addToCartButton;
    private Button decrementButton;
    private Button incrementButton;

    private RelativeLayout backBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product_preview, container, false);

        activity = (MainActivity) getActivity();

        activity.toggleSearchEditText();

        if (activity.getSupportFragmentManager().findFragmentById(R.id.fragment_container) instanceof SearchFragment){
            activity.toggleShowAllCategoriesButton();
        }

        imageView = view.findViewById(R.id.product_image_preview);
        titleTextView = view.findViewById(R.id.product_title_preview);
        authorTextView = view.findViewById(R.id.product_author_preview);
        priceTextView = view.findViewById(R.id.product_price_preview);

        quantityTextView = view.findViewById(R.id.quantity);

        addToCartButton = view.findViewById(R.id.add_to_cart);
        decrementButton = view.findViewById(R.id.decrement_quantity);
        incrementButton = view.findViewById(R.id.increment_quantity);

        decrementButton.setEnabled(false);

        decrementButton.setOnClickListener(v -> decrementQuantity());
        incrementButton.setOnClickListener(v -> incrementQuantity());
        addToCartButton.setOnClickListener(v -> addToCart());

        backBar = view.findViewById(R.id.preview_back_button_container);
        backBar.setOnClickListener(v -> hidePreview());

        if (getArguments() != null && activity.getProducts().size() > 0){
            DatabaseHelper helper = new DatabaseHelper(activity);
            chosenProduct = helper.getProductByIdFromDB(getArguments().getInt("productId") + 1);

            try{
                int drawableId = Integer.parseInt(chosenProduct.getImage());

                imageView.setImageDrawable(getResources().getDrawable(drawableId));
            } catch (Exception e){
                imageView.setImageBitmap(activity.getBitmapFromImage(chosenProduct.getImage()));
            }

            titleTextView.setText(chosenProduct.getName());
            authorTextView.setText(chosenProduct.getAuthor());
            priceTextView.setText(chosenProduct.getPrice() + " PLN");
        }

        updateQuantityComponent();

        adminPanel = view.findViewById(R.id.product_preview_admin_panel);

        if (activity.getUser() != null && activity.getUser().getUserType() == 1){
            adminPanel.setVisibility(View.VISIBLE);

            currentQuantity = view.findViewById(R.id.current_quantity);

            setCurrentQuantityTextView();

            adminIncrementQuantity = view.findViewById(R.id.admin_increment_quantity);
            adminDecrementQuantity = view.findViewById(R.id.admin_decrement_quantity);
            applyQuantityChanges = view.findViewById(R.id.apply_stock_quantity_changes);
            adminQuantity = view.findViewById(R.id.admin_quantity);

            adminIncrementQuantity.setOnClickListener(v -> incrementQuantityInStock());
            adminDecrementQuantity.setOnClickListener(v -> decrementQuantityInStock());

            applyQuantityChanges.setOnClickListener(v -> showDialog());

            adminQuantity.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    if (adminQuantity.getText().toString() != "-"){
                        try{
                            changeStockQuantity = Integer.valueOf(adminQuantity.getText().toString());

                            if (changeStockQuantity < chosenProduct.getQuantity() * (-1)){
                                changeStockQuantity = chosenProduct.getQuantity() * (-1);
                                adminQuantity.setText(changeStockQuantity + "");
                                adminDecrementQuantity.setEnabled(false);
                                return;
                            }

                            else if (changeStockQuantity == chosenProduct.getQuantity() * (-1)){
                                adminDecrementQuantity.setEnabled(false);
                                return;
                            }

                            applyQuantityChanges.setEnabled(true);
                            return;
                        } catch (NumberFormatException e){
                            changeStockQuantity = 0;
                        }
                    }

                    applyQuantityChanges.setEnabled(false);
                }
            });

            applyQuantityChanges.setEnabled(false);
        }

        else{
            adminPanel.setVisibility(View.GONE);
        }

        quantityTextView.setText(quantity + "");

        return view;
    }

    private void updateQuantityComponent(){
        int maxQuanity = countMaxQuantity();

        if (maxQuanity == 0){
            quantity = 0;
            quantityTextView.setText("0");
            addToCartButton.setEnabled(false);
            incrementButton.setEnabled(false);
            decrementButton.setEnabled(false);
        }

        else if (maxQuanity == 1){
            quantity = 1;
            quantityTextView.setText(quantity + "");
            addToCartButton.setEnabled(true);
            incrementButton.setEnabled(false);
        }

        else {
            if (quantity > maxQuanity){
                quantity = maxQuanity;
            }

            quantityTextView.setText(quantity + "");
            addToCartButton.setEnabled(true);
            incrementButton.setEnabled(true);
        }
    }

    private void addToCart(){
        activity.addProductToCart(chosenProduct, Integer.valueOf(quantityTextView.getText().toString()), false);
        addToCartButton.setEnabled(false);

        Toast.makeText(activity, getString(R.string.item_added_to_cart), Toast.LENGTH_SHORT).show();
    }

    public void hidePreview(){
        getParentFragmentManager().beginTransaction().setCustomAnimations(R.anim.fragment_slide_in_right, R.anim.fragment_slide_out_right).hide(this).commit();
        activity.toggleSearchEditText();

        if (activity.getSupportFragmentManager().findFragmentById(R.id.fragment_container) instanceof SearchFragment){
            activity.toggleShowAllCategoriesButton();
        }
    }

    private int countMaxQuantity(){
        DatabaseHelper helper = new DatabaseHelper(activity);
        chosenProduct = helper.getProductByIdFromDB(chosenProduct.getId());

        for (int i = 0; i < activity.getCart().size(); i++){
            if (activity.getCart().get(i).getProduct_id() == chosenProduct.getId()){
                int finalQuantity = chosenProduct.getQuantity() - activity.getCart().get(i).getQuantity();

                if (finalQuantity < 0){
                    return 0;
                }
                return finalQuantity;
            }
        }

        return chosenProduct.getQuantity();
    }

    private void incrementQuantity(){
        int maxQuantity = countMaxQuantity();

        if (quantity + 1 <= maxQuantity){
            quantity++;

            decrementButton.setEnabled(true);

            quantityTextView.setText(quantity + "");
        }

        if (quantity == maxQuantity){
            incrementButton.setEnabled(false);
        }

        else {
            incrementButton.setEnabled(true);
        }
    }

    private void decrementQuantity(){
        if (quantity - 1 >= 1){
            quantity--;

            incrementButton.setEnabled(true);

            quantityTextView.setText(quantity + "");
        }

        if (quantity == 1){
            decrementButton.setEnabled(false);
        }

        else {
            decrementButton.setEnabled(true);
        }
    }

    private void incrementQuantityInStock(){
        adminDecrementQuantity.setEnabled(true);
        changeStockQuantity++;

        if (changeStockQuantity == 0){
            adminQuantity.setText("");
            applyQuantityChanges.setEnabled(false);
            return;
        }

        adminQuantity.setText(changeStockQuantity + "");
    }

    private void decrementQuantityInStock(){
        if (changeStockQuantity - 1 <= chosenProduct.getQuantity() * (-1)){
            changeStockQuantity--;
            adminDecrementQuantity.setEnabled(false);
            adminQuantity.setText(changeStockQuantity + "");
            return;
        }

        else if (changeStockQuantity - 1 == 0){
            changeStockQuantity--;
            adminQuantity.setText("");
            return;
        }

        changeStockQuantity--;

        adminQuantity.setText(changeStockQuantity + "");
        adminDecrementQuantity.setEnabled(true);
    }

    private void setCurrentQuantityTextView(){
        DatabaseHelper helper = new DatabaseHelper(activity);

        chosenProduct = helper.getProductByIdFromDB(chosenProduct.getId());
        currentQuantity.setText(getString(R.string.current_quantity) + " " + chosenProduct.getQuantity());
    }

    private void applyChanges(){
        DatabaseHelper helper = new DatabaseHelper(activity);

        helper.updateRowById(chosenProduct, chosenProduct.getId(), Schema.ProductsSchema.QUANTITY_COLUMN, String.valueOf((chosenProduct.getQuantity() + changeStockQuantity)));

        setCurrentQuantityTextView();

        changeStockQuantity = 0;
        adminQuantity.setText("");
        adminDecrementQuantity.setEnabled(true);
        adminIncrementQuantity.setEnabled(true);
        applyQuantityChanges.setEnabled(false);

        updateQuantityComponent();
    }

    private void showDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage(activity.getResources().getString(R.string.modify_quantity_dialog_message) + "\n\n" + chosenProduct.getName() + ": " + chosenProduct.getQuantity() + " -> " + (chosenProduct.getQuantity() + changeStockQuantity));
        builder.setTitle(activity.getResources().getString(R.string.modify_quantity_dialog_title));
        builder.setCancelable(false);
        builder.setNegativeButton(activity.getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.setPositiveButton(activity.getResources().getString(R.string.apply_changes), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                applyChanges();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }
}
