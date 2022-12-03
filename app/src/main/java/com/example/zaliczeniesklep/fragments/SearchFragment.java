package com.example.zaliczeniesklep.fragments;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.zaliczeniesklep.MainActivity;
import com.example.zaliczeniesklep.R;
import com.example.zaliczeniesklep.database_entity.Product;
import com.example.zaliczeniesklep.helper.DatabaseHelper;
import com.example.zaliczeniesklep.adapters.ProductsAdapter;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class SearchFragment extends Fragment {
    private RecyclerView recyclerViewProducts;
    private MainActivity activity;
    private ImageView searchIcon;
    private EditText searchEditText;
    private TextView noBooksFound;
    private TextView showMoreResults;
    private int numberOfShowProducts = 0;
    private List<Product> products;
    private List<Product> filteredProducts;
    private List<Product> temporarlyShownProducts;
    private boolean allResultsShown = false;
    private int selectedCategory;
    private Button selectCategoryButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        activity = (MainActivity) getActivity();

        searchIcon = view.findViewById(R.id.search_search_bar_image);
        searchEditText = view.findViewById(R.id.search_search_bar_edit_text);
        noBooksFound = view.findViewById(R.id.no_books_found_text_view);
        showMoreResults = view.findViewById(R.id.search_show_more_results_text_view);
        selectCategoryButton = view.findViewById(R.id.select_category_button);

        selectCategoryButton.setOnClickListener(v -> changeCategory());

        searchEditText.setHint( getString(R.string.nav_search) + "...");

        searchIcon.setOnClickListener(v -> hideKeyboard());

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String searchFor = searchEditText.getText().toString();

                if (searchFor.contains("\n")){
                    searchEditText.setText(searchFor.substring(0, searchFor.length() - 1));

                    hideKeyboard();
                }

                else{
                    filterProducts(searchFor);
                }
            }
        });

        recyclerViewProducts = view.findViewById(R.id.search_recycler_view_products);

        recyclerViewProducts.setLayoutManager(new GridLayoutManager(getActivity().getApplicationContext(), MainActivity.NUM_OF_COLS));

        if (activity.getProducts().size() == 0){
            DatabaseHelper helper = new DatabaseHelper(getActivity().getApplicationContext());

            products = helper.getProductsFromDB();

            activity.setProducts(products);
        }

        else{
            products = activity.getProducts();
        }

        selectedCategory = 0;

        filteredProducts = products;

        temporarlyShownProducts = filteredProducts;

        if (getArguments() != null){
            if (getArguments().getInt("CategoryId", 0) > 0){
                selectedCategory = getArguments().getInt("CategoryId", 0);

                filteredProducts = filteredProducts.stream().filter(product -> product.getCategory_id() == selectedCategory).collect(Collectors.toList());

                selectCategoryButton.setText(getResources().getStringArray(R.array.categories)[selectedCategory - 1]);

                temporarlyShownProducts = filteredProducts;
            }

            else{
                selectCategoryButton.setText(R.string.categories_all);
            }

            if (getArguments().getString("searchedPhrase") != null){
                searchEditText.setText(getArguments().getString("searchedPhrase"));

                temporarlyShownProducts = filteredProducts.stream().filter(product ->
                                product.getName().toLowerCase(Locale.ROOT).contains(getArguments().getString("searchedPhrase").toLowerCase(Locale.ROOT))
                                        || product.getAuthor().toLowerCase(Locale.ROOT).contains(getArguments().getString("searchedPhrase").toLowerCase(Locale.ROOT))
                                        || product.getTags().toLowerCase(Locale.ROOT).contains(getArguments().getString("searchedPhrase").toLowerCase(Locale.ROOT)))
                        .collect(Collectors.toList());
            }
        }

        showMoreResults.setOnClickListener(v -> showMoreProducts(temporarlyShownProducts.size()));

        showMoreProducts(temporarlyShownProducts.size());

        ProductsAdapter productsAdapter = new ProductsAdapter(activity, temporarlyShownProducts.stream().limit(numberOfShowProducts).collect(Collectors.toList()));

        recyclerViewProducts.setAdapter(productsAdapter);

        toggleNoBooksFound(temporarlyShownProducts.size());

        return view;
    }

    private void hideKeyboard(){
        searchEditText.clearFocus();

        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(getView().getWindowToken(), 0);
    }

    private void showMoreProducts(int numOfProducts){
        if (numOfProducts > numberOfShowProducts){
            allResultsShown = false;

            if (numberOfShowProducts + 12 < numOfProducts){
                numberOfShowProducts += 12;
            }

            else if (numberOfShowProducts + 12 >= numOfProducts){
                numberOfShowProducts = numOfProducts;
                allResultsShown = true;
                showMoreResults.setVisibility(View.GONE);
            }
        }

        recyclerViewProducts.setAdapter(new ProductsAdapter(activity, filteredProducts.stream().limit(numberOfShowProducts).collect(Collectors.toList())));
    }

    private void changeCategory(){
        hideKeyboard();

        activity.toggleNavigationMenu();
        activity.toggleSearchEditText();

        Bundle bundle = new Bundle();
        bundle.putString("searchedPhrase", searchEditText.getText().toString());

        ShowAllCategoriesFragment fragment = new ShowAllCategoriesFragment();
        fragment.setArguments(bundle);

        getChildFragmentManager().beginTransaction().setCustomAnimations(R.anim.fragment_slide_in_top, R.anim.fragment_slide_out_top).replace(R.id.search_fragment_layout, fragment).commit();
    }

    private void filterProducts(String searchFor){
        temporarlyShownProducts = filteredProducts.stream().filter(product ->
                                   product.getName().toLowerCase(Locale.ROOT).contains(searchFor.toLowerCase(Locale.ROOT))
                                || product.getAuthor().toLowerCase(Locale.ROOT).contains(searchFor.toLowerCase(Locale.ROOT))
                                || product.getTags().toLowerCase(Locale.ROOT).contains(searchFor.toLowerCase(Locale.ROOT)))
                                .collect(Collectors.toList());

        numberOfShowProducts = 0;

        showMoreProducts(temporarlyShownProducts.size());

        recyclerViewProducts.setAdapter(new ProductsAdapter(activity, temporarlyShownProducts.stream().limit(numberOfShowProducts).collect(Collectors.toList())));

        toggleNoBooksFound(temporarlyShownProducts.size());
    }

    private void toggleNoBooksFound(int numOfResults){
        if (numOfResults == 0){
            noBooksFound.setVisibility(View.VISIBLE);
            showMoreResults.setVisibility(View.GONE);
        }

        else{
            noBooksFound.setVisibility(View.GONE);

            if (!allResultsShown){
                showMoreResults.setVisibility(View.VISIBLE);
            }
        }
    }
}
