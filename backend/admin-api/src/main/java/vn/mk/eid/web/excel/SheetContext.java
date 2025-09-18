package vn.mk.eid.web.excel;

import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.metadata.WriteTable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SheetContext {
    private WriteSheet sheet;
    private WriteTable dataTableNoHead;
    private boolean headerWritten = false;

    public SheetContext(WriteSheet sheet, WriteTable dataTableNoHead) {
        this.sheet = sheet;
        this.dataTableNoHead = dataTableNoHead;
    }
    public WriteSheet getSheet() { return sheet; }
    public WriteTable tableForNextWrite() {
        if (!headerWritten) {
            headerWritten = true;
        }
        return dataTableNoHead;
    }
}
