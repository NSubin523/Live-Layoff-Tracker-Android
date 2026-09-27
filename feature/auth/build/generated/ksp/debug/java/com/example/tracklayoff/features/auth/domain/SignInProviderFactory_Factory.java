package com.example.tracklayoff.features.auth.domain;

import com.example.tracklayoff.features.auth.client.GoogleSignInClient;
import com.example.tracklayoff.features.auth.client.PhoneSignInClient;
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
public final class SignInProviderFactory_Factory implements Factory<SignInProviderFactory> {
  private final Provider<GoogleSignInClient> googleSignInClientProvider;

  private final Provider<PhoneSignInClient> phoneSignInClientProvider;

  public SignInProviderFactory_Factory(Provider<GoogleSignInClient> googleSignInClientProvider,
      Provider<PhoneSignInClient> phoneSignInClientProvider) {
    this.googleSignInClientProvider = googleSignInClientProvider;
    this.phoneSignInClientProvider = phoneSignInClientProvider;
  }

  @Override
  public SignInProviderFactory get() {
    return newInstance(googleSignInClientProvider.get(), phoneSignInClientProvider.get());
  }

  public static SignInProviderFactory_Factory create(
      Provider<GoogleSignInClient> googleSignInClientProvider,
      Provider<PhoneSignInClient> phoneSignInClientProvider) {
    return new SignInProviderFactory_Factory(googleSignInClientProvider, phoneSignInClientProvider);
  }

  public static SignInProviderFactory newInstance(GoogleSignInClient googleSignInClient,
      PhoneSignInClient phoneSignInClient) {
    return new SignInProviderFactory(googleSignInClient, phoneSignInClient);
  }
}
