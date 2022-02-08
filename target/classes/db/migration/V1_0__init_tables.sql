create table if not exists users (
    id integer not null auto_increment,
    code varchar(255),
    email varchar(255) not null,
    is_moderator TINYINT not null,
    name varchar(255) not null,
    password varchar(255) not null,
    photo TEXT,
    reg_time datetime(6) not null,
    primary key (id)
) engine=InnoDB;

create table if not exists posts (
    id integer not null auto_increment,
    is_active TINYINT not null,
    moderation_status enum("NEW", "ACCEPTED", "DECLINED") not null,
    text text not null,
    time datetime(6) not null,
    title varchar(255) not null,
    view_count integer not null,
    moderator_id integer,
    user_id integer not null,
    primary key (id)
) engine=InnoDB;

create table if not exists post_votes (
    id integer not null auto_increment,
    time datetime(6) not null,
    value TINYINT not null,
    post_id integer not null,
    user_id integer not null,
    primary key (id)
) engine=InnoDB;

create table if not exists tags (
    id integer not null auto_increment,
    name varchar(255) not null,
    primary key (id)
) engine=InnoDB;

create table if not exists tag2post (
    id integer not null auto_increment,
    post_id integer not null,
    tag_id integer not null,
    primary key (id)
) engine=InnoDB;

create table if not exists post_comments (
    id integer not null auto_increment,
    text text not null,
    time datetime(6) not null,
    post_id integer not null,
    parent_id integer,
    user_id integer not null,
    primary key (id)
) engine=InnoDB;

create table if not exists captcha_codes (
    id integer not null auto_increment,
    code TINYTEXT not null,
    secret_code TINYTEXT not null,
    time datetime(6) not null,
    primary key (id)
) engine=InnoDB;

create table if not exists global_settings (
    id integer not null auto_increment,
    code varchar(255) not null,
    name varchar(255) not null,
    value varchar(255) not null,
    primary key (id)
) engine=InnoDB;