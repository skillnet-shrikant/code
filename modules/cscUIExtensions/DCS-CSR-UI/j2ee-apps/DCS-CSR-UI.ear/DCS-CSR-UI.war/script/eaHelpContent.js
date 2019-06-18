// -------------------------------------------------------------------
// EA help content definitions for CSC
// -------------------------------------------------------------------
atg.ea.registerCSCHelpContent = function () {
  atg.ea.registerHelpContent({ "id":"ea_csc_order_search", "excerpt":getResource("ea.csc.helpContent.ea_csc_order_search"), "content":"" });
  atg.ea.registerHelpContent({ "id":"ea_csc_product_view", "excerpt":getResource("ea.csc.helpContent.ea_csc_product_view"), "content":"" });
  atg.ea.registerHelpContent({ "id":"ea_csc_product_item_price", "content":getResource("ea.csc.helpContent.ea_csc_product_item_price") });
  atg.ea.registerHelpContent({ "id":"ea_csc_order_submit", "content":getResource("ea.csc.helpContent.ea_csc_order_submit") });
  atg.ea.registerHelpContent({ "id":"ea_csc_order_submit_footer", "content":getResource("ea.csc.helpContent.ea_csc_order_submit_footer") });
  atg.ea.registerHelpContent({ "id":"ea_csc_order_submit_create_schedule", "content":getResource("ea.csc.helpContent.ea_csc_order_submit_create_schedule") });
  atg.ea.registerHelpContent({ "id":"ea_csc_order_submit_create_schedule_footer", "content":getResource("ea.csc.helpContent.ea_csc_order_submit_create_schedule_footer") });
  atg.ea.registerHelpContent({ "id":"ea_csc_order_create_schedule", "content":getResource("ea.csc.helpContent.ea_csc_order_create_schedule") });
  atg.ea.registerHelpContent({ "id":"ea_csc_order_create_schedule_footer", "content":getResource("ea.csc.helpContent.ea_csc_order_create_schedule_footer") });
  atg.ea.registerHelpContent({ "id":"ea_csc_order_scheduled_days_of_week", "content":getResource("ea.csc.helpContent.ea_csc_order_scheduled_days_of_week") });
  atg.ea.registerHelpContent({ "id":"ea_csc_order_scheduled_weeks", "content":getResource("ea.csc.helpContent.ea_csc_order_scheduled_weeks") });
  atg.ea.registerHelpContent({ "id":"ea_csc_order_scheduled_dates_in_month", "content":getResource("ea.csc.helpContent.ea_csc_order_scheduled_dates_in_month") });
  atg.ea.registerHelpContent({ "id":"ea_csc_order_scheduled_actions", "content":getResource("ea.csc.helpContent.ea_csc_order_scheduled_actions") });
  atg.ea.registerHelpContent({ "id":"ea_csc_order_scheduled_status_failed", "content":getResource("ea.csc.helpContent.ea_csc_order_scheduled_status_failed") });
  atg.ea.registerHelpContent({ "id":"ea_csc_order_copy", "content":getResource("ea.csc.helpContent.ea_csc_order_copy") });
  atg.ea.registerHelpContent({ "id":"ea_csc_order_view_cancel", "content":getResource("ea.csc.helpContent.ea_csc_order_view_cancel") });
  atg.ea.registerHelpContent({ "id":"ea_csc_purchased_isReturnable", "content":getResource("ea.csc.helpContent.ea_csc_purchased_isReturnable") });
  atg.ea.registerHelpContent({ "id":"ea_csc_instore_pickup_available", "content":getResource("ea.csc.helpContent.ea_csc_instore_pickup_available") }); 
  atg.ea.registerHelpContent({ "id":"ea_csc_instore_pickup_billing_logic", "content":getResource("ea.csc.helpContent.ea_csc_instore_pickup_billing_logic") });
};

//console.debug("EA | atg.ea.registerCSCHelpContent called");

dojo.addOnLoad(atg.ea.registerCSCHelpContent);

