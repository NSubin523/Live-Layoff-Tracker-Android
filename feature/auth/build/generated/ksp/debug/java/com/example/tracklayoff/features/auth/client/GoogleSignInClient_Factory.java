package com.example.tracklayoff.features.auth.client;

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
public final class GoogleSignInClient_Factory implements Factory<GoogleSignInClient> {
  @Override
  public GoogleSignInClient get() {
    return newInstance();
  }

  public static GoogleSignInClient_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static GoogleSignInClient newInstance() {
    return new GoogleSignInClient();
  }

  private static final class InstanceHolder {
    private static final GoogleSignInClient_Factory INSTANCE = new GoogleSignInClient_Factory();
  }
}
