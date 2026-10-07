package com.example.tracklayoff.features.feed.ui.viewmodel;

import com.example.tracklayoff.core.common.util.AppLifecycleTracker;
import com.example.tracklayoff.core.network.NetworkObserver;
import com.example.tracklayoff.features.feed.data.repository.FeedRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
@QualifierMetadata
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
public final class FeedViewModel_Factory implements Factory<FeedViewModel> {
  private final Provider<FeedRepository> feedRepositoryProvider;

  private final Provider<NetworkObserver> networkObserverProvider;

  private final Provider<AppLifecycleTracker> appLifecycleTrackerProvider;

  public FeedViewModel_Factory(Provider<FeedRepository> feedRepositoryProvider,
      Provider<NetworkObserver> networkObserverProvider,
      Provider<AppLifecycleTracker> appLifecycleTrackerProvider) {
    this.feedRepositoryProvider = feedRepositoryProvider;
    this.networkObserverProvider = networkObserverProvider;
    this.appLifecycleTrackerProvider = appLifecycleTrackerProvider;
  }

  @Override
  public FeedViewModel get() {
    return newInstance(feedRepositoryProvider.get(), networkObserverProvider.get(), appLifecycleTrackerProvider.get());
  }

  public static FeedViewModel_Factory create(Provider<FeedRepository> feedRepositoryProvider,
      Provider<NetworkObserver> networkObserverProvider,
      Provider<AppLifecycleTracker> appLifecycleTrackerProvider) {
    return new FeedViewModel_Factory(feedRepositoryProvider, networkObserverProvider, appLifecycleTrackerProvider);
  }

  public static FeedViewModel newInstance(FeedRepository feedRepository,
      NetworkObserver networkObserver, AppLifecycleTracker appLifecycleTracker) {
    return new FeedViewModel(feedRepository, networkObserver, appLifecycleTracker);
  }
}
