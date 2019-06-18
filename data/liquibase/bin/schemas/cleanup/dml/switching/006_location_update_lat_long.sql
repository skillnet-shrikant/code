merge into dcs_location dest
                using (select loc.location_id,addr.postal_code,zip.latitude,zip.longitude from dcs_location loc, dcs_location_addr addr, atg_core.zipcode_usa zip
where loc.location_id=addr.location_id
and zip.zipcode=addr.postal_code
and zip.primaryrecord='P' ) src
                on (dest.location_id = src.location_id)
                when matched then 
                                update set dest.latitude = src.latitude,dest.longitude=src.longitude where dest.location_id=src.location_id;