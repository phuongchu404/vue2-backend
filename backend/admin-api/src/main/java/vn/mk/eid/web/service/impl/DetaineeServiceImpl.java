package vn.mk.eid.web.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import vn.mk.eid.common.constant.Constants;
import vn.mk.eid.common.constant.ExceptionConstants;
import vn.mk.eid.common.dao.entity.DetaineeEntity;
import vn.mk.eid.common.dao.entity.DetentionCenterEntity;
import vn.mk.eid.common.dao.entity.DetentionHistoryEntity;
import vn.mk.eid.common.dao.entity.WardEntity;
import vn.mk.eid.common.dao.repository.DetaineeRepository;
import vn.mk.eid.common.dao.repository.DetentionHistoryRepository;
import vn.mk.eid.common.dao.repository.DetentionCenterRepository;
import vn.mk.eid.common.dao.repository.WardRepository;
import vn.mk.eid.common.data.ServiceResult;
import vn.mk.eid.web.constant.DetaineeStatus;
import vn.mk.eid.web.constant.DetentionHistoryType;
import vn.mk.eid.web.constant.Gender;
import vn.mk.eid.web.dto.request.detainee.DetaineeCreateRequest;
import vn.mk.eid.web.dto.request.detainee.DetaineeUpdateRequest;
import vn.mk.eid.web.dto.request.detainee.QueryDetaineeRequest;
import vn.mk.eid.web.dto.response.DetaineeResponse;
import vn.mk.eid.web.exception.ResourceNotFoundException;
import vn.mk.eid.web.repository.DetaineeRepositoryCustom;
import vn.mk.eid.web.service.DetaineeService;
import vn.mk.eid.web.service.SequenceService;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.time.ZoneId;
@Service
@RequiredArgsConstructor
@Slf4j
public class DetaineeServiceImpl implements DetaineeService {
    private final DetaineeRepository detaineeRepository;
    private final DetaineeRepositoryCustom detaineeRepositoryCustom;
    private final DetentionCenterRepository detentionCenterRepository;
	private final WardRepository wardRepository;
    private final DetentionHistoryRepository detaineeHistoryRepository;
    private final SequenceService sequenceService;

    @Override
    public ServiceResult createDetainee(DetaineeCreateRequest request) {
        DetentionCenterEntity detentionCenter = detentionCenterRepository.findById(request.getDetentionCenterId())
                .orElseThrow(() -> new ResourceNotFoundException(ExceptionConstants.DETAINEE_NOT_FOUND));

        String detaineeCode = sequenceService.genCode(Constants.CodePrefix.DETAINEE_CODE);
        DetaineeEntity detainee = new DetaineeEntity();
        detainee.setDetaineeCode(detaineeCode);
        detainee.setGender(request.getGender());
        detainee.setProfileNumber(request.getProfileNumber());
        detainee.setFullName(request.getFullName());
        detainee.setAliasName(request.getAliasName());
        detainee.setDateOfBirth(request.getDateOfBirth());
        detainee.setPlaceOfBirth(request.getPlaceOfBirth());
        detainee.setIdNumber(request.getIdNumber());
        detainee.setIdIssueDate(request.getIdIssueDate());
        detainee.setIdIssuePlace(request.getIdIssuePlace());

        detainee.setEthnicityId(request.getEthnicityId());
        detainee.setReligionId(request.getReligionId());
        detainee.setNationalityId(request.getNationalityId());

        // Address information
        detainee.setPermanentAddress(request.getPermanentAddress());
        detainee.setPermanentWardId(request.getPermanentWardId());
        detainee.setTemporaryAddress(request.getTemporaryAddress());
        detainee.setTemporaryWardId(request.getTemporaryWardId());
        detainee.setCurrentAddress(request.getCurrentAddress());
        detainee.setCurrentWardId(request.getCurrentWardId());

        // Occupation and family
        detainee.setOccupation(request.getOccupation());
        detainee.setFatherName(request.getFatherName());
        detainee.setMotherName(request.getMotherName());
        detainee.setSpouseName(request.getSpouseName());

        // Legal information
        detainee.setDetentionDate(request.getDetentionDate());
        detainee.setExpectedReleaseDate(request.getExpectedReleaseDate());
        detainee.setCaseNumber(request.getCaseNumber());
        detainee.setCharges(request.getCharges());
        detainee.setSentenceDuration(request.getSentenceDuration());
        detainee.setCourtName(request.getCourtName());

        // Detention information
        detainee.setDetentionCenterId(detentionCenter.getId());

        detainee.setCellNumber(request.getCellNumber());
        detainee.setStatus(DetaineeStatus.DETAINED.name());
        detainee.setNotes(request.getNotes());

        detainee = detaineeRepository.save(detainee);

        // Create initial detention history
        createDetentionHistory(detainee, detentionCenter, request.getDetentionDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate(), request.getCellNumber(), "Initial detention", DetentionHistoryType.INITIAL.name());

        // Update detention center population
        updateDetentionCenterPopulation(detentionCenter);

        log.info("Created detainee with code: {}", detaineeCode);
        return ServiceResult.ok(convertToResponse(detainee, detentionCenter));
    }

