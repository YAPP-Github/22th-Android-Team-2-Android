package com.yapp.bol.data.repository

import com.yapp.bol.data.datasource.impl.RemoteDataSource
import com.yapp.bol.data.mapper.MapperToDomain.fileUploadToDomain
import com.yapp.bol.data.mapper.MapperToDomain.gameToDomain
import com.yapp.bol.data.mapper.MapperToDomain.mapperToBaseItem
import com.yapp.bol.data.mapper.MapperToDomain.mapperToCheckGroupJoinByAccessCodeItem
import com.yapp.bol.data.mapper.MapperToDomain.mapperToGetGroupItem
import com.yapp.bol.data.mapper.MapperToDomain.memberListToDomain
import com.yapp.bol.data.mapper.MapperToDomain.newGroupToDomain
import com.yapp.bol.data.mapper.MapperToDomain.toDomain
import com.yapp.bol.data.mapper.MapperToDomain.validToDomain
import com.yapp.bol.domain.model.ApiResult
import com.yapp.bol.domain.model.BaseItem
import com.yapp.bol.domain.model.CheckGroupJoinByAccessCodeItem
import com.yapp.bol.domain.model.GameItem
import com.yapp.bol.domain.model.LoginItem
import com.yapp.bol.domain.model.MemberItems
import com.yapp.bol.domain.model.NewGroupItem
import com.yapp.bol.domain.model.user.group.GetGroupItem
import com.yapp.bol.domain.repository.Repository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.io.File
import javax.inject.Inject

class RepositoryImpl @Inject constructor(
    private val remoteDataSource: RemoteDataSource,
) : Repository {
    override suspend fun login(type: String, token: String): LoginItem? {
        return remoteDataSource.login(type, token).toDomain()
    }

    override fun postFileUpload(file: File): Flow<ApiResult<String>> {
        return remoteDataSource.postFileUpload(file).map {
            it.fileUploadToDomain()
        }
    }

    override fun postCreateGroup(
        name: String,
        description: String,
        organization: String,
        imageUrl: String,
        nickname: String,
    ): Flow<ApiResult<NewGroupItem>> {
        return remoteDataSource.postCreateGroup(name, description, organization, imageUrl, nickname).map {
            it.newGroupToDomain()
        }
    }

    override fun getGameList(groupId: Int): Flow<ApiResult<List<GameItem>>> {
        return remoteDataSource.getGameList(groupId).map {
            it.gameToDomain()
        }
    }

    override fun getValidateNickName(groupId: Int, nickname: String): Flow<ApiResult<Boolean>> {
        return remoteDataSource.getValidateNickName(groupId, nickname).map {
            it.validToDomain()
        }
    }

    override fun getMemberList(
        groupId: Int,
        pageSize: Int,
        cursor: String?,
        nickname: String?,
    ): Flow<ApiResult<MemberItems>> {
        return remoteDataSource.getMemberList(groupId, pageSize, cursor, nickname).map {
            it.memberListToDomain()
        }
    }

    override suspend fun postGuestMember(groupId: Int, nickname: String) {
        remoteDataSource.postGuestMember(groupId, nickname)
    }

    override fun joinGroup(groupId: String, accessCode: String, nickname: String): Flow<ApiResult<BaseItem>> {
        return remoteDataSource.joinGroup(groupId, accessCode, nickname).map { it.mapperToBaseItem() }
    }

    override fun checkGroupJoinAccessCode(
        groupId: String,
        accessCode: String,
    ): Flow<ApiResult<CheckGroupJoinByAccessCodeItem>> {
        return remoteDataSource.checkGroupJoinAccessCode(groupId, accessCode).map {
            it.mapperToCheckGroupJoinByAccessCodeItem()
        }
    }

    override fun getGroupInfo(groupId: Int): Flow<ApiResult<GetGroupItem>> {
        return remoteDataSource.getGroupInfo(groupId).map {
            it.mapperToGetGroupItem()
        }
    }
}
