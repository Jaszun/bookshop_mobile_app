package com.example.zaliczeniesklep;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.zaliczeniesklep.database_entity.CartItem;
import com.example.zaliczeniesklep.database_entity.Category;
import com.example.zaliczeniesklep.database_entity.Product;
import com.example.zaliczeniesklep.database_entity.User;
import com.example.zaliczeniesklep.fragments.AddBookFragment;
import com.example.zaliczeniesklep.fragments.CartFragment;
import com.example.zaliczeniesklep.fragments.HomeFragment;
import com.example.zaliczeniesklep.fragments.ProfileFragment;
import com.example.zaliczeniesklep.fragments.SearchFragment;
import com.example.zaliczeniesklep.fragments.ShowOrdersFragment;
import com.example.zaliczeniesklep.helper.DatabaseHelper;
import com.example.zaliczeniesklep.schema.Schema;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//<div>Icons made by <a href="https://www.flaticon.com/authors/pixel-perfect" title="Pixel perfect">Pixel perfect</a> from <a href="https://www.flaticon.com/" title="Flaticon">www.flaticon.com</a></div><div>Icons made by <a href="https://www.freepik.com" title="Freepik">Freepik</a> from <a href="https://www.flaticon.com/" title="Flaticon">www.flaticon.com</a></div>

public class MainActivity extends AppCompatActivity {
    public final static int MY_PERMISSION_REQUEST_SEND_SMS = 1;
    public static int NUM_OF_COLS = 2;

    private BottomAppBar bottomAppBar;
    private BottomNavigationView bottomNavigationView;
    private FloatingActionButton floatingActionButton;

    private User user;

    private List<Product> products;
    private List<Category> categories;
    private List<CartItem> cart;

    private boolean admin = false;
    private boolean rememberUser = false;

    // TODO - add AddNewBookFragment and update addNewBook method

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        countNumOfGridCols();

        products = new ArrayList<>();
        categories = new ArrayList<>();
        cart = new ArrayList<>();

        bottomNavigationView = this.findViewById(R.id.bottom_app_navigation);
        bottomAppBar = this.findViewById(R.id.bottom_app_bar);

        floatingActionButton = this.findViewById(R.id.floating_action_button);
        floatingActionButton.setOnClickListener(v -> addNewBook());

        loadData();

        setNavigationMenu();

        loadDataAfterWipe();

        bottomNavigationView.setOnItemSelectedListener(item -> switchFragment(item));

        setCart();

        moveToFragment(getCurrentFragmentId());

        askForPermission(Manifest.permission.SEND_SMS, MY_PERMISSION_REQUEST_SEND_SMS);
    }

    public void refershProducts(){
        DatabaseHelper helper = new DatabaseHelper(this);

        products = helper.getProductsFromDB();
    }

    private void countNumOfGridCols(){
        DisplayMetrics displayMetrics = new DisplayMetrics();

        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        int screenWidth = displayMetrics.widthPixels;

        int recyclerViewProductItemWidthPx = (int) (200 * getResources().getDisplayMetrics().density);

        NUM_OF_COLS = (screenWidth / recyclerViewProductItemWidthPx < 2 ? 2 : (int) (screenWidth / recyclerViewProductItemWidthPx));
    }

    private int getCurrentFragmentId(){ // 0 - Home | 1 - Search | 2 - Cart | 3 - Profile
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);

        if (currentFragment instanceof SearchFragment){
            return 1;
        }

        else if (currentFragment instanceof CartFragment){
            return 2;
        }

        else if (currentFragment instanceof ProfileFragment){
            return 3;
        }

        return 0;
    }

    public void moveToFragment(int fragmentId){
        Fragment fragment;
        int itemId;

        switch (fragmentId){
            case 1:
                fragment = new SearchFragment();
                itemId = R.id.search;
                break;
            case 2:
                fragment = new CartFragment();
                itemId = R.id.cart;
                break;
            case 3:
                fragment = new ProfileFragment();
                itemId = R.id.profile;
                break;
            default:
                fragment = new HomeFragment();
                itemId = R.id.home;
        }

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
        bottomNavigationView.setSelectedItemId(itemId);
    }

    private void saveData() {
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putInt("currentFragment", getCurrentFragmentId());
        editor.putBoolean("rememberUser", rememberUser);

        int userId = -1;

        if (user != null){
            userId = user.getId();
        }

        editor.putInt("userId",userId);

        editor.apply();
    }

    private void loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);

