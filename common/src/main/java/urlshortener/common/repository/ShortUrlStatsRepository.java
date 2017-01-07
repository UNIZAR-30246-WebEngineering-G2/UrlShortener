package urlshortener.common.repository;

import urlshortener.common.domain.ShortUrlStats;

public interface ShortUrlStatsRepository {

	ShortUrlStats findByHash(String hash);

	ShortUrlStats save(ShortUrlStats sus);

	void update(ShortUrlStats sus);

	void delete(String hash);

	void deleteAll();

	Long count();
}
