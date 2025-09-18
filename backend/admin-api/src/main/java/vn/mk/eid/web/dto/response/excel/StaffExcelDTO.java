package vn.mk.eid.web.dto.response.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class StaffExcelDTO {
    @ExcelProperty("Mã cán bộ")
    private String staffCode;

    @ExcelProperty("Họ và tên")
    private String fullName;

    @ExcelProperty("Số CCCD/CMND")
    private String idNumber;

    @ExcelProperty("Giới tính")
    private String gender;

    @ExcelProperty("Ngày sinh")
    private String dateOfBirth;

    @ExcelProperty("Nơi sinh")
    private String placeOfBirth;

    @ExcelProperty("Số điện thoại")
    private String phone;
    @ExcelProperty("Email")
    private String email;
    @ExcelProperty("Cấp bậc")
    private String rank;

    @ExcelProperty("Trạng thái")
    private String status;

    @ExcelProperty("Đơn vị")
    private String departmentName;
}
