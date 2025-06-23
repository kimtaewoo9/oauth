package com.example.oauth.member.repository;

import com.example.oauth.member.entity.Member;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

	Optional<Member> findByEmail(String email);

	Optional<Member> findBySocialId(String sub);
}
