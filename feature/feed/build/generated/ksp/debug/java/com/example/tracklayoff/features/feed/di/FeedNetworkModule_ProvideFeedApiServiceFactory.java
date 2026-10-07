package com.example.tracklayoff.features.feed.di;

import com.example.tracklayoff.features.feed.data.remote.FeedApiService;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;
import retrofit2.Retrofit;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("com.example.tracklayoff.core.network.LayoffTrackerRetrofit")
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
public final class FeedNetworkModule_ProvideFeedApiServiceFactory implements Factory<FeedApiService> {
  private final Provider<Retrofit> retrofitProvider;

  public FeedNetworkModule_ProvideFeedApiServiceFactory(Provider<Retrofit> retrofitProvider) {
    this.retrofitProvider = retrofitProvider;
  }

  @Override
  public FeedApiService get() {
    return provideFeedApiService(retrofitProvider.get());
  }

  public static FeedNetworkModule_ProvideFeedApiServiceFactory create(
      Provider<Retrofit> retrofitProvider) {
    return new FeedNetworkModule_ProvideFeedApiServiceFactory(retrofitProvider);
  }

  public static FeedApiService provideFeedApiService(Retrofit retrofit) {
    return Preconditions.checkNotNullFromProvides(FeedNetworkModule.INSTANCE.provideFeedApiService(retrofit));
  }
}
