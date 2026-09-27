package com.example.tracklayoff.core.network;

import com.google.firebase.messaging.FirebaseMessaging;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
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
public final class NetworkModule_ProvideFirebaseMessagingFactory implements Factory<FirebaseMessaging> {
  @Override
  public FirebaseMessaging get() {
    return provideFirebaseMessaging();
  }

  public static NetworkModule_ProvideFirebaseMessagingFactory create() {
    return InstanceHolder.INSTANCE;
  }

  public static FirebaseMessaging provideFirebaseMessaging() {
    return Preconditions.checkNotNullFromProvides(NetworkModule.INSTANCE.provideFirebaseMessaging());
  }

  private static final class InstanceHolder {
    private static final NetworkModule_ProvideFirebaseMessagingFactory INSTANCE = new NetworkModule_ProvideFirebaseMessagingFactory();
  }
}
