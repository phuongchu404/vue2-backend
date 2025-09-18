package vn.mk.eid.web.excel;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.metadata.WriteTable;
import com.alibaba.excel.write.metadata.style.WriteCellStyle;
import com.alibaba.excel.write.metadata.style.WriteFont;
import com.alibaba.excel.write.style.HorizontalCellStyleStrategy;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import vn.mk.eid.common.data.Paging;
import vn.mk.eid.common.data.ServiceResult;
import vn.mk.eid.web.dto.request.staff.QueryStaffRequest;
import vn.mk.eid.web.dto.response.StaffResponse;
import vn.mk.eid.web.dto.response.excel.StaffExcelDTO;
import vn.mk.eid.web.service.StaffService;

import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class StaffExportExcel {
    private static final int PAGE_SIZE = 5_000;
    private static final int SHEET_MAX_ROWS = 100_000;
    private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private final StaffService staffService;


    public void exportMultiSheet(HttpServletResponse response, QueryStaffRequest request) throws Exception {
        // ========= Response headers=========
        setDownloadHeaders(response, "danh-sach-can-bo.xlsx");

        // ========= Styles =========
        WriteCellStyle headStyle = new WriteCellStyle();
        WriteFont headFont = new WriteFont();
        headFont.setBold(true);
        headFont.setFontHeightInPoints((short) 11);
        headFont.setFontName("Calibri");
        headStyle.setWriteFont(headFont);
        headStyle.setHorizontalAlignment(HorizontalAlignment.CENTER);
        headStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        WriteCellStyle contentStyle = new WriteCellStyle();
        contentStyle.setHorizontalAlignment(HorizontalAlignment.LEFT);
        contentStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        HorizontalCellStyleStrategy styles = new HorizontalCellStyleStrategy(headStyle, contentStyle);
        LongestMatchColumnWidthStyleStrategy autoWidth = new LongestMatchColumnWidthStyleStrategy();

        // ========= Writer =========
        ExcelWriter writer = EasyExcel
                .write(response.getOutputStream(), StaffExcelDTO.class)
                .registerWriteHandler(styles)
                .registerWriteHandler(autoWidth)
                .build();

        try {
            int columns = excelColumnCount(StaffExcelDTO.class);

            int sheetIndex = -1;
            int rowsInCurrentSheet = 0;

            // Sheet đầu tiên
            SheetContext ctx = createNewSheet(writer, ++sheetIndex, columns);

            int page = 0;
            Page<StaffResponse> slice;
            do {
                Pageable pageable = PageRequest.of(page, PAGE_SIZE, Sort.by(Sort.Direction.DESC, "createdAt"));
                ServiceResult<Page<StaffResponse>> responsePage = staffService.getStaffWithPaging(request, pageable);
                if (!responsePage.isSuccess()) {
                    throw new RuntimeException("Lỗi khi đọc dữ liệu cán bộ: " + responsePage.getMessage());
                }
                slice = responsePage.getData();

                List<StaffExcelDTO> batch = slice.getContent().stream()
                        .map(this::toDto)
                        .collect(Collectors.toList());

                int idx = 0;
                while (idx < batch.size()) {
                    int remaining = SHEET_MAX_ROWS - rowsInCurrentSheet;
                    if (remaining == 0) {
                        // Sheet mới
                        ctx = createNewSheet(writer, ++sheetIndex, columns);
                        rowsInCurrentSheet = 0;
                        remaining = SHEET_MAX_ROWS;
                    }
                    int take = Math.min(remaining, batch.size() - idx);
                    List<StaffExcelDTO> sub = batch.subList(idx, idx + take);

                    writer.write(sub, ctx.getSheet(), ctx.tableForNextWrite());

                    rowsInCurrentSheet += take;
                    idx += take;
                }

                page++;
            } while (!slice.isLast());
        } finally {
            writer.finish();
        }
    }

    /**
     * Tạo sheet mới: Dòng 1 = Title (merge A1..last), Dòng 2 = Header (từ DTO)
     */
    private SheetContext createNewSheet(ExcelWriter writer,
                                        int sheetIndex, int columns) {
        String name = "Cán bộ (" + (sheetIndex + 1) + ")";
        WriteSheet sheet = EasyExcel.writerSheet(sheetIndex, name)
                .relativeHeadRowIndex(1) // header xuống dòng 2 (sau title)
                .registerWriteHandler(new TitleHandler("Danh sách cán bộ", columns))
                .build();

        WriteTable dataTableNoHead = EasyExcel.writerTable(1).needHead(false).build();

        return new SheetContext(sheet,  dataTableNoHead);
    }

    /**
     * Đếm số cột = số field có @ExcelProperty trong DTO
     */
    private int excelColumnCount(Class<?> dtoClass) {
        int c = 0;
        for (Field f : dtoClass.getDeclaredFields()) {
            if (f.isAnnotationPresent(ExcelProperty.class)) c++;
        }
        return Math.max(c, 1);
    }

    /**
     * Hàng title: [title, "", "", ...] theo tổng số cột
     */
    private List<String> buildMergedTitleRow(String title, int totalCols) {
        List<String> row = new ArrayList<>(Collections.nCopies(totalCols, ""));
        row.set(0, title);
        return row;
    }

    /**
     * Map từ response -> DTO xuất Excel
     */
    private StaffExcelDTO toDto(StaffResponse e) {
        StaffExcelDTO dto = new StaffExcelDTO();
        dto.setStaffCode(e.getStaffCode());
        dto.setFullName(nvl(e.getFullName()));
        dto.setGender("MALE".equals(e.getGender()) ? "Nam"
                : "FEMALE".equals(e.getGender()) ? "Nữ" : "Khác");
        dto.setDateOfBirth(fmt(e.getDateOfBirth()));
        dto.setIdNumber(nvl(e.getIdNumber()));
        dto.setRank(nvl(e.getRank()));
        dto.setPhone(e.getPhone());
        dto.setEmail(nvl(e.getEmail()));
        dto.setDepartmentName(nvl(e.getDepartmentName()));
        dto.setPlaceOfBirth(e.getPlaceOfBirth());
        dto.setStatus(nvl(e.getStatus()));
        return dto;
    }

    private String fmt(LocalDate date) {
        return date == null ? "" : date.format(DF);
    }

    private String nvl(String s) {
        return s == null ? "" : s;
    }

    private void setDownloadHeaders(HttpServletResponse response, String fileName) throws UnsupportedEncodingException {

        String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8.toString()).replaceAll("\\+", "%20");
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + encodedFileName + "\"; filename*=UTF-8''" + encodedFileName);
        response.setHeader("Access-Control-Expose-Headers", "Content-Disposition, Content-Type");
    }
}
