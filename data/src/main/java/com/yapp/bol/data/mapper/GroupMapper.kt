package com.yapp.bol.data.mapper

import com.yapp.bol.data.model.group.GroupDetailResponse
import com.yapp.bol.data.model.group.JoinedGroupResponse
import com.yapp.bol.data.model.group.OwnerDTO
import com.yapp.bol.domain.model.ApiResult
import com.yapp.bol.domain.model.GroupDetailItem
import com.yapp.bol.domain.model.JoinedGroupItem
import com.yapp.bol.domain.model.OwnerItem

object GroupMapper {

    fun ApiResult<JoinedGroupResponse>.toDomain(): ApiResult<List<JoinedGroupItem>> =
        when (this) {
            is ApiResult.Success -> ApiResult.Success(this.data.toDomain())
            is ApiResult.Error -> ApiResult.Error(exception)
        }

    private fun JoinedGroupResponse.toDomain(): List<JoinedGroupItem> =
        this.contents.map { joinedGroupDTO ->
            JoinedGroupItem(
                id = joinedGroupDTO.id,
                name = joinedGroupDTO.name,
                description = joinedGroupDTO.description,
                organization = joinedGroupDTO.organization,
                profileImageUrl = joinedGroupDTO.profileImageUrl
            )
        }

    fun ApiResult<GroupDetailResponse>.toDetailItem(): ApiResult<GroupDetailItem> =
        when (this) {
            is ApiResult.Success -> ApiResult.Success(this.data.toItem())
            is ApiResult.Error -> ApiResult.Error(exception)
        }

    private fun GroupDetailResponse.toItem(): GroupDetailItem =
        GroupDetailItem(
            id = this.id,
            name = this.name,
            description = this.description,
            organization = this.organization,
            profileImageUrl = this.profileImageUrl,
            accessCode = this.accessCode,
            memberCount = this.memberCount,
            owner = this.owner.toItem()
        )

    private fun OwnerDTO.toItem(): OwnerItem =
        OwnerItem(
            id = this.id,
            role = this.role,
            nickname = this.nickname,
            level = this.level
        )
}
