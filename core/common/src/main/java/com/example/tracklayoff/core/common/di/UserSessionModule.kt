package com.example.tracklayoff.core.common.di

import com.example.tracklayoff.core.common.user.UserRepository
import com.example.tracklayoff.core.common.user.UserSession
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class UserSessionModule {
    @Binds abstract fun bindUserSession(repository: UserRepository): UserSession
}
