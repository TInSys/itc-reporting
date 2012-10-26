-- Database: "ITCReporting"

-- DROP DATABASE "ITCReporting";

CREATE DATABASE "ITCReporting"
  WITH OWNER = "ITCReporting"
       ENCODING = 'UTF8'
       TABLESPACE = pg_default
       CONNECTION LIMIT = -1;

-- Table: tax_period

-- DROP TABLE tax_period;

CREATE TABLE tax_period
(
  id serial NOT NULL,
  start_date date,
  stop_date date,
  CONSTRAINT tax_period_pkey PRIMARY KEY (id )
);

ALTER TABLE tax_period
  OWNER TO "ITCReporting";
  
-- Table: fiscal_period

-- DROP TABLE fiscal_period;

CREATE TABLE fiscal_period
(
  id serial NOT NULL,
  month smallint,
  year smallint,
  CONSTRAINT fiscal_period_pkey PRIMARY KEY (id )
)
WITH (
  OIDS=FALSE
);
ALTER TABLE fiscal_period
  OWNER TO "ITCReporting";

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
  zone bigint,
  period bigint,
  currency_iso character varying(3),
  CONSTRAINT fx_rate_pkey PRIMARY KEY (id ),
  CONSTRAINT period_fkey FOREIGN KEY (period)
      REFERENCES fiscal_period (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT zone_fkey FOREIGN KEY (zone)
      REFERENCES zone (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
);
ALTER TABLE fx_rate
  OWNER TO "ITCReporting";

-- Index: fki_period_id

-- DROP INDEX fki_period_id;

CREATE INDEX fki_period_id
  ON fx_rate
  USING btree
  (id );




-- Table: tax

-- DROP TABLE tax;

CREATE TABLE tax
(
  id serial NOT NULL,
  rate numeric(19,6),
  zone bigint,
  period bigint,
  CONSTRAINT tax_pkey PRIMARY KEY (id ),
  CONSTRAINT period_fkey FOREIGN KEY (period)
      REFERENCES tax_period (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT zone_fkey FOREIGN KEY (zone)
      REFERENCES zone (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
);
ALTER TABLE tax
  OWNER TO "ITCReporting";

-- Index: fki_period_fkey

-- DROP INDEX fki_period_fkey;

CREATE INDEX fki_period_fkey
  ON tax
  USING btree
  (period );

-- Index: fki_zone_fkey

-- DROP INDEX fki_zone_fkey;

CREATE INDEX fki_zone_fkey
  ON tax
  USING btree
  (id );


-- Table: application

-- DROP TABLE application;

CREATE TABLE application
(
  id serial NOT NULL,
  vendor_id character varying(20),
  name character varying(50),
  CONSTRAINT application_pkey PRIMARY KEY (id )
);

ALTER TABLE application
  OWNER TO "ITCReporting";


-- Table: sales

-- DROP TABLE sales;

CREATE TABLE sales
(
  id serial NOT NULL,
  sold_units bigint,
  individual_price numeric(19,6),
  total_price numeric(19,6),
  country_code character varying(3),
  period bigint,
  zone bigint,
  application bigint,
  CONSTRAINT sales_pkey PRIMARY KEY (id ),
  CONSTRAINT application_fkey FOREIGN KEY (application)
      REFERENCES application (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT period_fkey FOREIGN KEY (period)
      REFERENCES fiscal_period (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT zone_fkey FOREIGN KEY (zone)
      REFERENCES zone (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
);
ALTER TABLE sales
  OWNER TO "ITCReporting";

-- Index: fki_application_fkey

-- DROP INDEX fki_application_fkey;

CREATE INDEX fki_application_fkey
  ON sales
  USING btree
  (id );

