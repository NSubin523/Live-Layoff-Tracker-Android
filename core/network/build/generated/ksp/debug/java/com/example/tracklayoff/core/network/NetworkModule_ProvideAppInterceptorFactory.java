package com.example.tracklayoff.core.network;

import com.google.firebase.auth.FirebaseAuth;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
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
public final class NetworkModule_ProvideAppInterceptorFactory implements Factory<AppInterceptor> {
  private final Provider<FirebaseAuth> firebaseAuthProvider;

  public NetworkModule_ProvideAppInterceptorFactory(Provider<FirebaseAuth> firebaseAuthProvider) {
    this.firebaseAuthProvider = firebaseAuthProvider;
  }

  @Override
  public AppInterceptor get() {
    return provideAppInterceptor(firebaseAuthProvider.get());
  }

  public static NetworkModule_ProvideAppInterceptorFactory create(
      Provider<FirebaseAuth> firebaseAuthProvider) {
    return new NetworkModule_ProvideAppInterceptorFactory(firebaseAuthProvider);
  }

  public static AppInterceptor provideAppInterceptor(FirebaseAuth firebaseAuth) {
    return Preconditions.checkNotNullFromProvides(NetworkModule.INSTANCE.provideAppInterceptor(firebaseAuth));
  }
}
