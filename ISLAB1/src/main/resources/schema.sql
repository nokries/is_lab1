CREATE SEQUENCE IF NOT EXISTS coordinates_seq    START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE IF NOT EXISTS ring_seq           START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE IF NOT EXISTS magic_city_seq     START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE IF NOT EXISTS book_creature_seq  START WITH 1 INCREMENT BY 1;

CREATE TABLE IF NOT EXISTS coordinates (
    id BIGINT  PRIMARY KEY DEFAULT nextval('coordinates_seq'),
    x  INTEGER NOT NULL,
    y  INTEGER NOT NULL,
    CONSTRAINT coordinates_id_positive CHECK (id > 0),
    CONSTRAINT coordinates_x_max       CHECK (x <= 735)
);

CREATE TABLE IF NOT EXISTS ring (
    id     BIGINT       PRIMARY KEY DEFAULT nextval('ring_seq'),
    name   VARCHAR(255) NOT NULL,
    power  INTEGER      NOT NULL,
    weight INTEGER      NOT NULL,
    CONSTRAINT ring_id_positive     CHECK (id > 0),
    CONSTRAINT ring_name_not_blank  CHECK (btrim(name) <> ''),
    CONSTRAINT ring_power_positive  CHECK (power > 0),
    CONSTRAINT ring_weight_positive CHECK (weight > 0)
);

CREATE TABLE IF NOT EXISTS magic_city (
    id                  BIGINT           PRIMARY KEY DEFAULT nextval('magic_city_seq'),
    name                VARCHAR(255)     NOT NULL,
    area                DOUBLE PRECISION NOT NULL,
    population          INTEGER          NOT NULL,
    establishmentdate   TIMESTAMP,
    governor            VARCHAR(16),
    capital             BOOLEAN          NOT NULL DEFAULT FALSE,
    populationdensity   BIGINT           NOT NULL,
    CONSTRAINT city_id_positive         CHECK (id > 0),
    CONSTRAINT city_name_not_blank      CHECK (btrim(name) <> ''),
    CONSTRAINT city_area_positive       CHECK (area > 0),
    CONSTRAINT city_population_positive CHECK (population > 0),
    CONSTRAINT city_density_positive    CHECK (populationdensity > 0),
    CONSTRAINT city_governor_valid      CHECK (governor IN ('HOBBIT', 'ELF', 'HUMAN', 'GOLLUM'))
);

CREATE TABLE IF NOT EXISTS book_creature (
    id                 BIGINT       PRIMARY KEY DEFAULT nextval('book_creature_seq'),
    name               VARCHAR(255) NOT NULL,
    coordinates_fk_id  BIGINT       NOT NULL REFERENCES coordinates (id),
    creationdate       TIMESTAMP    NOT NULL DEFAULT now(),
    age                BIGINT,
    creaturetype       VARCHAR(16),
    city_fk_id         BIGINT       REFERENCES magic_city (id),
    attacklevel        BIGINT       NOT NULL,
    ring_fk_id         BIGINT       NOT NULL REFERENCES ring (id),
    CONSTRAINT creature_id_positive     CHECK (id > 0),
    CONSTRAINT creature_name_not_blank  CHECK (btrim(name) <> ''),
    CONSTRAINT creature_age_positive    CHECK (age > 0),
    CONSTRAINT creature_attack_positive CHECK (attacklevel > 0),
    CONSTRAINT creature_type_valid      CHECK (creaturetype IN ('HOBBIT', 'ELF', 'HUMAN', 'GOLLUM'))
);