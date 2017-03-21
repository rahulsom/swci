CREATE TABLE REQUESTS (
    id                  varchar2(36) PRIMARY KEY,
    confirmationNumber  varchar2(10),
    firstName           varchar(40),
    lastName            varchar(40),
    checkinTime         DATETIME
);