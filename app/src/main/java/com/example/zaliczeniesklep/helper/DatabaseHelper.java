package com.example.zaliczeniesklep.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import com.example.zaliczeniesklep.database_entity.CartItem;
import com.example.zaliczeniesklep.database_entity.Category;
import com.example.zaliczeniesklep.database_entity.DatabaseEntity;
import com.example.zaliczeniesklep.database_entity.Order;
import com.example.zaliczeniesklep.database_entity.Product;
import com.example.zaliczeniesklep.database_entity.User;
import com.example.zaliczeniesklep.schema.Schema;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    public DatabaseHelper(Context context) {
        super(context, Schema.DatabaseSchema.DB_NAME, null, Schema.DatabaseSchema.DB_VERSION);
    }

    public List<Product> getProductsFromDB(){
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + Schema.DatabaseSchema.PRODUCTS_TABLE, null);

        List<Product> products = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                products.add(new Product(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getInt(2),
                        cursor.getString(3),
                        cursor.getFloat(4),
                        cursor.getInt(5),
                        cursor.getString(6),
                        cursor.getString(7)
                        ));
            } while (cursor.moveToNext());
        }

        cursor.close();

        return products;
    }

    public Product getProductByIdFromDB(int id){
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + Schema.DatabaseSchema.PRODUCTS_TABLE + " WHERE " + Schema.ProductsSchema.ID_COLUMN + " = " + id, null);

        List<Product> products = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                products.add(new Product(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getInt(2),
                        cursor.getString(3),
                        cursor.getFloat(4),
                        cursor.getInt(5),
                        cursor.getString(6),
                        cursor.getString(7)
                ));
            } while (cursor.moveToNext());
        }

        cursor.close();

        return products.get(0);
    }

    public List<CartItem> getCartsFromDB(){
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + Schema.DatabaseSchema.CART_TABLE, null);

        List<CartItem> cartItems = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                cartItems.add(new CartItem(
                        cursor.getInt(0),
                        cursor.getInt(1),
                        cursor.getInt(2),
                        cursor.getInt(3)
                ));
            } while (cursor.moveToNext());
        }

        cursor.close();

        return cartItems;
    }

    public List<CartItem> getCartForUser(int userId){
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + Schema.DatabaseSchema.CART_TABLE + " WHERE " + Schema.CartSchema.USER_ID_COLUMN + " = " + userId, null);

        List<CartItem> cartItems = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                cartItems.add(new CartItem(
                        cursor.getInt(0),
                        cursor.getInt(1),
                        cursor.getInt(2),
                        cursor.getInt(3)
                ));
            } while (cursor.moveToNext());
        }

        cursor.close();

        return cartItems;
    }

    public List<User> getUsersFromDB(){
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + Schema.DatabaseSchema.USERS_TABLE, null);

        List<User> users = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                users.add(new User(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getInt(4)
                ));
            } while (cursor.moveToNext());
        }

        cursor.close();

        return users;
    }

    public List<Category> getCategoriesFromDB(){
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + Schema.DatabaseSchema.CATEGORIES_TABLE, null);

        List<Category> categories = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                categories.add(new Category(
                        cursor.getInt(0),
                        cursor.getString(1),
                        Integer.valueOf(cursor.getString(2))
                ));
            } while (cursor.moveToNext());
        }

        cursor.close();

        return categories;
    }

    public List<Order> getOrdersFromDB(){
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + Schema.DatabaseSchema.ORDER_TABLE, null);

        List<Order> orders = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                orders.add(new Order(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getInt(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getFloat(5),
                        cursor.getString(6),
                        cursor.getString(7),
                        cursor.getInt(8)
                        )
                );
            } while (cursor.moveToNext());
        }

        cursor.close();

        return orders;
    }

    public List<Order> getOrdersForUser(int userId){
        if (userId != -1){
            SQLiteDatabase db = this.getReadableDatabase();

            Cursor cursor = db.rawQuery("SELECT * FROM " + Schema.DatabaseSchema.ORDER_TABLE + " WHERE " + Schema.OrdersSchema.USER_ID_COLUMN + " = " + userId, null);

            List<Order> orders = new ArrayList<>();

            if (cursor.moveToFirst()) {
                do {
                    orders.add(new Order(
                                    cursor.getInt(0),
                                    cursor.getString(1),
                                    cursor.getInt(2),
                                    cursor.getString(3),
                                    cursor.getString(4),
                                    cursor.getFloat(5),
                                    cursor.getString(6),
                                    cursor.getString(7),
                                    cursor.getInt(8)
                            )
                    );
                } while (cursor.moveToNext());
            }

            cursor.close();

            return orders;
        }

        return null;
    }

    public List<Order> getOrdersByExecution(int status){
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + Schema.DatabaseSchema.ORDER_TABLE + " WHERE " + Schema.OrdersSchema.IS_EXECUTED_COLUMN + " = " + status, null);

        List<Order> orders = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                orders.add(new Order(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getInt(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getFloat(5),
                        cursor.getString(6),
                        cursor.getString(7),
                        cursor.getInt(8)
                        )
                    );
            } while (cursor.moveToNext());
        }

        cursor.close();

        return orders;
    }

    public Order getOrderByIdFromDB(int id){
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + Schema.DatabaseSchema.ORDER_TABLE + " WHERE " + Schema.OrdersSchema.ID_COLUMN + " = " + id, null);

        List<Order> orders = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                orders.add(new Order(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getInt(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getFloat(5),
                        cursor.getString(6),
                        cursor.getString(7),
                        cursor.getInt(8)
                ));
            } while (cursor.moveToNext());
        }

        cursor.close();

        return orders.get(0);
    }

    public void addRow(DatabaseEntity entity){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = setContentValuesForEntity(entity);

        String[] schema = getSchemaForEntity(entity);

        db.insert(schema[0], null, values);

        db.close();
    }

    public void addNewProduct(Product product){
        SQLiteDatabase db = this.getWritableDatabase();

        String preperedStatement = "INSERT INTO " +
                Schema.DatabaseSchema.PRODUCTS_TABLE + " (" +
                Schema.ProductsSchema.NAME_COLUMN  + ", " +
                Schema.ProductsSchema.CATEGORY_ID_COLUMN + ", " +
                Schema.ProductsSchema.AUTHOR_COLUMN + ", " +
                Schema.ProductsSchema.PRICE_COLUMN + ", " +
                Schema.ProductsSchema.QUANTITY_COLUMN + ", " +
                Schema.ProductsSchema.TAGS_COLUMN + ", " +
                Schema.ProductsSchema.IMAGE_COLUMN + ")" +
                " VALUES (?, ?, ?, ?, ?, ?, ?)";

        Log.i("123456789", preperedStatement);

        SQLiteStatement statement = db.compileStatement(preperedStatement);

        statement.bindString(1, product.getName());
        statement.bindLong(2, product.getCategory_id());
        statement.bindString(3, product.getAuthor());
        statement.bindDouble(4, product.getPrice());
        statement.bindLong(5, product.getQuantity());
        statement.bindString(6, product.getTags());
        statement.bindString(7, String.valueOf(product.getImage()));

        long rowId = statement.executeInsert();

        Log.i("123456789", rowId + "");
    }

    public void addNewUser(User user){
        SQLiteDatabase db = this.getWritableDatabase();

        String preperedStatement = "INSERT INTO " +
                Schema.DatabaseSchema.USERS_TABLE + " (" +
                Schema.UsersSchema.EMAIL_COLUMN + ", " +
                Schema.UsersSchema.PASSWORD_COLUMN + ", " +
                Schema.UsersSchema.PHONE_NUMBER_COLUMN + ", " +
                Schema.UsersSchema.USER_TYPE_COLUMN + ")" +
                " VALUES (?, ?, ?, ?)";

        Log.i("123456789", preperedStatement);

        SQLiteStatement statement = db.compileStatement(preperedStatement);

        statement.bindString(1, user.getEmail());
        statement.bindString(2, user.getPassword());
        statement.bindString(3, user.getPhoneNumber());
        statement.bindLong(4, user.getUserType());

        long rowId = statement.executeInsert();

        Log.i("123456789", rowId + "");
    }

    public void addNewOrder(Order order){
        SQLiteDatabase db = this.getWritableDatabase();

        String preperedStatement = "INSERT INTO " +
                Schema.DatabaseSchema.ORDER_TABLE + " (" +
                Schema.OrdersSchema.DATE_COLUMN + ", " +
                Schema.OrdersSchema.USER_ID_COLUMN + ", " +
                Schema.OrdersSchema.BUYER_COLUMN + ", " +
                Schema.OrdersSchema.ITEMS_COLUMN + ", " +
                Schema.OrdersSchema.PRICE_COLUMN + ", " +
                Schema.OrdersSchema.EMAIL_COLUMN + ", " +
                Schema.OrdersSchema.PHONE_NUMBER_COLUMN + ", " +
                Schema.OrdersSchema.IS_EXECUTED_COLUMN + ")" +
                " VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        Log.i("123456789", preperedStatement);

        SQLiteStatement statement = db.compileStatement(preperedStatement);

        statement.bindString(1, order.getDate());
        statement.bindLong(2, order.getUserId());
        statement.bindString(3, order.getBuyer());
        statement.bindString(4, order.getItems());
        statement.bindDouble(5, order.getPrice());
        statement.bindString(6, order.getEmail());
        statement.bindString(7, order.getPhoneNumber());
        statement.bindDouble(8, 0);

        long rowId = statement.executeInsert();

        for (Order o : getOrdersFromDB()){
            Log.i("123456789", o.toString());
        }

        Log.i("123456789", rowId + "");
    }

    public void updateRowById(DatabaseEntity entity, int rowId, String updatedColumn, String newValue){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(updatedColumn, newValue);

        String[] schema = getSchemaForEntity(entity);

        db.update(schema[0], cv, schema[1] + " = ?", new String[]{String.valueOf(rowId)});
    }

    public void deleteRowById(DatabaseEntity entity, int rowId){
        SQLiteDatabase db = this.getWritableDatabase();

        String[] schema = getSchemaForEntity(entity);

        db.delete(schema[0], schema[1] + " = ?", new String[]{String.valueOf(rowId)});
    }

    public void deleteRowByCondition(DatabaseEntity entity, String chosenColumn, String contition){
        SQLiteDatabase db = this.getWritableDatabase();

        String[] schema = getSchemaForEntity(entity);

        db.delete(schema[0], chosenColumn + " = ?", new String[]{contition});
    }

    private ContentValues setContentValuesForEntity(DatabaseEntity entity){
        ContentValues values = new ContentValues();

        if (entity instanceof CartItem){
            Schema.CartSchema schema = new Schema.CartSchema();

            values.put(schema.USER_ID_COLUMN, ((CartItem) entity).getUser_id());
            values.put(schema.PRODUCT_ID_COLUMN, ((CartItem) entity).getProduct_id());
            values.put(schema.QUANTITY_COLUMN, ((CartItem) entity).getQuantity());
        }

        else if(entity instanceof Category){
            Schema.CategoriesSchema schema = new Schema.CategoriesSchema();

            values.put(schema.NAME_COLUMN, ((Category) entity).getName());
            values.put(schema.IMAGE_COLUMN, String.valueOf(((Category) entity).getDrawableImageId()));
        }

        else if(entity instanceof Product){
            Schema.ProductsSchema schema = new Schema.ProductsSchema();

            values.put(schema.NAME_COLUMN, ((Product) entity).getName());
            values.put(schema.CATEGORY_ID_COLUMN, ((Product) entity).getCategory_id());
            values.put(schema.AUTHOR_COLUMN, ((Product) entity).getAuthor());
            values.put(schema.PRICE_COLUMN, ((Product) entity).getPrice());
            values.put(schema.QUANTITY_COLUMN, ((Product) entity).getQuantity());
            values.put(schema.TAGS_COLUMN, ((Product) entity).getTags());
            values.put(schema.IMAGE_COLUMN, String.valueOf(((Product) entity).getImage()));
        }

        else if(entity instanceof User){
//            Schema.UsersSchema schema = new Schema.UsersSchema();
//
//            values.put(schema.EMAIL_COLUMN, ((User) entity).getEmail());
//            values.put(schema.PASSWORD_COLUMN, ((User) entity).getPassword());
//            values.put(schema.PHONE_NUMBER_COLUMN, ((User) entity).getPhoneNumber());
//            values.put(schema.USER_TYPE_COLUMN, ((User) entity).getUserType());

            addNewUser((User) entity);
        }

        else if(entity instanceof Order){
//            Schema.OrdersSchema schema = new Schema.OrdersSchema();
//
//            values.put(schema.DATE_COLUMN, ((Order) entity).getDate());
//            values.put(schema.USER_ID_COLUMN, ((Order) entity).getUserId());
//            values.put(schema.BUYER_COLUMN, ((Order) entity).getBuyer());
//            values.put(schema.ITEMS_COLUMN, ((Order) entity).getItems());
//            values.put(schema.PRICE_COLUMN, ((Order) entity).getPrice());
//            values.put(schema.EMAIL_COLUMN, ((Order) entity).getEmail());
//            values.put(schema.PHONE_NUMBER_COLUMN, ((Order) entity).getPhoneNumber());
//            values.put(schema.IS_EXECUTED_COLUMN, ((Order) entity).getIsExecuted());

            addNewOrder((Order) entity);
        }

        return values;
    }

    private String[] getSchemaForEntity(DatabaseEntity entity){
        String tableName = "";
        String idColName = "";

        if (entity instanceof CartItem){
            tableName = Schema.DatabaseSchema.CART_TABLE;
            idColName = Schema.CartSchema.ID_COLUMN;
        }

        else if(entity instanceof Category){
            tableName = Schema.DatabaseSchema.CATEGORIES_TABLE;
            idColName = Schema.CategoriesSchema.ID_COLUMN;
        }

        else if(entity instanceof Product){
            tableName = Schema.DatabaseSchema.PRODUCTS_TABLE;
            idColName = Schema.ProductsSchema.ID_COLUMN;
        }

        else if(entity instanceof User){
            tableName = Schema.DatabaseSchema.USERS_TABLE;
            idColName = Schema.UsersSchema.ID_COLUMN;
        }

        else if(entity instanceof Order){
            tableName = Schema.DatabaseSchema.ORDER_TABLE;
            idColName = Schema.OrdersSchema.ID_COLUMN;
        }

        return new String[]{tableName, idColName};
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + Schema.DatabaseSchema.CATEGORIES_TABLE + " ("
                + Schema.CategoriesSchema.ID_COLUMN + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + Schema.CategoriesSchema.NAME_COLUMN + " TEXT UNIQUE, "
                + Schema.CategoriesSchema.IMAGE_COLUMN + " TEXT)";

        db.execSQL(query);

        query = "CREATE TABLE " + Schema.DatabaseSchema.CART_TABLE + " ("
                + Schema.CartSchema.ID_COLUMN + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + Schema.CartSchema.USER_ID_COLUMN + " INTEGER, "
                + Schema.CartSchema.PRODUCT_ID_COLUMN + " INTEGER, "
                + Schema.CartSchema.QUANTITY_COLUMN + " INTEGER)";

        db.execSQL(query);

        query = "CREATE TABLE " + Schema.DatabaseSchema.PRODUCTS_TABLE + " ("
                + Schema.ProductsSchema.ID_COLUMN + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + Schema.ProductsSchema.NAME_COLUMN + " TEXT, "
                + Schema.ProductsSchema.CATEGORY_ID_COLUMN + " INTEGER, "
                + Schema.ProductsSchema.AUTHOR_COLUMN + " TEXT, "
                + Schema.ProductsSchema.PRICE_COLUMN + " REAL, "
                + Schema.ProductsSchema.QUANTITY_COLUMN + " INTEGER, "
                + Schema.ProductsSchema.TAGS_COLUMN + " INTEGER, "
                + Schema.ProductsSchema.IMAGE_COLUMN + " TEXT)";

        db.execSQL(query);

        query = "CREATE TABLE " + Schema.DatabaseSchema.USERS_TABLE + " ("
                + Schema.UsersSchema.ID_COLUMN + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + Schema.UsersSchema.EMAIL_COLUMN + " TEXT UNIQUE, "
                + Schema.UsersSchema.PASSWORD_COLUMN + " TEXT, "
                + Schema.UsersSchema.PHONE_NUMBER_COLUMN + " TEXT UNIQUE, "
                + Schema.UsersSchema.USER_TYPE_COLUMN + " INTEGER)";

        db.execSQL(query);

        query = "CREATE TABLE " + Schema.DatabaseSchema.ORDER_TABLE + " ("
                + Schema.OrdersSchema.ID_COLUMN + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + Schema.OrdersSchema.DATE_COLUMN + " TEXT, "
                + Schema.OrdersSchema.USER_ID_COLUMN + " INTEGER, "
                + Schema.OrdersSchema.BUYER_COLUMN + " TEXT, "
                + Schema.OrdersSchema.ITEMS_COLUMN + " TEXT, "
                + Schema.OrdersSchema.PRICE_COLUMN + " REAL, "
                + Schema.OrdersSchema.EMAIL_COLUMN + " TEXT, "
                + Schema.OrdersSchema.PHONE_NUMBER_COLUMN + " TEXT, "
                + Schema.OrdersSchema.IS_EXECUTED_COLUMN + " INTEGER DEFAULT 0)";

        Log.i("123456789", query);

        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + Schema.DatabaseSchema.PRODUCTS_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + Schema.DatabaseSchema.USERS_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + Schema.DatabaseSchema.CART_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + Schema.DatabaseSchema.CATEGORIES_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + Schema.DatabaseSchema.ORDER_TABLE);

        onCreate(db);
    }
}
