package com.example.tracklayoff.features.reporting.di

import com.example.tracklayoff.features.reporting.data.api.ReportingApiService
import com.example.tracklayoff.features.reporting.domain.CentralTelemetryInterface
import com.example.tracklayoff.features.reporting.domain.TelemetryImplementation
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class TelemetryModule {

    companion object {
        @Provides
        @Singleton
        fun provideTelemetryApiService(retrofit: Retrofit): ReportingApiService {
            return retrofit.create(ReportingApiService::class.java)
        }
    }


    @Binds
    @Singleton
    abstract fun bindCentralTelemetry(
        telemetryImplementation: TelemetryImplementation
    ): CentralTelemetryInterface
}
