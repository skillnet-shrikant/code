select epub_target.display_name as "Target",
	epub_prj_tg_snsht.snapshot_id as "Snapshot",
	epub_project.display_name as "Project" 
from epub_target, 
	epub_project, 
	epub_prj_tg_snsht
where epub_prj_tg_snsht.project_id in
	(select project_id from epub_project 
		where workspace is not null and
			checked_in = '1'
	) and
	epub_target.target_id = epub_prj_tg_snsht.target_id and
	epub_prj_tg_snsht.project_id = epub_project.project_id
order by epub_project.checkin_date desc