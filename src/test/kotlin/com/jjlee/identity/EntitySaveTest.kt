package com.jjlee.identity

import com.jjlee.identity.domain.user.userV1.UserV1
import com.jjlee.identity.domain.user.userV1.UserV1Repository
import com.jjlee.identity.domain.user.userV2.UserV2
import com.jjlee.identity.domain.user.userV2.UserV2Repository
import com.jjlee.identity.domain.user.userV3.UserV3
import com.jjlee.identity.domain.user.userV3.UserV3Repository
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest


@SpringBootTest
class EntitySaveTest {

    @Autowired
    private lateinit var userV1Repository: UserV1Repository

    @Autowired
    private lateinit var userV2Repository: UserV2Repository

    @Autowired
    private lateinit var userV3Repository: UserV3Repository

    @Test
    @DisplayName("V1: 유저 엔티티를 생성할 때 Select 쿼리가 발생한다.")
    fun userV1Save() {
        val user = UserV1("test")
        userV1Repository.save(user)
        userV1Repository.flush()
        /*
            Hibernate:
                select
                    u1_0.id,
                    u1_0.name
                from
                    user_v1 u1_0
                where
                    u1_0.id=?
            Hibernate:
                insert
                into
                    user_v1
                    (name,id)
                values
                    (?,?)
         */
    }

    @Test
    @DisplayName("V2: Persistable 인터페이스 구현을 통해 Select 쿼리를 발생시키지 않는다.")
    fun userV2Save() {
        val user = UserV2("test")
        userV2Repository.save(user)
        userV2Repository.flush()
        /*
            Hibernate:
                insert
                into
                    user_v2
                    (name,id)
                values
                    (?,?)
         */
    }

    @Test
    @DisplayName("V2: 막 생성된 Entity의 경우 isNew가 true로 반환되어 Delete Query가 발생하지 않는다.")
    fun userV2SaveAndDelete() {
        val user = UserV2("test")
        userV2Repository.save(user)
        userV2Repository.flush()

        userV2Repository.delete(user)
        userV2Repository.flush()
        /*
            Hibernate:
                insert
                into
                    user_v2
                    (name,id)
                values
                    (?,?)
         */
    }

    @Test
    @DisplayName("V3: @PostPersist와 @PostLoad를 통해 isNew의 상태관리를 하여 Delete Query를 발생시킨다.")
    fun userV3SaveAndDelete() {
        val user = UserV3("test")
        userV3Repository.save(user)
        userV3Repository.flush()

        userV3Repository.delete(user)
        userV3Repository.flush()
        /*
            Hibernate:
                insert
                into
                    user_v3
                    (name,id)
                values
                    (?,?)
            Hibernate:
                select
                    u1_0.id,
                    u1_0.name
                from
                    user_v3 u1_0
                where
                    u1_0.id=?
            Hibernate:
                delete
                from
                    user_v3
                where
                    id=?
         */
    }
}