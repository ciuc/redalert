package ro.antiprotv.sugar.db;

public enum Category {
    FOOD(1), DRINKS(2), CLEANING(3), OTHER(99);
    int code;
    Category(int code) {
        this.code = code;
    }

    public static int getCodeFromString(String category) {
        int code = 99;
        for (Category c : Category.values()) {
            if (c.toString().equals(category)) {
                code = c.code;
                break;
            }
        }
        return code;
    }

    public static Category fromString(String category) {
        Category cat = Category.OTHER;
        for (Category c : Category.values()) {
            if (c.toString().equals(category)) {
                cat = c;
                break;
            }
        }
        return cat;
    }

    public static String fromCode(int code) {
        Category cat = Category.OTHER;
        for (Category c : Category.values()) {
            if (c.code == code) {
                cat = c;
                break;
            }
        }
        return cat.toString();

    }
}
