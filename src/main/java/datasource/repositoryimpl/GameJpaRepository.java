package datasource.repositoryimpl;

import datasource.entity.GameEntity;
import domain.model.GameStatus;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface GameJpaRepository extends CrudRepository<GameEntity, UUID> {
    List<GameEntity> findByStatus(GameStatus status);

    @Query("SELECT g FROM GameEntity g WHERE " +
            "g.winnerId = :userId OR " +
            "(g.status = domain.model.GameStatus.DRAW AND (g.playerXId = :userId OR g.playerOId = :userId)) " +
            "ORDER BY g.createdAt DESC")
    List<GameEntity> findFinishedGamesByUserId(@Param("userId") UUID userId);

    @Query(value = """
        SELECT 
            user_id,
            login,
            win_ratio
        FROM (
            SELECT 
                u.id AS user_id,
                u.login AS login,
                CASE 
                    -- Если нет поражений и ничьих: коэффициент равен числу побед
                    WHEN COUNT(CASE WHEN g.status = 'DRAW' OR (g.winner_id IS NOT NULL AND g.winner_id != u.id) THEN 1 END) = 0 
                    THEN CAST(COUNT(CASE WHEN g.winner_id = u.id THEN 1 END) AS double precision)
                    -- Иначе: делим победы на сумму (поражения + ничьи)
                    ELSE CAST(COUNT(CASE WHEN g.winner_id = u.id THEN 1 END) AS double precision) / 
                         COUNT(CASE WHEN g.status = 'DRAW' OR (g.winner_id IS NOT NULL AND g.winner_id != u.id) THEN 1 END)
                END AS win_ratio,
                COUNT(CASE WHEN g.winner_id = u.id THEN 1 END) AS wins_count
            FROM users u
            -- Внимание: имена колонок в Postgres именно playerxid и playeroid (без подчеркивания)
            JOIN games g ON (g.playerxid = u.id OR g.playeroid = u.id)
            WHERE g.status IN ('X_WON', 'O_WON', 'DRAW')
            GROUP BY u.id, u.login
        ) stats
        ORDER BY win_ratio DESC, wins_count DESC
        LIMIT :limit
        """, nativeQuery = true)
    List<Object[]> findLeaderboardNative(@Param("limit") int limit);

}