    @Override
    public ServiceResult updateDetainee(Long id, DetaineeUpdateRequest request) {
        DetaineeEntity detainee = detaineeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ExceptionConstants.DETAINEE_NOT_FOUND));
        DetentionCenterEntity detentionCenter = detentionCenterRepository.findById(request.getDetentionCenterId())
                .orElseThrow(() -> new ResourceNotFoundException(ExceptionConstants.DETENTION_CENTER_NOT_FOUND));

        // Update basic information
        detainee.setFullName(request.getFullName());
        detainee.setAliasName(request.getAliasName());
        detainee.setGender(Gender.getCodeById(request.getGender()));
        detainee.setDateOfBirth(request.getDateOfBirth());
        detainee.setPlaceOfBirth(request.getPlaceOfBirth());
        detainee.setIdNumber(request.getIdNumber());
        detainee.setIdIssueDate(request.getIdIssueDate());
        detainee.setIdIssuePlace(request.getIdIssuePlace());

        detainee.setEthnicityId(request.getEthnicityId());
        detainee.setReligionId(request.getReligionId());
        detainee.setNationalityId(request.getNationalityId());

        // Update addresses
        detainee.setPermanentAddress(request.getPermanentAddress());
        detainee.setPermanentWardId(request.getPermanentWardId());
        detainee.setTemporaryAddress(request.getTemporaryAddress());
        detainee.setTemporaryWardId(request.getTemporaryWardId());
        detainee.setCurrentAddress(request.getCurrentAddress());
        detainee.setCurrentWardId(request.getCurrentWardId());

        // Update other fields
        detainee.setOccupation(request.getOccupation());
        detainee.setFatherName(request.getFatherName());
        detainee.setMotherName(request.getMotherName());
        detainee.setSpouseName(request.getSpouseName());
        detainee.setExpectedReleaseDate(request.getExpectedReleaseDate());
        detainee.setCaseNumber(request.getCaseNumber());
        detainee.setCharges(request.getCharges());
        detainee.setSentenceDuration(request.getSentenceDuration());
        detainee.setCourtName(request.getCourtName());
        detainee.setCellNumber(request.getCellNumber());
        detainee.setNotes(request.getNotes());

        detainee = detaineeRepository.save(detainee);
        log.info("Updated detainee with ID: {}", id);
        return ServiceResult.ok(convertToResponse(detainee, detentionCenter));
    }

    @Override
    public ServiceResult getDetainee(Long id) {
        DetaineeEntity detainee = detaineeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ExceptionConstants.DETAINEE_NOT_FOUND));
        DetentionCenterEntity detentionCenter = detentionCenterRepository.findById(detainee.getDetentionCenterId())
                .orElseThrow(() -> new ResourceNotFoundException(ExceptionConstants.DETENTION_CENTER_NOT_FOUND));
        return ServiceResult.ok(convertToResponse(detainee, detentionCenter));
    }

    @Override
    public ServiceResult getDetaineeByCode(String code) {
        DetaineeEntity detainee = detaineeRepository.findByDetaineeCode(code)
                .orElseThrow(() -> new ResourceNotFoundException(ExceptionConstants.DETAINEE_NOT_FOUND));
        DetentionCenterEntity detentionCenter = detentionCenterRepository.findById(detainee.getDetentionCenterId())
                .orElseThrow(() -> new ResourceNotFoundException(ExceptionConstants.DETENTION_CENTER_NOT_FOUND));
        return ServiceResult.ok(convertToResponse(detainee, detentionCenter));
    }

    @Override
    public ServiceResult getWithPaging(QueryDetaineeRequest request, Pageable pageable) {
        Page<DetaineeResponse> page = detaineeRepositoryCustom.getWithPaging(request, pageable);
        return ServiceResult.ok(page);
    }

