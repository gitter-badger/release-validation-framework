
/******************************************************************************** 
	file-centric-snapshot-description-space-before-tag

	Assertion:
	All active FSNs associated with active concepts have a space before the semantic tag.

********************************************************************************/
	insert into qa_result (runid, assertionuuid, assertiontext, details)
	select 
		<RUNID>,
		'<ASSERTIONUUID>',
		'<ASSERTIONTEXT>',
		concat('DESCRIPTION: id=',a.id, ' : The FSN has no space before the semantic tag.')	
	from curr_description_s a , curr_concept_s b
	where typeid ='900000000000003001'
	and a.active = 1
	and b.active = 1
	and a.conceptid = b.id
	and a.term not like '% (%';
	commit;
	