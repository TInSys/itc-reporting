CREATE OR REPLACE FUNCTION updateITCReportingDB() RETURNS integer  AS 
$$
BEGIN
-- check if logged in correct DB
if (current_database()<>'ITCReporting'  or current_user <>'postgres') then
   RAISE EXCEPTION 'Bad DB --> % or user --> %',current_database(),current_user;
else
-- check if table version exists
   DECLARE version_exists text;
           r RECORD;
           cpt int;
   begin
      select table_name into version_exists from information_schema.tables where table_schema ='public' and table_name='version';
      if version_exists is null then
         begin
         CREATE TABLE version
         ("number" smallint NOT NULL DEFAULT 0,
         CONSTRAINT version_pk PRIMARY KEY (number ));
         ALTER TABLE version OWNER TO "ITCReporting";
         insert into version (number) values (0);
         end;
      end if;
-- start update if needed.
         declare current_DBversion INTEGER;
         begin
            select number into current_DBversion from version limit 1;
            RAISE NOTICE 'Current DB version : %',current_DBversion;

-- update version 1
            if (current_DBversion < 1) then
               RAISE NOTICE 'Updating to version 1...';
               begin
                  ALTER TABLE sales ADD COLUMN individual_proceeds numeric(19,6);
                  ALTER TABLE sales ADD COLUMN total_proceeds numeric(19,6);

                  current_DBversion = current_DBversion + 1;
                  update version set number = current_DBversion;
                  RAISE NOTICE 'Done. Current version is now %',current_DBversion;
                  RAISE NOTICE '';
               end;
            end if;
-- update version 2
            if (current_DBversion < 2) then
               RAISE NOTICE 'Updating to version 2...';
               begin
                  CREATE TABLE company
                  (  id serial NOT NULL,
                     name character varying(100),
                     currency_iso character varying(3),
                     CONSTRAINT company_pkey PRIMARY KEY (id )
                  );
                  ALTER TABLE company OWNER TO "ITCReporting";

                  current_DBversion = current_DBversion + 1;
                  update version set number = current_DBversion;
                  RAISE NOTICE 'Done. Current version is now %',current_DBversion;
                  RAISE NOTICE '';
               end;
            end if;

         end;
   end;
end if;
RETURN 0;
END;
$$
language 'plpgsql' ; 
select updateITCReportingDB();
 