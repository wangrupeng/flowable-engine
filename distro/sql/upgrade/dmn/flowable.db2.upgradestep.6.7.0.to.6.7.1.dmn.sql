
UPDATE ".ACT_DMN_DATABASECHANGELOGLOCK SET LOCKED = 1, LOCKEDBY = '192.168.68.111 (192.168.68.111)', LOCKGRANTED = TIMESTAMP('2021-11-11 16:06:33.044') WHERE ID = 1 AND LOCKED = 0;

UPDATE ".ACT_DMN_DATABASECHANGELOGLOCK SET LOCKED = 0, LOCKEDBY = NULL, LOCKGRANTED = NULL WHERE ID = 1;
