
/******************************************************************************** 
	file-centric-snapshot-description-trim

	Assertion:
	No active Terms contain leading or trailing spaces.

********************************************************************************/
	insert into qa_result (runid, assertionuuid, assertiontext, details)
	select 
		<RUNID>,
		'<ASSERTIONUUID>',
		'<ASSERTIONTEXT>',
		concat('Description: id=',a.id, ' has active term with leading or trailing spaces.') 	
	from curr_description_s a 
	where a.active = 1
	and ( a.term != LTRIM(term) or a.term != RTRIM(term)); 
	commit;
	
	