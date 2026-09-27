package com.example.tracklayoff.features.notifications.util;

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
public final class NotificationEventBus_Factory implements Factory<NotificationEventBus> {
  @Override
  public NotificationEventBus get() {
    return newInstance();
  }

  public static NotificationEventBus_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static NotificationEventBus newInstance() {
    return new NotificationEventBus();
  }

  private static final class InstanceHolder {
    private static final NotificationEventBus_Factory INSTANCE = new NotificationEventBus_Factory();
  }
}
