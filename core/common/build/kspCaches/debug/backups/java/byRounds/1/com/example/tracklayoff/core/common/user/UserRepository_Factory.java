package com.example.tracklayoff.core.common.user;

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
public final class UserRepository_Factory implements Factory<UserRepository> {
  private final Provider<FirebaseAuth> firebaseAuthProvider;

  public UserRepository_Factory(Provider<FirebaseAuth> firebaseAuthProvider) {
    this.firebaseAuthProvider = firebaseAuthProvider;
  }

  @Override
  public UserRepository get() {
    return newInstance(firebaseAuthProvider.get());
  }

  public static UserRepository_Factory create(Provider<FirebaseAuth> firebaseAuthProvider) {
    return new UserRepository_Factory(firebaseAuthProvider);
  }

  public static UserRepository newInstance(FirebaseAuth firebaseAuth) {
    return new UserRepository(firebaseAuth);
  }
}
