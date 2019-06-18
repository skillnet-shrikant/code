create table tmp_bvreviews_data
(
  external_id      		varchar2(40),
  overall_rating   		NUMBER,
  is_removed			NUMBER
);
CREATE INDEX tmp_bvreviews_data_idx ON tmp_bvreviews_data (external_id);
