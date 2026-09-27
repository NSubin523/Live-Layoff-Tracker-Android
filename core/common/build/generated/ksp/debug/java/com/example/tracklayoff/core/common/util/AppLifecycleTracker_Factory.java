package com.example.tracklayoff.core.common.util;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata("javax.inject.Singleton")
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
public final class AppLifecycleTracker_Factory implements Factory<AppLifecycleTracker> {
  @Override
  public AppLifecycleTracker get() {
    return newInstance();
  }

  public static AppLifecycleTracker_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static AppLifecycleTracker newInstance() {
    return new AppLifecycleTracker();
  }

  private static final class InstanceHolder {
    private static final AppLifecycleTracker_Factory INSTANCE = new AppLifecycleTracker_Factory();
  }
}
