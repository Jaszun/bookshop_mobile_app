package com.example.zaliczeniesklep.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.zaliczeniesklep.MainActivity;
import com.example.zaliczeniesklep.R;
import com.example.zaliczeniesklep.adapters.OrderAdapter;
import com.example.zaliczeniesklep.database_entity.Order;
import com.example.zaliczeniesklep.helper.DatabaseHelper;
import com.example.zaliczeniesklep.schema.Schema;


public class ShowOrderDetailsFragment extends Fragment {
    private MainActivity activity;

    private RelativeLayout orderBackButton;
    private RecyclerView recyclerView;

    private TextView dateOfPurchase;
    private TextView orderPrice;
    private TextView orderExecuted;
    private TextView orderId;
    private TextView orderPhoneNum;

    private Button confirmExecution;

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
        orderId = view.findViewById(R.id.order_details_id);
        orderPhoneNum = view.findViewById(R.id.order_details_phone);

        confirmExecution = view.findViewById(R.id.confirm_order_execution);
        confirmExecution.setOnClickListener(v -> confirmOrderExecution());

        if (activity.getUser() != null && activity.getUser().getUserType() == 1){
            orderPhoneNum.setVisibility(View.VISIBLE);

            view.findViewById(R.id.order_details_phone_label).setVisibility(View.VISIBLE);

            if (order.getIsExecuted() == 0){
                confirmExecution.setVisibility(View.VISIBLE);
            }

            else {
                confirmExecution.setVisibility(View.GONE);
            }
        }

        else {
            view.findViewById(R.id.order_details_phone_label).setVisibility(View.GONE);

            orderPhoneNum.setVisibility(View.GONE);
            confirmExecution.setVisibility(View.GONE);
        }

        orderId.setText(order.getId() + "");
        dateOfPurchase.setText(order.getDate().split(" ")[0]);
        orderPrice.setText(order.getPrice() + " PLN");
        orderPhoneNum.setText(order.getPhoneNumber());

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

    private void confirmOrderExecution(){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage(activity.getResources().getString(R.string.execute_order_message));
        builder.setTitle(activity.getResources().getString(R.string.execute_order_title));
        builder.setCancelable(false);
        builder.setNegativeButton(activity.getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.setPositiveButton(activity.getResources().getString(R.string.apply_changes), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                DatabaseHelper helper = new DatabaseHelper(activity);

                helper.updateRowById(order, order.getId(), Schema.OrdersSchema.IS_EXECUTED_COLUMN, "1");

                ((ShowOrdersFragment) getParentFragment()).loadOrdersToListView();

                closeFragment();

                Toast.makeText(activity, activity.getString(R.string.submited_execution), Toast.LENGTH_SHORT).show();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
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