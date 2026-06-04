CREATE TABLE IF NOT EXISTS signup (
    formno VARCHAR(50) NOT NULL,
    name VARCHAR(100),
    fname VARCHAR(100),
    dob VARCHAR(50),
    gender VARCHAR(50),
    email VARCHAR(100),
    marital VARCHAR(50),
    address VARCHAR(255),
    city VARCHAR(100),
    pincode VARCHAR(50),
    state VARCHAR(100),
    PRIMARY KEY (formno)
);

CREATE TABLE IF NOT EXISTS Signuptwo (
    formno VARCHAR(50) NOT NULL,
    rel VARCHAR(50),
    cate VARCHAR(50),
    inc VARCHAR(50),
    edu VARCHAR(100),
    occ VARCHAR(100),
    pan VARCHAR(50),
    addhar VARCHAR(50),
    scitizen VARCHAR(50),
    eAccount VARCHAR(50),
    PRIMARY KEY (formno)
);

CREATE TABLE IF NOT EXISTS signupthree (
    formno VARCHAR(50) NOT NULL,
    atype VARCHAR(100),
    cardno VARCHAR(50),
    pin VARCHAR(50),
    fac VARCHAR(255),
    PRIMARY KEY (formno)
);

CREATE TABLE IF NOT EXISTS login (
    formno VARCHAR(50),
    card_number VARCHAR(50) NOT NULL,
    pin VARCHAR(50),
    PRIMARY KEY (card_number)
);

CREATE TABLE IF NOT EXISTS bank (
    pin VARCHAR(50),
    date VARCHAR(100),
    type VARCHAR(50),
    amount VARCHAR(50)
);
