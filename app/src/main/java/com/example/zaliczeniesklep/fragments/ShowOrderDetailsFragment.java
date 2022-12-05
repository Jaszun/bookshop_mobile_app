package com.example.zaliczeniesklep.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.zaliczeniesklep.MainActivity;
import com.example.zaliczeniesklep.R;
import com.example.zaliczeniesklep.adapters.OrderAdapter;
import com.example.zaliczeniesklep.database_entity.Order;
import com.example.zaliczeniesklep.helper.DatabaseHelper;


public class ShowOrderDetailsFragment extends Fragment {
    private MainActivity activity;

    private RelativeLayout orderBackButton;
    private RecyclerView recyclerView;

    private TextView dateOfPurchase;
    private TextView orderPrice;
    private TextView orderExecuted;

    private Order order;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_show_order_details, container, false);

        activity = (MainActivity) getActivity();

        activity.toggleCartComponents();

        if (getArguments() != null){
            DatabaseHelper helper = new DatabaseHelper(activity);

            order = helper.getOrderByIdFromDB(getArguments().getInt("orderId"));
        }

        else {
            closeFragment();
        }

        recyclerView = view.findViewById(R.id.show_order_details_recycler_view_products);

        dateOfPurchase = view.findViewById(R.id.order_details_date_of_purchase);
        orderPrice = view.findViewById(R.id.order_details_price);
        orderExecuted = view.findViewById(R.id.order_executed);

        dateOfPurchase.setText(order.getDate());
        orderPrice.setText(order.getPrice() + " PLN");

        if (order.getIsExecuted() == 0){
            orderExecuted.setText(activity.getString(R.string.order_not_executed));
            orderExecuted.setTextColor(activity.getColor(R.color.app_color_secondary));
        }

        else{
            orderExecuted.setText(activity.getString(R.string.order_executed));
            orderExecuted.setTextColor(activity.getColor(R.color.white));
        }

        orderBackButton = view.findViewById(R.id.show_order_details_back_button_container);
        orderBackButton.setOnClickListener(v -> closeFragment());

        recyclerView.setAdapter(new OrderAdapter(activity, order));

        return view;
    }

    private void closeFragment(){
        hideKeyboard();
        activity.toggleCartComponents();

        getParentFragmentManager().beginTransaction().setCustomAnimations(R.anim.fragment_slide_in_top, R.anim.fragment_slide_out_top).hide(this).commit();
    }

    private void hideKeyboard(){
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(getView().getWindowToken(), 0);
    }
}