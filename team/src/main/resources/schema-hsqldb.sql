-- Clean database

DROP TABLE CLICK IF EXISTS;
DROP TABLE SHORTURLSTATS IF EXISTS;
DROP TABLE SHORTURL IF EXISTS;
DROP TABLE USERS IF EXISTS;


-- Users

CREATE TABLE USERS(
  EMAIL VARCHAR(256) PRIMARY KEY,
  PASSWORD VARCHAR(512)
);
-- ShortURL

CREATE TABLE SHORTURL(
	HASH		VARCHAR(30) PRIMARY KEY,	-- Key
	TARGET		VARCHAR(1024),				-- Original URL
	SPONSOR		VARCHAR(1024),				-- Sponsor URL
	CREATED 	TIMESTAMP,					-- Creation date
	OWNER		VARCHAR(256) NOT NULL FOREIGN KEY REFERENCES USERS(EMAIL),				-- User id
	MODE		INTEGER,					-- Redirect mode
	SAFE		BOOLEAN,						-- Safe target
	IP			VARCHAR(20),				-- IP
	COUNTRY		VARCHAR(50),					-- Country
	TIMEPUBLICITY INTEGER,        -- Time of publicity
	URLPUBLICITY VARCHAR (1024),   -- Publicity URL
	LASTCHANGE TIMESTAMP,
	ACTIVE BOOLEAN,
	UPDATE_STATUS INTEGER,
	LAST_TIME_UP TIMESTAMP
);

CREATE TABLE SHORTURLSTATS (
  HASH 		VARCHAR(10) NOT NULL FOREIGN KEY REFERENCES SHORTURL(HASH) PRIMARY KEY,	-- Foreing key
  RTIME_AVERAGE INTEGER,  --Average response time (When checking in scheduler the URL target)
  RTIME_NUMBER INTEGER,   --Number of average response times saved
  LAST_RTIME INTEGER,     --Last  response time taken
  D_TIME INTEGER,          --Down time
  STIME_AVERAGE INTEGER,  --Average service time (Request to short an URL and get the shortened URL)
  STIME_NUMBER INTEGER    --Number of average service times saved
);

-- Click

CREATE TABLE CLICK(
    ID 			BIGINT IDENTITY,			-- KEY
	HASH 		VARCHAR(10) NOT NULL FOREIGN KEY REFERENCES SHORTURL(HASH),	-- Foreing key
	CREATED 	TIMESTAMP,					-- Creation date
	REFERRER	VARCHAR(1024),				-- Traffic origin
	BROWSER		VARCHAR(50),				-- Browser
	PLATFORM	VARCHAR(50),				-- Platform
	IP			VARCHAR(20),				-- IP
	COUNTRY		VARCHAR(50),					-- Country
	LATITUDE VARCHAR(256),           --Latitude
	LONGITUDE VARCHAR(256)            --Longitude
)