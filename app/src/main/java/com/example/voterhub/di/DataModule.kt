package com.example.voterhub.di

import android.content.Context
import androidx.room.Room
import com.example.voterhub.data.datastore.SearchHistoryDataStore
import com.example.voterhub.data.local.AppDatabase
import com.example.voterhub.data.local.dao.SectionDao
import com.example.voterhub.data.local.dao.VoterDao
import com.example.voterhub.data.remote.FakeVoterApi
import com.example.voterhub.data.remote.VoterApi
import com.example.voterhub.data.repository.OfflineFirstVoterRepository
import com.example.voterhub.domain.repository.SearchHistoryRepository
import com.example.voterhub.domain.repository.VoterRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "voterhub.db")
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideSectionDao(db: AppDatabase): SectionDao = db.sectionDao()

    @Provides
    fun provideVoterDao(db: AppDatabase): VoterDao = db.voterDao()

    @Provides
    @Singleton
    fun provideVoterApi(fake: FakeVoterApi): VoterApi = fake

    @Provides
    @Singleton
    fun provideVoterRepository(
        sectionDao: SectionDao,
        voterDao: VoterDao,
        api: VoterApi,
        @IoDispatcher dispatcher: kotlinx.coroutines.CoroutineDispatcher
    ): VoterRepository = OfflineFirstVoterRepository(sectionDao, voterDao, api, dispatcher)
}

@Module
@InstallIn(SingletonComponent::class)
abstract class StoreModule {
    @Binds
    @Singleton
    abstract fun bindSearchHistoryRepository(store: SearchHistoryDataStore): SearchHistoryRepository
}

