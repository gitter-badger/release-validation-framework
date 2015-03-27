
/******************************************************************************** 
	file-centric-snapshot-definition-double-spaces

	Assertion:
	No active definiitons associated with active concept contain double spaces.

********************************************************************************/
	
/* 	view of current snapshot made by finding terms containing double spaces */
	drop table if exists v_curr_snapshot;
	create table if not exists  v_curr_snapshot as
	select a.id 
	from curr_textdefinition_s a , curr_concept_s b
	where a.active = 1
	and a.conceptid = b.id
	and b.active = 1
	and a.term like '%  %'; 

	
/* 	inserting exceptions in the result table */
	insert into qa_result (runid, assertionuuid, assertiontext, details)
	select 
		<RUNID>,
		'<ASSERTIONUUID>',
		'<ASSERTIONTEXT>',
		concat('TEXTDEF : id=',a.id, ':Active Terms of active concept containing double spaces.') 	
	from v_curr_snapshot a;


	drop table if exists v_curr_snapshot;

	