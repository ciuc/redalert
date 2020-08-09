package ro.antiprotv.sugar.repository.db

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import ro.antiprotv.sugar.R

enum class AlertType(
        @ColorRes val color: Int,
        @StringRes val colorName: Int,
        @DrawableRes val icon: Int
) {
    // keep DELETED first, otherwise you'll mess up DB queries
    DELETED(R.color.deleted, R.string.deleted, R.drawable.ic_error_outline_black_24dp),
    RED(R.color.red, R.string.red, R.drawable.ic_error_outline_black_24dp),
    ORANGE(R.color.orange, R.string.orange, R.drawable.ic_error_outline_black_24dp),
    //YELLOW(R.color.yellow, R.string.yellow, R.drawable.ic_priority_high_black_24dp);
}