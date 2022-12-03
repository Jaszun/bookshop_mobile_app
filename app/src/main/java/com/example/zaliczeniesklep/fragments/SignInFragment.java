package com.example.zaliczeniesklep.fragments;

import android.content.Context;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.zaliczeniesklep.MainActivity;
import com.example.zaliczeniesklep.R;
import com.example.zaliczeniesklep.database_entity.User;
import com.example.zaliczeniesklep.helper.DatabaseHelper;

import java.util.List;
import java.util.Locale;

public class SignInFragment extends Fragment {
    private MainActivity activity;

    private boolean formType = true; // true - login | false - register

    private RelativeLayout backBar;
    private RelativeLayout signInInputs;

    private Button signInButton;

    private TextView changeFormTypeTitleTextView;
    private TextView changeFormTypeTextView;

    private ImageView passwordVisibility;
    private ImageView repetedPasswordVisibility;

    private EditText loginEditText;
    private EditText passwordEditText;
    private EditText repetedPasswordEditText;
    private EditText phoneNumEditText;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sign_in, container, false);

        activity = (MainActivity) getActivity();

        activity.toggleLoginComponents();

        signInButton = view.findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(v -> signIn());

        backBar = view.findViewById(R.id.sign_in_back_button_container);
        backBar.setOnClickListener(v -> hideFragment());

        changeFormTypeTitleTextView = view.findViewById(R.id.change_form_type_title);

        changeFormTypeTextView = view.findViewById(R.id.change_form_type);
        changeFormTypeTextView.setOnClickListener(v -> changeFormType());

        signInInputs = view.findViewById(R.id.sign_in_form_inputs);

        loginEditText = view.findViewById(R.id.email_edit_text);
        passwordEditText = view.findViewById(R.id.password_edit_text);
        repetedPasswordEditText = view.findViewById(R.id.repete_password_edit_text);
        phoneNumEditText = view.findViewById(R.id.phone_num_edit_text);

        passwordVisibility = view.findViewById(R.id.show_password);
        repetedPasswordVisibility = view.findViewById(R.id.show_repeted_password);

        passwordVisibility.setOnClickListener(v -> togglePasswordVisibility());
        repetedPasswordVisibility.setOnClickListener(v -> toggleRepetedPasswordVisibility());

        return view;
    }

    private void togglePasswordVisibility(){
        if (passwordEditText.getInputType() == 129){
            passwordEditText.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            passwordVisibility.setBackground(getResources().getDrawable(R.drawable.ic_baseline_visibility_off_24));
        }

        else {
            passwordEditText.setInputType(129);
            passwordVisibility.setBackground(getResources().getDrawable(R.drawable.ic_baseline_visibility_24));
        }
    }

    private void toggleRepetedPasswordVisibility(){
        if (repetedPasswordEditText.getInputType() == 129){
            repetedPasswordEditText.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            repetedPasswordVisibility.setBackground(getResources().getDrawable(R.drawable.ic_baseline_visibility_off_24));
        }

        else {
            repetedPasswordEditText.setInputType(129);
            repetedPasswordVisibility.setBackground(getResources().getDrawable(R.drawable.ic_baseline_visibility_24));
        }
    }

    private void signIn(){
        if (checkInputLength(loginEditText) && checkInputLength(passwordEditText)){
            String login = loginEditText.getText().toString();
            String password = passwordEditText.getText().toString();

            DatabaseHelper helper = new DatabaseHelper(activity);

            List<User> users = helper.getUsersFromDB();

            if (formType){
                for (User u : users){
                    if (u.getEmail().toLowerCase(Locale.ROOT).equals(login.toLowerCase(Locale.ROOT))){
                        if (u.getPassword().equals(password)){
                            activity.setUser(u);

                            ((ProfileFragment) getParentFragment()).updateView();

                            hideFragment();

                            return;
                        }

                        Toast.makeText(activity, "Niepoprawne dane logowania", Toast.LENGTH_SHORT).show();

                        return;
                    }
                }

                Toast.makeText(activity, "Taki użytkownik nie istnieje", Toast.LENGTH_SHORT).show();
            }

            else {
                if (checkInputLength(repetedPasswordEditText) && checkInputLength(phoneNumEditText)){
                    String repetedPassword = repetedPasswordEditText.getText().toString();
                    String phoneNum = phoneNumEditText.getText().toString();

                    for (User u : users){
                        if (u.getEmail().toLowerCase(Locale.ROOT).equals(login.toLowerCase(Locale.ROOT))){
                            loginEditText.setError(getString(R.string.user_already_exists_error_message));

                            return;
                        }

                        else if (u.getPhoneNumber().equals(phoneNum)){
                            phoneNumEditText.setError(getString(R.string.phone_num_already_taken_error_message));

                            return;
                        }
                    }

                    if (!isEmailValid(login)){
                        loginEditText.setError(getString(R.string.invalid_email_error_message));

                        return;
                    }

                    if (password.length() < 8){
                        passwordEditText.setError(getString(R.string.password_too_short_error_message));

                        return;
                    }

                    if (!password.equals(repetedPassword)){
                        repetedPasswordEditText.setError(getString(R.string.repeted_password_not_the_same_error_message));

                        return;
                    }

                    if (!phoneNum.matches("^[0-9]{9}$")){
                        phoneNumEditText.setError(getString(R.string.invalid_phone_num_error_message));

                        return;
                    }

                    registerNewUser(new User(login, password, phoneNum, 0));
                }
            }
        }
    }

    private void registerNewUser(User user){
        DatabaseHelper helper = new DatabaseHelper(activity);

        helper.addNewUser(user);
        
        loginEditText.setText("");
        passwordEditText.setText("");
        repetedPasswordEditText.setText("");
        phoneNumEditText.setText("");
        
        changeFormType();

        Toast.makeText(activity, getString(R.string.user_added), Toast.LENGTH_LONG).show();
    }

    private boolean isEmailValid(CharSequence email){
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean checkInputLength(EditText editText){
        if (editText.getText().toString().length() == 0){
            editText.setError(getString(R.string.empty_field_error_message));

            return false;
        }

        return true;
    }

    public void hideFragment(){
        hideKeyboard();

        getParentFragmentManager().beginTransaction().setCustomAnimations(R.anim.fragment_slide_in_top, R.anim.fragment_slide_out_top).hide(this).commit();

        activity.toggleLoginComponents();
    }

    private void changeFormType(){
        formType = !formType;

        if (formType){
            signInInputs.setVisibility(View.GONE);

            changeFormTypeTitleTextView.setText(R.string.dont_have_account);
            changeFormTypeTextView.setText(R.string.register_for_free);
            signInButton.setText(R.string.log_in);
        }

        else {
            signInInputs.setVisibility(View.VISIBLE);

            changeFormTypeTitleTextView.setText(R.string.do_have_accont);
            changeFormTypeTextView.setText(R.string.log_in_underline);
            signInButton.setText(R.string.register);
        }
    }

    private void hideKeyboard(){
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(getView().getWindowToken(), 0);
    }
}
