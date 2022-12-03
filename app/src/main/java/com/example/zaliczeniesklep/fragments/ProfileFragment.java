package com.example.zaliczeniesklep.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.zaliczeniesklep.MainActivity;
import com.example.zaliczeniesklep.R;

public class ProfileFragment extends Fragment {
    private MainActivity activity;

    private RelativeLayout welcomePanel;
    private RelativeLayout buttonContainer;
    private RelativeLayout appInfo;
    private RelativeLayout saveLoginInfo;

    private RelativeLayout checkOrders;

    private Button loginButon;

    private CheckBox checkBox;

    private TextView welcomeMessageTextView;
    private TextView logoutTextView;

    private Spinner chooseThemeSpinner;
    private Spinner chooseLangSpinner;

    // TODO - make changing theme and language work or delete it

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        activity = (MainActivity) getActivity();

        loginButon = view.findViewById(R.id.login_button);

        checkBox = view.findViewById(R.id.save_login_checkbox);
        checkBox.setOnClickListener(v -> toggleSaveUser());

        if (activity.isRememberUser()){
            checkBox.setChecked(true);
        }

        else{
            checkBox.setChecked(false);
        }

        appInfo = view.findViewById(R.id.app_info);
        welcomePanel = view.findViewById(R.id.welcome_panel);
        buttonContainer = view.findViewById(R.id.login_button_container);
        saveLoginInfo = view.findViewById(R.id.save_login_info);

        checkOrders = view.findViewById(R.id.check_orders);
        checkOrders.setOnClickListener(v -> showOrders());

        welcomeMessageTextView = view.findViewById(R.id.welcome_message);
        logoutTextView = view.findViewById(R.id.logout);

        logoutTextView.setOnClickListener(v -> logout());

        chooseThemeSpinner = view.findViewById(R.id.choose_theme);
        chooseLangSpinner = view.findViewById(R.id.choose_language);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(activity, R.array.themes, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        chooseThemeSpinner.setAdapter(adapter);

        adapter = ArrayAdapter.createFromResource(activity, R.array.languages, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        chooseLangSpinner.setAdapter(adapter);

        loginButon.setOnClickListener(v -> runSignInFragment());

        appInfo.setOnClickListener(v -> {
            Toast.makeText(activity, getResources().getString(R.string.hint_category_author) + ": Joachim Urban 4pp", Toast.LENGTH_LONG).show();
        });

        updateView();

        return view;
    }

    private void showOrders(){
        //TODO - add orders list view

        Toast.makeText(activity, "Showing orders", Toast.LENGTH_SHORT).show();
    }

    private void toggleSaveUser(){
        activity.setRememberUser(!activity.isRememberUser());
    }

    private void logout(){
        activity.setUser(null);
        welcomeMessageTextView.setText("");
        activity.setRememberUser(false);
        checkBox.setChecked(false);

        updateView();
    }

    public void updateView(){
        if (activity.getUser() != null){
            String user = activity.getUser().getEmail();

            buttonContainer.setVisibility(View.GONE);
            welcomePanel.setVisibility(View.VISIBLE);

            String welcomeMessage = getString(R.string.welcome) + ", " + user;

            if (user.contains("@")){
                welcomeMessage = getString(R.string.welcome) + ", " + user.substring(0, user.indexOf("@"));
            }

            welcomeMessageTextView.setText(welcomeMessage);
            logoutTextView.setVisibility(View.VISIBLE);
            saveLoginInfo.setVisibility(View.VISIBLE);
        }

        else{
            buttonContainer.setVisibility(View.VISIBLE);
            welcomePanel.setVisibility(View.GONE);
            logoutTextView.setVisibility(View.GONE);
            saveLoginInfo.setVisibility(View.GONE);
        }
    }

    private void runSignInFragment(){
        getChildFragmentManager().beginTransaction().setCustomAnimations(R.anim.fragment_slide_in_top, R.anim.fragment_slide_out_top).replace(R.id.profile_fragment_layout, new SignInFragment()).commit();
    }
}
