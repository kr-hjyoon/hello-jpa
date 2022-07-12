 
# 설치시 주의사항
  * H2 database의 경우 설치한 버젼과 클라이언트 버젼을 맞춘다.
    * pom.xml 최신패키지로 변경 필요 (오류발생) 

# Entity Manager
  * 여러 쓰레드간에서 공유하면 안됨
  * Entity Manger Factory는 하나만 생성해서 애플리케이션 전체에서 공유
  * reqquest 올때마나 Entity Manager를 만든다 
  * JPA 모든 데이터 변경은 Transcation안에서 실행
  * Persistence Manger 1: N Entity Manger  

# JPQL
  * 테이블이 아닌 객체를 대상으로 검색하는 객체 지향 쿼리
  * SQL을 추상화해서 특정 데이터베이스 SQL에 의존X
  * JPQL을 한마디로 정의하면 객체 지향 SQL

# Entity Life Cycle  !!! 매우 중요 
  * new/transient: 비영속 상태 
  * 속성 컨텍스트와 전혀 관계가 없는 새로운 상태
  * managed: 영속 상태   
    * 영속성 컨텍스트에 관리되는 상태 , em.persist(객체) 하는 순간부터 영속상태, DB에 저장되는건 아님
    * 시제 DB에 저장되는건  commit이 일어나는 순간 DB에 query가 실행되어 반영된다.
  * detached : 준영속 상태
    * 영속성 컨텍스트에 저장되었다가 분리된 상태
    * em.detach(entity) 특정 엔티티만 준영속 상태로 전환
    * em.clear()   영속성 컨텍스트를 완전히 초기화
    * em.close()  영속성 컨텍스트를 종료
  * removed: 삭제 상태 


## 영속성 컨텐스트의 이점 
  * 1차 캐시 ( 성능, 운영에서 체감되지는 않음 한 request에서 동일한 객체 접근할일이 별루 많지 않음  )
    * 1차캐시에 없으면 DB에서 조회하여 반환함 
  * 동일성(identity) 보장  ( repeatable read 등급의 격리수준을 app 차원에서 제공)
  * 트랜잭션을 지원하는 쓰기 지연(transactional write-behind)
    * 쓰기지연 저장소에 쌓아두고 commit 하는 시점에 실제 write됨
    * hibernate.jdbc.batch_size 설정으로 스로틀링 가능
  * 변경 감지(Dirty Checking)
    * update 시에 별도로 persist 하지 않아도 된다.
  * 지연 로딩(Lazy Loading)

## flush 란
  * 영속성 컨텍스트의 변경내용을 DB에 반영한는 것
  * 직접 호출하는법 : em.flush()
  * commit 하는 순간 자동으로 flush()가 일어남.
  * JPQL query를 실행할때 자동으로 flush() 일어남 

## 매핑 어노테이션 정리 
  * @Column
    * 컬럼 매핑 
  * @Temporal
    * 날짜 타입 매핑
      * Date, LocalDate LocalDateTime과 연관 
  * @Enumerated
    * enum 타입 매핑
      * 속성:  EnumType.STRING 사용 권고, 확장에 열려있기 때문에 
  * @Lob
    * BLOB, CLOB 매핑
  * @Transient
    * 특정 필드를 컬럼에 매핑하지 않음(매핑 무시)

## Entity 매핑 
  * 객체와 테이블 매핑: @Entity, @Table
  * 필드와 컬럼 매핑: @Column
  * 기본 키 매핑: @Id
  * 연관관계 매핑: @ManyToOne,@JoinColumn


## 기본 키 매핑 방법
  * 직접 할당: @Id만 사용
  * 자동 생성(@GeneratedValue)
    * IDENTITY: 데이터베이스에 위임, MYSQL
    * SEQUENCE: 데이터베이스 시퀀스 오브젝트 사용, ORACLE
      * @SequenceGenerator 필요
    * TABLE: 키 생성용 테이블 사용, 모든 DB에서 사용
      * @TableGenerator 필요
    * AUTO: 방언에 따라 자동 지정, 기본값

