/*
 * Active complex map refset members must have a non blank mapTarget for higher map group.
 */
insert into qa_result (runid, assertionuuid, assertiontext, details)
 select
 	<RUNID>,
 	'<ASSERTIONUUID>',
	 '<ASSERTIONTEXT>',
 	concat('ComplexMap: id=',a.id,': Higher-level map group with only blank map targets') 
 from curr_complexmaprefset_s a
	where a.active = 1
  	and a.mapGroup > 1
  	and not exists
    (select * from curr_complexmaprefset_s b
     where a.refSetId = b.refSetId
       and a.referencedComponentId = b.referencedComponentId
       and a.mapGroup = b.mapGroup
       and b.mapTarget != '');
commit;