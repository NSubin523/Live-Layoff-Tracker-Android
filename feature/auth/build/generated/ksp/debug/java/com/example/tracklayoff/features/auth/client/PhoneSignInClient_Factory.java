package com.example.tracklayoff.features.auth.client;

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
public final class PhoneSignInClient_Factory implements Factory<PhoneSignInClient> {
  private final Provider<FirebaseAuth> firebaseAuthProvider;

  public PhoneSignInClient_Factory(Provider<FirebaseAuth> firebaseAuthProvider) {
    this.firebaseAuthProvider = firebaseAuthProvider;
  }

  @Override
  public PhoneSignInClient get() {
    return newInstance(firebaseAuthProvider.get());
  }

  public static PhoneSignInClient_Factory create(Provider<FirebaseAuth> firebaseAuthProvider) {
    return new PhoneSignInClient_Factory(firebaseAuthProvider);
  }

  public static PhoneSignInClient newInstance(FirebaseAuth firebaseAuth) {
    return new PhoneSignInClient(firebaseAuth);
  }
}
