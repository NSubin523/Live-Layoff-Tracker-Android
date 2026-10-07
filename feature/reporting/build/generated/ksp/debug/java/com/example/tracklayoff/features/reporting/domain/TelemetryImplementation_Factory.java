package com.example.tracklayoff.features.reporting.domain;

import com.example.tracklayoff.core.common.util.TelemetryQueue;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
public final class TelemetryImplementation_Factory implements Factory<TelemetryImplementation> {
  private final Provider<TelemetryQueue> queueProvider;

  public TelemetryImplementation_Factory(Provider<TelemetryQueue> queueProvider) {
    this.queueProvider = queueProvider;
  }

  @Override
  public TelemetryImplementation get() {
    return newInstance(queueProvider.get());
  }

  public static TelemetryImplementation_Factory create(Provider<TelemetryQueue> queueProvider) {
    return new TelemetryImplementation_Factory(queueProvider);
  }

  public static TelemetryImplementation newInstance(TelemetryQueue queue) {
    return new TelemetryImplementation(queue);
  }
}
