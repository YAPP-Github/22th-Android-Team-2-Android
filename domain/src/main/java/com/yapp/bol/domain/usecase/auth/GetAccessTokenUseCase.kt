package com.yapp.bol.domain.usecase.auth

import com.yapp.bol.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAccessTokenUseCase @Inject constructor(
    private val repository: AuthRepository,
) {
    operator fun invoke(): Flow<String> = repository.accessToken
}
