package vn.mk.eid.web.excel;

import com.alibaba.excel.write.handler.AbstractSheetWriteHandler;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import com.alibaba.excel.write.metadata.holder.WriteWorkbookHolder;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;

public class TitleHandler extends AbstractSheetWriteHandler {
    private final String title;
    private final int colSize;

    public TitleHandler(String title, int colSize) {
        this.title = title;
        this.colSize = colSize;
    }

    @Override
    public void afterSheetCreate(WriteWorkbookHolder wbHolder, WriteSheetHolder shHolder) {
        Sheet sheet = shHolder.getSheet();

        // Merge A1..last
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, colSize - 1));

        Row row = sheet.createRow(0);
        Cell cell = row.createCell(0);
        cell.setCellValue(title);

        CellStyle style = sheet.getWorkbook().createCellStyle();
        Font font = sheet.getWorkbook().createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 14);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        cell.setCellStyle(style);
    }
}