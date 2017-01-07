package urlshortener.common.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import urlshortener.common.domain.ShortUrlStats;

import java.sql.*;


@Repository
public class ShortUrlStatsRepositoryImpl implements ShortUrlStatsRepository {

	private static final Logger log = LoggerFactory
			.getLogger(ShortUrlStatsRepositoryImpl.class);

	private static final RowMapper<ShortUrlStats> rowMapper = new RowMapper<ShortUrlStats>() {
		@Override
		public ShortUrlStats mapRow(ResultSet rs, int rowNum) throws SQLException {
			return new ShortUrlStats(rs.getString("hash"), rs.getInt("rtime_average"),
					rs.getInt("rtime_number"),rs.getInt("last_rtime"),rs.getInt("d_time"),
					rs.getInt("stime_average"),rs.getInt("stime_number"));
		}
	};

	@Autowired
	protected JdbcTemplate jdbc;

	public ShortUrlStatsRepositoryImpl() {
	}

	public ShortUrlStatsRepositoryImpl(JdbcTemplate jdbc) {
		this.jdbc = jdbc;
	}

	@Override
	public ShortUrlStats findByHash(String hash) {
		try {
			return jdbc.queryForObject("SELECT * FROM shorturlstats WHERE hash=?",
					rowMapper, hash);
		} catch (Exception e) {
			log.debug("When select for key " + hash, e);
			return null;
		}
	}

	@Override
	public ShortUrlStats save(final ShortUrlStats sus) {
		try {
			jdbc.update("INSERT INTO shorturlstats VALUES (?,?,?,?,?,?,?)",
					sus.getHash(), sus.getRtime_average(), sus.getRtime_number(), sus.getLast_rtime(),
					sus.getD_time(), sus.getStime_average(), sus.getStime_number());
		} catch (DuplicateKeyException e) {
			log.error("When insert for key " + sus.getHash(), e);
			return sus;
		} catch (Exception e) {
			log.error("When insert", e);
			return null;
		}
		return sus;
	}

	@Override
	public void update(ShortUrlStats sus) {
		log.debug("Hash: "+ sus.getHash() + " Response average: " + sus.getRtime_average()
		+ " Response number: " + sus.getRtime_number() + " Last response: " + sus.getLast_rtime()
		+ " Down time: " + sus.getD_time() + " Service average: " + sus.getStime_average()
		+ " Service number: " + sus.getStime_number());
		try {
			jdbc.update(
					"update shorturlstats set RTIME_AVERAGE=?, RTIME_NUMBER=?, LAST_RTIME=?, D_TIME=?, " +
							"STIME_AVERAGE=?, STIME_NUMBER=? where hash=?",
					sus.getRtime_average(), sus.getRtime_number(), sus.getLast_rtime(),
					sus.getD_time(), sus.getStime_average(), sus.getStime_number(), sus.getHash());
			
		} catch (Exception e) {
			log.info("When update for hash " +sus.getHash(), e);
		}
	}

	@Override
	public void delete(String hash) {
		try {
			jdbc.update("delete from shorturlstats where hash=?", hash);
		} catch (Exception e) {
			log.debug("When delete for hash " + hash, e);
		}
	}

	@Override
	public void deleteAll() {
		try {
			jdbc.update("delete from shorturlstats");
		} catch (Exception e) {
			log.debug("When delete all", e);
		}
	}

	@Override
	public Long count() {
		try {
			return jdbc
					.queryForObject("select count(*) from shorturlstats", Long.class);
		} catch (Exception e) {
			log.debug("When counting", e);
		}
		return -1L;
	}
}
