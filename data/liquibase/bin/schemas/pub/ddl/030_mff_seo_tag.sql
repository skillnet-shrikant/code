CREATE TABLE mff_seo_tag (
	seo_tag_id 						VARCHAR2(40)		 NOT NULL,  	
	robots_index        			NUMBER(1) DEFAULT 1  NOT NULL,
	robots_follow        			NUMBER(1) DEFAULT 1  NOT NULL,
	canonical_url         			VARCHAR2(254),
	asset_version 					NUMBER(19) 			 NOT NULL,
	constraint mff_seo_tag_pk primary key (seo_tag_id,asset_version)
);
