package com.example.tracklayoff.core.database.di;

import com.example.tracklayoff.core.database.AppDatabase;
import com.example.tracklayoff.features.feed.data.local.dao.CompanyDao;
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
public final class DatabaseModule_ProvideCompanyDaoFactory implements Factory<CompanyDao> {
  private final Provider<AppDatabase> databaseProvider;

  public DatabaseModule_ProvideCompanyDaoFactory(Provider<AppDatabase> databaseProvider) {
    this.databaseProvider = databaseProvider;
  }

  @Override
  public CompanyDao get() {
    return provideCompanyDao(databaseProvider.get());
  }

  public static DatabaseModule_ProvideCompanyDaoFactory create(
      Provider<AppDatabase> databaseProvider) {
    return new DatabaseModule_ProvideCompanyDaoFactory(databaseProvider);
  }

  public static CompanyDao provideCompanyDao(AppDatabase database) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideCompanyDao(database));
  }
}
