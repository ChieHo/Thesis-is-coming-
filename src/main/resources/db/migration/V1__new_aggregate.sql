create table profil (
                        id           serial primary key,
                        fach_id      uuid default gen_random_uuid(),
                        name         varchar(250),
                        email        varchar(250),
                        github_login varchar(40),
                        constraint uq_profil_fach_id unique (fach_id)
);

create table link (
                      profil      integer     ,
                      url         varchar(500),
                      anzeigetext varchar(250),
                      constraint fk_link_profil foreign key (profil) references profil (id)
);

create table profil_fachgebiet (
                                   profil integer     ,
                                   name   varchar(250),
                                   constraint fk_profil_fachgebiet_profil foreign key (profil) references profil (id)
);

create table profile_files (
                               id          serial primary key,
                               name        varchar(250),
                               description text,
                               path        varchar(250),
                               type        varchar(20),
                               size        bigint,
                               upload_date timestamp,
                               profil      integer,
                               constraint fk_profile_files_profil foreign key (profil) references profil (id)
);

create table thema (
                       id           serial primary key,
                       fach_id      uuid default gen_random_uuid(),
                       titel        varchar(250),
                       beschreibung text,
                       profil       integer,
                       constraint uq_thema_fach_id unique (fach_id),
                       constraint fk_thema_profil foreign key (profil) references profil (id)
);

create table thema_fachgebiet (
                                  thema integer     ,
                                  name  varchar(250),
                                  constraint fk_thema_fachgebiet_thema foreign key (thema) references thema (id)
);

create table thema_link (
                            thema       integer     ,
                            url         varchar(500),
                            anzeigetext varchar(250),
                            constraint fk_thema_link_thema foreign key (thema) references thema (id)
);

create table thema_files (
                             id          serial primary key,
                             name        varchar(250),
                             description text,
                             path        varchar(250),
                             type        varchar(20),
                             size        bigint,
                             upload_date timestamp,
                             thema       integer,
                             constraint fk_thema_files_thema foreign key (thema) references thema (id)
);

create table thema_voraussetzung (
                                     thema        integer     ,
                                     voraussetzung varchar(250),
                                     constraint fk_voraussetzung_thema foreign key (thema) references thema (id)
);


