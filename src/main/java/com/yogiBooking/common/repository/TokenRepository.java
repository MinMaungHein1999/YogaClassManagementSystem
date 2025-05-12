package com.yogiBooking.common.repository;

import com.yogiBooking.common.entity.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token, Integer> {

  @Query(value = """
      SELECT t FROM Token t INNER JOIN User u
      ON t.userId = u.id
      WHERE u.id = :id AND (t.expired = false OR t.revoked = false)
      """)
  List<Token> findAllValidTokenByUser(Long id);

  Optional<Token> findByToken(String token);

  @Transactional
  @Modifying
  @Query("DELETE FROM Token t WHERE (t.expired = true OR t.revoked = true) AND t.createdAt <= :cutoffTime")
  int deleteOldInvalidTokens(@Param("cutoffTime") LocalDateTime cutoffTime);
}
