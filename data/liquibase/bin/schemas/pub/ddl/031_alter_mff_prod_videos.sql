alter table mff_product_video add (
	asset_version int not null,
	CONSTRAINT mff_prd_vid_pk PRIMARY KEY (product_id, seq_num,asset_version)
);