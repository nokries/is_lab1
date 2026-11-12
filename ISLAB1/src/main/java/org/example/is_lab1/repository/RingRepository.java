package org.example.is_lab1.repository;

import org.example.is_lab1.models.entity.Ring;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RingRepository extends JpaRepository<Ring, Long> {
    // Свободные кольца (без владельца). Кольцо-заглушка "Без кольца" в список не попадает.
    @Query(value = """
        SELECT r FROM Ring r
        WHERE r.name <> :noRing AND r.id NOT IN (
            SELECT b.ring.id FROM BookCreature b
        )
        """,
            countQuery = """
        SELECT COUNT(r) FROM Ring r
        WHERE r.name <> :noRing AND r.id NOT IN (
            SELECT b.ring.id FROM BookCreature b
        )
        """)
    Page<Ring> findRingsWithoutOwner(@Param("noRing") String noRing, Pageable pageable);
}
