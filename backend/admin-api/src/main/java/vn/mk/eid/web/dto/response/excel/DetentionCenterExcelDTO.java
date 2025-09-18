package vn.mk.eid.web.dto.response.excel;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.time.LocalDate;

@Data
@ExcelIgnoreUnannotated
public class DetentionCenterExcelDTO {
    @ExcelProperty("Mã trại giam")
    private String code;

    @ExcelProperty("Tên trại giam")
    private String name;

    private String address;
    private String wardFullName;
    private String provinceFullName;

    @ExcelProperty("Số điện thoại")
    private String phone;

    @ExcelProperty("Email")
    private String email;

    @ExcelProperty("Giám thị")
    private String director;

    @ExcelProperty("Phó giám thị")
    private String deputyDirector;

    @ExcelProperty("Ngày thành lập")
    private String establishedDate;

    @ExcelProperty("Sức chứa")
    private Integer capacity;

    @ExcelProperty("Số phạm nhân hiện tại")
    private Integer currentPopulation;

    @ExcelProperty("Địa chỉ")
    private String fullAddress;

    public void setFullAddress() {
        this.fullAddress = String.join(", ",
                nvl(address), nvl(wardFullName), nvl(provinceFullName)).replaceAll("(,\\s*)+$", "");
    }
    private String nvl(String s){ return s == null ? "" : s; }
}
