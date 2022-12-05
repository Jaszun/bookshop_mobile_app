package com.example.zaliczeniesklep.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.zaliczeniesklep.MainActivity;
import com.example.zaliczeniesklep.R;
import com.example.zaliczeniesklep.database_entity.Order;
import com.example.zaliczeniesklep.helper.DatabaseHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class ShowOrdersFragment extends Fragment {
    private RelativeLayout back;
    private ListView listView;
    private TextView noOrders;
    private HashMap<String, Object> ordersMap;
    private ArrayList<HashMap<String, Object>> ordersList;
    private SimpleAdapter adapter;
    private String[] from = {"id", "date", "price"};
    private int[] to = {R.id.order_item_id, R.id.order_item_date, R.id.order_item_price};
    private MainActivity activity;
    private List<Order> orders;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_show_orders, container, false);

        activity = (MainActivity) getActivity();

        if (activity.getUser() == null){
            hideFragment();
        }

        activity.toggleLoginComponents();

        listView = view.findViewById(R.id.show_orders_list_view);

        noOrders = view.findViewById(R.id.no_orders);

        back = view.findViewById(R.id.show_orders_back_button_container);
        back.setOnClickListener(v -> hideFragment());

        loadOrdersToListView();

        return view;
    }

    public void loadOrdersToListView(){
        DatabaseHelper helper = new DatabaseHelper(activity);

        ordersList = new ArrayList<>();

        if (getArguments() != null){
            orders = helper.getOrdersByExecution(getArguments().getInt("orderType"));
        }

        else {
            orders = helper.getOrdersForUser(activity.getUser().getId());
        }

        Collections.reverse(orders);

        if (orders.size() == 0){
            noOrders.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
        }

        else{
            noOrders.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
        }

        for (int i = 0; i < orders.size(); i++){
            Order order = orders.get(i);

            ordersMap = new HashMap<>();
            ordersMap.put("id", (i + 1) + ".");
            ordersMap.put("date", order.getDate().substring(0, order.getDate().indexOf(" ")));
            ordersMap.put("price", order.getPrice() + " PLN");

            ordersList.add(ordersMap);
        }

        adapter = new SimpleAdapter(
                getActivity().getApplicationContext(),
                ordersList,
                R.layout.item_list_view_show_order,
                from,
                to);

        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Bundle bundle = new Bundle();
                bundle.putInt("orderId", orders.get(i).getId());

                Fragment fragment = new ShowOrderDetailsFragment();
                fragment.setArguments(bundle);

                getChildFragmentManager().beginTransaction().setCustomAnimations(R.anim.fragment_slide_in_top, R.anim.fragment_slide_out_top).replace(R.id.show_order_fragment_layout, fragment).commit();
            }
        });
    }

    private void hideFragment(){
        activity.toggleLoginComponents();

        getParentFragmentManager().beginTransaction().setCustomAnimations(R.anim.fragment_slide_in_top, R.anim.fragment_slide_out_top).hide(this).commit();
    }
}