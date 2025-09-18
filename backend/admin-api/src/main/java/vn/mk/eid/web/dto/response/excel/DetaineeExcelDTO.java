package vn.mk.eid.web.dto.response.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class DetaineeExcelDTO {
    @ExcelProperty("Mã phạm nhân")
    private String detaineeCode;

    @ExcelProperty("Họ và tên")
    private String fullName;

    @ExcelProperty("Giới tính")
    private String gender;

    @ExcelProperty("Ngày sinh")
    private String dateOfBirth;

    @ExcelProperty("Số CCCD/CMND")
    private String idNumber;

    @ExcelProperty("Ngày bắt")
    private String arrestDate;

    @ExcelProperty("Buồng giam")
    private String cellNumber;

    @ExcelProperty("Tội danh")
    private String charges;

    @ExcelProperty("Trạng thái")
    private String status;
}
