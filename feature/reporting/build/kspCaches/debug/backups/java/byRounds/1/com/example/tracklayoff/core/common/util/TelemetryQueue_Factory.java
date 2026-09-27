package com.example.tracklayoff.core.common.util;

import com.example.tracklayoff.core.common.user.UserRepository;
import com.example.tracklayoff.core.network.NetworkObserver;
import com.example.tracklayoff.features.reporting.data.api.ReportingApiService;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;
import kotlinx.coroutines.CoroutineDispatcher;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("com.example.tracklayoff.core.common.di.IoDispatcher")
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
public final class TelemetryQueue_Factory implements Factory<TelemetryQueue> {
  private final Provider<ReportingApiService> reportingApiServiceProvider;

  private final Provider<UserRepository> userRepositoryProvider;

  private final Provider<CoroutineDispatcher> ioDispatcherProvider;

  private final Provider<NetworkObserver> networkObserverProvider;

  public TelemetryQueue_Factory(Provider<ReportingApiService> reportingApiServiceProvider,
      Provider<UserRepository> userRepositoryProvider,
      Provider<CoroutineDispatcher> ioDispatcherProvider,
      Provider<NetworkObserver> networkObserverProvider) {
    this.reportingApiServiceProvider = reportingApiServiceProvider;
    this.userRepositoryProvider = userRepositoryProvider;
    this.ioDispatcherProvider = ioDispatcherProvider;
    this.networkObserverProvider = networkObserverProvider;
  }

  @Override
  public TelemetryQueue get() {
    return newInstance(reportingApiServiceProvider.get(), userRepositoryProvider.get(), ioDispatcherProvider.get(), networkObserverProvider.get());
  }

  public static TelemetryQueue_Factory create(
      Provider<ReportingApiService> reportingApiServiceProvider,
      Provider<UserRepository> userRepositoryProvider,
      Provider<CoroutineDispatcher> ioDispatcherProvider,
      Provider<NetworkObserver> networkObserverProvider) {
    return new TelemetryQueue_Factory(reportingApiServiceProvider, userRepositoryProvider, ioDispatcherProvider, networkObserverProvider);
  }

  public static TelemetryQueue newInstance(ReportingApiService reportingApiService,
      UserRepository userRepository, CoroutineDispatcher ioDispatcher,
      NetworkObserver networkObserver) {
    return new TelemetryQueue(reportingApiService, userRepository, ioDispatcher, networkObserver);
  }
}
