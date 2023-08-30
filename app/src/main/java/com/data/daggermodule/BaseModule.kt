package com.data.daggermodule

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.data.dataHolders.DataStoreHelper
import com.data.repositoryImpl.BaseRestRepository
import com.domain.RestApi.BaseRestApi
import com.data.repositoryImpl.RepositoryImpl
import com.domain.RestApi.LoginRestApi
import com.veriKlick.R
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.security.KeyStore
import java.security.cert.Certificate
import java.security.cert.CertificateFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager

@Module
@InstallIn(SingletonComponent::class)
class BaseModule {
    //context: Context,dataStore: DataStore<Preferences>
    @Provides
    @Singleton
    fun userdataStoreprovider(): DataStore<Preferences> {
        return DataStoreHelper.getDataStore()
    }

    @Provides
    @Singleton
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
        val interception = HttpLoggingInterceptor()
        interception.level = HttpLoggingInterceptor.Level.BODY
        return interception
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(interceptor: HttpLoggingInterceptor): OkHttpClient.Builder {
        // fun provideOkHttpClient():  OkHttpClient.Builder {
        return OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .readTimeout(1, TimeUnit.MINUTES)
            .writeTimeout(1, TimeUnit.MINUTES)
            //.callTimeout(60, TimeUnit.SECONDS)
            .connectTimeout(1, TimeUnit.MINUTES)
            .pingInterval(1, TimeUnit.MINUTES)
            .retryOnConnectionFailure(true)
    }
    val TAG="moduleErrorTag"
    @Provides
    @Singleton
    fun getRetrofitClient(interceptor: HttpLoggingInterceptor): BaseRestApi
    // fun getRetrofitClient() :BaseRestApi
    {

        var retrofit:Retrofit?=null

        try {
            val httpClient = OkHttpClient.Builder().protocols(listOf(Protocol.HTTP_1_1))
                .writeTimeout(1, TimeUnit.MINUTES)
                .readTimeout(1, TimeUnit.MINUTES)
                .connectTimeout(1, TimeUnit.MINUTES)
                .addInterceptor(interceptor)
                .retryOnConnectionFailure(true)
                .followSslRedirects(true)
                .followRedirects(true)
                .build()
            // val httpClient=OkHttpClient.Builder().build() api.veriklick.com
            //api.veriklick.in
            // api.veriklick.com
              retrofit = Retrofit.Builder().baseUrl("https://api.veriklick.com")
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient)
                .build()
        }catch (e:HttpException){
            Log.d(TAG, "getRetrofitClient: exception https 90 ${e.message}")
        }catch (e:Exception)
        {
            Log.d(TAG, "getRetrofitClient: exception 93 ${e.message}")
        }catch (e:IOException)
        {
            Log.d(TAG, "getClientForLogin: io ex ${e.message} 97")
        }
        val baserestapi=retrofit?.create(BaseRestApi::class.java)!!
        return baserestapi
    }

    @Provides
    @Singleton
    fun getRepo(baseRestApi: BaseRestApi, loginRestApi: LoginRestApi): BaseRestRepository {
        return RepositoryImpl(baseRestApi, loginRestApi)//,loginBaseRestApi
    }

    @Provides
    @Singleton
    fun getClientForLogin(interceptor: HttpLoggingInterceptor): LoginRestApi
    // fun getClientForLogin() :LoginRestApi
    {
        var retrofit:Retrofit?=null
        try {
            val httpClient = OkHttpClient.Builder().addInterceptor(interceptor)
                .writeTimeout(1, TimeUnit.MINUTES)
                .readTimeout(1, TimeUnit.MINUTES)
                .connectTimeout(1, TimeUnit.MINUTES)
                .retryOnConnectionFailure(true)
                .followSslRedirects(true)
                .followRedirects(true)
                .build()
            // val httpClient=OkHttpClient.Builder().build()//https://veridialapi.veriklick.com
            //https://veridialapi.veriklick.in
             retrofit = Retrofit.Builder()
                .baseUrl("https://veridialapi.veriklick.com")// veridialapi.veriklick.in
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient).build()
        }catch (e:HttpException){
            Log.d(TAG, "getRetrofitClient: exception https 127 ${e.message}")
        }catch (e:Exception)
        {
            Log.d(TAG, "getRetrofitClient: exception  130 ${e.message}")
        }
        catch (e:IOException)
        {
            Log.d(TAG, "getClientForLogin: io ex ${e.message} 135")
        }


        return retrofit?.create(LoginRestApi::class.java)!!
    }
}

  /*  private fun getUnsafeOkHttpClient(mContext: Context) :
            OkHttpClient.Builder? {


        var mCertificateFactory : CertificateFactory =
            CertificateFactory.getInstance("X.509")
        var mInputStream = mContext.resources.openRawResource(R.raw.cert)
        var mCertificate : Certificate = mCertificateFactory.generateCertificate(mInputStream)
        mInputStream.close()
        val mKeyStoreType = KeyStore.getDefaultType()
        val mKeyStore = KeyStore.getInstance(mKeyStoreType)
        mKeyStore.load(null, null)
        mKeyStore.setCertificateEntry("ca", mCertificate)

        val mTmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm()
        val mTrustManagerFactory = TrustManagerFactory.getInstance(mTmfAlgorithm)
        mTrustManagerFactory.init(mKeyStore)

        val mTrustManagers = mTrustManagerFactory.trustManagers

        val mSslContext = SSLContext.getInstance("SSL")
        mSslContext.init(null, mTrustManagers, null)
        val mSslSocketFactory = mSslContext.socketFactory

        val builder = OkHttpClient.Builder()
        builder.sslSocketFactory(mSslSocketFactory, mTrustManagers[0] as X509TrustManager)
        builder.hostnameVerifier { _, _ -> true }
        return builder
}
*/












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

