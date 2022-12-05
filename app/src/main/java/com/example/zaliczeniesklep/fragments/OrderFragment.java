package com.example.zaliczeniesklep.fragments;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.zaliczeniesklep.MainActivity;
import com.example.zaliczeniesklep.R;
import com.example.zaliczeniesklep.adapters.OrderAdapter;
import com.example.zaliczeniesklep.database_entity.CartItem;
import com.example.zaliczeniesklep.database_entity.Order;
import com.example.zaliczeniesklep.database_entity.Product;
import com.example.zaliczeniesklep.helper.DatabaseHelper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class OrderFragment extends Fragment {
    private MainActivity activity;

    private RelativeLayout orderBackButton;
    private TextView finalPriceTextView;
    private RecyclerView recyclerView;
    private Button orderButton;

    private EditText buyerTextView;
    private EditText emailTextView;
    private EditText phoneNumTextView;

    private float price = 0;
    private List<CartItem> cart;

    // TODO - send email with order

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_order, container, false);

        activity = (MainActivity) getActivity();

        activity.toggleCartComponents();

        if (getArguments() != null){
            price = getArguments().getFloat("price");
        }

        else {
            closeFragment();
        }

        cart = activity.getCart();

        recyclerView = view.findViewById(R.id.order_recycler_view_products);

        finalPriceTextView = view.findViewById(R.id.order_final_price);
        finalPriceTextView.setText((String.format("%.02f", price) + "PLN"));

        buyerTextView = view.findViewById(R.id.buyer_edit_text);
        emailTextView = view.findViewById(R.id.order_email_edit_text);
        phoneNumTextView = view.findViewById(R.id.order_phone_num_edit_text);

        orderButton = view.findViewById(R.id.make_an_order_button);
        orderButton.setOnClickListener(v -> order());

        orderBackButton = view.findViewById(R.id.order_back_button_container);
        orderBackButton.setOnClickListener(v -> closeFragment());

        recyclerView.setAdapter(new OrderAdapter(activity, cart));

        if (activity.getUser() != null){
            emailTextView.setText(activity.getUser().getEmail());
            phoneNumTextView.setText(activity.getUser().getPhoneNumber());
        }

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

    private boolean isEmailValid(CharSequence email){
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailTextView.setError(getString(R.string.invalid_email_error_message));

            return false;
        }
        return true;
    }

    private boolean isPhoneNumValid(String phoneNum){
        if (!phoneNum.matches("^[0-9]{9}$")){
            phoneNumTextView.setError(getString(R.string.invalid_phone_num_error_message));

            return false;
        }

        return true;
    }

    private boolean isInputNotEmpty(TextView textView){
        if (textView.length() == 0){
            textView.setError(getString(R.string.empty_field_error_message));

            return false;
        }

        return true;
    }

    private void order(){
        if (!isInputNotEmpty(buyerTextView) || !isInputNotEmpty(emailTextView) || !isInputNotEmpty(phoneNumTextView)){
            return;
        }

        String buyer = buyerTextView.getText().toString();
        String email = emailTextView.getText().toString();
        String phone = phoneNumTextView.getText().toString();

        if (!isEmailValid(email) || !isPhoneNumValid(phone)){
            return;
        }

        LocalDateTime orderDate = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

        String formattedDate = orderDate.format(formatter);

        int userId = (activity.getUser() == null ? -1 : activity.getUser().getId());

        String items = "";

        for (CartItem item : cart){
            items += item.getItemForOrder() + "&";
        }

        items = items.substring(0, items.length() - 1);

        Order order = new Order(formattedDate, userId, buyer, items, (float) (Math.round(price * 100.0) / 100.0), email, phone);

        DatabaseHelper helper = new DatabaseHelper(activity);
        helper.addNewOrder(order);

//        Log.i("123123123123", createMessageFromOrder(order));

        sendMessages(order);
        
        activity.clearCart();

        ((CartFragment)getParentFragment()).updateRecyclerView();
        
        closeFragment();

        Toast.makeText(activity, getString(R.string.order_accepted), Toast.LENGTH_SHORT).show();
    }


    private String createMessageFromOrder(Order order){
        String message = getString(R.string.order_accepted_message) + "\n\n";

        message += getString(R.string.purchase_date) + " " + order.getDate() + "\n";
        message += getString(R.string.buyer) + " " + order.getBuyer() + "\n";

        DatabaseHelper helper = new DatabaseHelper(activity);
        List<Product> products = helper.getProductsFromDB();

        for (int i = 0; i < cart.size(); i++){
            CartItem item = cart.get(i);

            message += (i + 1) + ". " + products.get(item.getProduct_id() - 1).getName() + ", " + products.get(item.getProduct_id() - 1).getAuthor() + ", " + getString(R.string.quantity) + " " + item.getQuantity() + ";\n";
        }

        message += "\n" + getString(R.string.final_price) + " " + String.format("%.02f", order.getPrice()) + "PLN";

        return message;
    }

    public static boolean isSimSupport(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        return !(tm.getSimState() == TelephonyManager.SIM_STATE_ABSENT);
    }

    private void sendMessages(Order order){
        String message = createMessageFromOrder(order);

        if (isSimSupport(activity) && activity.checkPermission(Manifest.permission.SEND_SMS, MainActivity.MY_PERMISSION_REQUEST_SEND_SMS)){
            sendSms(message);
        }

        sendEmail(message);
    }

    private void sendEmail(String message){
        new Thread(new Runnable() {
            @Override
            public void run() {
//                try {
//                    GMailSender sender = new GMailSender("takozsosem@gmail.com", "takozsosem123");
//                    sender.sendMail(getString(R.string.order_accepted_message), message, "takozsosem@gmail.com", emailTextView.getText().toString());
//                } catch (Exception e) {
//                    Log.i("123123123123", e.getMessage(), e);
//                }
            }

        }).start();
    }

    private void sendSms(String message){
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED){
            String destinationNumber = phoneNumTextView.getText().toString();

            ArrayList<String> multipartText = null;

            if (!destinationNumber.equals("") && !message.equals("")){
                SmsManager smsManager = SmsManager.getDefault();

                multipartText = smsManager.divideMessage(message);

                smsManager.sendMultipartTextMessage(
                        destinationNumber,
                        null,
                        multipartText,
                        null,
                        null
                );
            }
        }
    }
}