CREATE TABLE "travel"
(
    "id"           SERIAL       NOT NULL,
    "title"        VARCHAR(255) NOT NULL,
    "description"  VARCHAR(255) NOT NULL,
    "organizer_id" BIGINT       NOT NULL,
    "created_at"   TIMESTAMP    NOT NULL
);
ALTER TABLE
    "travel"
    ADD PRIMARY KEY ("id");
ALTER TABLE
    "travel"
    ADD CONSTRAINT "travel_title_unique" UNIQUE ("title");
CREATE TABLE "invitation"
(
    "code"       VARCHAR PRIMARY KEY,
    "travel_id"  INTEGER REFERENCES travel (id),
    "uses_last"  INTEGER DEFAULT 1,
    "expires_at" TIMESTAMP NOT NULL
);

CREATE TABLE "intermediate_point"
(
    "id"         SERIAL  NOT NULL,
    "start_date" DATE    NOT NULL,
    "end_date"   DATE    NOT NULL,
    "address_id" INTEGER,
    "travel_id"  INTEGER NOT NULL
);
ALTER TABLE
    "intermediate_point"
    ADD PRIMARY KEY ("id");
CREATE TABLE "creating_travel"
(
    "id"           SERIAL       NOT NULL,
    "title"        VARCHAR(255) NOT NULL,
    "description"  VARCHAR(255) NULL,
    "organizer_id" BIGINT NULL
);
ALTER TABLE
    "creating_travel"
    ADD PRIMARY KEY ("id");
ALTER TABLE
    "creating_travel"
    ADD CONSTRAINT "creating_travel_title_unique" UNIQUE ("title");
COMMENT
ON COLUMN
    "creating_travel"."id" IS 'Telegram UserId';
CREATE TABLE "travel_participant"
(
    "travel_id" INTEGER NOT NULL,
    "person_id" BIGINT  NOT NULL
);
CREATE TABLE "registering_person"
(
    "id"          BIGINT       NOT NULL,
    "age"         INTEGER NULL,
    "address_id"  INTEGER NULL,
    "name"        VARCHAR(255) NULL,
    "last_action" VARCHAR(255) NOT NULL
);
ALTER TABLE
    "registering_person"
    ADD PRIMARY KEY ("id");
COMMENT
ON COLUMN
    "registering_person"."id" IS 'Telegram UserId';
CREATE TABLE "note"
(
    "id"         SERIAL  NOT NULL,
    "travel_id"  INTEGER NOT NULL,
    "file_name" VARCHAR NULL,
    "creator_id" BIGINT  NOT NULL,
    "is_public"  BOOLEAN NOT NULL,
    "content"    TEXT NULL
);
ALTER TABLE
    "note"
    ADD PRIMARY KEY ("id");
CREATE TABLE "address"
(
    "id"       serial       NOT NULL,
    "city"     VARCHAR(255) NOT NULL,
    "country"  VARCHAR(255) NOT NULL,
    "street"   VARCHAR(255) NULL,
    "building" VARCHAR(255) NULL
);
ALTER TABLE
    "address"
    ADD PRIMARY KEY ("id");
CREATE TABLE "creating_intermediate_point"
(
    "id"         SERIAL NOT NULL,
    "start_date" DATE NULL,
    "end_date"   DATE NULL,
    "travel_id"  INTEGER NULL,
    "address_id" INTEGER NULL
);
ALTER TABLE
    "creating_intermediate_point"
    ADD PRIMARY KEY ("id");
CREATE TABLE "person"
(
    "id"          BIGINT       NOT NULL,
    "age"         INTEGER NULL,
    "address_id"  INTEGER      NOT NULL,
    "bio"         VARCHAR(255) NULL,
    "name"        VARCHAR(255) NOT NULL,
    "last_action" VARCHAR(255)
);
ALTER TABLE
    "person"
    ADD PRIMARY KEY ("id");
COMMENT
ON COLUMN
    "person"."id" IS 'Telegram userId';
ALTER TABLE
    "note"
    ADD CONSTRAINT "note_travel_id_foreign" FOREIGN KEY ("travel_id") REFERENCES "travel" ("id") ON DELETE CASCADE ;
ALTER TABLE
    "travel"
    ADD CONSTRAINT "travel_organizer_id_foreign" FOREIGN KEY ("organizer_id") REFERENCES "person" ("id");
ALTER TABLE
    "person"
    ADD CONSTRAINT "person_address_id_foreign" FOREIGN KEY ("address_id") REFERENCES "address" ("id");
ALTER TABLE
    "travel_participant"
    ADD CONSTRAINT "travel_participant_travel" FOREIGN KEY ("travel_id") REFERENCES "travel" ("id") ON DELETE CASCADE ;
ALTER TABLE
    "intermediate_point"
    ADD CONSTRAINT "intermediate_point_travel_id_foreign" FOREIGN KEY ("travel_id") REFERENCES "travel" ("id") ON DELETE CASCADE ;
ALTER TABLE
    "note"
    ADD CONSTRAINT "note_creator_id_foreign" FOREIGN KEY ("creator_id") REFERENCES "person" ("id");
ALTER TABLE
    "intermediate_point"
    ADD CONSTRAINT "intermediate_point_address_id_foreign_addr" FOREIGN KEY ("address_id") REFERENCES "address" ("id");
ALTER TABLE
    "travel_participant"
    ADD CONSTRAINT "travel_participant_person_id_foreign_pers" FOREIGN KEY ("person_id") REFERENCES "person" ("id");
