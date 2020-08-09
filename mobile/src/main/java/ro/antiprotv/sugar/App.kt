package ro.antiprotv.sugar

import android.app.Application
import androidx.room.Room
import com.facebook.stetho.Stetho
import org.koin.android.ext.koin.androidContext
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.context.loadKoinModules
import org.koin.core.context.startKoin
import org.koin.dsl.module
import ro.antiprotv.sugar.repository.db.RedAlertRoomDatabase
import ro.antiprotv.sugar.ui.AlertListFragment
import ro.antiprotv.sugar.ui.AlertViewModel
import ro.antiprotv.sugar.ui.ItemListFragment
import ro.antiprotv.sugar.ui.ItemViewModel
import ro.antiprotv.sugar.util.NotificationManager

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        Stetho.initializeWithDefaults(this)
        startKoin {
            androidContext(this@App)
            loadKoinModules(modules)
        }
    }

    private val modules = module {
        single {
            Room.databaseBuilder(get(), RedAlertRoomDatabase::class.java, "redalert.db")
                    .fallbackToDestructiveMigration()
                    .build()
        }

        single { AlertListFragment() }
        single { ItemListFragment() }
        single { NotificationManager(get()) }

        viewModel { AlertViewModel(get()) }
        viewModel { ItemViewModel(get()) }
    }
}