package ro.antiprotv.sugar.repository.db

enum class Category(val code: Int, val resId: String) {
    FOOD(1, "item_category_food"),
    DRINK(2, "item_category_drink"),
    CLEANING(3, "item_category_cleaning"),
    OTHER(99, "item_category_other");

    companion object {
        fun fromString(category: String): Category {
            var cat = OTHER
            for (c in values()) {
                if (c.toString() == category) {
                    cat = c
                    break
                }
            }
            return cat
        }

        fun fromCode(code: Int): Category {
            var cat = OTHER
            for (c in values()) {
                if (c.code == code) {
                    cat = c
                    break
                }
            }
            return cat
        }
    }

}