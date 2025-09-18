package vn.mk.eid.web.service.impl;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;
import vn.mk.eid.common.constant.Constants;
import vn.mk.eid.common.dao.repository.*;
import vn.mk.eid.common.data.ServiceResult;
import vn.mk.eid.common.util.DateUtils;
import vn.mk.eid.web.constant.DetentionHistoryType;
import vn.mk.eid.web.dto.report.OverviewStatistics;
import vn.mk.eid.web.dto.report.ReportInsight;
import vn.mk.eid.web.dto.report.ReportResponse;
import vn.mk.eid.web.dto.request.report.DetaineeReportStatus;
import vn.mk.eid.web.dto.response.report.*;
import vn.mk.eid.web.repository.DetaineeRepositoryCustom;
import vn.mk.eid.web.repository.StaffRepositoryCustom;
import vn.mk.eid.web.service.RedisService;
import vn.mk.eid.web.service.ReportService;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
@Transactional
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {
    private final DetaineeRepository detaineeRepository;
    private final DetaineeRepositoryCustom detaineeRepositoryCustom;
    private final StaffRepository staffRepository;
    private final StaffRepositoryCustom staffRepositoryCustom;
    private final IdentityRecordRepository identityRecordRepository;
    private final FingerprintCardRepository fingerprintCardRepository;
    private final DetentionCenterRepository detentionCenterRepository;
    private final RedisService redisService;

    @Override
    public ServiceResult getReportOverview() {
        String currentMonth = DateUtils.convertLocalDateToString(LocalDate.now(), Constants.C_YYYY_MM);
        int lengthOfMonth = LocalDate.now().lengthOfMonth();

        Pair<Integer, Integer> detentionCenterPair = getDetentionCenterOverview();
        Pair<Integer, Integer> detaineePair = getDetaineeOverview(currentMonth, lengthOfMonth);
        Pair<Integer, Integer> staffPair = getStaffOverview(currentMonth, lengthOfMonth);
        Pair<Integer, Integer> identityPair = getIdentityRecordOverview(currentMonth, lengthOfMonth);
        Pair<Integer, Integer> fingerPair = getFingerOverview(currentMonth, lengthOfMonth);

        int currentDetainee = detaineePair.getLeft();
        int previousMonthDetainee = detaineePair.getRight();
        int totalStaff = staffPair.getLeft();
        int previousMonthStaff = staffPair.getRight();
        int totalIdentity = identityPair.getLeft();
        int previousMonthIdentity = identityPair.getRight();
        int totalFinger = fingerPair.getLeft();
        int previousMonthFinger = fingerPair.getRight();

        OverviewReportResponse response = OverviewReportResponse.builder()
                .totalDetentionCenter(detentionCenterPair.getLeft())
                .totalDetainees(currentDetainee)
                .detaineeChange(currentDetainee - previousMonthDetainee)
                .totalStaff(totalStaff)
                .staffChange(totalStaff - previousMonthStaff)
                .totalIdentity(totalIdentity)
                .identityChange(totalIdentity - previousMonthIdentity)
                .totalFingerprint(totalFinger)
                .fingerprintChange(totalFinger - previousMonthFinger)
                .build();
        return ServiceResult.ok(response);
    }

    @Override
    public ServiceResult getDetaineeReportByStatus(DetaineeReportStatus request) {
        DetaineeReportByStatusResponse response = new DetaineeReportByStatusResponse();
        List<String> allType = Arrays.asList(DetentionHistoryType.INITIAL.name(), DetentionHistoryType.RELEASED.name(), DetentionHistoryType.TRANSFER.name(), DetentionHistoryType.DECEASED.name());
        List<DetaineeReportByStatus> data = detaineeRepositoryCustom.getDetaineeReportByStatus(request);
        Map<String, Integer> dataMap = new HashMap<>();

        for (DetaineeReportByStatus item : data) {
            response.setCount(response.getCount() + item.getCount());
            dataMap.put(item.getType(), item.getCount());
        }

        List<DetaineeReportByStatus> details = new ArrayList<>();
        for (String type : allType) {
            DetaineeReportByStatus item = new DetaineeReportByStatus();
            item.setType(type);
            item.setCount(dataMap.getOrDefault(type, 0));
            if (response.getCount() > 0) {
                item.setPercent(BigDecimal.valueOf(item.getCount()).divide(BigDecimal.valueOf(response.getCount()), 2, RoundingMode.HALF_UP));
            }
            details.add(item);
        }

        response.setDetails(details);
        return ServiceResult.ok(response);
    }

    @Override
    public ServiceResult getDetaineeReportByMonth(DetaineeReportStatus request) {
        List<DetaineeReportByMonth> data = detaineeRepositoryCustom.getDetaineeReportByMonth(request);
        if (!data.isEmpty()) {
            String firstMonth = data.get(0).getMonth();
            LocalDate toDate = YearMonth.parse(firstMonth, DateTimeFormatter.ofPattern(Constants.C_MM_YYYY)).atDay(1);
            int preTotal;
            Optional<Integer> countOptional = detaineeRepository.getTotalDetainee(Boolean.FALSE, toDate);
            if (countOptional.isPresent()) {
                preTotal = countOptional.get();

                for (DetaineeReportByMonth item : data) {
                    item.setTotal(preTotal + item.getNewDetainees() - item.getReleased());
                    preTotal = item.getTotal();
                }
            }
        }

        return ServiceResult.ok(data);
    }

    @Override
    public ServiceResult getStaffReportByDepartment(DetaineeReportStatus request) {
        List<StaffReportByDepartment> data = staffRepositoryCustom.getReportByDepartment(request);
        return ServiceResult.ok(data);
    }

    @Override
    public OverviewStatistics getOverviewStatistics() {
        return null;
    }

    @Override
    public ReportResponse generateReport(String type, LocalDate fromDate, LocalDate toDate) {
        return null;
    }

    @Override
    public ReportResponse getDetaineesByStatusReport(LocalDate fromDate, LocalDate toDate) {
        return null;
    }

    @Override
    public ReportResponse getDetaineesByMonthReport(LocalDate fromDate, LocalDate toDate) {
        return null;
    }

    @Override
    public ReportResponse getStaffByDepartmentReport() {
        return null;
    }

    @Override
    public ReportResponse getIdentityRecordsReport(LocalDate fromDate, LocalDate toDate) {
        return null;
    }

    @Override
    public ReportResponse getFingerprintCardsReport(LocalDate fromDate, LocalDate toDate) {
        return null;
    }

    @Override
    public ReportResponse getMonthlySummaryReport(LocalDate month) {
        return null;
    }

    @Override
    public ReportResponse getDepartmentTrendsReport(Long departmentId, LocalDate fromDate, LocalDate toDate) {
        return null;
    }

    @Override
    public ReportResponse getDepartmentComparisonReport(LocalDate reportDate) {
        return null;
    }

    @Override
    public byte[] exportReportToExcel(String type, LocalDate fromDate, LocalDate toDate) {
        return new byte[0];
    }

    @Override
    public byte[] exportReportToPDF(String type, LocalDate fromDate, LocalDate toDate) {
        return new byte[0];
    }

    @Override
    public List<ReportInsight> getReportInsights(String reportType, LocalDate fromDate, LocalDate toDate) {
        return Collections.emptyList();
    }

    @Override
    public void refreshReportCache(String reportType) {

    }

    @Override
    public void clearAllReportCache() {

    }

    private Pair<Integer, Integer> getDetentionCenterOverview() {
        Integer detentionCenterCount = detentionCenterRepository.countByIsActiveIsTrue();
        return Pair.of(detentionCenterCount, detentionCenterCount);
    }

    private Pair<Integer, Integer> getDetaineeOverview(String currentMonth, Integer lengthOfMonth) {
        int currentDetainee = 0;
        int previousMonthDetainee = 0;

        Optional<Integer> currentDetaineeOptional = detaineeRepository.getTotalDetainee(null, LocalDate.now());
        if (currentDetaineeOptional.isPresent()) {
            currentDetainee = currentDetaineeOptional.get();
        }

        Object detaineeRedisData = redisService.getValue(Constants.ReportRedisKey.TOTAL_DETAINEE_PREVIOUS + currentMonth);
        if (detaineeRedisData != null) {
            previousMonthDetainee = Integer.parseInt(String.valueOf(detaineeRedisData));
        } else {
            Optional<Integer> previousOptional = detaineeRepository.getTotalDetainee(Boolean.TRUE, LocalDate.now().withDayOfMonth(1));
            if (previousOptional.isPresent()) {
                previousMonthDetainee = previousOptional.get();
                redisService.setValueWithExpireTime(
                        Constants.ReportRedisKey.TOTAL_DETAINEE_PREVIOUS + currentMonth,
                        previousMonthDetainee,
                        lengthOfMonth,
                        TimeUnit.DAYS
                );
            }
        }
        return Pair.of(currentDetainee, previousMonthDetainee);
    }

    private Pair<Integer, Integer> getStaffOverview(String currentMonth, Integer lengthOfMonth) {
        int totalStaff = 0;
        int previousMonthStaff = 0;

        Optional<Integer> totalStaffOptional = staffRepository.getTotalStaff(null);
        if (totalStaffOptional.isPresent()) {
            totalStaff = totalStaffOptional.get();
        }

        Object staffRedisData = redisService.getValue(Constants.ReportRedisKey.TOTAL_STAFF_PREVIOUS + currentMonth);
        if (staffRedisData != null) {
            previousMonthStaff = Integer.parseInt(String.valueOf(staffRedisData));
        } else {
            Optional<Integer> previousOptional = staffRepository.getTotalStaff(Boolean.TRUE);
            if (previousOptional.isPresent()) {
                previousMonthStaff = previousOptional.get();
                redisService.setValueWithExpireTime(
                        Constants.ReportRedisKey.TOTAL_STAFF_PREVIOUS + currentMonth,
                        previousMonthStaff,
                        lengthOfMonth,
                        TimeUnit.DAYS
                );
            }
        }
        return Pair.of(totalStaff, previousMonthStaff);
    }

    private Pair<Integer, Integer> getIdentityRecordOverview(String currentMonth, Integer lengthOfMonth) {
        int totalIdentity = 0;
        int previousMonthIdentity = 0;

        Optional<Integer> totalIdentityOptional = identityRecordRepository.getTotalIdentity(null);
        if (totalIdentityOptional.isPresent()) {
            totalIdentity = totalIdentityOptional.get();
        }

        Object identityRedisData = redisService.getValue(Constants.ReportRedisKey.TOTAL_IDENTITY_PREVIOUS + currentMonth);
        if (identityRedisData != null) {
            previousMonthIdentity = Integer.parseInt(String.valueOf(identityRedisData));
        } else {
            Optional<Integer> previousOptional = identityRecordRepository.getTotalIdentity(Boolean.TRUE);
            if (previousOptional.isPresent()) {
                previousMonthIdentity = previousOptional.get();
                redisService.setValueWithExpireTime(
                        Constants.ReportRedisKey.TOTAL_IDENTITY_PREVIOUS + currentMonth,
                        previousMonthIdentity,
                        lengthOfMonth,
                        TimeUnit.DAYS
                );
            }
        }
        return Pair.of(totalIdentity, previousMonthIdentity);
    }

    private Pair<Integer, Integer> getFingerOverview(String currentMonth, Integer lengthOfMonth) {
        int totalFinger = 0;
        int previousMonthFinger = 0;

        Optional<Integer> totalFingerOptional = fingerprintCardRepository.getTotalFingerCard(null);
        if (totalFingerOptional.isPresent()) {
            totalFinger = totalFingerOptional.get();
        }

        Object fingerRedisData = redisService.getValue(Constants.ReportRedisKey.TOTAL_FINGER_PREVIOUS + currentMonth);
        if (fingerRedisData != null) {
            previousMonthFinger = Integer.parseInt(String.valueOf(fingerRedisData));
        } else {
            Optional<Integer> previousOptional = fingerprintCardRepository.getTotalFingerCard(Boolean.TRUE);
            if (previousOptional.isPresent()) {
                previousMonthFinger = previousOptional.get();
                redisService.setValueWithExpireTime(
                        Constants.ReportRedisKey.TOTAL_FINGER_PREVIOUS + currentMonth,
                        previousMonthFinger,
                        lengthOfMonth,
                        TimeUnit.DAYS
                );
            }
        }
        return Pair.of(totalFinger, previousMonthFinger);
    }
}
