/*
 * © Copyright 2019 EPA CAERS Project Team
 *
 * This file is part of the Common Air Emissions Reporting System (CAERS).
 *
 * CAERS is free software: you can redistribute it and/or modify it under the 
 * terms of the GNU General Public License as published by the Free Software Foundation, 
 * either version 3 of the License, or (at your option) any later version.
 *
 * CAERS is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without 
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the 
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with CAERS.  If 
 * not, see <https://www.gnu.org/licenses/>.
*/
package gov.epa.cef.web.service.impl;

import gov.epa.cef.web.domain.Emission;
import gov.epa.cef.web.domain.EmissionsReport;
import gov.epa.cef.web.domain.FacilitySourceTypeCode;
import gov.epa.cef.web.domain.ReportingPeriod;
import gov.epa.cef.web.exception.ApplicationException;
import gov.epa.cef.web.exception.NotExistException;
import gov.epa.cef.web.repository.EmissionsReportRepository;
import gov.epa.cef.web.repository.ReportingPeriodRepository;
import gov.epa.cef.web.service.LookupService;
import gov.epa.cef.web.service.ReportingPeriodService;
import gov.epa.cef.web.service.dto.EmissionBulkEntryHolderDto;
import gov.epa.cef.web.service.dto.ReportingPeriodBulkEntryDto;
import gov.epa.cef.web.service.dto.ReportingPeriodDto;
import gov.epa.cef.web.service.dto.ReportingPeriodUpdateResponseDto;
import gov.epa.cef.web.service.dto.bulkUpload.ReportingPeriodBulkUploadDto;
import gov.epa.cef.web.service.mapper.BulkUploadMapper;
import gov.epa.cef.web.service.mapper.ReportingPeriodMapper;
import gov.epa.cef.web.util.CalculationUtils;
import gov.epa.cef.web.util.ConstantUtils;
import gov.epa.cef.web.util.MassUomConversion;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ReportingPeriodServiceImpl implements ReportingPeriodService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private ReportingPeriodRepository repo;

    @Autowired
    private EmissionsReportRepository reportRepo;

    @Autowired
    private LookupService lookupService;

    @Autowired
    private ReportingPeriodMapper mapper;

    @Autowired
    private EmissionsReportStatusServiceImpl reportStatusService;

    @Autowired
    private EmissionServiceImpl emissionService;
    
    @Autowired
    private BulkUploadMapper bulkUploadMapper;

    public ReportingPeriodDto create(ReportingPeriodDto dto) {

        ReportingPeriod period = mapper.fromDto(dto);

        if (dto.getCalculationMaterialCode() != null) {
            period.setCalculationMaterialCode(lookupService.retrieveCalcMaterialCodeEntityByCode(dto.getCalculationMaterialCode().getCode()));
        }

        if (dto.getCalculationParameterTypeCode() != null) {
            period.setCalculationParameterTypeCode(lookupService.retrieveCalcParamTypeCodeEntityByCode(dto.getCalculationParameterTypeCode().getCode()));
        }

        if (dto.getCalculationParameterUom() != null) {
            period.setCalculationParameterUom(lookupService.retrieveUnitMeasureCodeEntityByCode(dto.getCalculationParameterUom().getCode()));
        }

        if (dto.getEmissionsOperatingTypeCode() != null) {
            period.setEmissionsOperatingTypeCode(lookupService.retrieveEmissionsOperatingTypeCodeEntityByCode(dto.getEmissionsOperatingTypeCode().getCode()));
        }

        if (dto.getReportingPeriodTypeCode() != null) {
            period.setReportingPeriodTypeCode(lookupService.retrieveReportingPeriodCodeEntityByCode(dto.getReportingPeriodTypeCode().getCode()));
        }
        
        if (dto.getFuelUseMaterialCode() != null) {
            period.setFuelUseMaterialCode(lookupService.retrieveCalcMaterialCodeEntityByCode(dto.getFuelUseMaterialCode().getCode()));
        }
        
        if (dto.getFuelUseUom() != null) {
            period.setFuelUseUom(lookupService.retrieveUnitMeasureCodeEntityByCode(dto.getFuelUseUom().getCode()));
        }
        
        if (dto.getHeatContentUom() != null) {
            period.setHeatContentUom(lookupService.retrieveUnitMeasureCodeEntityByCode(dto.getHeatContentUom().getCode()));
        }

        period.getOperatingDetails().forEach(od -> {
            od.setReportingPeriod(period);
        });

        ReportingPeriodDto result = mapper.toDto(repo.save(period));
        reportStatusService.resetEmissionsReportForEntity(Collections.singletonList(result.getId()), ReportingPeriodRepository.class);
        return result;
    }

    public ReportingPeriodUpdateResponseDto update(ReportingPeriodDto dto) {

        ReportingPeriod period = repo.findById(dto.getId())
            .orElseThrow(() -> new NotExistException("Reporting Period", dto.getId()));

        mapper.updateFromDto(dto, period);

        if (dto.getCalculationMaterialCode() != null) {
            period.setCalculationMaterialCode(lookupService.retrieveCalcMaterialCodeEntityByCode(dto.getCalculationMaterialCode().getCode()));
        }

        if (dto.getCalculationParameterTypeCode() != null) {
            period.setCalculationParameterTypeCode(lookupService.retrieveCalcParamTypeCodeEntityByCode(dto.getCalculationParameterTypeCode().getCode()));
        }

        if (dto.getCalculationParameterUom() != null) {
            period.setCalculationParameterUom(lookupService.retrieveUnitMeasureCodeEntityByCode(dto.getCalculationParameterUom().getCode()));
        }

        if (dto.getEmissionsOperatingTypeCode() != null) {
            period.setEmissionsOperatingTypeCode(lookupService.retrieveEmissionsOperatingTypeCodeEntityByCode(dto.getEmissionsOperatingTypeCode().getCode()));
        }

        if (dto.getReportingPeriodTypeCode() != null) {
            period.setReportingPeriodTypeCode(lookupService.retrieveReportingPeriodCodeEntityByCode(dto.getReportingPeriodTypeCode().getCode()));
        }
        
        if (dto.getFuelUseMaterialCode() != null) {
            period.setFuelUseMaterialCode(lookupService.retrieveCalcMaterialCodeEntityByCode(dto.getFuelUseMaterialCode().getCode()));
        } else {
        	period.setFuelUseMaterialCode(null);
        }
        
        if (dto.getFuelUseUom() != null) {
            period.setFuelUseUom(lookupService.retrieveUnitMeasureCodeEntityByCode(dto.getFuelUseUom().getCode()));
        } else {
        	period.setFuelUseUom(null);
        }
        
        if (dto.getHeatContentUom() != null) {
            period.setHeatContentUom(lookupService.retrieveUnitMeasureCodeEntityByCode(dto.getHeatContentUom().getCode()));
        } else {
        	period.setHeatContentUom(null);
        }

        ReportingPeriodUpdateResponseDto result = new ReportingPeriodUpdateResponseDto();

        period.getEmissions().forEach(emission -> {
            if (!(Boolean.TRUE.equals(emission.getTotalManualEntry()) 
                    || Boolean.TRUE.equals(emission.getEmissionsCalcMethodCode().getTotalDirectEntry()))) {
                try {
                    Emission updatedEmission = emissionService.calculateTotalEmissions(emission, period);
                    emission.setEmissionsFactor(updatedEmission.getEmissionsFactor());
                    emission.setTotalEmissions(updatedEmission.getTotalEmissions());
                    result.getUpdatedEmissions().add(emission.getPollutant().getPollutantName());
                } catch (ApplicationException e) {
                    result.getFailedEmissions().add(emission.getPollutant().getPollutantName());
                }
            } else {
                result.getNotUpdatedEmissions().add(emission.getPollutant().getPollutantName());
            }

            emission.setCalculatedEmissionsTons(calculateEmissionTons(emission));
        });

        ReportingPeriodDto resultDto = mapper.toDto(repo.save(period));
        reportStatusService.resetEmissionsReportForEntity(Collections.singletonList(resultDto.getId()), ReportingPeriodRepository.class);

        result.setReportingPeriod(resultDto);
        return result;
    }

    /* (non-Javadoc)
     * @see gov.epa.cef.web.service.impl.ReportingPeriodService#retrieveById(java.lang.Long)
     */
    @Override
    public ReportingPeriodDto retrieveById(Long id) {
        ReportingPeriod result = repo
            .findById(id)
            .orElse(null);
        return mapper.toDto(result);
    }

    /* (non-Javadoc)
     * @see gov.epa.cef.web.service.impl.ReportingPeriodService#retrieveForReleasePoint(java.lang.Long)
     */
    @Override
    public List<ReportingPeriodDto> retrieveForEmissionsProcess(Long processId) {
        List<ReportingPeriod> result = repo.findByEmissionsProcessId(processId);
        return mapper.toDtoList(result);
    }

    /**
     * Retrieve Reporting Periods for Bulk Entry for a specific facility site
     * @param facilitySiteId
     * @return
     */
    public List<ReportingPeriodBulkEntryDto> retrieveBulkEntryReportingPeriodsForFacilitySite(Long facilitySiteId) {

        List<ReportingPeriod> entities = repo.findByFacilitySiteId(facilitySiteId).stream()
                .filter(rp -> !"PS".equals(rp.getEmissionsProcess().getOperatingStatusCode().getCode()))
                .filter(rp -> {
                    FacilitySourceTypeCode typeCode = rp.getEmissionsProcess().getEmissionsUnit().getFacilitySite().getFacilitySourceTypeCode();
                    return !"PS".equals(rp.getEmissionsProcess().getEmissionsUnit().getOperatingStatusCode().getCode())
                            || (typeCode != null && ConstantUtils.FACILITY_SOURCE_LANDFILL_CODE.contentEquals(typeCode.getCode()));
                }).collect(Collectors.toList());

        List<ReportingPeriodBulkEntryDto> result = mapper.toBulkEntryDtoList(entities);

        if (!entities.isEmpty()) {
            // find the last year reported
            Optional<EmissionsReport> lastReport = reportRepo.findFirstByMasterFacilityRecordIdAndYearLessThanOrderByYearDesc(
                    entities.get(0).getEmissionsProcess().getEmissionsUnit().getFacilitySite().getEmissionsReport().getMasterFacilityRecord().getId(),
                    entities.get(0).getEmissionsProcess().getEmissionsUnit().getFacilitySite().getEmissionsReport().getYear());

            if (lastReport.isPresent()) {
                result.forEach(dto -> {
                    List<ReportingPeriod> oldEntities = repo.retrieveByTypeIdentifierParentFacilityYear(dto.getReportingPeriodTypeCode().getCode(),
                            dto.getEmissionsProcessIdentifier(),
                            dto.getUnitIdentifier(), 
                            lastReport.get().getMasterFacilityRecord().getId(), 
                            lastReport.get().getYear());
                    if (!oldEntities.isEmpty()) {
                        dto.setPreviousCalculationParameterValue(oldEntities.get(0).getCalculationParameterValue().toString());
                        if (oldEntities.get(0).getCalculationParameterUom() != null) {
                        	dto.setPreviousCalculationParameterUomCode(oldEntities.get(0).getCalculationParameterUom().getCode());
                        } else {
                        	dto.setPreviousCalculationParameterUomCode(null);
                        }
                    }
                });
            }
        }

        return result;
    }

    /**
     * Update the throughput for multiple Reporting Periods at once
     * @param facilitySiteId
     * @param dtos
     * @return
     */
    public List<EmissionBulkEntryHolderDto> bulkUpdate(Long facilitySiteId, List<ReportingPeriodBulkEntryDto> dtos) {

        List<ReportingPeriod> periods = dtos.stream().map(dto -> {
            ReportingPeriod period = repo.findById(dto.getReportingPeriodId())
                    .orElseThrow(() -> new NotExistException("Reporting Period", dto.getReportingPeriodId()));
            period.setCalculationParameterValue(new BigDecimal(dto.getCalculationParameterValue()));
            return period;
        }).collect(Collectors.toList());

        repo.saveAll(periods);

        return emissionService.bulkUpdate(facilitySiteId, Collections.emptyList());
    }

    private BigDecimal calculateEmissionTons(Emission emission) {
        try {
            BigDecimal calculatedEmissionsTons = CalculationUtils.convertMassUnits(emission.getTotalEmissions(), 
                    MassUomConversion.valueOf(emission.getEmissionsUomCode().getCode()), 
                    MassUomConversion.TON);
            return calculatedEmissionsTons;
        } catch (IllegalArgumentException ex) {
            logger.debug("Could not perform emission conversion. {}", ex.getLocalizedMessage());
            return null;
        }
    }


    /**
     * Retrieve a list of reporting periods for the given program system code and emissions report year
     * @param programSystemCode
     * @param emissionsReportYear
     * @return
     */ 
    public List<ReportingPeriodBulkUploadDto>retrieveReportingPeriods(String programSystemCode, Short emissionsReportYear) {
    	List<ReportingPeriod> reportingPeriods = repo.findByPscAndEmissionsReportYear(programSystemCode, emissionsReportYear);
    	return bulkUploadMapper.reportingPeriodToDtoList(reportingPeriods);
    }

}
