package com.yapp.bol.data.model.group

data class MemberListResponse(
    val contents: List<MemberDTO>,
    val cursor: String,
    val hasNext: Boolean,
)
