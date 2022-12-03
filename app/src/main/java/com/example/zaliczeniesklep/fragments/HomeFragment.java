package com.example.zaliczeniesklep.fragments;

import android.content.Context;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.zaliczeniesklep.MainActivity;
import com.example.zaliczeniesklep.R;
import com.example.zaliczeniesklep.database_entity.Category;
import com.example.zaliczeniesklep.database_entity.Product;
import com.example.zaliczeniesklep.helper.DatabaseHelper;
import com.example.zaliczeniesklep.adapters.CategoriesAdapter;
import com.example.zaliczeniesklep.adapters.ProductsAdapter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class HomeFragment extends Fragment {
    private RecyclerView recyclerViewCategories;
    private RecyclerView recyclerViewProducts;
    private TextView showAllCategoriesTextView;
    private MainActivity activity;
    private ImageView searchIcon;
    private EditText searchEditText;
    private TextView showMoreResults;
    private int numberOfShowProducts = 0;
    private List<Category> categories;
    private List<Product> products;

    private ListView searchHints;
    private String[] from = {"hint", "category"};
    private int[] to = {R.id.hint, R.id.hint_category};
    private HashMap<String, Object> hintsMap;
    private ArrayList<HashMap<String, Object>> hintsList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        activity = (MainActivity) getActivity();

        searchIcon = view.findViewById(R.id.home_search_bar_image);
        searchEditText = view.findViewById(R.id.home_search_bar_edit_text);
        showMoreResults = view.findViewById(R.id.show_more_results_text_view);

        searchHints = view.findViewById(R.id.search_hint_list);

        showMoreResults.setOnClickListener(v -> showMoreProducts());

        searchEditText.setHint( getString(R.string.nav_search) + "...");

        searchIcon.setOnClickListener(v -> search(searchEditText.getText().toString()));

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

                    search(searchFor.substring(0, searchFor.length() - 1));
                }

                else if (!searchFor.equals("")){
                    searchHints.setVisibility(View.VISIBLE);

                    showHints(searchFor);
                }

                else{
                    searchHints.setVisibility(View.GONE);
                    searchHints.setAdapter(null);
                }
            }
        });

        recyclerViewProducts = view.findViewById(R.id.home_recycler_view_products);
        recyclerViewCategories = view.findViewById(R.id.recycler_view_categories);

        recyclerViewProducts.setLayoutManager(new GridLayoutManager(getActivity().getApplicationContext(), MainActivity.NUM_OF_COLS));

        showAllCategoriesTextView = view.findViewById(R.id.categories_show_all);

        if (activity.getCategories().size() == 0){
            DatabaseHelper helper = new DatabaseHelper(getActivity().getApplicationContext());

            categories = helper.getCategoriesFromDB();
            products = helper.getProductsFromDB();

            activity.setCategories(categories);
            activity.setProducts(products);
        }

        else{
            categories = activity.getCategories();
            products = activity.getProducts();
        }

        showMoreProducts();

        CategoriesAdapter categoriesAdapter = new CategoriesAdapter(activity, categories);
        ProductsAdapter productsAdapter = new ProductsAdapter(activity, products.stream().limit(numberOfShowProducts).collect(Collectors.toList()));

        recyclerViewCategories.setAdapter(categoriesAdapter);
        recyclerViewProducts.setAdapter(productsAdapter);

        showAllCategoriesTextView.setOnClickListener(v -> showAllCategories());

        searchHints.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String searchFor = (String) ((HashMap<String, Object>) adapterView.getAdapter().getItem(i)).get("hint");

                search(searchFor);
            }
        });

        return view;
    }

    public void showAllCategories(){
        activity.toggleNavigationMenu();
        activity.toggleSearchEditText();

        getChildFragmentManager().beginTransaction().setCustomAnimations(R.anim.fragment_slide_in_top, R.anim.fragment_slide_out_top).replace(R.id.home_fragment_layout, new ShowAllCategoriesFragment()).commit();
    }

    private void changeIconColor(){
        searchIcon.setBackgroundTintMode(PorterDuff.Mode.DARKEN);
    }

    private void showMoreProducts(){
        if (products.size() > numberOfShowProducts){
            if (numberOfShowProducts + 12 <= products.size()){
                numberOfShowProducts += 12;
            }

            else if (numberOfShowProducts + 12 > products.size()){
                numberOfShowProducts = products.size();

                showMoreResults.setVisibility(View.GONE);
            }
        }

        recyclerViewProducts.setAdapter(new ProductsAdapter(activity, products.stream().limit(numberOfShowProducts).collect(Collectors.toList())));
    }

    private void search(String searchFor){
        searchEditText.clearFocus();

        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(getView().getWindowToken(), 0);

        searchHints.setAdapter(null);

        if (!searchFor.equals("")){
            Bundle bundle = new Bundle();
            bundle.putString("searchedPhrase", searchFor);

            SearchFragment fragment = new SearchFragment();
            fragment.setArguments(bundle);

            activity.selectNavigationItem(R.id.search);
            activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
        }
    }

    private void showHints(String searchFor){
        hintsList = new ArrayList<>();

        List<Product> filteredProducts = products.stream().filter(product ->
                        product.getName().toLowerCase(Locale.ROOT).contains(searchFor.toLowerCase(Locale.ROOT))
                                || product.getAuthor().toLowerCase(Locale.ROOT).contains(searchFor.toLowerCase(Locale.ROOT))
                                || product.getTags().toLowerCase(Locale.ROOT).contains(searchFor.toLowerCase(Locale.ROOT)))
                        .collect(Collectors.toList());

        String hint = "";
        String category = "";

        List<String> usedHints = new ArrayList<>();

        for (Product p : filteredProducts){
            int phrasePos = p.toString().length();
            int temp;

            if (p.getName().toLowerCase(Locale.ROOT).contains(searchFor.toLowerCase(Locale.ROOT))
                    && !usedHints.contains(p.getName())){
                temp = p.getName().toLowerCase(Locale.ROOT).indexOf(searchFor.toLowerCase());

                if (temp < phrasePos){
                    phrasePos = temp;
                    hint = p.getName();
                    category = getResources().getString(R.string.hint_category_book);

                    hintsMap = new HashMap<>();
                    hintsMap.put("hint", hint);
                    hintsMap.put("category", category);

                    hintsList.add(hintsMap);

                    usedHints.add(hint);
                }
            }

            for (String s : p.getAuthor().split(" ")){
                if (s.toLowerCase(Locale.ROOT).contains(searchFor.toLowerCase(Locale.ROOT))
                        && !usedHints.contains(p.getAuthor())){
                    temp = s.toLowerCase(Locale.ROOT).indexOf(searchFor.toLowerCase());

                    if (temp < phrasePos){
                        phrasePos = temp;
                        hint = p.getAuthor();
                        category = getResources().getString(R.string.hint_category_author);

                        hintsMap = new HashMap<>();
                        hintsMap.put("hint", hint);
                        hintsMap.put("category", category);

                        hintsList.add(hintsMap);

                        usedHints.add(hint);
                    }
                }
            }

            for (String s : p.getTagsArray()){
                if (s.toLowerCase(Locale.ROOT).contains(searchFor.toLowerCase(Locale.ROOT))
                        && !usedHints.contains(s)){
                    temp = s.toLowerCase(Locale.ROOT).indexOf(searchFor.toLowerCase());

                    if (temp < phrasePos){
                        phrasePos = temp;
                        hint = s;
                        category = getResources().getString(R.string.hint_category_tag);

                        hintsMap = new HashMap<>();
                        hintsMap.put("hint", hint);
                        hintsMap.put("category", category);

                        hintsList.add(hintsMap);

                        usedHints.add(hint);
                    }
                }
            }

            //git

//            if (!hint.equals("") && !category.equals("") && !usedHints.contains(hint)){
//
//            }
        }

        hintsList = (ArrayList<HashMap<String, Object>>) hintsList.stream().sorted(Comparator.comparing(hintMap -> ((String) hintMap.get("hint")).toLowerCase(Locale.ROOT).indexOf(searchFor.toLowerCase(Locale.ROOT)))).limit(5).collect(Collectors.toList());

        if (hintsList.size() > 0){
            SimpleAdapter adapter = new SimpleAdapter(
                    getActivity().getApplicationContext(),
                    hintsList,
                    R.layout.item_listview_search_hint,
                    from,
                    to);

            searchHints.setAdapter(adapter);
        }

        else {
            searchHints.setVisibility(View.GONE);
            searchHints.setAdapter(null);
        }
    }
}
