package com.accenture.dansmarue.di.modules;

import android.app.Application;
import android.content.Context;
import android.util.Base64;
import android.util.Log;

import com.accenture.dansmarue.BuildConfig;
import com.accenture.dansmarue.services.ApiServiceEquipement;
import com.accenture.dansmarue.services.AuthentParisAccountService;
import com.accenture.dansmarue.services.SiraApiService;
import com.accenture.dansmarue.services.SiraApiServiceMock;
import com.accenture.dansmarue.services.SiraHttpResponseInterceptor;
import com.accenture.dansmarue.utils.PrefManager;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.concurrent.TimeUnit;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import dagger.Module;
import dagger.Provides;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by PK on 22/03/2017.
 * Dagger Module defining the provided injection for the Application MVP architecture
 */
@Module
public class ApplicationModule {

    private static final String TAG = ApplicationModule.class.getName();

    private String siraUrl;
    private String siraUrlMock;
    private String urlEquipement;
    private String authentUrl;
    private Application application;
    private Context context;

    private String graviteeKey ="";

    public ApplicationModule(final Application application) {
        this.application = application;
        this.context = application.getApplicationContext();
        this.siraUrl = BuildConfig.SIRA_API_URL;
        this.authentUrl = BuildConfig.AUTHENT_API_URL;
        this.siraUrlMock = BuildConfig.SIRA_API_URL_MOCK;
        this.urlEquipement = BuildConfig.API_URL_EQUIPEMENT;

        decryptTokenGravitee();
    }

    /**
     * Decrypt Gravitee token
     * 
     */
    private void decryptTokenGravitee() {
        
    }

    @Provides
    @Singleton
    Application provideApplication() {
        return application;
    }

    @Provides
    @Singleton
    PrefManager providePrefManager() {
        return new PrefManager(application);
    }

    @Provides
    @Singleton
    Context provideContext() {
        return context;
    }

    @Singleton
    @Provides
    GsonConverterFactory provideGsonConverterFactory() {
        return GsonConverterFactory.create();
    }

