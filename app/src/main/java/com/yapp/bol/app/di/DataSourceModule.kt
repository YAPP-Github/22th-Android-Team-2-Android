package com.yapp.bol.app.di

import com.yapp.bol.data.datasource.impl.RemoteDataSource
import com.yapp.bol.data.datasource.auth.AuthDataSource
import com.yapp.bol.data.datasource.auth.impl.AuthDataSourceImpl
import com.yapp.bol.data.datasource.group.GroupDataSource
import com.yapp.bol.data.datasource.group.impl.GroupDataSourceImpl
import com.yapp.bol.data.datasource.impl.RemoteDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface DataSourceModule {

    @Binds
    fun bindsRemoteDataSource(remoteDataSourceImpl: RemoteDataSourceImpl): RemoteDataSource

    @Binds
    fun bindsAuthDatasource(authDataSourceImpl: AuthDataSourceImpl): AuthDataSource

    @Binds
    fun bindsGroupDatasource(groupDataSourceImpl: GroupDataSourceImpl): GroupDataSource
}
