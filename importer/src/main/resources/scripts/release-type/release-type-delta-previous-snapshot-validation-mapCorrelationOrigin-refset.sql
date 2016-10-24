/*  
 * There must be actual changes made to previously published mapcorrelationOriginRefset components in order for them to appear in the current delta.
*/
	insert into qa_result (runid, assertionuuid, concept_id, details)
	select 
	<RUNID>,
	'<ASSERTIONUUID>',
	a.referencedcomponentid,
	concat('mapcorrelationOriginRefset: id=',a.id, ' is in the detla file, but no actual changes made since the previous release.') 	
	from curr_mapCorrelationOriginRefset_d a
	left join prev_mapCorrelationOriginRefset_s b
		on a.id = b.id
		and a.active = b.active
		and a.moduleid = b.moduleid
		and a.refsetid = b.refsetid
		and a.referencedcomponentid = b.referencedcomponentid
		and a.mapTarget = b.mapTarget
		and a.attributeId = b.attributeId
		and a.correlationId = b.correlationId
		and a.contentOriginId = b.contentOriginId
	where b.id is not null;
