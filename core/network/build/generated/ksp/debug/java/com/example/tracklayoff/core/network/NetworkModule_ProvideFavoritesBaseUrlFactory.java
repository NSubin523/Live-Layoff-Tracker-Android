package com.example.tracklayoff.core.network;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("com.example.tracklayoff.core.network.FavoritesBaseUrl")
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
public final class NetworkModule_ProvideFavoritesBaseUrlFactory implements Factory<String> {
  @Override
  public String get() {
    return provideFavoritesBaseUrl();
  }

  public static NetworkModule_ProvideFavoritesBaseUrlFactory create() {
    return InstanceHolder.INSTANCE;
  }

  public static String provideFavoritesBaseUrl() {
    return Preconditions.checkNotNullFromProvides(NetworkModule.INSTANCE.provideFavoritesBaseUrl());
  }

  private static final class InstanceHolder {
    private static final NetworkModule_ProvideFavoritesBaseUrlFactory INSTANCE = new NetworkModule_ProvideFavoritesBaseUrlFactory();
  }
}
