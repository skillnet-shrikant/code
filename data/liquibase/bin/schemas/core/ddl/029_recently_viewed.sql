create table mff_rec_viewed_prod (
	id	varchar2(40)	not null,
	product_id	varchar2(40)	null,
	site_id	varchar2(40)	null,
	time_stamp	date	not null
,constraint rec_viewed_prod_p primary key (id));


create table mff_rec_viewed (
	user_id	varchar2(40)	not null,
	sequence_id	integer	not null,
	rec_viewed_prod	varchar2(40)	not null
,constraint mff_rec_viewed_p primary key (user_id,sequence_id));