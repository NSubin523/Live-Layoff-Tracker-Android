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
    "com.example.tracklayoff.core.network.FavoritesRetrofit",
    "com.example.tracklayoff.core.network.FavoritesBaseUrl"
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
public final class NetworkModule_ProvideFavoritesRetrofitFactory implements Factory<Retrofit> {
  private final Provider<String> baseUrlProvider;

  private final Provider<OkHttpClient> okHttpClientProvider;

  public NetworkModule_ProvideFavoritesRetrofitFactory(Provider<String> baseUrlProvider,
      Provider<OkHttpClient> okHttpClientProvider) {
    this.baseUrlProvider = baseUrlProvider;
    this.okHttpClientProvider = okHttpClientProvider;
  }

  @Override
  public Retrofit get() {
    return provideFavoritesRetrofit(baseUrlProvider.get(), okHttpClientProvider.get());
  }

  public static NetworkModule_ProvideFavoritesRetrofitFactory create(
      Provider<String> baseUrlProvider, Provider<OkHttpClient> okHttpClientProvider) {
    return new NetworkModule_ProvideFavoritesRetrofitFactory(baseUrlProvider, okHttpClientProvider);
  }

  public static Retrofit provideFavoritesRetrofit(String baseUrl, OkHttpClient okHttpClient) {
    return Preconditions.checkNotNullFromProvides(NetworkModule.INSTANCE.provideFavoritesRetrofit(baseUrl, okHttpClient));
  }
}
