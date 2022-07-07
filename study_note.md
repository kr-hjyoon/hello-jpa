 
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