//    @Override
//    public ServiceResult releaseDetainee(Long id, Date releaseDate, String reason) {
//        DetaineeEntity detainee = detaineeRepository.findById(id)
//                .orElseThrow(() -> new ResourceNotFoundException("Detainee not found"));
//
//        detainee.setActualReleaseDate(releaseDate);
//        detainee.setStatus("RELEASED");
//
//        // End current detention history
//        DetentionHistory currentDetention = detentionHistoryRepository.findCurrentDetention(detainee)
//                .orElseThrow(() -> new RuntimeException("Current detention history not found"));
//        currentDetention.setEndDate(releaseDate);
//        detentionHistoryRepository.save(currentDetention);
//
//        detainee = detaineeRepository.save(detainee);
//
//        // Update detention center population
//        updateDetentionCenterPopulation(detainee.getDetentionCenter());
//
//        log.info("Released detainee with ID: {} on {}", id, releaseDate);
//        return convertToResponse(detainee);
//    }
//    public DetaineeResponse transferDetainee(Long id, Long toDetentionCenterId, LocalDate transferDate,
//                                             String cellNumber, String reason) {
//        Detainee detainee = detaineeRepository.findById(id)
//                .orElseThrow(() -> new ResourceNotFoundException("Detainee not found"));
//
//        DetentionCenter fromCenter = detainee.getDetentionCenter();
//        DetentionCenter toCenter = detentionCenterRepository.findById(toDetentionCenterId)
//                .orElseThrow(() -> new ResourceNotFoundException("Target Detention Center not found"));
//
//        // End current detention history
//        DetentionHistory currentDetention = detentionHistoryRepository.findCurrentDetention(detainee)
//                .orElseThrow(() -> new RuntimeException("Current detention history not found"));
//        currentDetention.setEndDate(transferDate);
//        detentionHistoryRepository.save(currentDetention);
//
//        // Update detainee information
//        detainee.setDetentionCenter(toCenter);
//        detainee.setCellNumber(cellNumber);
//
//        // Create new detention history
//        createDetentionHistory(detainee, toCenter, transferDate, cellNumber, reason);
//
//        // Create transfer history
//        // Note: You'll need to create TransferHistory entity and repository
//        // createTransferHistory(detainee, fromCenter, toCenter, transferDate, reason);
//
//        detainee = detaineeRepository.save(detainee);
//
//        // Update both centers' population
//        updateDetentionCenterPopulation(fromCenter);
//        updateDetentionCenterPopulation(toCenter);
//
//        log.info("Transferred detainee with ID: {} from {} to {}", id, fromCenter.getName(), toCenter.getName());
//        return convertToResponse(detainee);
//    }

    @Override
    public ServiceResult deleteDetainee(Long id) {
        DetaineeEntity detainee = detaineeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ExceptionConstants.DETAINEE_NOT_FOUND));

        Integer centerId = detainee.getDetentionCenterId();
        detaineeRepository.delete(detainee);

        if (centerId != null) {
            DetentionCenterEntity center = detentionCenterRepository.findById(centerId)
                    .orElseThrow(() -> new ResourceNotFoundException(ExceptionConstants.DETENTION_CENTER_NOT_FOUND));
            updateDetentionCenterPopulation(center);
        }

        log.info("Deleted detainee with ID: {}", id);
        return ServiceResult.ok();
    }

    @Override
    public ServiceResult getAllNoPaging() {
        List<DetaineeEntity> detainee = detaineeRepository.findAll();

        return ServiceResult.ok(detainee.stream().map(this::convertToResponse));
    }

    @Override
    public ServiceResult getTop3NewestDetainees() {
        List<DetaineeEntity> detainees = detaineeRepository.findTop3OrderByUpdatedAtDesc(DetaineeStatus.DETAINED.toString(), PageRequest.of(0,3));
        return ServiceResult.ok(detainees.stream().map(this::convertToResponse));
    }

    @Override
    public ServiceResult getDetaineeCount() {
        Long count = detaineeRepository.countByStatusDetained();
        return ServiceResult.ok(count);
    }


    private String generateDetaineeCode() {
        String prefix = "DN";
        String year = String.valueOf(LocalDate.now().getYear());
        long count = detaineeRepository.count() + 1;
        return String.format("%s%s%06d", prefix, year, count);
    }

    private DetaineeResponse convertToResponse(DetaineeEntity detainee, DetentionCenterEntity detentionCenter) {
        DetaineeResponse response = new DetaineeResponse();
        response.setId(detainee.getId());
        response.setDetaineeCode(detainee.getDetaineeCode());
        response.setProfileNumber(detainee.getProfileNumber());
        response.setFullName(detainee.getFullName());
        response.setAliasName(detainee.getAliasName());
        response.setGender(detainee.getGender());
        response.setDateOfBirth(detainee.getDateOfBirth());
        response.setPlaceOfBirth(detainee.getPlaceOfBirth());
        response.setIdNumber(detainee.getIdNumber());
        response.setIdIssueDate(detainee.getIdIssueDate());
        response.setIdIssuePlace(detainee.getIdIssuePlace());
        response.setNationalityId(detainee.getNationalityId());
        response.setEthnicityId(detainee.getEthnicityId());
        response.setReligionId(detainee.getReligionId());

        response.setPermanentAddress(detainee.getPermanentAddress());
        response.setPermanentWardId(detainee.getPermanentWardId());
        response.setTemporaryAddress(detainee.getTemporaryAddress());
        response.setTemporaryWardId(detainee.getTemporaryWardId());
        response.setCurrentAddress(detainee.getCurrentAddress());
        response.setCurrentWardId(detainee.getCurrentWardId());
        if(detainee.getPermanentWardId() != null) {
            Optional<WardEntity> optionalWard = wardRepository.findById(detainee.getPermanentWardId());
            optionalWard.ifPresent(wardEntity -> {
                response.setPermanentWardId(wardEntity.getCode());
                response.setPermanentProvinceId(wardEntity.getProvince().getCode());
            });
        }
        if(detainee.getTemporaryWardId() != null) {
            Optional<WardEntity> optionalWard = wardRepository.findById(detainee.getTemporaryWardId());
            optionalWard.ifPresent(wardEntity -> {
                response.setTemporaryWardId(wardEntity.getCode());
                response.setTemporaryProvinceId(wardEntity.getProvince().getCode());
            });
        }
        if(detainee.getCurrentWardId() != null) {
            Optional<WardEntity> optionalWard = wardRepository.findById(detainee.getCurrentWardId());
            optionalWard.ifPresent(wardEntity -> {
                response.setCurrentWardId(wardEntity.getCode());
                response.setCurrentProvinceId(wardEntity.getProvince().getCode());
            });
        }

        response.setOccupation(detainee.getOccupation());
        response.setFatherName(detainee.getFatherName());
        response.setMotherName(detainee.getMotherName());
        response.setSpouseName(detainee.getSpouseName());
        response.setDetentionDate(detainee.getDetentionDate());
        response.setExpectedReleaseDate(detainee.getExpectedReleaseDate());
        response.setActualReleaseDate(detainee.getActualReleaseDate());
        response.setCaseNumber(detainee.getCaseNumber());
        response.setCharges(detainee.getCharges());
        response.setSentenceDuration(detainee.getSentenceDuration());
        response.setCourtName(detainee.getCourtName());

        response.setDetentionCenterId(detainee.getDetentionCenterId());
        response.setDetentionCenterCode(detentionCenter.getCode());
        response.setDetentionCenterName(detentionCenter.getName());

        response.setCellNumber(detainee.getCellNumber());
        response.setStatus(detainee.getStatus());
        response.setNotes(detainee.getNotes());
        response.setCreatedAt(detainee.getCreatedAt());
        response.setUpdatedAt(detainee.getUpdatedAt());

        return response;
    }

	private DetaineeResponse convertToResponse(DetaineeEntity detainee) {
        DetaineeResponse response = new DetaineeResponse();
        response.setId(detainee.getId());
        response.setDetaineeCode(detainee.getDetaineeCode());
        response.setProfileNumber(detainee.getProfileNumber());
        response.setFullName(detainee.getFullName());
        response.setCharges(detainee.getCharges());
        return response;
    }

    private void createDetentionHistory(DetaineeEntity detainee, DetentionCenterEntity detentionCenter,
                                    LocalDate startDate, String cellNumber, String reason, String type) {
        DetentionHistoryEntity history = new DetentionHistoryEntity();
        history.setDetaineeId(detainee.getId());
        history.setDetentionCenterId(detentionCenter.getId());
        history.setStartDate(startDate);
        history.setCellNumber(cellNumber);
        history.setReason(reason);
        history.setType(type);
        detaineeHistoryRepository.save(history);
    }

    private void updateDetentionCenterPopulation(DetentionCenterEntity center) {
        long currentPopulation = detaineeRepository.countCurrentDetainees(center.getId());
        center.setCurrentPopulation((int) currentPopulation);
        detentionCenterRepository.save(center);
    }
}
