package com.yapp.bol.domain.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.yapp.bol.domain.model.ApiResult
import com.yapp.bol.domain.model.GroupItem
import com.yapp.bol.domain.repository.GroupRepository
import com.yapp.bol.domain.utils.GroupPagingConfig.GROUP_LIST_STARTING_PAGE_INDEX
import javax.inject.Inject

class GroupListPagingSource @Inject constructor(
    private val groupRepository: GroupRepository,
    private val keyword: String
) : PagingSource<Int, GroupItem>() {
    override fun getRefreshKey(state: PagingState<Int, GroupItem>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, GroupItem> {
        val page = params.key ?: GROUP_LIST_STARTING_PAGE_INDEX
        return try {
            val response = groupRepository.searchGroup(
                name = keyword,
                page = page,
                pageSize = params.loadSize
            )
            val success = response as ApiResult.Success
            LoadResult.Page(
                data = success.data.groupItemList,
                prevKey = if (page > 1) page - 1 else null,
                nextKey = if (response.data.hasNext) page + 1 else null
            )
        } catch (exception: Exception) {
            return LoadResult.Error(exception)
        }
    }
}
