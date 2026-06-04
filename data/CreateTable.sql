/* -- CREAZIONE DEL DATABASE -- */ 
create table users(
	username varchar(255) primary key,
	password varchar(255) not null,
	type varchar(13) not null check(type in ('user' , 'administrator')) default 'user'
);

create table texts(
	title varchar(255) primary key,
	lenght integer not null,
	path varchar(255) not null, 
	analysis varchar(255)
);
	
create table match(
	id integer primary key autoincrement,
	match_date timestamp not null default current_timestamp,
	text_id varchar(255) references texts(title)
);

create table participation(
	user_id varchar(255) references users(username),
	match_id integere references match(id),
	score float not null default 0.0,
	response_time float not null,
	primary key (user_id , match_id)	
);
	
	