    @Singleton
    @Provides
    X509TrustManager provideTrustManager() {
        return new X509TrustManager() {
            @Override
            public void checkClientTrusted(
                    java.security.cert.X509Certificate[] chain,
                    String authType) {
            }

            @Override
            public void checkServerTrusted(
                    java.security.cert.X509Certificate[] chain,
                    String authType) {
            }

            @Override
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return new java.security.cert.X509Certificate[0];
            }
        };
    }


    @Singleton
    @Provides
    SSLSocketFactory provideSSLSocketFactory(final X509TrustManager trustManager) {
        try {
            // Install the all-trusting trust manager
            final SSLContext sslContext;

            // Create a trust manager that does not validate certificate chains
            final TrustManager[] trustAllCerts = new TrustManager[]{trustManager};

            sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustAllCerts,
                    new java.security.SecureRandom());

            // Create an ssl socket factory with our all-trusting manager
            return sslContext
                    .getSocketFactory();
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            e.printStackTrace();
        }
        return null;
    }

    // timeout 120
    @Singleton
    @Provides
    @Named("glideOkHttpClientt")
    OkHttpClient provideGlideOkHttpClient(final X509TrustManager trustManager, final SSLSocketFactory sSLSocketFactory) {
        return new OkHttpClient.Builder()
                .connectTimeout(120, TimeUnit.SECONDS)
                .readTimeout(120, TimeUnit.SECONDS)
                .hostnameVerifier(org.apache.http.conn.ssl.SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER)
                .sslSocketFactory(sSLSocketFactory, trustManager)
                .build();
    }

    //timeout 240
    @Singleton
    @Provides
    @Named("siraOkHttpClientt")
    OkHttpClient provideSiraOkHttpClient(final X509TrustManager trustManager, final SSLSocketFactory sSLSocketFactory) {
        return new OkHttpClient.Builder()
                .connectTimeout(240, TimeUnit.SECONDS)
                .readTimeout(240, TimeUnit.SECONDS).addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request request = chain.request().newBuilder().addHeader(BuildConfig.GRAVITEE_API_KEY, graviteeKey).build();
                        return chain.proceed(request);
                    }
                })
                .addInterceptor(new HttpLoggingInterceptor()
                        .setLevel(HttpLoggingInterceptor.Level.BODY))
                .addInterceptor(new SiraHttpResponseInterceptor())
                .hostnameVerifier(org.apache.http.conn.ssl.SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER)
                .sslSocketFactory(sSLSocketFactory, trustManager).build();
    }

    // timeout 120
    @Singleton
    @Provides
    @Named("authentOkHttpClientt")
    OkHttpClient provideAuthentOkHttpClient(final X509TrustManager trustManager, final SSLSocketFactory sSLSocketFactory) {
        return new OkHttpClient.Builder()
                .connectTimeout(120, TimeUnit.SECONDS)
                .readTimeout(120, TimeUnit.SECONDS)
                .addInterceptor(new HttpLoggingInterceptor()
                        .setLevel(HttpLoggingInterceptor.Level.BODY))
                .addInterceptor(new SiraHttpResponseInterceptor())
                .hostnameVerifier(org.apache.http.conn.ssl.SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER)
                .sslSocketFactory(sSLSocketFactory, trustManager)
                .build();
    }

    @Singleton
    @Provides
    RxJava2CallAdapterFactory provideRxJavaCallAdapterFactory() {
        return RxJava2CallAdapterFactory.create();
    }

    @Singleton
    @Provides
    @Named("siraRetrofit")
    Retrofit provideSiraRetrofit(@Named("siraOkHttpClientt") OkHttpClient client, GsonConverterFactory converterFactory, RxJava2CallAdapterFactory adapterFactory) {
        return new Retrofit.Builder()
                .baseUrl(siraUrl)
                .addConverterFactory(converterFactory)
                .addCallAdapterFactory(adapterFactory)
                .client(client)
                .build();
    }

    @Singleton
    @Provides
    @Named("siraRetrofitMock")
    Retrofit provideSiraRetrofitMock(@Named("authentOkHttpClientt") OkHttpClient client, GsonConverterFactory converterFactory, RxJava2CallAdapterFactory adapterFactory) {
        return new Retrofit.Builder()
                .baseUrl(siraUrlMock)
                .addConverterFactory(converterFactory)
                .addCallAdapterFactory(adapterFactory)
                .client(client)
                .build();
    }

    @Singleton
    @Provides
    @Named("retrofitEquipement")
    Retrofit provideRetrofitEquipement(@Named("authentOkHttpClientt") OkHttpClient client, GsonConverterFactory converterFactory, RxJava2CallAdapterFactory adapterFactory) {
        return new Retrofit.Builder()
                .baseUrl(urlEquipement)
                .addConverterFactory(converterFactory)
                .addCallAdapterFactory(adapterFactory)
                .client(client)
                .build();
    }


    @Singleton
    @Provides
    @Named("authentRetrofit")
    Retrofit provideAuthenRetrofit(@Named("authentOkHttpClientt") OkHttpClient client, GsonConverterFactory converterFactory, RxJava2CallAdapterFactory adapterFactory) {
        return new Retrofit.Builder()
                .baseUrl(authentUrl)
                .addConverterFactory(converterFactory)
                .addCallAdapterFactory(adapterFactory)
                .client(client)
                .build();
    }

    @Provides
    @Singleton
    SiraApiService provideSiraApiService(@Named("siraRetrofit") final Retrofit retrofit) {
        return retrofit.create(SiraApiService.class);
    }

    @Provides
    @Singleton
    SiraApiServiceMock provideSiraApiServiceMock(@Named("siraRetrofitMock") final Retrofit retrofit) {
        return retrofit.create(SiraApiServiceMock.class);
    }

    @Provides
    @Singleton
    ApiServiceEquipement provideApiServiceEquipement(@Named("retrofitEquipement") final Retrofit retrofit) {
        return retrofit.create(ApiServiceEquipement.class);
    }


    @Provides
    @Singleton
    AuthentParisAccountService provideAuthentParisAccountService(@Named("authentRetrofit") final Retrofit retrofit) {
        return retrofit.create(AuthentParisAccountService.class);
    }
}
