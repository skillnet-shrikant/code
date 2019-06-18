-- Reset snapshot
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

--

-- env cleanup

delete from EPUB_AGENT_TRNPRT;
delete from EPUB_WF_COLL_TRANS;
delete from EPUB_WF_IND_TRANS;
delete from DSI_COLL_TRANS;
delete from DSI_IND_TRANS;
delete from DSS_COLL_TRANS;
delete from DSS_IND_TRANS;
delete from DSS_SERVER_ID;
delete from EPUB_WF_SERVER_ID;
delete from DSI_SERVER_ID;
delete from DSS_SERVER_ID;

delete from avm_workspace where ws_id in (select AVM_DEVLINE.ID from AVM_DEVLINE where name in (select workspace from epub_project where STATUS=0));

delete from AVM_DEVLINE where name in (select workspace from epub_project where STATUS=0);

delete from epub_ind_workflow where PROCESS_ID in (select epub_process.PROCESS_ID from epub_process where project in (
select project_id from epub_project where STATUS=0));

delete from epub_proc_history where PROCESS_ID in (
select epub_process.PROCESS_ID from epub_process where project in (
select project_id from epub_project where STATUS=0));

delete from EPUB_PR_HISTORY where project_id in (select project_id from epub_project where STATUS=0);

delete from epub_process where project in (select project_id from epub_project where STATUS=0);

delete from epub_project where STATUS=0;

--

-- full agent clean up

delete from EPUB_TR_AGENTS;
delete from EPUB_TARGET;
delete from EPUB_TL_TARGETS;
delete from EPUB_PRJ_TG_SNSHT;
delete from EPUB_PR_HISTORY;
delete from EPUB_AGENT_TRNPRT;
delete from EPUB_INCLUD_ASSET;
delete from EPUB_PRINC_ASSET;
delete from EPUB_AGENT;
delete from EPUB_PR_TG_STATUS;
delete from EPUB_PROJECT;
delete from EPUB_PR_TG_AP_TS;
delete from EPUB_DEPLOYMENT;
delete from EPUB_PR_TG_DP_TS;
delete from EPUB_PROC_HISTORY;
delete from EPUB_PR_TG_DP_ID;
delete from EPUB_INT_PRJ_HIST;
delete from EPUB_PROC_TASKINFO;
delete from EPUB_WORKFLOW_STRS;
delete from EPUB_IND_WORKFLOW;
delete from EPUB_PROCESS;
delete from EPUB_PR_HISTORY;
delete from EPUB_PROC_HISTORY;
delete from avm_asset_lock;