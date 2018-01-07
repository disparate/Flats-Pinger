package kazarovets.flatspinger.di

import android.arch.persistence.room.Room
import android.content.Context
import dagger.Module
import dagger.Provides
import kazarovets.flatspinger.db.AppDatabase
import kazarovets.flatspinger.db.dao.FlatsDao
import kazarovets.flatspinger.repository.FlatsRepository
import kazarovets.flatspinger.rx.SchedulersFacade
import kazarovets.flatspinger.viewmodel.FlatInfosViewModelFactory
import javax.inject.Singleton


@Module
class FlatsModule {

    @Singleton
    @Provides
    fun provideFlatsDatabase(applicationContext: Context): AppDatabase {
        return Room.databaseBuilder(applicationContext,
                AppDatabase::class.java, "flats.db").build()
    }

    @Singleton
    @Provides
    fun provideFlatsDao(flatsDatabase: AppDatabase): FlatsDao = flatsDatabase.flatsDao()

    @Singleton
    @Provides
    fun provideFlatsRepository(flatsDao: FlatsDao): FlatsRepository = FlatsRepository(flatsDao)

    @Singleton
    @Provides
    fun provideFlatInfosViewModelFactory(flatsRepository: FlatsRepository,
                                         schedulersFacade: SchedulersFacade): FlatInfosViewModelFactory =
            FlatInfosViewModelFactory(flatsRepository, schedulersFacade)
}