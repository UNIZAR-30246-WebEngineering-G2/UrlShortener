package urlshortener.common.web.fixture;

import urlshortener.common.domain.ShortURL;

import java.sql.Timestamp;
import java.util.Calendar;

public class ShortURLFixture {

	public static ShortURL someUrl() {
		return new ShortURL("someKey", "http://example.com/", null, null, null,
				null, 307, true, null, null, null, null,null,true,0);
	}
	public static ShortURL anyUrl() {
		return new ShortURL("someKey2", "http://example.com/", null, null, null,
				null, 307, true, null, null, null, null,null,false,0);
	}
}