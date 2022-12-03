package com.example.zaliczeniesklep.schema;

public final class Schema {
    private Schema(){}

    public final static class DatabaseSchema {
        public static final String DB_NAME = "bookshop";
        public static final int DB_VERSION = 1;

        public static final String CATEGORIES_TABLE = "categories";
        public static final String PRODUCTS_TABLE = "products";
        public static final String USERS_TABLE = "users";
        public static final String CART_TABLE = "cart";
        public static final String ORDER_TABLE = "orders";
    }

    public final static class OrdersSchema {
        public static final String ID_COLUMN = "order_id";
        public static final String USER_ID_COLUMN = "order_user_id";
        public static final String DATE_COLUMN = "order_date";
        public static final String BUYER_COLUMN = "order_buyer";
        public static final String ITEMS_COLUMN = "order_items";
        public static final String PRICE_COLUMN = "order_price";
        public static final String EMAIL_COLUMN = "order_email";
        public static final String PHONE_NUMBER_COLUMN = "order_phone_num";
        public static final String IS_EXECUTED_COLUMN = "order_is_executed";
    }

    public final static class CategoriesSchema {
        public static final String ID_COLUMN = "category_id";
        public static final String NAME_COLUMN = "category_name";
        public static final String IMAGE_COLUMN = "category_image";
    }

    public final static class ProductsSchema {
        public static final String ID_COLUMN = "product_id";
        public static final String NAME_COLUMN = "product_name";
        public static final String CATEGORY_ID_COLUMN = "product_category_id";
        public static final String AUTHOR_COLUMN = "product_author";
        public static final String PRICE_COLUMN = "product_price";
        public static final String QUANTITY_COLUMN = "product_quantity";
        public static final String TAGS_COLUMN = "product_tags";
        public static final String IMAGE_COLUMN = "product_image";
    }

    public final static class UsersSchema {
        public static final String ID_COLUMN = "user_id";
        public static final String EMAIL_COLUMN = "user_email";
        public static final String PASSWORD_COLUMN = "user_password";
        public static final String PHONE_NUMBER_COLUMN = "user_phone_number";
        public static final String USER_TYPE_COLUMN = "user_type";
    }

    public final static class CartSchema {
        public static final String ID_COLUMN = "id";
        public static final String USER_ID_COLUMN = "user_id";
        public static final String PRODUCT_ID_COLUMN = "product_id";
        public static final String QUANTITY_COLUMN = "quantity";
    }
}
