 
/*
 * Active extended map with age rules do not have mapRule ending with "<= 0"
 *
 */
 insert into qa_result (runid, assertionuuid, concept_id, details)
 select
 	<RUNID>,
 	'<ASSERTIONUUID>',
 	a.referencedcomponentid,
 	concat('ExtendedMap: id=',a.id,': illegal mapRule value for age rule:',a.mapRule)       
 from curr_extendedmaprefset_s a
	where a.active = 1
  	and a.mapRule like '%<= 0';
 commit;
