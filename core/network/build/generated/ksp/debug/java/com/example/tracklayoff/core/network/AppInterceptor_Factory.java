package com.example.tracklayoff.core.network;

import com.google.firebase.auth.FirebaseAuth;
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
public final class AppInterceptor_Factory implements Factory<AppInterceptor> {
  private final Provider<FirebaseAuth> firebaseAuthProvider;

  public AppInterceptor_Factory(Provider<FirebaseAuth> firebaseAuthProvider) {
    this.firebaseAuthProvider = firebaseAuthProvider;
  }

  @Override
  public AppInterceptor get() {
    return newInstance(firebaseAuthProvider.get());
  }

  public static AppInterceptor_Factory create(Provider<FirebaseAuth> firebaseAuthProvider) {
    return new AppInterceptor_Factory(firebaseAuthProvider);
  }

  public static AppInterceptor newInstance(FirebaseAuth firebaseAuth) {
    return new AppInterceptor(firebaseAuth);
  }
}