## IDENTITY 전략 - 특징
  * 기본 키 생성을 데이터베이스에 위임
  * 주로 MySQL, PostgreSQL, SQL Server, DB2에서 사용
    * (예: MySQL의 AUTO_ INCREMENT)
  * JPA는 보통 트랜잭션 커밋 시점에 INSERT SQL 실행
  * JPA는 보통 트랜잭션 커밋 시점에 INSERT SQL 실행
    * AUTO_ INCREMENT는 데이터베이스에 INSERT SQL을 실행한 이후에 ID 값을 알 수 있음
    * IDENTITY 전략은 em.persist() 시점에 즉시 INSERT SQL 실행하고 DB에서 식별자를 조회
    * 단점: 모아서 Insert하는게 안됨

## SEQUENCE 전략 - 특징
  * 데이터베이스 시퀀스는 유일한 값을 순서대로 생성하는 특별한 데이터베이스 오브젝트(예: 오라클 시퀀스)
  * 오라클, PostgreSQL, DB2, H2 데이터베이스에서 사용

## 권장하는 식별자 (PK) 전략
  * 기본 키 제약 조건: null 아님, 유일, 변하면 안된다.
  * 미래까지 이 조건을 만족하는 자연키는 찾기 어렵다. 대리키(대리키)를 사용하자.
  * 예를 들어 주민등록번호도 기본 키로 적절하기 않다.
  * 권장: Long형 + 대체키 + 키 생성전략 사용

## 실전 모델 
  * 회원 - 주문 (1:N)    MEMBER , ORDER , ORDER_ITEM, ITEM
  * 주문 - 주문상품(  1:N )
  * 주문상품- 상품 (N: 1)


## Getter /Setter 
  * Setter 보다는 생성자 입력방식이 유지보수에 좋은 의견 ( Setter를 통해 관리가 안되는 Set이 많이 있을수도 )

## 연관관계 매핑시 고려사항 3가지
  * 다중성
  * 단방향, 양방향(연관관계주인!!)
  * 연관관계의 주인

## 다중성
  * 다대일: @ManyToOne (가장많이 사용됨)
  * 일대다: @OneToMany (권장하지 않음)
    * DB입장에서는 무조껀 N(다)쪽에 외래키가 들어간다
    * 연관관계 주인임에도 외래키를 보유하고 있지 않는다. 
    * 객체와 테이블 차이 때문에 반대편 테이블의 외래키를 관리해야 한다. 
    * @JoinColumn 곡 사용해야함.  그렇지 않아면 JoinTable 전략으로 작동한다. 
    * @JoinColumn(name="TEAM_ID",insertable = false, updateable=false) 을 사용한다. 주인이 아니므로 
    * JPA 스펙상 공식적으로 존재 X 
  * 일대일: @OneToOne
  * 다대다: @ManyToMany (사용하면 안됨)

## 단방향, 양방향
  * 테이블
     * 외래 키 하나로 양쪽 조인 가능
     * 사실 방향이라는 개념이 없음
  * 객체
    * 참조용 필드가 있는 쪽으로만 참조 가능
    * 한쪽만 참조하면 단방향
    * 양쪽이 서로 참조하면 양방향

## 양방향 연관관계와 연관관계의 주인 !!! 매우 중요한 부분 
  * DB방식의 FK는 양방향으로 연관관계 설정이 가능 
  * 객체 에서는  Member에서는 team객체를 보유하고 있으나 Team에서는 member로 접근할수 없다
  * Team객체에 list members 속성을 추가하여 @OneToMany어노테이션으로 정의 하고 
  * 어노테이셔의 설정은 mappedBy="team"이라고 정의 (연결된 member객체의 연결 객체의 변수명 정의 )
    
## mappedBy 
  * 양방향 연관관계는 결국 단방향 연관관계를 2개를 설정하는 것 (VS 테이블은 FK 1개임 )

