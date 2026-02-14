package im.manus.atlas.di

import android.content.Context
import androidx.room.Room
import im.manus.atlas.data.local.AtlasDatabase
import im.manus.atlas.data.remote.AtlasApi
import im.manus.atlas.data.repository.AtlasRepositoryImpl
import im.manus.atlas.domain.repository.AtlasRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import timber.log.Timber
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor { message ->
            Timber.d(message)
        }.apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }

        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://joaovidaamazonlog.github.io/atlas/data/")
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideAtlasApi(retrofit: Retrofit): AtlasApi {
        return retrofit.create(AtlasApi::class.java)
    }

    @Provides
    @Singleton
    fun provideAtlasDatabase(
        @ApplicationContext context: Context
    ): AtlasDatabase {
        return Room.databaseBuilder(
            context,
            AtlasDatabase::class.java,
            "atlas_database"
        ).build()
    }

    @Provides
    @Singleton
    fun providePartnerDao(database: AtlasDatabase) = database.partnerDao()

    @Provides
    @Singleton
    fun provideDeliveryStationDao(database: AtlasDatabase) = database.deliveryStationDao()

    @Provides
    @Singleton
    fun provideAtlasRepository(impl: AtlasRepositoryImpl): AtlasRepository = impl
}