//        Gson gson = new Gson();

        int currentFragment = sharedPreferences.getInt("currentFragment", 0);
        int userId = sharedPreferences.getInt("userId", -1);

        rememberUser = sharedPreferences.getBoolean("rememberUser", false);

        if (userId != -1){
            setUser(new DatabaseHelper(this).getUsersFromDB().get(userId - 1));
        }

        else {
            setUser(null);
        }

        moveToFragment(currentFragment);
    }

    private boolean switchFragment(MenuItem item){
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);

        switch (item.getItemId()) {
            case R.id.home:
                if (!(currentFragment instanceof HomeFragment)){
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
                }

                else if (this.findViewById(R.id.home_main_scroll_view) != null){
                    this.findViewById(R.id.home_main_scroll_view).scrollTo(0, 0);
                }

                break;

            case R.id.search:
                if (!(currentFragment instanceof SearchFragment)) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new SearchFragment()).commit();
                }

                else if (this.findViewById(R.id.search_main_scroll_view) != null){
                    this.findViewById(R.id.search_main_scroll_view).scrollTo(0, 0);
                }

                break;

            case R.id.cart:
                if (!(currentFragment instanceof CartFragment)) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new CartFragment()).commit();
                }

                break;

            case R.id.profile:
                if (!(currentFragment instanceof ProfileFragment)) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProfileFragment()).commit();
                }

                break;
        }

        return true;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;

        saveData();

        if (user != null && user.getUserType() == 1){
            admin = true;
        }

        else{
            admin = false;
        }

        setNavigationMenu();

        setCart();
    }

    private void setCart(){
        if (user == null){
            getCartFromSharedPreferences();
        }

        else{
            DatabaseHelper helper = new DatabaseHelper(this);

            cart = helper.getCartForUser(user.getId());
        }

        Log.i("123123123", "CurrentCart: ");

        for (CartItem c : cart){
            Log.i("123123123", "- " + c.toString());
        }
    }

    public List<CartItem> getCart() {
        return cart;
    }

    public void clearCart(){
        DatabaseHelper helper = new DatabaseHelper(this);

        for (CartItem item : cart){
//            Log.i("123456789", products.get(item.getProduct_id() - 1).getCount()+ "");
//            Log.i("123456789", item.getQuantity() + "");
            helper.updateRowById(new Product(), item.getProduct_id(), Schema.ProductsSchema.QUANTITY_COLUMN, String.valueOf((products.get(item.getProduct_id() - 1).getQuantity() - item.getQuantity())));
        }

        cart = new ArrayList<>();

        if (user == null){
            saveCartInSharedPreferences();
        }

        else {
            helper.deleteRowByCondition(new CartItem(), Schema.CartSchema.USER_ID_COLUMN, String.valueOf(user.getId()));
        }
    }

    public void addProductToCart(Product product, int quantity, boolean changeFromCart){
        boolean productAlreadyInCart = false;

        if (user == null){
            for (CartItem i : cart){
                if (i.getProduct_id() == product.getId()){
                    int finalQuantity = (changeFromCart ? quantity : i.getQuantity() + quantity);

                    if (finalQuantity > product.getQuantity()){
                        finalQuantity = product.getQuantity();
                    }

                    i.setQuantity(finalQuantity);

                    productAlreadyInCart = true;

                    break;
                }
            }

            if (quantity > 0 && !productAlreadyInCart){
                cart.add(new CartItem(-1, product.getId(), quantity));
            }

            saveCartInSharedPreferences();
            Log.i("123123123", "Shared: ");
        }

        else{
            for (CartItem i : cart){
                if (i.getProduct_id() == product.getId()){
                    int finalQuantity = (changeFromCart ? quantity : i.getQuantity() + quantity);

                    if (finalQuantity > product.getQuantity()){
                        finalQuantity = product.getQuantity();
                    }

                    i.setQuantity(finalQuantity);

                    productAlreadyInCart = true;

                    DatabaseHelper helper = new DatabaseHelper(this);
                    helper.updateRowById(i, i.getId(), Schema.CartSchema.QUANTITY_COLUMN, String.valueOf(finalQuantity));

                    break;
                }
            }

            if (quantity > 0 && !productAlreadyInCart){
                CartItem item = new CartItem(user.getId(), product.getId(), quantity);

                cart.add(item);

                DatabaseHelper helper = new DatabaseHelper(this);
                helper.addRow(item);
            }

            Log.i("123123123", "Database: ");
        }

        for (CartItem c : cart){
            Log.i("123123123", "- " + c.toString());
        }
    }

    private void getCartFromSharedPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);

        Gson gson = new Gson();

        String cartJson = sharedPreferences.getString("cart", null);

        Type type = new TypeToken<List<CartItem>>() {}.getType();

        cart = gson.fromJson(cartJson, type);

        if (cart == null) {
            cart = new ArrayList<>();
        }
    }

    public void saveCartInSharedPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();

        Gson gson = new Gson();

        String cartJson = gson.toJson(cart);

        editor.putString("cart", cartJson);

        editor.apply();
    }

    public boolean isRememberUser() {
        return rememberUser;
    }

    public void setRememberUser(boolean rememberUser) {
        this.rememberUser = rememberUser;
    }

    public List<Product> getProducts() {
        return products;
    }

    public Product getProduct(int index) {
        return products.get(index);
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

    public void selectNavigationItem(int id){
        bottomNavigationView.setSelectedItemId(id);
    }

    public void toggleNavigationMenu(){
        if (bottomAppBar.getVisibility() == View.VISIBLE){
            bottomAppBar.setVisibility(View.GONE);

            if (admin){
                floatingActionButton.setVisibility(View.GONE);
            }
        }

        else{
            bottomAppBar.setVisibility(View.VISIBLE);

            if (admin){
                floatingActionButton.setVisibility(View.VISIBLE);
            }
        }
    }

    public void toggleHomeComponents(){
        NestedScrollView scrollView = this.findViewById(R.id.home_main_scroll_view);

        if (scrollView != null){
            if (scrollView.getVisibility() == View.VISIBLE){
                scrollView.setVisibility(View.GONE);
            }

            else {
                scrollView.setVisibility(View.VISIBLE);
            }
        }
    }

    public void toggleSearchComponents(){
        NestedScrollView scrollView = this.findViewById(R.id.search_main_scroll_view);

        if (scrollView != null){
            if (scrollView.getVisibility() == View.VISIBLE){
                scrollView.setVisibility(View.GONE);
            }

            else {
                scrollView.setVisibility(View.VISIBLE);
            }
        }
    }

    public void toggleSearchEditText(){
        EditText homeEditText = this.findViewById(R.id.home_search_bar_edit_text);
        EditText searchEditText = this.findViewById(R.id.search_search_bar_edit_text);

        if (homeEditText != null){
            if (homeEditText.isEnabled()){
                homeEditText.setEnabled(false);
            }

            else{
                homeEditText.setEnabled(true);
            }
        }

        if (searchEditText != null){
            if (searchEditText.isEnabled()){
                searchEditText.setEnabled(false);
            }

            else{
                searchEditText.setEnabled(true);
            }
        }
    }

    public void toggleShowAllCategoriesButton(){
        Button button = this.findViewById(R.id.select_category_button);

        if (button != null){
            if (button.isEnabled()){
                button.setEnabled(false);
            }

            else{
                button.setEnabled(true);
            }
        }
    }

    public void toggleCartComponents(){
        Button button = this.findViewById(R.id.order_button);

        if (button != null){
            RecyclerView recyclerView = this.findViewById(R.id.order_recycler_view_products);
            ImageButton imageButton = this.findViewById(R.id.share_button);

            if (button.isEnabled()){
                button.setEnabled(false);
                imageButton.setEnabled(false);
                recyclerView.setVisibility(View.GONE);
            }

            else{
                button.setEnabled(true);
                imageButton.setEnabled(true);
                recyclerView.setVisibility(View.VISIBLE);
            }
        }
    }

    public void toggleLoginComponents(){
        Button button = this.findViewById(R.id.login_button);

        if (button != null){
            Spinner themeSpinner = this.findViewById(R.id.choose_theme);
            Spinner langSpinner = this.findViewById(R.id.choose_language);
            RelativeLayout appInfo = this.findViewById(R.id.app_info);
            RelativeLayout checkOrders = this.findViewById(R.id.check_orders);

            if (button.isEnabled()){
                button.setEnabled(false);
                themeSpinner.setEnabled(false);
                langSpinner.setEnabled(false);
                appInfo.setVisibility(View.GONE);
                checkOrders.setVisibility(View.GONE);
            }

            else{
                button.setEnabled(true);
                themeSpinner.setEnabled(true);
                langSpinner.setEnabled(true);
                appInfo.setVisibility(View.VISIBLE);
                checkOrders.setVisibility(View.VISIBLE);
            }
        }
    }

    private void setNavigationMenu(){
        if (admin){
            floatingActionButton.show();
            bottomNavigationView.getMenu().getItem(2).setVisible(true);
        }

        else{
            floatingActionButton.hide();
            bottomNavigationView.getMenu().getItem(2).setVisible(false);
        }
    }

    private void addNewBook(){
        int containerViewId;

        switch (getCurrentFragmentId()){
            case 1:
                containerViewId = R.id.search_fragment_layout;
                break;
            case 2:
                containerViewId = R.id.cart_fragment_layout;
                break;
            case 3:
                containerViewId = R.id.profile_fragment_layout;
                break;
            default:
                containerViewId = R.id.home_fragment_layout;
        }

        Bundle bundle = new Bundle();
        bundle.putInt("currentFragmentId", getCurrentFragmentId());

        Fragment fragment = new AddBookFragment();
        fragment.setArguments(bundle);

        getSupportFragmentManager().findFragmentById(R.id.fragment_container).getChildFragmentManager().beginTransaction().setCustomAnimations(R.anim.fragment_slide_in_top, R.anim.fragment_slide_out_top).replace(containerViewId, fragment).commit();
    }

    public Bitmap getBitmapFromImage(String imageName){
        Bitmap bitmap = null;

        File storageDir = new File(String.valueOf(getExternalFilesDir("book_covers")));

        File image = new File(storageDir.getPath() + "/" + imageName);

        Uri uri = Uri.fromFile(image);

        try {
            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bitmap;
    }

    private void loadDataAfterWipe(){
        DatabaseHelper helper = new DatabaseHelper(getApplicationContext());

        if (helper.getCategoriesFromDB().size() == 0){

            /* ------------------ Users ------------------ */

            helper.addRow(new User("a", "a", "000000000", 1));

            /* ------------------ Categories ------------------ */

            helper.addRow(new Category("Fantastyka", R.drawable.i_fantasy)); // 1
            helper.addRow(new Category("Sci-Fi", R.drawable.i_scifi)); // 2
            helper.addRow(new Category("Romans", R.drawable.i_romance)); // 3
            helper.addRow(new Category("Powieść historyczna", R.drawable.i_history)); // 4
            helper.addRow(new Category("Nowela", R.drawable.i_novel)); // 5
            helper.addRow(new Category("Horror", R.drawable.i_horror)); // 6
            helper.addRow(new Category("Kryminał i thriller", R.drawable.i_thriller)); // 7
            helper.addRow(new Category("Poezja", R.drawable.i_poetry)); // 8
            helper.addRow(new Category("Biografia i reportaż", R.drawable.i_biography)); // 9
            helper.addRow(new Category("Powieść młodzieżowa", R.drawable.i_teenagers)); // 10
            helper.addRow(new Category("Literatura dziecięca", R.drawable.i_children)); // 11
            helper.addRow(new Category("Książki kucharskie", R.drawable.i_cooking)); // 12
            helper.addRow(new Category("Poradniki", R.drawable.i_handbooks)); // 13
            helper.addRow(new Category("Podręczniki", R.drawable.i_guide)); // 14

            /* ------------------ Products ------------------ */

            helper.addRow(new Product("Hobbit, czyli tam i z powrotem", 1, "J.R.R. Tolkien", 39.99f, 12, "Śródziemie:Władca Pierścieni", R.drawable.b_hobbit));
            helper.addRow(new Product("Władca Pierścieni. Trylogia", 1, "J.R.R. Tolkien", 109.99f, 21, "Śródziemie", R.drawable.b_lotr));
            helper.addRow(new Product("Ostatnie życzenie. Wiedźmin", 1, "Andrzej Sapkowski", 39.99f, 19, "Wiedźmin:Przygoda", R.drawable.b_ostzycz));
            helper.addRow(new Product("Miecz przeznaczenia. Wiedźmin", 1, "Andrzej Sapkowski", 39.99f, 13, "Wiedźmin:Przygoda", R.drawable.b_mprzez));
            helper.addRow(new Product("Krew elfów. Wiedźmin", 1, "Andrzej Sapkowski", 39.99f, 18, "Wiedźmin:Przygoda", R.drawable.b_kreelf));
            helper.addRow(new Product("Czas pogardy. Wiedźmin", 1, "Andrzej Sapkowski", 39.99f, 13, "Wiedźmin:Przygoda", R.drawable.b_czapog));
            helper.addRow(new Product("Chrzest ognia. Wiedźmin", 1, "Andrzej Sapkowski", 39.99f, 16, "Wiedźmin:Przygoda", R.drawable.b_chrogn));
            helper.addRow(new Product("Wieża jaskółki. Wiedźmin", 1, "Andrzej Sapkowski", 39.99f, 12, "Wiedźmin:Przygoda", R.drawable.b_wiejas));
            helper.addRow(new Product("Pani Jeziora. Wiedźmin", 1, "Andrzej Sapkowski", 44.99f, 14, "Wiedźmin:Przygoda", R.drawable.b_panjez));
            helper.addRow(new Product("Sezon burz. Wiedźmin", 1, "Andrzej Sapkowski", 39.99f, 11, "Wiedźmin:Przygoda", R.drawable.b_sezburz));

            helper.addRow(new Product("Mechaniczna pomarańcza", 2, "Anthony Burgess", 29.99f, 12, "Mocny:Dystopia:Socjologia", R.drawable.b_mechpom));
            helper.addRow(new Product("Rok 1984", 2, "George Orwell", 24.99f, 13, "Orwell:Dystopia:Socjologia", R.drawable.b_year1984));
            helper.addRow(new Product("Solaris", 2, "Stanisław Lem", 49.99f, 17, "Socjologia", R.drawable.b_solaris));
            helper.addRow(new Product("Diuna. Kroniki Diuny. Tom 1", 2, "Frank Herbert", 44.99f, 7, "Diuna", R.drawable.b_dune));
            helper.addRow(new Product("Mesjasz Diuny. Kroniki Diuny. Tom 2", 2, "Frank Herbert", 34.99f, 12, "Diuna", R.drawable.b_mesdiu));
            helper.addRow(new Product("Dzieci Diuny. Kroniki Diuny. Tom 3", 2, "Frank Herbert", 34.99f, 17, "Diuna", R.drawable.b_dzidiu));
            helper.addRow(new Product("Bóg Imperator Diuny. Kroniki Diuny. Tom 4", 2, "Frank Herbert", 34.99f, 19, "Diuna", R.drawable.b_bogimp));
            helper.addRow(new Product("Heretycy Diuny. Kroniki Diuny. Tom 5", 2, "Frank Herbert", 34.99f, 18, "Diuna", R.drawable.b_herdiu));
            helper.addRow(new Product("Kapitularz Diuną. Kroniki Diuny. Tom 6", 2, "Frank Herbert", 34.99f, 17, "Diuna", R.drawable.b_kapdiu));

            helper.addRow(new Product("Gwiazd naszych wina", 3, "John Green", 24.99f, 13, "Miłość:Choroba:Wzruszające", R.drawable.b_gwinas));
            helper.addRow(new Product("Tamte dni, tamte noce", 3, "Aciman Andre", 34.99f, 11, "Miłość:Wzruszające", R.drawable.b_tamdni));

            helper.addRow(new Product("Ogniem i mieczem", 4, "Henryk Sienkiewicz", 54.99f, 16, "Trylogia Sienkiewicza:Historia", R.drawable.b_ognimi));
            helper.addRow(new Product("Potop", 4, "Henryk Sienkiewicz", 54.99f, 12, "Trylogia Sienkiewicza:Historia", R.drawable.b_potop));
            helper.addRow(new Product("Pan Wołodyjowski", 4, "Henryk Sienkiewicz", 54.99f, 18, "Trylogia Sienkiewicza:Historia", R.drawable.b_panwol));

            helper.addRow(new Product("Latarnik", 5, "Henryk Sienkiewicz", 14.99f, 14, "Patriotyzm:Refleksja", R.drawable.b_lat));
            helper.addRow(new Product("Przemiana", 5, "Franz Kafka", 19.99f, 12, "Absurd:Refleksja", R.drawable.b_prze));

            helper.addRow(new Product("To", 6, "Stephen King", 44.99f, 19, "Klaun", R.drawable.b_it));
            helper.addRow(new Product("Frankenstein", 6, "Mary Shelley", 14.99f, 12, "Szalony naukowiec", R.drawable.b_franken));
            helper.addRow(new Product("Pan Taduesz", 6, "Adam Mickiewicz", 19.99f, 16, "Epopeja:Romantyzm", R.drawable.b_pant));
            helper.addRow(new Product("Zew Cthulhu", 6, "H.P. Lovecraft", 24.99f, 8, "Szaleństwo:Cthulhu", R.drawable.b_zewcth));

            helper.addRow(new Product("Morderstwo w Orient Expressie", 7, "Agathy Christie", 19.99f, 10, "Tajemnica", R.drawable.b_morwor));
            helper.addRow(new Product("Wendeta po śmierć", 7, "Nora Roberts", 49.99f, 14, "Tajemnica", R.drawable.b_wenpos));
            helper.addRow(new Product("Decyzja", 7, "Charlotte Link", 29.99f, 9, "Tajemnica", R.drawable.b_dec));

            helper.addRow(new Product("Ballady i romanse", 8, "Adam Mickiewicz", 19.99f, 19, "Ballady:Romanse", R.drawable.b_balirom));
            helper.addRow(new Product("Źdźbła trawy", 8, "Walt Whitman", 64.99f, 2, "Refleksja", R.drawable.b_zdztra));
            helper.addRow(new Product("Koniec i początek", 8, "Wisława Szymborska", 29.99f, 13, "Refleksja", R.drawable.b_konipo));

            helper.addRow(new Product("Życie na pełnej petardzie", 9, "Jan Kaczkowski", 24.99f, 17, "Religia:Motywujące:Wzruszające", R.drawable.b_zycienap));
            helper.addRow(new Product("Ziele na kraterze", 9, "Melchior Wańkowicz", 19.99f, 8, "Druga Wojna Światowa", R.drawable.b_zienakra));
            helper.addRow(new Product("Wspominając Kurta Cobaina", 9, "Danny Goldberg", 39.99f, 13, "Muzyka", R.drawable.b_wspkur));
            helper.addRow(new Product("Wojenka. O dzieciach, które dorosły bez ostrzeżenia", 9, "Magdalena Grzebałkowska", 49.99f, 6, "Druga Wojna Światowa", R.drawable.b_wojenka));
            helper.addRow(new Product("Frida Kahlo. Biografia", 9, "Maria Hesse", 49.99f, 8, "Frida", R.drawable.b_frikha));

            helper.addRow(new Product("Osobliwy dom pani Peregrine", 10, "Ransom Riggs", 34.99f, 12, "Przygoda", R.drawable.b_osodom));
            helper.addRow(new Product("W pustyni i w puszczy", 10, "Henryk Sienkiewicz", 24.99f, 21, "Afryka:Przygoda", R.drawable.b_wpusiw));

            helper.addRow(new Product("Koszmarny Karolek", 11, "Francesca Simon", 19.99f, 10, "Przygoda", R.drawable.b_kosmar));
            helper.addRow(new Product("Dziennik cwaniaczka", 11, "Jeff Kinney", 24.99f, 14, "Przygoda", R.drawable.b_dzicwa));

            helper.addRow(new Product("Lean in 15", 12, "Joe Wicks", 29.99f, 9, "Zdrowie:Diety", R.drawable.b_li15));
            helper.addRow(new Product("Jamie cooks Italy", 12, "Jamie Oliver", 114.99f, 11, "Kuchnia Orientalna", R.drawable.b_jamiecooksi));
            helper.addRow(new Product("Veg: Easy & Delicious Meals for Everyone", 12, "Jamie Oliver", 115.99f, 15, "Kuchnia Vege", R.drawable.b_jamieultveg));
            helper.addRow(new Product("Palestyna. Książka kucharska", 12, "Tamimi Sami", 69.99f, 8, "Kuchnia Orientalna", R.drawable.b_palestyna));

            helper.addRow(new Product("Minecraft. Poradnik dla początkujących", 13, "Milton Stephanie", 29.99f, 12, "Gry komputerowe", R.drawable.b_minbud));
            helper.addRow(new Product("Minecraft. Podręcznik użytkowania czerwonego kamienia", 13, "Jelley Craig", 34.99f, 9, "Gry komputerowe", R.drawable.b_mincze));
            helper.addRow(new Product("Minecraft. Podręcznik kreatywności", 13, "McBrien Thomas", 34.99f, 9, "Gry komputerowe", R.drawable.b_minkre));
            helper.addRow(new Product("Programowanie. Teoria i praktyka z wykorzystaniem C++", 13, "Bjarne Stroustrup", 129.99f, 5, "Programowanie", R.drawable.b_prog));

            helper.addRow(new Product("Teraz matura. Język angielski", 14, "Beata Polit", 36.99f, 8, "Matura:Język Angielski:Szkoła:Języki Obce", R.drawable.b_tmang));
            helper.addRow(new Product("Teraz matura. Język polski", 14, "Marianna Gutowska", 39.99f, 15, "Matura:Język Polski:Szkoła", R.drawable.b_tmpol));
            helper.addRow(new Product("Teraz matura. Matematyka", 14, "Piotr Krzemiński", 36.99f, 5, "Matura:Matematyka:Szkoła:Nauki Ścisłe", R.drawable.b_tmmat));
            helper.addRow(new Product("Gramatyka angielska do testów i egzaminów", 14, "Janusz Siuda", 19.49f, 38, "Język Angielski:Języki Obce:Szkoła:Religia", R.drawable.b_angsiu));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        saveData();
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (!rememberUser){
            SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);

            SharedPreferences.Editor editor = sharedPreferences.edit();

            editor.putInt("userId", -1);

            editor.apply();
        }

        else {
            saveData();
        }
    }

    private void askForPermission(String permission, int requestCode){
        if (ContextCompat.checkSelfPermission(MainActivity.this, permission) == PackageManager.PERMISSION_DENIED) {

            ActivityCompat.requestPermissions(MainActivity.this, new String[]{permission}, requestCode);
        }
    }

    public boolean checkPermission(String permission, int requestCode){
        if (ContextCompat.checkSelfPermission(MainActivity.this, permission) == PackageManager.PERMISSION_DENIED) {
            return false;
        }

        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == MY_PERMISSION_REQUEST_SEND_SMS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                Toast.makeText(MainActivity.this, "Send SMS Permission Granted", Toast.LENGTH_SHORT).show();
            }
            else {
//                Toast.makeText(MainActivity.this, "Send SMS Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onBackPressed() {
        List<Fragment> fragments = getSupportFragmentManager().getFragments();

        if (fragments.size() == 1 && fragments.get(0).getChildFragmentManager().getFragments().size() == 0){
            if (fragments.get(0) instanceof HomeFragment){
                super.onBackPressed();
            }

            else {
                moveToFragment(0);
            }
        }

        else {
            moveToFragment(getCurrentFragmentId());
        }
    }
}