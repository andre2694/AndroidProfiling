USE android_profiling;

CREATE TABLE IF NOT EXISTS Users (USERNAME VARCHAR(256) PRIMARY KEY NOT NULL,
                       PUBLICKEY VARCHAR(2048) NOT NULL,
                       AESKEY VARCHAR(2048) NOT NULL,
                       NONCE VARCHAR(256) NOT NULL,
                       CODE VARCHAR(4) NOT NULL,
			UNIQUE (USERNAME))
  ENGINE=INNODB;


CREATE TABLE IF NOT EXISTS Profiles (IMEI VARCHAR(256) PRIMARY KEY,
                                     SOFTWAREVERSION VARCHAR(256) NOT NULL,
                                     SIMCOUNTRYISO VARCHAR(256) NOT NULL,
                                     SIMOPERATOR VARCHAR(256) NOT NULL,
                                     SIMOPERATORNAME VARCHAR(256) NOT NULL,
                                     SIMSERIALNUMBER VARCHAR(256) NOT NULL,
                                     IMSINUMBER VARCHAR(256) NOT NULL,
                                     MACADDRESS VARCHAR(256) NOT NULL,
                                     IPADDRESS VARCHAR(256) NOT NULL,
                                     OSVERSION VARCHAR(256) NOT NULL,
                                     SDKVERSION VARCHAR(256) NOT NULL,
                                     DEVICENAME VARCHAR(256) NOT NULL,
                                     SCREENRESOLUTION VARCHAR(256) NOT NULL,
                                     KEYBOARDLANGUAGE VARCHAR(256) NOT NULL,

                                     USERNAME VARCHAR(256) NOT NULL,
  CONSTRAINT USER_USERNAME FOREIGN KEY(USERNAME)
  REFERENCES Users(USERNAME) ON DELETE CASCADE
)
  ENGINE=INNODB;


CREATE TABLE IF NOT EXISTS NetworksSSID (NETWORK VARCHAR(256) NOT NULL,
                      IMEI VARCHAR(256) NOT NULL,
                      CONSTRAINT NETWORKS_PROFILE FOREIGN KEY(IMEI)
                      REFERENCES Profiles(IMEI) ON DELETE CASCADE)
  ENGINE=INNODB;


CREATE TABLE IF NOT EXISTS GoogleAccounts (GOOGLEACCOUNT VARCHAR(256) NOT NULL,
                      IMEI VARCHAR(256) NOT NULL,
                      CONSTRAINT GOOGLE_PROFILE FOREIGN KEY(IMEI)
                      REFERENCES Profiles(IMEI) ON DELETE CASCADE)
  ENGINE=INNODB;


CREATE TABLE IF NOT EXISTS MemorizedAccounts (ACCOUNT VARCHAR(256) NOT NULL,
                      IMEI VARCHAR(256) NOT NULL,
                      CONSTRAINT ACCOUNTS_PROFILE FOREIGN KEY(IMEI)
                      REFERENCES Profiles(IMEI) ON DELETE CASCADE)
  ENGINE=INNODB;


CREATE TABLE IF NOT EXISTS InputMethods (INPUT VARCHAR(256) NOT NULL,
                      IMEI VARCHAR(256) NOT NULL,
                      CONSTRAINT INPUT_PROFILE FOREIGN KEY(IMEI)
                      REFERENCES Profiles(IMEI) ON DELETE CASCADE)
  ENGINE=INNODB;


CREATE TABLE IF NOT EXISTS InstalledApplications (APPLICATION VARCHAR(256) NOT NULL,
                      IMEI VARCHAR(256) NOT NULL,
                      CONSTRAINT APPLICATIONS_PROFILE FOREIGN KEY(IMEI)
                      REFERENCES Profiles(IMEI) ON DELETE CASCADE)
  ENGINE=INNODB;
