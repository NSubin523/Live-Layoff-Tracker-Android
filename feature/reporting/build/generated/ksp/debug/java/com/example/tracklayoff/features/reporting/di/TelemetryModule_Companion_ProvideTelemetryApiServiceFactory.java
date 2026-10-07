package com.example.tracklayoff.features.reporting.di;

import com.example.tracklayoff.features.reporting.data.api.ReportingApiService;
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
public final class TelemetryModule_Companion_ProvideTelemetryApiServiceFactory implements Factory<ReportingApiService> {
  private final Provider<Retrofit> retrofitProvider;

  public TelemetryModule_Companion_ProvideTelemetryApiServiceFactory(
      Provider<Retrofit> retrofitProvider) {
    this.retrofitProvider = retrofitProvider;
  }

  @Override
  public ReportingApiService get() {
    return provideTelemetryApiService(retrofitProvider.get());
  }

  public static TelemetryModule_Companion_ProvideTelemetryApiServiceFactory create(
      Provider<Retrofit> retrofitProvider) {
    return new TelemetryModule_Companion_ProvideTelemetryApiServiceFactory(retrofitProvider);
  }

  public static ReportingApiService provideTelemetryApiService(Retrofit retrofit) {
    return Preconditions.checkNotNullFromProvides(TelemetryModule.Companion.provideTelemetryApiService(retrofit));
  }
}
