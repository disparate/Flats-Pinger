package kazarovets.flatspinger.di

import android.arch.persistence.room.Room
import android.content.Context
import dagger.Module
import dagger.Provides
import kazarovets.flatspinger.db.AppDatabase
import kazarovets.flatspinger.db.dao.FlatsDao
import kazarovets.flatspinger.flats.adapter.FlatViewStateMapper
import kazarovets.flatspinger.repository.FlatsRepository
import kazarovets.flatspinger.rx.SchedulersFacade
import kazarovets.flatspinger.viewmodel.FlatDetailsViewModelFactory
import kazarovets.flatspinger.viewmodel.FlatInfosViewModelFactory
import javax.inject.Singleton


@Module
class FlatsModule {

    @Singleton
    @Provides
    fun provideFlatsDatabase(applicationContext: Context): AppDatabase {
        return Room.databaseBuilder(applicationContext,
                AppDatabase::class.java, "flats.db")
                .fallbackToDestructiveMigration()
                .build()
    }

    @Singleton
    @Provides
    fun provideFlatsDao(flatsDatabase: AppDatabase): FlatsDao = flatsDatabase.flatsDao()

    @Singleton
    @Provides
    fun provideFlatsRepository(flatsDao: FlatsDao, schedulersFacade: SchedulersFacade): FlatsRepository {
        return FlatsRepository(flatsDao, schedulersFacade)
    }

    @Singleton
    @Provides
    fun provideFlatInfosViewModelFactory(flatsRepository: FlatsRepository,
                                         appContext: Context,
                                         schedulersFacade: SchedulersFacade): FlatInfosViewModelFactory =
            FlatInfosViewModelFactory(flatsRepository,
                    schedulersFacade,
                    FlatViewStateMapper(appContext))

    @Singleton
    @Provides
    fun provideFlatDetailsVMFactory(flatsRepository: FlatsRepository,
                                    appContext: Context,
                                    schedulersFacade: SchedulersFacade): FlatDetailsViewModelFactory {
        return FlatDetailsViewModelFactory(flatsRepository,
                schedulersFacade)
    }
}