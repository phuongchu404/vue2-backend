package vn.mk.eid.web.dto.report;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PagedReportResponse extends ReportResponse{
    private Integer totalPages;
    private Boolean hasNext;
    private Boolean hasPrevious;

    public PagedReportResponse(String title, List<ReportColumn> columns,
                               List<Map<String, Object>> data, Integer totalRecords,
                               Integer pageSize, Integer currentPage) {
        super(title, columns, data);
        this.setTotalRecords(totalRecords);
        this.setPageSize(pageSize);
        this.setCurrentPage(currentPage);
        this.totalPages = (int) Math.ceil((double) totalRecords / pageSize);
        this.hasNext = currentPage < totalPages - 1;
        this.hasPrevious = currentPage > 0;
    }
}