## 연관관계의 주인 
  * 테이블은 외래 키 하나로 두 테이블이 연관관계를 맺음
  * 객체 양방향 관계는 A->B, B->A 처럼 참조가 2군데
  * 객체 양방향 관계는 참조가 2군데 있음. 둘중 테이블의 외래 키를 관리할 곳을 지정해야함
  * 연관관계의 주인: 외래 키를 관리하는 참조 ( 등록, 수정 가능)
  * 주인의 반대편: 외래 키에 영향을 주지 않음, 단순 조회만 가능(읽기만 가능)
  * 주인은 mappedBy 속성을 사용하지 X
  * 주인이 아니면 mappedBy 속성으로 주인 지정

## 누구를 주인으로 할꺼냐 @@@ IMPORTANT 
  * 외래키가 있는곳을 주인으로  
    * Team.id = PK  (주인 X )
    * Member.team_iod = FK  (주인 O)
    
## 양방향 매핑시 가장 많이 하는 실수
  * 연관관계의 주인에 값을 입력하지 않음 
  * Member 와  team 이 있고 Member가 OWNER(주인)인 상황이라면  
    * member.setTeam(team) 으로 입력해야 함 DB에 데이터가 맞게 생김  
    * team.getMembers.add(member)만  처리하는ㄴ건  제대로 DB에 데이터가 생기지 않음  
  * BUT 양쪽에 다 입력해주는게 정답이다.
    * 1차 캐시에 반영이 안됨(영속성 컨텍스트 )
    * 양방향 매핓시에 무한 루프 조심 
      * toString(), lombok, JSON 생성 라이브러리 
    * 매번 일일이 양방향 셋팅을 해주는건 사람이 실수로 인해 완벽하지 않는다 
      * Member.setTeam에  연관관계 편의 메소드를 생성하자 !!
      ```java
      public void changeTeam(Team team) {  // 순수 setter가 아니므로 이름을 setTeam > changeTeam으로 바꿈 
        this.team = team;
        team.getMembers().add(this);  // this는 member를 의미
      }
      ```
    * 단방향 매핑으로도 충분 / 실무에서 역으로 JPQL등을 통해 역탐색이 필요한 경우 양방향 구성 필요
    
## 객체 입장에서는 연관관계를 잘 끊어내야 한다.

##  일대일 관계 
   * 그 반대도 일대일 
   * 외래키가 아무곳에나 선택 가능
   * Member - Locker  
     * Member.locker  or  Locker.member
   * 주인은 외래키가 있는 곳 
   * Question  외래키가 어디에 있는게 좋을가?
     * Member에 있는 경우  (주테이블에 FK가 있는 경우 )
       * 이후에 멤버가 여러개의 locker를 가질수 있다는 변경이 들어올때 수용안됨
       * 어플 특성상 Member에서 Locker를 접근할 경우는 많은데 유리함 (성능,코드량, 멤버가 locker를 보유여부를 바로 알수 있다)
     * Locker에 있는 경우 (대상테이블에 FK가 있는 경우 )
       * 이후에 멤버가 여러개의 locker를 가질수 있다는 변경이 들어올때 됨 
       * 어플 특성상 Member에서 Locker를 접근할 경우는 많은데 불리함
       * 프로시 사용시에 지연로딩을 설정해도 항상 즉시 로딩됨 : fetchJoin 등 방법이 있긴 함. 

## 다대다 
  * 실무에서 쓰면 안됨

