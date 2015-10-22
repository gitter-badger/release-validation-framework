
/******************************************************************************** 
	file-centric-snapshot-description-unique-id

	Assertion:
	The current Description snapshot file has unique ids.

********************************************************************************/
	insert into qa_result (runid, assertionuuid, assertiontext, details)
	select 
		<RUNID>,
		'<ASSERTIONUUID>',
		'<ASSERTIONTEXT>',
		concat('DESC: id=',a.id, ':Non unique id in description release file.') 	
	from curr_description_s a	
	group by a.id
	having  count(a.id) > 1;