package com.jjlee.identity.controller

import com.jjlee.identity.dto.request.CreateUserDto
import com.jjlee.identity.service.TestService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class TestController(
    private val testService: TestService
) {

    // test: k6를 이용해 ULID id를 가진 유저 생성 테스트
    @PostMapping("/test")
    fun test(@RequestBody req: CreateUserDto) : ResponseEntity<String> {
        testService.saveUser(req)
        return ResponseEntity.ok("success")
    }

    // test: k6를 이용해 Long id를 가진 유저 생성 테스트
    @PostMapping("/test1")
    fun testUserLong(@RequestBody req: CreateUserDto) : ResponseEntity<String> {
        testService.saveUser1(req)
        return ResponseEntity.ok("success")
    }

}