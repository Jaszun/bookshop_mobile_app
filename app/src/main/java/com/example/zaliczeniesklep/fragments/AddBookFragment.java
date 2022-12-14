package com.example.zaliczeniesklep.fragments;

import static android.app.Activity.RESULT_OK;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.zaliczeniesklep.MainActivity;
import com.example.zaliczeniesklep.R;
import com.example.zaliczeniesklep.adapters.TagAdapter;
import com.example.zaliczeniesklep.database_entity.Product;
import com.example.zaliczeniesklep.helper.DatabaseHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AddBookFragment extends Fragment {
    private MainActivity activity;

    private String imageName;

    private int currentFragmentId = 0;
    private List<String> subcategories;
    private Uri imageUri;

    private RelativeLayout backButton;

    private TextView bookPreviewTitle;
    private TextView bookPreviewAuthor;
    private TextView bookPreviewPrice;

    private ImageView bookPreviewCover;

    private Spinner categoriesSpinner;

    private EditText bookTitle;
    private EditText bookAuthor;
    private EditText bookPrice;
    private EditText bookQuantity;
    private EditText bookSubcategory;

    private Button addSubcategoryButton;
    private Button chooseCoverImageButton;
    private Button addBookButton;

    private RecyclerView subcategoriesRecyclerView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_book, container, false);

        activity = (MainActivity) getActivity();

        if (getArguments() != null){
            currentFragmentId = getArguments().getInt("currentFragmentId");

            toggleCurrentFragmentComponents();
        }

        backButton = view.findViewById(R.id.add_book_back_button_container);
        backButton.setOnClickListener(v -> closeFragment());

        bookPreviewTitle = view.findViewById(R.id.add_book_title);
        bookPreviewAuthor = view.findViewById(R.id.add_book_author);
        bookPreviewPrice = view.findViewById(R.id.add_book_price);

        bookPreviewCover = view.findViewById(R.id.add_book_cover_img);

        bookTitle = view.findViewById(R.id.add_book_title_edit_text);
        bookAuthor = view.findViewById(R.id.add_book_author_edit_text);
        bookPrice = view.findViewById(R.id.add_book_price_edit_text);
        bookQuantity = view.findViewById(R.id.add_book_quantity_edit_text);
        bookSubcategory = view.findViewById(R.id.add_book_tag_name);

        addSubcategoryButton = view.findViewById(R.id.add_book_add_subcategory_button);
        addSubcategoryButton.setOnClickListener(v -> addSubcategory());

        chooseCoverImageButton = view.findViewById(R.id.add_book_choose_image);
        chooseCoverImageButton.setOnClickListener(v -> chooseImage());

        addBookButton = view.findViewById(R.id.add_book_button);
        addBookButton.setOnClickListener(v -> validateData());

        subcategoriesRecyclerView = view.findViewById(R.id.add_book_tags_recycler_view);

        categoriesSpinner = view.findViewById(R.id.add_book_category_spinner);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(activity, R.array.categories, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        categoriesSpinner.setAdapter(adapter);

        subcategories = new ArrayList<>();

        updateRecyclerView();

        bookTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                bookPreviewTitle.setText(bookTitle.getText().toString());
            }
        });

        bookAuthor.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                bookPreviewAuthor.setText(bookAuthor.getText().toString());
            }
        });

        bookPrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                bookPreviewPrice.setText(bookPrice.getText().toString() + " PLN");
            }
        });

        return view;
    }

    public void updateRecyclerView(){
        subcategoriesRecyclerView.setAdapter(new TagAdapter(activity, subcategories, this));
    }

    public void removeTag(String tag){
        subcategories.remove(tag);
    }

    private void addSubcategory() {
        if (bookSubcategory.length() != 0){
            if (!subcategories.contains(bookSubcategory.getText().toString())){
                subcategories.add(bookSubcategory.getText().toString());
                updateRecyclerView();
            }

            bookSubcategory.setText("");
        }

        hideKeyboard();
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);

        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                imageUri = data.getData();

//                Toast.makeText(getContext(), imageUri.toString(), Toast.LENGTH_SHORT).show();

                if (imageUri != null) {
                    bookPreviewCover.setImageURI(imageUri);
                }

                else {
                    bookPreviewCover.setImageDrawable(activity.getDrawable(R.drawable.ic_launcher_background));
                }
            }
        }
    }

    private void saveFileInInternalStorage(Context context, Uri photoUri){
        File storageDir = new File(String.valueOf(context.getExternalFilesDir("book_covers")));

        if (!storageDir.exists()){
            storageDir.mkdirs();
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        imageName = "b_" + timeStamp + ".jpg";

        Bitmap bitmap = null;

        try {
            bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), photoUri);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            FileOutputStream out = new FileOutputStream(storageDir + "/" + imageName);

            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);

            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void validateData() {
        if (bookTitle.length() == 0){
            bookTitle.setError(getString(R.string.empty_field_error_message));
            return;
        }

        else if (bookAuthor.length() == 0){
            bookAuthor.setError(getString(R.string.empty_field_error_message));
            return;
        }

        else if (bookPrice.length() == 0){
            bookPrice.setError(getString(R.string.empty_field_error_message));
            return;
        }

        else if (bookQuantity.length() == 0){
            bookQuantity.setError(getString(R.string.empty_field_error_message));
            return;
        }

        else if (subcategories.size() == 0){
            bookSubcategory.setError(getString(R.string.subcategory_error));
            return;
        }

        else if (imageUri == null){
            chooseCoverImageButton.setError(getString(R.string.image_not_chosen_error));
            return;
        }

        showDialog();
    }

    private void showDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage(activity.getResources().getString(R.string.add_book_dialog_message));
        builder.setTitle(activity.getResources().getString(R.string.add_book_dialog_title));
        builder.setCancelable(false);
        builder.setNegativeButton(activity.getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.setPositiveButton(activity.getResources().getString(R.string.add_sth), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                addBook();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    private void addBook(){
        saveFileInInternalStorage(activity, imageUri);

        DatabaseHelper helper = new DatabaseHelper(activity);

        String name = bookTitle.getText().toString();
        int categoryId = categoriesSpinner.getSelectedItemPosition() + 1;
        String author = bookAuthor.getText().toString();
        float price = Float.parseFloat(bookPrice.getText().toString());
        int quantity = Integer.parseInt(bookQuantity.getText().toString());
        String tags = Product.convertListToTags(subcategories);
        String image = imageName;

        helper.addNewProduct(new Product(name, categoryId, author, price, quantity, tags, image));

        Toast.makeText(activity, getString(R.string.added_book), Toast.LENGTH_SHORT).show();

        activity.refershProducts();

        closeFragment();
    }

    private void toggleCurrentFragmentComponents(){
        switch (currentFragmentId){
            case 1:
                activity.toggleSearchComponents();
                break;
            case 2:
                activity.toggleCartComponents();
                break;
            case 3:
                activity.toggleLoginComponents();
                break;
            default:
                activity.toggleHomeComponents();
        }
    }

    private void closeFragment(){
        hideKeyboard();

        toggleCurrentFragmentComponents();

        getParentFragmentManager().beginTransaction().setCustomAnimations(R.anim.fragment_slide_in_top, R.anim.fragment_slide_out_top).hide(this).commit();
    }

    private void hideKeyboard(){
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(getView().getWindowToken(), 0);
    }
}
