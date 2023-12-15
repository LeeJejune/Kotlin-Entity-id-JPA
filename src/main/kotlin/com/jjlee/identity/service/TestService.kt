package com.jjlee.identity.service

import com.jjlee.identity.domain.user.UserRepository
import com.jjlee.identity.domain.user.userLong.UserLongRepository
import com.jjlee.identity.dto.request.CreateUserDto
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class TestService(
    private val userRepository: UserRepository,
    private val userLongRepository: UserLongRepository
) {

    @Transactional
    fun saveUser(req: CreateUserDto) {
        userRepository.save(req.toUserEntity())
    }

    @Transactional
    fun saveUser1(req: CreateUserDto) {
        userLongRepository.save(req.toUserLongEntity())
    }
}