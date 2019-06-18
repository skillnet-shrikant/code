merge into mff_location dest
using (select location_id,hours from dcs_location_store) src
on(dest.location_id=src.location_id)
when matched then
update set dest.store_hours=src.hours;