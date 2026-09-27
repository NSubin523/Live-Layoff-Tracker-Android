package com.example.tracklayoff.features.feed.data.repository;

import com.example.tracklayoff.features.feed.data.local.dao.CompanyDao;
import com.example.tracklayoff.features.feed.data.remote.FeedApiService;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;
import kotlinx.coroutines.CoroutineDispatcher;

@ScopeMetadata
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
public final class FeedRepository_Factory implements Factory<FeedRepository> {
  private final Provider<FeedApiService> feedApiServiceProvider;

  private final Provider<CompanyDao> companyDaoProvider;

  private final Provider<CoroutineDispatcher> ioDispatcherProvider;

  public FeedRepository_Factory(Provider<FeedApiService> feedApiServiceProvider,
      Provider<CompanyDao> companyDaoProvider, Provider<CoroutineDispatcher> ioDispatcherProvider) {
    this.feedApiServiceProvider = feedApiServiceProvider;
    this.companyDaoProvider = companyDaoProvider;
    this.ioDispatcherProvider = ioDispatcherProvider;
  }

  @Override
  public FeedRepository get() {
    return newInstance(feedApiServiceProvider.get(), companyDaoProvider.get(), ioDispatcherProvider.get());
  }

  public static FeedRepository_Factory create(Provider<FeedApiService> feedApiServiceProvider,
      Provider<CompanyDao> companyDaoProvider, Provider<CoroutineDispatcher> ioDispatcherProvider) {
    return new FeedRepository_Factory(feedApiServiceProvider, companyDaoProvider, ioDispatcherProvider);
  }

  public static FeedRepository newInstance(FeedApiService feedApiService, CompanyDao companyDao,
      CoroutineDispatcher ioDispatcher) {
    return new FeedRepository(feedApiService, companyDao, ioDispatcher);
  }
}
