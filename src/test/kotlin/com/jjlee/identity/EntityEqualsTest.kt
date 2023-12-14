package com.jjlee.identity

import com.jjlee.identity.domain.post.Post
import com.jjlee.identity.domain.post.PostRepository
import com.jjlee.identity.domain.post.postBEV1.PostWithBasicEntityV1
import com.jjlee.identity.domain.post.postBEV1.PostWithBasicEntityV1Repository
import com.jjlee.identity.domain.user.User
import com.jjlee.identity.domain.user.UserRepository
import com.jjlee.identity.domain.user.userBEV1.UserWithBasicEntityV1
import com.jjlee.identity.domain.user.userBEV1.UserWithBasicEntityV1Repository
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class EntityEqualsTest {

    @Autowired
    private lateinit var postWithBasicEntityV1Repository: PostWithBasicEntityV1Repository

    @Autowired
    private lateinit var userWithBasicEntityV1Repository: UserWithBasicEntityV1Repository

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var postRepository: PostRepository

    @Test
    @DisplayName("공통 동일성 보장을 위해 Equals 및 HashCode를 구현하였지만, 프록시 객체로 인해 Equals가 false를 반환한다.")
    fun entityEqualsAndHashCodeV1(){
        val user = UserWithBasicEntityV1("이제준")
        val post = PostWithBasicEntityV1("제목", "내용", user)

        userWithBasicEntityV1Repository.save(user)
        postWithBasicEntityV1Repository.save(post)
        userWithBasicEntityV1Repository.flush()
        postWithBasicEntityV1Repository.flush()

        val findPost = postWithBasicEntityV1Repository.findById(post.id).get()

        println(user::class.java)
        println(findPost.writer::class.java)
        /*
            class com.jjlee.identity.domain.user.userBEV1.UserWithBasicEntityV1
            class com.jjlee.identity.domain.user.userBEV1.UserWithBasicEntityV1$HibernateProxy$DsJ1VGeJ
         */
        Assertions.assertTrue(user != findPost.writer)
    }

    @Test
    @DisplayName("공통 동일성 보장을 위해 Equals 함수를 Proxy 객체도 인식할 수 있도록 수정한다. Equals 함수가 true를 반환한다.")
    fun entityEqualsAndHashCode(){
        val user = User("이제준")
        val post = Post("제목", "내용", user)

        userRepository.save(user)
        postRepository.save(post)
        userRepository.flush()
        postRepository.flush()

        val findPost = postRepository.findById(post.id).get()
        Assertions.assertTrue(user == findPost.writer)
    }
}