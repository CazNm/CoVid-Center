package com.example.onboardingtestapplication.DI

import android.content.Context
import androidx.room.Room
import com.example.onboardingtestapplication.Model.CoVidCenterDataBase
import com.example.onboardingtestapplication.Model.CoVidCenterRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Inject
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideCoVidCenterDataBase(@ApplicationContext context: Context) : CoVidCenterDataBase {
        return Room.databaseBuilder(
            context,
            CoVidCenterDataBase::class.java,
            "CoVidCenterDB"
        ).fallbackToDestructiveMigration()

            .build()
    }

    @Singleton
    @Provides
    fun provideCoVidRepository(dataBase: CoVidCenterDataBase) : CoVidCenterRepository {
        return CoVidCenterRepository(dataBase)
    }
}

