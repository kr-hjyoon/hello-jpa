package hellojpa;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Team {
        @Id @GeneratedValue
        @Column(name="TEAM_ID")
        private Long id;

        @Column(name="USERNAME")
        private String name;

        @OneToMany( mappedBy = "team")  // Member 클래스의 연결된 필드이름 정의
        private List<Member> members = new ArrayList<>();

        public Long getId() {
                return id;
        }

        public void setId(Long id) {
                this.id = id;
        }

        public String getName() {
                return name;
        }

        public void setName(String name) {
                this.name = name;
        }

        public List<Member> getMembers() {
                return members;
        }

        public void setMembers(List<Member> members) {
                this.members = members;
        }

        /*
        // 연관관계 편의 메소드는 어느 한쪽에만 정의하는게 좋다
        public void addMember(Member member){
                member.setTeam(this);
                members.add(member);
        }*/

}
