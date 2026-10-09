package com.example.tracklayoff.features.auth.di

import com.example.tracklayoff.features.auth.data.AuthRepositoryImpl
import com.example.tracklayoff.features.auth.domain.AuthRepository
import com.example.tracklayoff.features.auth.domain.PhoneAuthGateway
import com.example.tracklayoff.features.auth.client.PhoneSignInClient
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AuthModule {

    @Binds
    @Singleton
    abstract fun bindPhoneAuthGateway(client: PhoneSignInClient): PhoneAuthGateway

    @Binds
    @Singleton
    abstract fun provideAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository
}