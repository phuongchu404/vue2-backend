package vn.mk.eid.web.dto.report;

import java.io.Serializable;

public class ReportColumnFactory implements Serializable {
    private static final long serialVersionUID = 1L;
    public static ReportColumn createTextColumn(String key, String title) {
        return new ReportColumn(key, title, "text", true, null, null);
    }

    public static ReportColumn createNumberColumn(String key, String title) {
        return new ReportColumn(key, title, "number", true, "#,##0", null);
    }

    public static ReportColumn createDateColumn(String key, String title) {
        return new ReportColumn(key, title, "date", true, "dd/MM/yyyy", null);
    }

    public static ReportColumn createPercentageColumn(String key, String title) {
        return new ReportColumn(key, title, "percentage", true, "#,##0.00%", null);
    }

    public static ReportColumn createCurrencyColumn(String key, String title) {
        return new ReportColumn(key, title, "currency", true, "#,##0 VNĐ", null);
    }

    public static ReportColumn createFixedWidthColumn(String key, String title, String type, Integer width) {
        return new ReportColumn(key, title, type, true, null, width);
    }
}
