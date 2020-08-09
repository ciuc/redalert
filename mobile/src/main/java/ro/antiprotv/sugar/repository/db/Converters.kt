package ro.antiprotv.sugar.repository.db

import androidx.room.TypeConverter

class Converters {
    @TypeConverter
    fun fromType(value: AlertType) = value.ordinal

    @TypeConverter
    fun toType(value: Int) = AlertType.values()[value]

    @TypeConverter
    fun fromCategory(value: Category) = value.ordinal

    @TypeConverter
    fun toCategory(value: Int) = Category.values()[value]

    @TypeConverter
    fun fromMeasureUnit(value: MeasureUnit) = value.ordinal

    @TypeConverter
    fun toMeasureUnit(value: Int) = MeasureUnit.values()[value]
}