## 상속관계 매핑
  * 슈퍼타입/서브타입 논리 모델을 실제 물리 모델로 구현하는 방법
    1. 각각 테이블로 변환 -> 조인 전략
       * 공통 테이블 부모 테이블로 , 다른 테이블로 자식 테이블로 하고 자식은 PK=FK 로 설정
       * 부모테일블의 자식의 Type을 정하는 필드를 보유
     2. 통합 테이블로 변환 -> 단일 테이블 전략
       * 작식의 다른 컬럼을 모두 하나의 테이블에 다 구성하는 장법( 성능이 좋고, 구조가 단순한 이점 )
    3. 서브타입 테이블로 변환 -> 구현 클래스마다 테이블 전략
       * 공통 테이블을 없애고  sub로 다 분리해서 테이블 생성하는 방법
       * 부모클래스는 abstract class로 만들어야 함 
       * 부모 클래스를 조회시에 자식테이블 전체를 union으로 처리한다.
  * join 전략을 기본으로 하고  정말 단순한경우 단일 테이블 전략도 고려 (굳이 복잡하게 할필요없을때 )
 
## 상속관계 주요 어노테이션
  * @Inheritance(strategy=InheritanceType.XXX)
  * JOINED: 조인 전략
  * SINGLE_TABLE: 단일 테이블 전략
  * TABLE_PER_CLASS: 구현 클래스마다 테이블 전략
  * @DiscriminatorColumn(name=“DTYPE”)
  * @DiscriminatorValue(“XXX”)

# MappedSuperclass
  * @MappedSuperclass
  * DB는 분리되어있지만 객체 입장에서 공통 속성을 분리해서 관리하고 싶을때 사용
  * 상속관계 매핑X
  * 엔티티X, 테이블과 매핑X
  * 부모 클래스를 상속 받는 자식 클래스에 매핑 정보만 제공
  * 조회, 검색 불가(em.find(BaseEntity) 불가)
  * 직접 생성해서 사용할 일이 없으므로 추상 클래스 권장
  * 수정일/수정자/생성일/생성자 같은데 사용하면 유용 

# 실무에서 상속관계를 쓰는가 
  * 복잡도를 관리 하는 비용이 높아짐
    * 데이터가 많아지고 파티셔닝도 들어가고 그러면 복잡한 테이블 구조가 관리가 어려울수도
  * 장점과 단점의 tradeoff를 넘어서는 시점에 시스템을 개비 


# 프록시 기초
  * JPA 프록시란/  데이터 베이스 조회를 미루는 가짜 데이터를 제공 하는것 
    * em.find() vs em.getReference()
    * em.find(): 데이터베이스를 통해서 실제 엔티티 객체 조회
    * em.getReference(): 데이터베이스 조회를 미루는 가짜 프록시 객체를 반환 
    * 엔티티 객체 조회
# 프록시의 특징
  * 프록시 객체는 처음 사용할 때 한 번만 초기화
  * 프록시 객체를 초기화 할 때, 프록시 객체가 실제 엔티티로 바뀌는 것은 아님
    * 초기화되면 프록시 객체를 통해서 실제 엔티티에 접근 가능
  * 프록시 객체는 원본 엔티티를 상속받음, 따라서 타입 체크시 주의해야함 
    * (== 비교 실패, 대신 instance of 사용: 매우 중요 !!!!! )
  * 영속성 컨텍스트에 찾는 엔티티가 이미 있으면 em.getReference()를 호출해도 실제 엔티티 반환
  * 영속성 컨텍스트에 찾는 프록시가 이미 있으면 em.getFind()를 호출해도 프록시를 반환
  * 영속성 컨텍스트의 도움을 받을 수 없는 준영속 상태일 때, 프록시를 초기화하면문제 발생
    * (하이버네이트는 org.hibernate.LazyInitializationException 예외를 터트림)

## 프록시 확인
  * 프록시 인스턴스의 초기화 여부 확인
    * emf.getPersistenceUnitUtil.isLoaded(Object entity)
  * 프록시 클래스 확인 방법
    * entity.getClass().getName() 출력(..javasist.. or HibernateProxy…)
  * 프록시 강제 초기화
    * org.hibernate.Hibernate.initialize(entity);
  * 참고: JPA 표준은 강제 초기화 없음
    * 강제 호출: member.getName()
    * 하이버네이터의 경우  Hibernate.initalize(엔티티); 초기화 가능 

