drop database syena;
create database syena;
USE syena;

drop table MEMBER_REGISTRATION;
create table MEMBER  ( ID BIGINT NOT NULL AUTO_INCREMENT, 
	DISPLAY_NAME VARCHAR(32),
    EMAIL VARCHAR(40) NOT NULL,
    INSTALLATION_ID VARCHAR(32) NULL,
    LATITUDE DOUBLE,
    LONGITUDE DOUBLE,
    ALTITUDE DOUBLE,
    DIRECTION VARCHAR(2),
    ACTIVE BOOLEAN,
    CREATED_DATE TIMESTAMP DEFAULT current_timestamp,
    UPDATED_DATE TIMESTAMP DEFAULT current_timestamp ON UPDATE current_timestamp,
    PRIMARY KEY (ID),
    UNIQUE(EMAIL),
    UNIQUE(INSTALLATION_ID)
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8;

CREATE TABLE MEMBER_REGISTRATION ( MEMBER_ID BIGINT NOT NULL ,
	REGISTRATION_TOKEN VARCHAR(256),
    CREATED_DATE TIMESTAMP DEFAULT current_timestamp,
    UPDATED_DATE TIMESTAMP DEFAULT current_timestamp ON UPDATE current_timestamp,
    PRIMARY KEY(MEMBER_ID),
    FOREIGN KEY (MEMBER_ID) REFERENCES MEMBER(ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
    
CREATE TABLE WATCH_STATUS (ID BIGINT NOT NULL AUTO_INCREMENT,
	STATUS VARCHAR(16) NOT NULL,
    DESCRIPTION VARCHAR(50),
    CREATED_DATE TIMESTAMP DEFAULT current_timestamp,
    UPDATED_DATE TIMESTAMP DEFAULT current_timestamp ON UPDATE current_timestamp,
    PRIMARY KEY (ID)
)ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8;

CREATE TABLE WATCH ( ID BIGINT NOT NULL AUTO_INCREMENT,
	ORIGIN_MEMBER_ID BIGINT NOT NULL,
    TARGET_MEMBER_ID BIGINT NOT NULL,
    STATUS VARCHAR(16) DEFAULT 'IN_ACTIVE',
    TARGET_ACCEPTED BOOLEAN,
    CREATED_DATE TIMESTAMP DEFAULT current_timestamp,
    UPDATED_DATE TIMESTAMP DEFAULT current_timestamp ON UPDATE current_timestamp,
    PRIMARY KEY (ID),
    FOREIGN KEY (ORIGIN_MEMBER_ID) REFERENCES MEMBER(ID),
    FOREIGN KEY (TARGET_MEMBER_ID) REFERENCES MEMBER(ID)
    
)ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8;

CREATE TABLE WATCH_CONFIGURATION (ID BIGINT NOT NULL AUTO_INCREMENT,
 ENTRY VARCHAR(32) NOT NULL,
    VALUE VARCHAR(32),
    ENTRY_TYPE VARCHAR(20),
    WATCH_ID BIGINT NOT NULL,
 TAG VARCHAR(16),
    CREATED_DATE TIMESTAMP DEFAULT current_timestamp,
    UPDATED_DATE TIMESTAMP DEFAULT current_timestamp ON UPDATE current_timestamp,
    PRIMARY KEY (ID),
 FOREIGN KEY (WATCH_ID) REFERENCES WATCH(ID)
 )ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8;  -- remove this >
	

CREATE TABLE WATCH_INSTANCE (ID BIGINT NOT NULL AUTO_INCREMENT,
	WATCH_ID BIGINT NOT NULL,
    DISTANCE_APART DOUBLE,
    ALTITUDE_APART DOUBLE,
    DIRECTION VARCHAR(8),
    CREATED_DATE TIMESTAMP DEFAULT current_timestamp,
    UPDATED_DATE TIMESTAMP DEFAULT current_timestamp ON UPDATE current_timestamp,
    PRIMARY KEY (ID),
    FOREIGN KEY (WATCH_ID) REFERENCES WATCH(ID)
)ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8;

CREATE TABLE WATCH_MESSAGE (ID BIGINT NOT NULL AUTO_INCREMENT,
	WATCH_ID BIGINT NOT NULL,
	MESSAGE VARCHAR(32),
    DESCRIPTION VARCHAR(256),
    CREATED_DATE TIMESTAMP DEFAULT current_timestamp,
    UPDATED_DATE TIMESTAMP DEFAULT current_timestamp ON UPDATE current_timestamp,
    PRIMARY KEY (ID),
    FOREIGN KEY (WATCH_ID) REFERENCES WATCH(ID)
)ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8;

CREATE TABLE WATCH_HISTORY (ID BIGINT NOT NULL AUTO_INCREMENT,
	WATCH_ID BIGINT NOT NULL,
	ORIGIN_MEMBER_ID BIGINT NOT NULL,
    ORIGIN_LATITUDE DOUBLE,
    ORIGIN_LONGITUDE DOUBLE,
    TARGET_MEMBER_ID BIGINT NOT NULL,
    TARGET_LATITUDE DOUBLE,
    TARGET_LONGITUDE DOUBLE,
    WATCH_STATUS_ID BIGINT,
    CREATED_DATE TIMESTAMP DEFAULT current_timestamp,
    UPDATED_DATE TIMESTAMP DEFAULT current_timestamp ON UPDATE current_timestamp,
    PRIMARY KEY (ID),
    FOREIGN KEY (ORIGIN_MEMBER_ID) REFERENCES MEMBER(ID),
    FOREIGN KEY (TARGET_MEMBER_ID) REFERENCES MEMBER(ID)
)ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8;
USE SYENA;
CREATE TABLE MEMBER_TXN (ID BIGINT NOT NULL AUTO_INCREMENT,
	MEMBER_ID BIGINT NOT NULL,
    PIN VARCHAR(6),
    TAG_CODE VARCHAR(8),
    TXN_INSTALLATION_ID VARCHAR(32),
    CREATED_DATE TIMESTAMP DEFAULT current_timestamp,
    UPDATED_DATE TIMESTAMP DEFAULT current_timestamp ON UPDATE current_timestamp,
    PRIMARY KEY (ID),
    FOREIGN KEY (MEMBER_ID) REFERENCES MEMBER(ID),
    UNIQUE(MEMBER_ID,TXN_INSTALLATION_ID),
    UNIQUE(TAG_CODE)
)ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8;

#INSERT INTO WATCH_STATUS(ID,STATUS,DESCRIPTION) VALUES(1,'CREATED','Watch is created but not started');
#INSERT INTO WATCH_STATUS(ID,STATUS,DESCRIPTION) VALUES(2,'STARTED','Watch is created but not started');
#INSERT INTO WATCH_STATUS(ID,STATUS,DESCRIPTION) VALUES(1,'CREATED','Watch is created but not started');
    