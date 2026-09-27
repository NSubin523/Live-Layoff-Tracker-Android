package com.example.tracklayoff.features.auth.ui;

import com.example.tracklayoff.core.common.user.UserRepository;
import com.example.tracklayoff.features.auth.domain.AuthRepository;
import com.example.tracklayoff.features.auth.domain.SignInProviderFactory;
import com.example.tracklayoff.features.reporting.domain.CentralTelemetryInterface;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
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
public final class AuthViewmodel_Factory implements Factory<AuthViewmodel> {
  private final Provider<AuthRepository> authRepositoryProvider;

  private final Provider<UserRepository> userRepositoryProvider;

  private final Provider<SignInProviderFactory> authProvider;

  private final Provider<CentralTelemetryInterface> telemetryProvider;

  public AuthViewmodel_Factory(Provider<AuthRepository> authRepositoryProvider,
      Provider<UserRepository> userRepositoryProvider, Provider<SignInProviderFactory> authProvider,
      Provider<CentralTelemetryInterface> telemetryProvider) {
    this.authRepositoryProvider = authRepositoryProvider;
    this.userRepositoryProvider = userRepositoryProvider;
    this.authProvider = authProvider;
    this.telemetryProvider = telemetryProvider;
  }

  @Override
  public AuthViewmodel get() {
    return newInstance(authRepositoryProvider.get(), userRepositoryProvider.get(), authProvider.get(), telemetryProvider.get());
  }

  public static AuthViewmodel_Factory create(Provider<AuthRepository> authRepositoryProvider,
      Provider<UserRepository> userRepositoryProvider, Provider<SignInProviderFactory> authProvider,
      Provider<CentralTelemetryInterface> telemetryProvider) {
    return new AuthViewmodel_Factory(authRepositoryProvider, userRepositoryProvider, authProvider, telemetryProvider);
  }

  public static AuthViewmodel newInstance(AuthRepository authRepository,
      UserRepository userRepository, SignInProviderFactory authProvider,
      CentralTelemetryInterface telemetry) {
    return new AuthViewmodel(authRepository, userRepository, authProvider, telemetry);
  }
}