## 즉시 로딩 (Eager Loading) VS 지연 로딩 (LAZY Loading ) 
  * 연관관계 정의에 fetch = FetchType.LAZY OR FetchType.EAGER 
    * @ManyToOne(fetch=FetchType.LAZY) 
    * @ManyToOne(fetch=FetchType.EAGER) 

## 프록시와 즉시로딩 주의
  * 가급적 지연 로딩만 사용(특히 실무에서) !!!!!!!!!
  * 즉시 로딩을 적용하면 예상하지 못한 SQL이 발생
  * 즉시 로딩은 JPQL에서 N+1 문제를 일으킨다.
    * 해결방법: 모두 LAZY로 상태에서   
      * FetchJoin   
      * e.g) (select m form Member m join fetch m.team)
      * EntityGraph (어노테이션)
      * BatchSize (다르게 푸는 )
  * @ManyToOne, @OneToOne은 기본이 즉시 로딩
    * -> LAZY로 설정
  * @OneToMany, @ManyToMany는 기본이 지연 로딩 

## 지연 로딩 활용 - 실무
  * 모든 연관관계에 지연 로딩을 사용해라!
  * 실무에서 즉시 로딩을 사용하지 마라!
  * JPQL fetch 조인이나, 엔티티 그래프 기능을 사용해라!(뒤에서 설명)
  * 즉시 로딩은 상상하지 못한 쿼리가 나간다.

## 영속성 전이: CASCADE
  * 영속성 전이: CASCADE
    * 특정 엔티티를 영속 상태로 만들 때 연관된 엔티티도 함께 영속
  * 상태로 만들도 싶을 때
    * 예: 부모 엔티티를 저장할 때 자식 엔티티도 함께 저장
    
  * e.g) 
    * @OneToMay(mappedBy="parent", cascade=All)
    * @OneToMany(mappedBy="parent", cascade=CascadeType.PERSIST)
  
## 영속성 전이: CASCADE - 주의!
  * 영속성 전이는 연관관계를 매핑하는 것과 아무 관련이 없음
  * 엔티티를 영속화할 때 연관된 엔티티도 함께 영속화하는 편리함을 제공할 뿐0
  * 단일 Entity (부모)에 자식이 모두 종속적일때만 써야 한다. 

## CASCADE의 종류
  * ALL: 모두 적용  !!!
  * PERSIST: 영속  !!!
  * REMOVE: 삭제
  * MERGE: 병합
  * REFRESH: REFRESH
  * DETACH: DETACH

## 고아 객체(ORPHAN)
  * 고아 객체 제거: 부모 엔티티와 연관관계가 끊어진 자식 엔티티를 자동으로 삭제
  * orphanRemoval = true
    * @OneToMay(mappedBy="parent", cascade=All, orphanRemoval = true)

  ```java
  Parent parent1 = em.find(Parent.class, id);
  parent1.getChildren().remove(0);
  //자식 엔티티를 컬렉션에서 제거
  ```
  * DELETE FROM CHILD WHERE ID=?

## 고아 객체 - 주의
  * 참조가 제거된 엔티티는 다른 곳에서 참조하지 않는 고아 객체로 보고 삭제하는 기능
  * 참조하는 곳이 하나일 때 사용해야함!
  * 특정 엔티티가 개인 소유할 때 사용
  * @OneToOne, @OneToMany만 가능

  * 참고: 개념적으로 부모를 제거하면 자식은 고아가 된다. 
  * 따라서 고아 객체 제거 기능을 활성화 하면,부모를 제거할 때 자식도 함께제거된다.
  * 이것은 CascadeType.REMOVE처럼 동작한다
  
## 영속성 전이 + 고아 객체, 생명주기
  * CascadeType.ALL + orphanRemovel=true
  * 스스로 생명주기를 관리하는 엔티티는 em.persist()로 영속화,
    * em.remove()로 제거
  * 두 옵션을 모두 활성화 하면 부모 엔티티를 통해서 자식의 생명
    * 주기를 관리할 수 있음
  * 도메인 주도 설계(DDD)의 Aggregate Root개념을 구현할 때 유용

