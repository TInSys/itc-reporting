-- Database: "ITCReporting"

-- DROP DATABASE "ITCReporting";

CREATE DATABASE "ITCReporting"
  WITH OWNER = "ITCReporting"
       ENCODING = 'UTF8'
       TABLESPACE = pg_default
       CONNECTION LIMIT = -1;

-- Table: period

-- DROP TABLE period;
CREATE TABLE period
(
  id serial NOT NULL,
  start_date date,
  stop_date date,
  CONSTRAINT period_pkey PRIMARY KEY (id)
);
ALTER TABLE period OWNER TO "ITCReporting";


-- Table: "zone"

-- DROP TABLE "zone";
CREATE TABLE "zone"
(
  id serial NOT NULL,
  code character varying(15),
  "name" character varying(50),
  currency_iso character varying(3),
  CONSTRAINT zone_pkey PRIMARY KEY (id)
);
ALTER TABLE "zone" OWNER TO "ITCReporting";



-- Table: fx_rate

-- DROP TABLE fx_rate;
CREATE TABLE fx_rate
(
  id serial NOT NULL,
  rate numeric(19,6),
  "zone" bigint,
  period bigint,
  currency_iso character varying(3),
  CONSTRAINT fx_rate_pkey PRIMARY KEY (id),
  CONSTRAINT period_fkey FOREIGN KEY (period)
      REFERENCES period (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT zone_fkey FOREIGN KEY ("zone")
      REFERENCES "zone" (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
);

ALTER TABLE fx_rate OWNER TO "ITCReporting";

-- Index: fki_period_id

-- DROP INDEX fki_period_id;
CREATE INDEX fki_period_id
  ON fx_rate
  USING btree
  (id);


-- Table: tax

-- DROP TABLE tax;
CREATE TABLE tax
(
  id serial NOT NULL,
  rate numeric(19,6),
  "zone" bigint,
  period bigint,
  CONSTRAINT tax_pkey PRIMARY KEY (id),
  CONSTRAINT period_fkey FOREIGN KEY (period)
      REFERENCES period (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT zone_fkey FOREIGN KEY ("zone")
      REFERENCES "zone" (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
);

ALTER TABLE tax OWNER TO "ITCReporting";

-- Index: fki_period_fkey

-- DROP INDEX fki_period_fkey;
CREATE INDEX fki_period_fkey
  ON tax
  USING btree
  (period);

-- Index: fki_zone_fkey

-- DROP INDEX fki_zone_fkey;
CREATE INDEX fki_zone_fkey
  ON tax
  USING btree
  (id);





