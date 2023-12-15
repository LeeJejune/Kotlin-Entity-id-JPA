## Kotlin 및 ULID를 통해 Entity ID를 관리하는 방법에 대한 고민의 과정 및 결론

해당 글은 Kotlin, JPA 환경에서 ID 생성 전략을 어떻게 가져갈지 고민하고, 그 고민의 과정을 정리한 글입니다.

### 🤔 1. 가지고 있던 고민

- 사내에 처음 도입되는 JPA
- 사내에서 사용중인 Id 전략과 다르게 가져갈건지
- 다르게 가져간다면 어떤 전략을 가져갈건지

이렇게 크게 3가지 고민이 있었다. 그래서 더욱 어떤 전략을 가져가야 효율적으로 개발할 수 있을지 고민했다.

### 🫠 2. 고민의 해결?!
JPA에서 일반적으로 PK로 @Id를 사용해서 정의한다. 이에 따른 타입은 보통 Long Type과 UUID Type이 시장에서 많이 사용되고 있었다.
- Long Type: Auto Increment를 사용해서 DB에서 관리하는 전략
- UUID Type: UUID를 사용해서 Application에서 관리하는 전략

위 두 가지 경우로 크게 나뉘었다. 이에 따른 각각의 장단점이 존재했다.
내가 찾아본 글들과 사내 개발자분들과의 의견을 종합해보면 아래와 같은 장단점이 존재했다.

**Long Type**
- DB의 Auto Increment를 사용할 수 있다.
- 쉽고, 보기가 편하다. Application level에서 관리할 필요가 적어진다.
- Auto Increment를 사용하기 서버가 종류된 후 다시 시작하면, 처음 시작한 값과 달라진다.
- 단일 DB 환경에서 여러 서버가 있을때 중복 Id 발생 가능성이 있다.
- 다음 Id를 유추 할 수 있다. (실제로 네트워크 패킷 까서 다음 것 유추했다는 분도 존재했다.)
- 정렬 가능하다.
- DB 직접 수정이 가능하다. (이는 장점일 수도 있고 단점일 수도 있다.)

**UUID Type**
- Application에서 관리하기 때문에, DB에 의존하지 않는다.
- 충돌 가능성이 매우적다.
- DB 직접수정이 불가능하다.
- Long Type 보다 저장할 때 사용하는 공간이 많다.
- Id를 통해 다음 Id를 유추하기 어렵다.
- 정렬이 어렵다.

이렇게 장단점이 존재했다. 하지만 나는 단순히 장단점만 보고 결정할 순 없었다. 왜냐하면 개발은 혼자하는게 아니라 팀끼리 하는 것이기 때문이다.
그래서 무엇이 더 효율적이고, 무엇이 더 우리 시스템의 상황에서 적합한지 고민했다.

### ❗️ 3. ULID

