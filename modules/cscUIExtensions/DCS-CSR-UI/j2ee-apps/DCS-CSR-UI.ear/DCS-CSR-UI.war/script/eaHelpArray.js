// -------------------------------------------------------------------
// EA help array structure for CSC
// -------------------------------------------------------------------
atg.ea.registerCSCHelpArray = function () {
  atg.ea.registerHelpArray({ id: "ea_csc_order_search", type: "inline", helpId: "ea_csc_order_search" });
  atg.ea.registerHelpArray({ id: "ea_csc_product_view", type: "inline", helpId: "ea_csc_product_view" });
  atg.ea.registerHelpArray({ id: "ea_csc_product_item_price", type: "popup", helpId: "ea_csc_product_item_price" });
  atg.ea.registerHelpArray({ id: "ea_csc_order_submit", type: "popup", helpId: "ea_csc_order_submit" });
  atg.ea.registerHelpArray({ id: "ea_csc_order_submit_footer", type: "popup", helpId: "ea_csc_order_submit_footer" });
  atg.ea.registerHelpArray({ id: "ea_csc_order_submit_create_schedule", type: "popup", helpId: "ea_csc_order_submit_create_schedule" });
  atg.ea.registerHelpArray({ id: "ea_csc_order_submit_create_schedule_footer", type: "popup", helpId: "ea_csc_order_submit_create_schedule_footer" });
  atg.ea.registerHelpArray({ id: "ea_csc_order_create_schedule", type: "popup", helpId: "ea_csc_order_create_schedule" });
  atg.ea.registerHelpArray({ id: "ea_csc_order_create_schedule_footer", type: "popup", helpId: "ea_csc_order_create_schedule_footer" });
  atg.ea.registerHelpArray({ id: "ea_csc_order_scheduled_days_of_week", type: "popup", helpId: "ea_csc_order_scheduled_days_of_week" });
  atg.ea.registerHelpArray({ id: "ea_csc_order_scheduled_weeks", type: "popup", helpId: "ea_csc_order_scheduled_weeks" });
  atg.ea.registerHelpArray({ id: "ea_csc_order_scheduled_dates_in_month", type: "popup", helpId: "ea_csc_order_scheduled_dates_in_month" });
  atg.ea.registerHelpArray({ id: "ea_csc_order_scheduled_actions", type: "popup", helpId: "ea_csc_order_scheduled_actions" });
  atg.ea.registerHelpArray({ id: "ea_csc_order_scheduled_status_failed", type: "popup", helpId: "ea_csc_order_scheduled_status_failed" });
  atg.ea.registerHelpArray({ id: "ea_csc_order_copy", type: "popup", helpId: "ea_csc_order_copy" });
  atg.ea.registerHelpArray({ id: "ea_csc_order_view_cancel", type: "popup", helpId: "ea_csc_order_view_cancel" });
  atg.ea.registerHelpArray({ id: "ea_csc_purchased_isReturnable", type: "popup", helpId: "ea_csc_purchased_isReturnable" });
  atg.ea.registerHelpArray({ id: "ea_csc_instore_pickup_available", type: "popup", helpId: "ea_csc_instore_pickup_available" });
  atg.ea.registerHelpArray({ id: "ea_csc_instore_pickup_billing_logic", type: "popup", helpId: "ea_csc_instore_pickup_billing_logic" });
};

//console.debug("EA | atg.ea.registerCSCHelpArray called");

dojo.addOnLoad(atg.ea.registerCSCHelpArray);
