
/******************************************************************************** 
component-centric-snapshot-expression-association-unique-mapTarget.sql

	Assertion:
	The mapTarget in expression association refset snapshot is unique.

********************************************************************************/
insert into qa_result (runid, assertionuuid, assertiontext, details)
 select
 	<RUNID>,
	'<ASSERTIONUUID>',
 	'<ASSERTIONTEXT>',
 	concat('MapTarget:',a.mapTarget,' is not unique in the expression association refset snapshot.')
 from curr_expressionAssociationRefset_s a
	group by a.mapTarget
	having count(a.mapTarget) > 1;
 commit;
 