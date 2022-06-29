package hellojpa;
import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
@Entity
public class Member {
    @Id
    @GeneratedValue
    @Column(name="MEMBER_ID")
    private Long id;

    @Column(name = "USERNAME")
    private String username;

    /* DB방식의 연결
    @Column(name ="TEAM_ID")
    private Long teamId;
    */

    /* Object 방식의 연결 */
    @ManyToOne
    @JoinColumn(name="TEAM_ID")
    private Team team;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    //public void setTeam(Team team) {    // 순수 Setter가 아니므로 이름을 바꾸는것도
    public void changeTeam(Team team){
        this.team = team;
        team.getMembers().add(this);  // this는 member를 의미
    }
}