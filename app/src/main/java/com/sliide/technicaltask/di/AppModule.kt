package com.sliide.technicaltask.di

import android.content.Context
import androidx.viewbinding.BuildConfig
import com.sliide.technicaltask.data.constant.URLHelper.baseURL
import com.sliide.technicaltask.data.remote.AppService
import com.sliide.technicaltask.data.remote.RemoteDataSource
import com.sliide.technicaltask.data.repos.AppRepository
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.sliide.technicaltask.utils.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import org.jetbrains.annotations.NotNull
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.inject.Singleton
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    private const val TOKEN = "64bcb48d7497235b74624e120852e29511215df332a4ad4973e587dccd15aef9"
    private lateinit var appRepository: AppRepository

    //Retrofit
    @Singleton
    @Provides
    fun providesHttpLoggingInterceptor() = HttpLoggingInterceptor().apply {
        level =
            if (!BuildConfig.DEBUG) HttpLoggingInterceptor.Level.NONE else HttpLoggingInterceptor.Level.BODY
    }

    @Singleton
    @Provides
    fun provideAuthorizationInterceptor(
        connection: CheckInternetConnection,
    ): Interceptor {
        return Interceptor { chain ->
            var request = chain.request()
            val requestBuilder = request.newBuilder()
            val cacheControl = CacheControl.Builder()
                .maxAge(60, TimeUnit.SECONDS)
                .build()
            if (connection.isNetworkAvailable()) {
                val maxAge = 30 // read from cache for 1 minute
                //request.newBuilder()
                requestBuilder
                    .addHeader("X-Requested-With", "XMLHttpRequest")
                    .addHeader("Authorization", "Bearer ${TOKEN}")
                    .addHeader("Content-Type", "application/json")
                    .header("Cache-Control", "public, max-age=$maxAge")
                    .cacheControl(cacheControl)
                    .build()
                request = requestBuilder.build()
                var response = chain.proceed(request)
                var tryCount = 0
                while (!response.isSuccessful && tryCount < 3 && response.code == 504) {
                    tryCount++
                    // retry the request
                    response = chain.proceed(request)
                }

                if (!response.isSuccessful && response.code == 504)
                    throw NetworkConnectivityError()
                else
                    response

            } else {
                val maxStale = 60 * 60 * 24 * 28 // tolerate 4-weeks stale
                // request.newBuilder()
                requestBuilder
                    .header("Cache-Control", "public, only-if-cached, max-stale=$maxStale")
                    .build()
                request = requestBuilder.build()
                chain.proceed(request)
            }

        }
    }

    @Provides
    @NotNull
    fun provideAuth(@ApplicationContext @NotNull context: Context): OnAuthFailed {
        return object : OnAuthFailed {
            override fun onFailedAuth() {
            }
        }
    }

    @Singleton
    @Provides
    fun OAuth2Authenticator(
        onAuthFailed: OnAuthFailed
    ): Authenticator {
        return okhttp3.Authenticator { route: Route?, response: Response? ->
            if (response!!.code == 401) {

            }
            if (onAuthFailed != null) {
                onAuthFailed.onFailedAuth()
            }
            return@Authenticator null
        }
    }

    @Singleton
    @Provides
    fun provideOKHttpClient(
        loggingInterceptor: HttpLoggingInterceptor,
        authInterceptor: Interceptor,
        authenticator: Authenticator,
        @ApplicationContext context: Context
    ): OkHttpClient {        // Create a trust manager that does not validate certificate chains
        val trustAllCerts =
            arrayOf<TrustManager>(
                object : X509TrustManager {
                    @Throws(CertificateException::class)
                    override fun checkClientTrusted(
                        chain: Array<X509Certificate>,
                        authType: String
                    ) {
                    }

                    @Throws(CertificateException::class)
                    override fun checkServerTrusted(
                        chain: Array<X509Certificate>,
                        authType: String
                    ) {
                    }

                    override fun getAcceptedIssuers(): Array<X509Certificate> {
                        return arrayOf()
                    }
                }
            )

        // Install the all-trusting trust manager
        val sslContext = SSLContext.getInstance("SSL")
        sslContext.init(null, trustAllCerts, SecureRandom())
        // Create an ssl socket factory with our all-trusting manager
        val sslSocketFactory = sslContext.socketFactory

        val builder = OkHttpClient.Builder()
        builder.sslSocketFactory(
            sslSocketFactory,
            trustAllCerts[0] as X509TrustManager
        )
        builder.hostnameVerifier { hostname, session -> true }
        val cacheSize = 10 * 1024 * 1024 // 10 MB

        val cache = Cache(context.cacheDir, cacheSize.toLong())
        return OkHttpClient.Builder()
            .connectTimeout(1, TimeUnit.MINUTES)
            .writeTimeout(15, TimeUnit.SECONDS) // write timeout
            .readTimeout(30, TimeUnit.SECONDS) // read timeout
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)
            .authenticator(authenticator)
            .cache(cache)
            .build()
    }

    @Provides
    fun provideGson(): Gson = GsonBuilder().create()

    @Singleton
    @Provides
    fun provideRetrofit(okhttpClient: OkHttpClient, gson: Gson): Retrofit {
      return  Retrofit.Builder()
            .baseUrl(baseURL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(okhttpClient)
            .build()
    }

    //App service
    @Singleton
    @Provides
    fun provideAppService(retrofit: Retrofit): AppService =
        retrofit.create(AppService::class.java)

    @Singleton
    @Provides
    fun provideRemoteDataSource(appService: AppService) =
        RemoteDataSource(appService)

    @Singleton
    @Provides
    fun provideRepository(
        remoteDataSource: RemoteDataSource
    ): AppRepository {
        appRepository = AppRepository(
            remoteDataSource
        )
        return appRepository
    }
}