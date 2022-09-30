package com.data.daggermodule


import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.data.dataHolders.DataStoreHelper
import com.data.repositoryImpl.BaseRestRepository
import com.domain.RestApi.BaseRestApi
import com.data.repositoryImpl.RepositoryImpl
import com.domain.RestApi.LoginRestApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class BaseModule {
//context: Context,dataStore: DataStore<Preferences>
    @Provides
    @Singleton
    fun userdataStoreprovider():DataStore<Preferences>
    {
        return DataStoreHelper.getDataStore()
    }

    @Provides
    @Singleton
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
         val interception=HttpLoggingInterceptor()
        interception.level=HttpLoggingInterceptor.Level.BODY
        return interception
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(interceptor: HttpLoggingInterceptor):  OkHttpClient.Builder {
      // fun provideOkHttpClient():  OkHttpClient.Builder {
        return OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .readTimeout(600, TimeUnit.SECONDS)
            .writeTimeout(900, TimeUnit.SECONDS)
            //.callTimeout(60, TimeUnit.SECONDS)
            .connectTimeout(100, TimeUnit.SECONDS)
            .pingInterval(5, TimeUnit.SECONDS)
    }

    @Provides
    @Singleton
    fun getRetrofitClient(interceptor: HttpLoggingInterceptor) :BaseRestApi
   // fun getRetrofitClient() :BaseRestApi
    {
        val httpClient=OkHttpClient.Builder().addInterceptor(interceptor).build()
       // val httpClient=OkHttpClient.Builder().build()
        val retrofit= Retrofit.Builder().baseUrl("https://api.veriklick.in")// https://api.veriklick.in
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClient).build()
        return retrofit.create(BaseRestApi::class.java)
    }

    @Provides
    @Singleton
    fun getRepo(baseRestApi: BaseRestApi,loginRestApi: LoginRestApi): BaseRestRepository
    {
    return RepositoryImpl(baseRestApi,loginRestApi)//,loginBaseRestApi
    }

    @Provides
    @Singleton
    fun getClientForLogin(interceptor: HttpLoggingInterceptor) :LoginRestApi
   // fun getClientForLogin() :LoginRestApi

    {
        val httpClient=OkHttpClient.Builder().addInterceptor(interceptor).build()
       // val httpClient=OkHttpClient.Builder().build()
        val retrofit= Retrofit.Builder().baseUrl("https://veridialapi.veriklick.in")// https://api.veriklick.in
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClient).build()
        return retrofit.create(LoginRestApi::class.java)
    }
}













/* @Provides
  @Singleton
  suspend  fun dataStoreEditorprovider(dataStore: DataStore<Preferences>):MutablePreferences
  {
      var mutablepre:MutablePreferences?=null
      dataStore.edit {
          mutablepre=it!!
      }
      return mutablepre!!
  }*/


/* @Provides
    @Singleton
    suspend fun getUserCredential(dataStore: DataStore<Preferences>): Bundle
    {
        val bundle=Bundle()
        val emailPreferences= preferencesKey<String>(AppConstants.USER_EMAIL_ID)
        val passwordPreferences= preferencesKey<String>(AppConstants.USER_PASSWORD)

        val datas=dataStore.data.first()

        val email=datas[emailPreferences]
        val password=datas[passwordPreferences]

        bundle.putString(AppConstants.USER_EMAIL_ID,email)
        bundle.putString(AppConstants.USER_PASSWORD,password)
        return bundle
    }*/

