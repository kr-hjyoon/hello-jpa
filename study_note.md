 
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
  * 다대일: @ManyToOne
  * 일대다: @OneToMany
  * 일대일: @OneToOne
  * 다대다: @ManyToMany

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
  * 주인아 아니면 mappedBy 속성으로 주인 지정 


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


  

 



