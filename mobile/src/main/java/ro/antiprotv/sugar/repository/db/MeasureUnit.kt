package ro.antiprotv.sugar.repository.db

enum class MeasureUnit(val code: Int, val resId: String) {
    KG(1, "item_unit_kg"),
    BOX(2, "item_unit_box"),
    BOTTLE(3, "item_unit_bottle"),
    PIECE(4, "item_unit_pc"),
    PACK(5, "item_unit_pack"),
    OTHER(99, "item_unit_other");

    companion object {
        fun fromString(measureUnitName: String): MeasureUnit {
            var measureUnit = OTHER
            for (m in values()) {
                if (m.toString() == measureUnitName) {
                    measureUnit = m
                    break
                }
            }
            return measureUnit
        }

        fun fromCode(code: Int): String {
            var mu = OTHER
            for (m in values()) {
                if (m.code == code) {
                    mu = m
                    break
                }
            }
            return mu.toString()
        }
    }

}