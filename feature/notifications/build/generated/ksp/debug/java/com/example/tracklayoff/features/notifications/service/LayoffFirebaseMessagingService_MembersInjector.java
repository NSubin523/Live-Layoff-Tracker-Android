package com.example.tracklayoff.features.notifications.service;

import com.example.tracklayoff.core.common.di.IoDispatcher;
import com.example.tracklayoff.core.common.util.AppLifecycleTracker;
import com.example.tracklayoff.features.notifications.util.NotificationEventBus;
import com.example.tracklayoff.features.reporting.domain.CentralTelemetryInterface;
import com.google.firebase.messaging.FirebaseMessaging;
import dagger.MembersInjector;
import dagger.internal.DaggerGenerated;
import dagger.internal.InjectedFieldSignature;
import dagger.internal.QualifierMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;
import kotlinx.coroutines.CoroutineDispatcher;

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
public final class LayoffFirebaseMessagingService_MembersInjector implements MembersInjector<LayoffFirebaseMessagingService> {
  private final Provider<AppLifecycleTracker> lifecycleTrackerProvider;

  private final Provider<NotificationEventBus> notificationEventBusProvider;

  private final Provider<CoroutineDispatcher> ioDispatcherProvider;

  private final Provider<FirebaseMessaging> firebaseMessagingProvider;

  private final Provider<CentralTelemetryInterface> telemetryProvider;

  public LayoffFirebaseMessagingService_MembersInjector(
      Provider<AppLifecycleTracker> lifecycleTrackerProvider,
      Provider<NotificationEventBus> notificationEventBusProvider,
      Provider<CoroutineDispatcher> ioDispatcherProvider,
      Provider<FirebaseMessaging> firebaseMessagingProvider,
      Provider<CentralTelemetryInterface> telemetryProvider) {
    this.lifecycleTrackerProvider = lifecycleTrackerProvider;
    this.notificationEventBusProvider = notificationEventBusProvider;
    this.ioDispatcherProvider = ioDispatcherProvider;
    this.firebaseMessagingProvider = firebaseMessagingProvider;
    this.telemetryProvider = telemetryProvider;
  }

  public static MembersInjector<LayoffFirebaseMessagingService> create(
      Provider<AppLifecycleTracker> lifecycleTrackerProvider,
      Provider<NotificationEventBus> notificationEventBusProvider,
      Provider<CoroutineDispatcher> ioDispatcherProvider,
      Provider<FirebaseMessaging> firebaseMessagingProvider,
      Provider<CentralTelemetryInterface> telemetryProvider) {
    return new LayoffFirebaseMessagingService_MembersInjector(lifecycleTrackerProvider, notificationEventBusProvider, ioDispatcherProvider, firebaseMessagingProvider, telemetryProvider);
  }

  @Override
  public void injectMembers(LayoffFirebaseMessagingService instance) {
    injectLifecycleTracker(instance, lifecycleTrackerProvider.get());
    injectNotificationEventBus(instance, notificationEventBusProvider.get());
    injectIoDispatcher(instance, ioDispatcherProvider.get());
    injectFirebaseMessaging(instance, firebaseMessagingProvider.get());
    injectTelemetry(instance, telemetryProvider.get());
  }

  @InjectedFieldSignature("com.example.tracklayoff.features.notifications.service.LayoffFirebaseMessagingService.lifecycleTracker")
  public static void injectLifecycleTracker(LayoffFirebaseMessagingService instance,
      AppLifecycleTracker lifecycleTracker) {
    instance.lifecycleTracker = lifecycleTracker;
  }

  @InjectedFieldSignature("com.example.tracklayoff.features.notifications.service.LayoffFirebaseMessagingService.notificationEventBus")
  public static void injectNotificationEventBus(LayoffFirebaseMessagingService instance,
      NotificationEventBus notificationEventBus) {
    instance.notificationEventBus = notificationEventBus;
  }

  @InjectedFieldSignature("com.example.tracklayoff.features.notifications.service.LayoffFirebaseMessagingService.ioDispatcher")
  @IoDispatcher
  public static void injectIoDispatcher(LayoffFirebaseMessagingService instance,
      CoroutineDispatcher ioDispatcher) {
    instance.ioDispatcher = ioDispatcher;
  }

  @InjectedFieldSignature("com.example.tracklayoff.features.notifications.service.LayoffFirebaseMessagingService.firebaseMessaging")
  public static void injectFirebaseMessaging(LayoffFirebaseMessagingService instance,
      FirebaseMessaging firebaseMessaging) {
    instance.firebaseMessaging = firebaseMessaging;
  }

  @InjectedFieldSignature("com.example.tracklayoff.features.notifications.service.LayoffFirebaseMessagingService.telemetry")
  public static void injectTelemetry(LayoffFirebaseMessagingService instance,
      CentralTelemetryInterface telemetry) {
    instance.telemetry = telemetry;
  }
}