## JPA의 데이터 타입 분류
  * 엔티티 타입
    * @Entity로 정의하는 객체
    * 데이터가 변해도 식별자로 지속해서 추적 가능
    * 예) 회원 엔티티의 키나 나이 값을 변경해도 식별자로 인식 가능
  * 값 타입
    * int, Integer, String처럼 단순히 값으로 사용하는 자바 기본 타입이나 객체
    * 식별자가 없고 값만 있으므로 변경시 추적 불가
    * 예) 숫자 100을 200으로 변경하면 완전히 다른 값으로 대체

## JPA의 데이터 타입 : 값타입
  * 기본값 타입 : 생명주기를 엔티티에 의존 , 값타입은 공유 X > SideEffect 없음
    * 자바 기본 타입(int, double)
    * 래퍼 클래스(Integer, Long)   > 레퍼런스이지만 변경이 안디므로 Side Feect 없음
    * String
  * 임베디드 타입(embedded type, 복합 값 타입)
  * 컬렉션 값 타입(collection value type)

# 임베디드 타입 
  * 새로운 값 타입을 직접 정의할 수 있음
  * JPA는 임베디드 타입(embedded type)이라 함
  * 주로 기본 값 타입을 모아서 만들어서 복합 값 타입이라고도 함
  * int, String과 같은 값 타입 !!!!

# 임베디드 타입 사용법
  * @Embeddable: 값 타입을 정의하는 곳에 표시
  * @Embedded: 값 타입을 사용하는 곳에 표시
  * 기본 생성자 필수
  
# 임베디드 타입의 장점
  * 재사용 
  * 높은 응집도
  * Period.isWork()처럼 해당 값 타입만 사용하는 의미 있는 메소드를 만들 수 있음
  * 임베디드 타입을 포함한 모든 값 타입은, 값 타입을 소유한 엔티티에 생명주기를 의존함

# 임베디드 타입과 테이블 매핑
  * 임베디드 타입은 엔티티의 값일 뿐이다.
  * 임베디드 타입을 사용하기 전과 후에 매핑하는 테이블은 같다.
  * 객체와 테이블을 아주 세밀하게(find-grained) 매핑하는 것이 가능

# @AttributeOverride: 속성 재정의
  * 한 엔티티에서 같은 값 타입을 사용하면?
  * 컬럼 명이 중복됨
  * @AttributeOverrides, @AttributeOverride를 사용해서 컬러 명 속성을 재정의

# 값 타입과 불변 객체
  * 값 타입은 복잡한 객체 세상을 조금이라도 단순화하려고 만든 개념이다. 
  * 따라서 값 타입은 단순하고 안전하게 다룰 수 있어야 한다

# 값 타입 공유 참조
  * 임베디드 타입 같은 값 타입을 여러 엔티티에서 공유하면 위험함
  * 부작용(side effect) 발생  -

# 값 타입 복사
  * 값 타입의 실제 인스턴스인 값을 공유하는 것은 위험
  * 대신 값(인스턴스)를 복사해서 사용  
  ```java
  Address newAdress= new Address (address.getCity(), address.getZipcode()); // 객체를 복사하여 사용
   
  ```
   

# 객체 타입의 한계
  * 항상 값을 복사해서 사용하면 공유 참조로 인해 발생하는 부작용을 피할 수 있다.
  * 문제는 임베디드 타입처럼 직접 정의한 값 타입은 자바의 기본타입이 아니라 객체 타입이다.
  * 자바 기본 타입에 값을 대입하면 값을 복사한다.
  * 객체 타입은 참조 값을 직접 대입하는 것을 막을 방법이 없다.
  * 객체의 공유 참조는 피할 수 없다.

  ```java
  Address a = new Address(“Old”);
  Address b = a; //객체 타입은 참조를 전달
  b. setCity(“New”)
        
  // 객체 값을 복사하는 방식으로 해야 한다. 
  Address newAdress= new Address (address.getCity(), address.getZipcode()); // 객체를 복사하여 사용
  ```

