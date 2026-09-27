package com.example.tracklayoff.core.network;

import android.content.Context;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
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
public final class NetworkObserver_Factory implements Factory<NetworkObserver> {
  private final Provider<Context> contextProvider;

  public NetworkObserver_Factory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public NetworkObserver get() {
    return newInstance(contextProvider.get());
  }

  public static NetworkObserver_Factory create(Provider<Context> contextProvider) {
    return new NetworkObserver_Factory(contextProvider);
  }

  public static NetworkObserver newInstance(Context context) {
    return new NetworkObserver(context);
  }
}
