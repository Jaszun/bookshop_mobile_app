package com.example.zaliczeniesklep.fragments;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.example.zaliczeniesklep.MainActivity;
import com.example.zaliczeniesklep.R;
import com.example.zaliczeniesklep.database_entity.Category;
import com.example.zaliczeniesklep.database_entity.Product;
import com.example.zaliczeniesklep.helper.DatabaseHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ShowAllCategoriesFragment extends Fragment {
    private DatabaseHelper helper;
    private TextView backButton;
    private ListView listView;
    private String[] categoriesNames;
    private HashMap<String, Object> categoryMap;
    private ArrayList<HashMap<String, Object>> categoriesList;
    private SimpleAdapter adapter;
    private String[] from = {"name", "icon", "numberOfBooks"};
    private int[] to = {R.id.category_name, R.id.category_icon, R.id.number_of_books};
    private MainActivity activity;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_show_all_categories, container, false);

        activity = (MainActivity) getActivity();

        listView = view.findViewById(R.id.list_view_categories);

        backButton = view.findViewById(R.id.text_view_categories_back);
        backButton.setOnClickListener(v -> backToHome());

        categoriesNames = getResources().getStringArray(R.array.categories);
        categoriesList = new ArrayList<>();

        helper = new DatabaseHelper(getActivity().getApplicationContext());

        List<Category> categories = helper.getCategoriesFromDB();
        List<Product> products = helper.getProductsFromDB();

        for (Product p : products){
            Log.i("1234567", p.toString());
        }

        categoryMap = new HashMap<>();
        categoryMap.put("name", getResources().getString(R.string.categories_all));
        categoryMap.put("icon", R.drawable.i_book);
        categoryMap.put("numberOfBooks", getResources().getString(R.string.categories_number_of_books) + " " + products.size());

        categoriesList.add(categoryMap);

        for (int i = 0; i < categoriesNames.length; i++){
            categoryMap = new HashMap<>();
            categoryMap.put("name", categoriesNames[i]);
            categoryMap.put("icon", categories.get(i).getDrawableImageId());
            categoryMap.put("numberOfBooks", getResources().getString(R.string.categories_number_of_books) + " " + getNumberOfBooksInCategory(categories.get(i).getId(), products));

            categoriesList.add(categoryMap);
        }

        adapter = new SimpleAdapter(
                getActivity().getApplicationContext(),
                categoriesList,
                R.layout.item_listview_category,
                from,
                to);

        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Bundle bundle = new Bundle();
                bundle.putInt("CategoryId", i);

                if (getArguments() != null && !getArguments().getString("searchedPhrase").equals("")){
                    bundle.putString("searchedPhrase", getArguments().getString("searchedPhrase"));
                }

                SearchFragment fragment = new SearchFragment();
                fragment.setArguments(bundle);

                activity.toggleSearchEditText();
                activity.toggleNavigationMenu();
                activity.selectNavigationItem(R.id.search);
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
            }
        });

        return view;
    }

    private void backToHome(){
        getParentFragmentManager().beginTransaction().setCustomAnimations(R.anim.fragment_slide_in_top, R.anim.fragment_slide_out_top).hide(this).commit();

        activity.toggleSearchEditText();
        activity.toggleNavigationMenu();
    }

    private int getNumberOfBooksInCategory(int category, List<Product> products){
        int numberOfBooks = 0;

        for (Product p : products){
            if (p.getCategory_id() == category) numberOfBooks++;
        }

        return numberOfBooks;
    }
}