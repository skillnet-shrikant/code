package oms.commerce.settlement;

public interface SettlementConstants {

  public static final String SETTLEMENT_PROP_AMOUNT = "settlementAmount";
  public static final String SETTLEMENT_PROP_PARTIAL_SETTLEMENT = "partialSettlement";
  public static final String SETTLEMENT_PROP_PG_ID = "pgId";
  public static final String SETTLEMENT_PROP_ORDER_ID = "orderId";
  public static final String SETTLEMENT_PROP_CREATE_DATE = "creationDate";
  public static final String SETTLEMENT_PROP_STATUS = "settlementStatus";
  public static final String SETTLEMENT_PROP_SETTLE_DATE = "settlementDate";
  public static final String SETTLEMENT_PROP_TYPE = "settlementType";

  public static final String SETTLEMENT_PROP_ORDER_NUMBER = "orderNumber";
  public static final String SETTLEMENT_PROP_PG_DESC = "pgDesc";

  public static final String SETTLEMENT_ITEM_DESCRIPTOR = "paymentSettlement";

  public static final String PG_SETTLEMENT_ITEM_DESCRIPTOR = "pgSettlement";
  public static final String PG_SETTLEMENT_PROP_INVOICEID = "invoiceId";
  public static final String PG_SETTLEMENT_PROP_DATE = "date";
  public static final String PG_SETTLEMENT_PROP_SETTLEMENT_AMOUNT = "settlementAmount";

  public static final String PG_GC_SETTLEMENT_ITEM_DESCRIPTOR = "pgGcSettlement";
  public static final String PG_GC_SETTLEMENT_PROP_GC_NUMBER = "giftCardNumber";
  public static final String PG_GC_SETTLEMENT_PROP_GC_PIN = "giftCardPin";
  public static final String PG_GC_SETTLEMENT_PROP_DATE = "date";
  public static final String PG_GC_SETTLEMENT_SETTLEMENT_AMOUNT = "settlementAmount";

  public static final String PAYMENTGROUP_PROP_SETTLEMENTS = "settlements";
  public static final String PAYMENTGROUP_SETTLEMENT_ITEM_DESCRIPTOR_NAME = "pgSettlement";
  public static final String PAYMENTGROUP_GC_SETTLEMENTS = "giftCardSettlements";

  /**
   * The settlement status at the time of creating the settlement record
   */
  public static String SETTLE_STATUS_INITIAL = "INITIAL";

  /**
   * Settlement status for a settlement record that has successfully been
   * processed.
   */
  public static String SETTLE_STATUS_SETTLED = "SETTLED";

  /**
   * Settlement status ERROR indicates that the settlement has errored out and
   * will be retried on the next attempt.
   */
  public static String SETTLE_STATUS_ERROR = "ERROR";

  /**
   * Settlement status of FAILURE indicates that the settlement errored out and
   * the maximum number of retry attempts has been reached.
   */
  public static String SETTLE_STATUS_FAILURE = "FAILURE";

  public static final int SETTLEMENT_TYPE_DEBIT = 1;
  public static final int SETTLEMENT_TYPE_CREDIT = 0;

}