## 불변 객체
  * 객체 타입을 수정할 수 없게 만들면 부작용을 원천 차단
  * 값 타입은 불변 객체(immutable object)로 설계해야함
  * 불변 객체: 생성 시점 이후 절대 값을 변경할 수 없는 객체
  * 생성자로만 값을 설정하고 수정자(Setter)를 만들지 않으면 됨 !!!!!
  * 참고: Integer, String은 자바가 제공하는 대표적인 불변 객체

# 값 타입의 비교
  * 동일성(identity) 비교: 인스턴스의 참조 값을 비교, == 사용
  * 동등성(equivalence) 비교: 인스턴스의 값을 비교, equals()사용
  * 값 타입은 a.equals(b)를 사용해서 동등성 비교를 해야 함
  * 값 타입의 equals() 메소드를 적절하게 재정의(주로 모든 필드사용)

# Immutable Object의 장단점
  * 장점
    * 객체에 대한 신뢰도가 높아집니다. 
    * 객체가 한번 생성되어서 그게 변하지 않는다면 transaction 내에서 그 객체가 변하지 않기에 우리가 믿고 쓸 수 있기 때문입니다.
    * 생성자, 접근메소드에 대한 방어 복사가 필요없습니다.
    * 멀티스레드 환경에서 동기화 처리없이 객체를 공유할 수 있습니다.
  * 단점
    * 객체가 가지는 값마다 새로운 객체가 필요합니다. 
    * 따라서 메모리 누수와 새로운 객체를 계속 생성해야하기 때문에 성능저하를 발생시킬 수 있습니다.

# 값 타입 컬렉션
  * 값 타입을 하나 이상 저장할 때 사용함 
  * @ElementCollection, @CollectionTable 사용
  * 데이터베이스는 컬렉션을 같은 테이블에 저장할 수 없다.
  * 컬렉션을 저장하기 위한 별도의 테이블이 필요함
  * 라이프 사이클은 Entity를 따라간다. 별도로 persist 할필요 없다. 
  * 값 타입 컬렉션도 지연 로딩 전략 사용
  * 값 타입 컬렉션은 영속성 전에(Cascade) + 고아 객체 제거 기능을 필수로 가진다고 볼 수 있다.

# 값 타입 컬렉션의 제약사항
  * 값 타입은 엔티티와 다르게 식별자 개념이 없다.
  * 값은 변경하면 추적이 어렵다.
  * 값 타입 컬렉션에 변경 사항이 발생하면, 주인 엔티티와 연관된 모든 데이터를 삭제하고, 값 타입 컬렉션에 있는 현재 값을 모두 다시 저장한다.
  * 값 타입 컬렉션을 매핑하는 테이블은 모든 컬럼을 묶어서 기본키를 구성해야 함: null 입력X, 중복 저장X

## 값 타입 컬렉션 대안
  * 실무에서는 상황에 따라 값 타입 컬렉션 대신에 일대다 관계를고려
  * 일대다 관계를 위한 엔티티를 만들고, 여기에서 값 타입을 사용
  * 영속성 전이(Cascade) + 고아 객체 제거를 사용해서 값 타입 컬렉션 처럼 사용
  * EX) AddressEntity

# 값 타입 컬렉션은 언제 쓰나?
  * 값 타입은 정말 값 타입이라 판단될 때만 사용 (정말 단순한 값)
  * 엔티티와 값 타입을 혼동해서 엔티티를 값 타입으로 만들면 안됨
  * 식별자가 필요하고, 지속해서 값을 추적, 변경해야 한다면 그것은 값 타입이 아닌 엔티티

