package hellojpa;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.Iterator;
import java.util.List;

public class JpaMain {
    public static void main(String[] args) {
        EntityManagerFactory emf  = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx  = em.getTransaction();

        tx.begin();

        try{
            Team team = new Team();
            team.setName("TeamA");
            em.persist(team);

            Member member = new Member();
            member.setUsername("member1");
            member.setTeam(team);
            em.persist(member);

            Locker locker = new Locker();
            locker.setName("locker11");
            em.persist(locker);

            Member member2 = new Member();
            member2.setUsername("member2");
            member2.setTeam(team);
            member2.setLocker(locker);
            em.persist(member2);


            em.flush();
            em.clear();

            Member findMember = em.find(Member.class, member.getId());
            List<Member> members = findMember.getTeam().getMembers();

            for (Member m : members){
                System.out.println("member=" + m.getUsername());
            }

            // JPQL 단순예제
            /*
            // Insert
            Member member = new Member();
            member.setId(1L);
            member.setName("hello");
            em.persist(member);


            // Select
            Member findMember = em.find(Member.class, 1L);
            System.out.println("findMember id:" + findMember.getId());
            System.out.println("findMember name:" + findMember.getName());

            // Update
            Member findMember = em.find(Member.class, 1L);
            findMember.setName("hello2");       // 별도로 액션없이 commit 시에 변동사항을 자동으로 변경처리한다.  DirtyChecking

            // Delete
            Member findMember = em.find(Member.class, 1L);
            em.remove(findMember);
            List<Member> result = em.createQuery("select m from Member as m ", Member.class)
                    .getResultList();

            for (Member member: result ) {
                System.out.println("member.name = " + member.getName());
            }
            */
            // tx.commit();

        }catch (Exception e){
            tx.rollback();
        }finally {
            em.close();
        }
        emf.close();

        System.out.println("SUCCESSFULLY FINISHED");
    }
}