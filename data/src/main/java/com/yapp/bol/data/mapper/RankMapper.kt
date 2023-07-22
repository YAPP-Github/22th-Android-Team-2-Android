package com.yapp.bol.data.mapper

import com.yapp.bol.data.model.rank.UserRankApiResponse
import com.yapp.bol.domain.model.ApiResult
import com.yapp.bol.domain.model.UserRankItem
import com.yapp.bol.domain.model.UserRankListItem

internal object RankMapper {

    fun ApiResult<UserRankApiResponse>.toDomain(): ApiResult<UserRankListItem> =
        when (this) {
            is ApiResult.Success -> ApiResult.Success(
                UserRankListItem(
                    userRankItemList = data.toDomain()
                )
            )
            is ApiResult.Error -> ApiResult.Error(exception)
        }

    private fun UserRankApiResponse.toDomain(): List<UserRankItem> =
        this.contents.map { userRankDTO ->
            UserRankItem(
                id = userRankDTO.id,
                rank = userRankDTO.rank,
                name = userRankDTO.name,
                winRate = userRankDTO.winRate,
                playCount = userRankDTO.playCount,
            )
        }
}
