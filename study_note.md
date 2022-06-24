 
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

# Entity Life Cycle 
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
 

