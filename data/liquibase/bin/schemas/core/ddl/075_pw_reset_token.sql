create table pw_reset_token (
  token_hash VARCHAR2(64) NOT NULL,
  email VARCHAR2(255) NOT NULL,
  creation_time timestamp NOT NULL,
  CONSTRAINT pw_reset_token_p PRIMARY KEY (token_hash)
);



