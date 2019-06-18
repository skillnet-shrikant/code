delete from mff_content_items where CONTENT_ID= '600004' and CONTENT_SECTION = 'a10008';

delete from wcm_article where ID = 'a10008';

UPDATE WCM_ARTICLE SET NAME = 'Store Hours', ARTICLE_BODY = '<h3 class="store-hours-title">Store Hours</h3><div class="store-hours">
 <strong>Monday - Friday: </strong>7am - 9pm<br>
 <strong>Saturday: </strong>7am - 8pm<br>
 <strong>Sunday: </strong>8am - 6pm<br>
 <strong>Sunday (Fargo): </strong>Noon - 6pm
</div>' WHERE ID = 'a10005';


update wcm_article set NAME = 'Gas Mart Hours', ARTICLE_BODY = '<h3 class="store-hours-title">Gasmart Hours</h3>
<div class="store-hours">
 <strong>Sunday - Saturday: </strong>6am - 10pm<br>
</div>' where ID = 'a10006';

update wcm_article set NAME = 'Auto Service Center Hours', ARTICLE_BODY = '<h3 class="store-hours-title">Auto Service Center Hours</h3>
<div class="store-hours">
 <strong>Monday - Friday: </strong>7am - 7pm<br>
 <strong>Saturday: </strong>7am - 6pm<br>
 <strong>Sunday: </strong>8am - 6pm<br>
 <strong>Sunday (Fargo): </strong>Noon - 6pm
</div>' where ID = 'a10007';

commit;