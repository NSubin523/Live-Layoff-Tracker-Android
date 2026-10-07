package com.example.tracklayoff.core.network;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("com.example.tracklayoff.core.network.LayoffTrackerBaseUrl")
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
public final class NetworkModule_ProvideLayoffTrackerBaseUrlFactory implements Factory<String> {
  @Override
  public String get() {
    return provideLayoffTrackerBaseUrl();
  }

  public static NetworkModule_ProvideLayoffTrackerBaseUrlFactory create() {
    return InstanceHolder.INSTANCE;
  }

  public static String provideLayoffTrackerBaseUrl() {
    return Preconditions.checkNotNullFromProvides(NetworkModule.INSTANCE.provideLayoffTrackerBaseUrl());
  }

  private static final class InstanceHolder {
    private static final NetworkModule_ProvideLayoffTrackerBaseUrlFactory INSTANCE = new NetworkModule_ProvideLayoffTrackerBaseUrlFactory();
  }
}