# 값타입 매핑(실정)
  * 객체에 @Embeddable 어노테이션  
  * 필드생성 , 
  * 필드별 getter setter 생성 ( setter는 private로 )
  * equals 와 hasCode  Override ( IDE 자동 생성 코드 이용,)
    * Use getters during code genertaion 체크  
      * JPA일때는  getter가 아닌 필드 직접 접근하면 문제 될수있다. proxy 사용시 데이터 접근 X
  * Entity 의  member변수에 @Embedded 로 지정 

# JPA는 다양한 쿼리 방법을 지원
  * JPQL
  * JPA Criteria
  * QueryDSL
  * 네이티브 SQL
    * 표준 sql을 벗어나서 , 벤더 종속적인 문법 등을 사용할때 필요하다. 
  * JDBC API 직접 사용, MyBatis, SpringJdbcTemplate 함께사용

# JPQL ( Java Persistence query language , 객체지향 쿼리 언어 )
  * JPA를 사용하면 엔티티 객체를 중심으로 개발
  * 문제는 검색 쿼리
  * 검색을 할 때도 테이블이 아닌 엔티티 객체를 대상으로 검색
  * 모든 DB 데이터를 객체로 변환해서 검색하는 것은 불가능
  * 애플리케이션이 필요한 데이터만 DB에서 불러오려면 결국 검색 조건이 포함된 SQL이 필요

# JPQL #2
  * JPA는 SQL을 추상화한 JPQL이라는 객체 지향 쿼리 언어 제공
  * SQL과 문법 유사, SELECT, FROM, WHERE, GROUP BY,HAVING, JOIN 지원
  * JPQL은 엔티티 객체를 대상으로 쿼리
  * SQL은 데이터베이스 테이블을 대상으로 쿼리

# JPQL #3
  * 테이블이 아닌 객체를 대상으로 검색하는 객체 지향 쿼리
  * SQL을 추상화해서 특정 데이터베이스 SQL에 의존X
  * JPQL을 한마디로 정의하면 객체 지향 SQL

# Criteria 소개
  * 문자가 아닌 자바코드로 JPQL을 작성할 수 있음
  * JPQL 빌더 역할
  * JPA 공식 기능
  * 단점: 너무 복잡하고 실용성이 없다.
  *  Criteria 대신에 QueryDSL 사용 권장

# QueryDSL 소개
  ```java
    //JPQL
    //select m from Member m where m.age > 18

    JPAFactoryQuery query = new JPAQueryFactory(em);
    QMember m = QMember.member;
    List<Member> list = 
        query.selectFrom(m)
            .where(m.age.gt(18))
            .orderBy(m.name.desc())
            .fetch();
  ```
# QueryDSL 소개
  * 문자가 아닌 자바코드로 JPQL을 작성할 수 있음
  * JPQL 빌더 역할
  * 컴파일 시점에 문법 오류를 찾을 수 있음
  * 동적쿼리 작성 편리함
  * 단순하고 쉬움
  * 실무 사용 권장
 
# 네이티브 SQL 소개
  * JPA가 제공하는 SQL을 직접 사용하는 기능
  * JPQL로 해결할 수 없는 특정 데이터베이스에 의존적인 기능
  * 예) 오라클 CONNECT BY, 특정 DB만 사용하는 SQL 힌트
  * 영속성 관리가 됨 ( 쿼리 실행시 flush 됨 )
  ```java
    String sql = “SELECT ID, AGE, TEAM_ID, NAME FROM MEMBER WHERE NAME = ‘kim’";
    List<Member> resultList = em.createNativeQuery(sql, Member.class).getResultList();
  ```

# JDBC 직접 사용, SpringJdbcTemplate 등
  * JPA를 사용하면서 JDBC 커넥션을 직접 사용하거나, 스프링 JdbcTemplate, 마이바티스등을 함께 사용 가능
  * 단 영속성 컨텍스트를 적절한 시점에 강제로 플러시 필요!!!!!
  * 예) JPA를 우회해서 SQL을 실행하기 직전에 영속성 컨텍스트 수동 플러시