고민을 하던 중 ULID라는 것을 알게 되었다. ULID는 UUID와 비슷하지만, UUID의 단점들을 해결할 수 있었다.
[또한 내 고민에 대한 어느정도의 방향성을 제시 해주는 글을 보게 되었다.](https://spoqa.github.io/2022/08/16/kotlin-jpa-entity.html)

이 글에서 설명하는 바를 추리면 아래와 같다.

ULID는 UUID와 비슷하지만, UUID의 단점들을 해결할 수 있었다.
- 시간 순 정렬이 된다.
- UUID와 호환이 가능하다.
- [ULID 라이브러리 사용을 통해 동시적인 Entity 생성 시 다음에 생성되는 Entity의 밀리초를 1 증가시켜준다.](https://github.com/f4b6a3/ulid-creator)

위 같은 장점이 있어, 이를 기반으로 팀원들과 함께 Long Type과 ULID Type 중 ULID를 선택했다.

### ⚙️ 4. ULID를 사용하기 위한 작업 및 JPA Save 과정.
JPA의 Save 과정은 아래와 같다.
```java
@Transactional
@Override
public <S extends T> S save(S entity) {

  Assert.notNull(entity, "Entity must not be null.");

  if (entityInformation.isNew(entity)) {
    em.persist(entity);
    return entity;
  } else {
    return em.merge(entity);
  }
}
```
isNew가 True면 즉, 새롭게 생성된 Entity로 판단하여 persist, 아니면 merge를 한다.
이를 직접적으로 신규 생성 여부를 결정하는 메소드는 아래와 같다. 
```java
public boolean isNew(T entity) {

  ID id = getId(entity);
  Class<ID> idType = getIdType();

  if (!idType.isPrimitive()) {
    return id == null;
  }

  if (id instanceof Number) {
    return ((Number) id).longValue() == 0L;
  }

  throw new IllegalArgumentException(String.format("Unsupported primitive id type %s", idType));
}
```
즉, 원시 타입이 아닌 경우 null로 판단한다. 어떻게 생각하면 PK는 not null인데, 조금 어색한 부분이 있다.
Long의 영속화 되기 전까지 모두 0으로 가지고 있다. 그렇다고 이게 모두 같은 엔티티라는 것은 아니다.

이 두 가지 부분이 어색했다. 그래서 영속화 전에 같이 생성될 수 있게 구성할 수 있다.
``` kotlin
MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
abstract class BasicEntity : Persistable<UUID> {
    @Id
    @Column(name = "id")
    @JdbcTypeCode(value = SqlTypes.VARCHAR)
    private val id: UUID = UlidCreator.getMonotonicUlid().toUuid()

    @CreatedDate
    @Column(nullable = false)
    var createdAt: LocalDateTime = LocalDateTime.now()
        protected set

    @Transient
    private var _isNew = true

    override fun getId(): UUID = id

    override fun isNew(): Boolean = _isNew

    override fun equals(other: Any?): Boolean {
        if (other == null) {
            return false
        }

        if (other !is HibernateProxy && this::class != other::class) {
            return false
        }

        return id == getIdentifier(other)
    }

    private fun getIdentifier(obj: Any): Serializable {
        return if (obj is HibernateProxy) {
            obj.hibernateLazyInitializer.identifier as Serializable
        } else {
            (obj as BasicEntity).id
        }
    }

    override fun hashCode() = Objects.hashCode(id)

    @PostPersist
    @PostLoad
    protected fun load() {
        _isNew = false
    }
}
```
1. 영속화 전에 Id를 생성하기 때문에 Persistable을 상속받아 isNew와 getId를 구현한다. 이를 통해 merge가 호출되고, Select 쿼리가 나가는 것을 방지한다.
2. 영속화 한 후, isNew를 false로 변경한다. @PostPersist와 @PostLoad를 통해 각각 영속화 이후와 영속화한 데이터를 조회한 이후에 함수가 실행되도록 할 수 있다.
3. 상태 관리를 위한 IsNew는 @Transient를 통해 영속화 되지 않도록 한다.
4. 공통 동일성 보장을 위해 equals와 hashCode를 구현한다. 이는 HibernateProxy를 통해 Lazy Loading을 할 때, 동일성을 보장하기 위함이다.

**위 과정들을 Test를 통해 확인해보았다.**
- [EntitySaveTest](https://github.com/LeeJejune/Kotlin-Entity-id-JPA/blob/main/src/test/kotlin/com/jjlee/identity/EntitySaveTest.kt) (1~3번 과정 테스트 코드.)
- [EntityEqualsTest](https://github.com/LeeJejune/Kotlin-Entity-id-JPA/blob/main/src/test/kotlin/com/jjlee/identity/EntityEqualsTest.kt) (4번 과정 테스트 코드.)

위와 같은 테스트 코드를 통해 확인해보았고, 최종적으로 ULID를 사용하기로 결정했다.

## 🤔 5. 직접 ULID를 사용하면서 생긴 고민
사내에서는 ULID를 기반으로 Entity 생성전략을 꾸렸고, 이를 토대로 개발해나갔다.
하지만 개발을 하면서 생긴 고민이 있었다. 바로 ULID를 사용하면서 생기는 고민들이었다.

**아래와 같은 고민이 있었다.**
1. 꼭 ULID여야 했나? Long Type이어도 큰 문제가 없었지 않았을까? 
2. 우리의 서비스가 Long의 단점이 발생할 수 있는 상황인가?
3. 과연 정말 생성 시간의 차이가 클까?

너무너무 궁금해 곧바로 이에대한 고민을 챕터 채널에 털어 놓았다.

1, 2번의 경우 우리의 기술적 배경이 존재했다. 아래와 같다.
- 이전에 DB의 데이터를 직접 추가해 오류가 발생했던 히스토리가 존재했다.
- 우리의 서비스 중 Long을 사용 이시 다음 Id를 유추할 수 있는 부분이 존재했다.
- createdAt을 통한 정렬이 필요한 상황이 많이 존재했다. (이는 ULID를 통해 id 정렬로 해결했다.)

3번의 경우는 k6를 통해 save 테스트를 진행했다. (vus 10000, duration 30s)
- ULID 사용 시 5.31s
- Long 사용 시 5.29s

크게 차이가 나지 않았다. (테스트 환경이나, 테스트 방법에 따라 다를 수 있다.) 이는 결국 큰 차이가 없다는 것을 의미했다.
또한, 사내 서비스의 프로덕션 및 테스트 환경에서 복잡한 쿼리가 필요한 조회 및 생성의 경우 큰 문제 없이 잘 동작했다.

그렇기에 ULID를 사용한 것이 타당하다고 묻는다면 그렇다고 볼 수 있다.

## 👀 6. 결론
내가 내린 결론은 "정답이 없다" 이다. 결국 해당 상황에 적합한 것을 선택하는 것이 좋고, 그것을 선택하는 기술적 배경을 무시할 수 없는 것 같다.

실제로 Long Type을 사용하는 것이 더 효율적이고 적합한 상황도 존재할 것이다. 또한, ULID를 사용하는 것이 더 효율적이고 적합한 상황도 존재할 것이다. 그리고 이는 개발자의 성향에 따라 달라질 수 있다고 생각한다.


뿐만 아니라 이외에도 다양한 전략(TSID 등)이 존재한다. 결국 해당 상황에서 필요한 기술을 선택하는 것이 개발자의 중요한 역량인 것 같다고 느꼈다.

결론적으로 이러한 고민을 하는 것이 재미있었다. 이런 질문을 던지는 것도 좋아하고 새로운 것에 대한 호기심도 많다.
이러한 과정을 통해 더욱 성장했다고 믿어 의심치 않다.




