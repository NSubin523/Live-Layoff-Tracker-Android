package com.example.tracklayoff.core.network;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata({
    "com.example.tracklayoff.core.network.LayoffTrackerRetrofit",
    "com.example.tracklayoff.core.network.LayoffTrackerBaseUrl"
})
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast"
})
public final class NetworkModule_ProvideLayoffTrackerRetrofitFactory implements Factory<Retrofit> {
  private final Provider<String> baseUrlProvider;

  private final Provider<OkHttpClient> okHttpClientProvider;

  public NetworkModule_ProvideLayoffTrackerRetrofitFactory(Provider<String> baseUrlProvider,
      Provider<OkHttpClient> okHttpClientProvider) {
    this.baseUrlProvider = baseUrlProvider;
    this.okHttpClientProvider = okHttpClientProvider;
  }

  @Override
  public Retrofit get() {
    return provideLayoffTrackerRetrofit(baseUrlProvider.get(), okHttpClientProvider.get());
  }

  public static NetworkModule_ProvideLayoffTrackerRetrofitFactory create(
      Provider<String> baseUrlProvider, Provider<OkHttpClient> okHttpClientProvider) {
    return new NetworkModule_ProvideLayoffTrackerRetrofitFactory(baseUrlProvider, okHttpClientProvider);
  }

  public static Retrofit provideLayoffTrackerRetrofit(String baseUrl, OkHttpClient okHttpClient) {
    return Preconditions.checkNotNullFromProvides(NetworkModule.INSTANCE.provideLayoffTrackerRetrofit(baseUrl, okHttpClient));
  }
}
