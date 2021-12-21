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

import gov.epa.cef.web.domain.EmissionsProcess;
import gov.epa.cef.web.domain.EmissionsReport;
import gov.epa.cef.web.exception.AppValidationException;
import gov.epa.cef.web.repository.EmissionsProcessRepository;
import gov.epa.cef.web.repository.EmissionsReportRepository;
import gov.epa.cef.web.service.EmissionsProcessService;
import gov.epa.cef.web.service.LookupService;
import gov.epa.cef.web.service.dto.EmissionsProcessDto;
import gov.epa.cef.web.service.dto.EmissionsProcessSaveDto;
import gov.epa.cef.web.service.dto.bulkUpload.EmissionsProcessBulkUploadDto;
import gov.epa.cef.web.service.mapper.BulkUploadMapper;
import gov.epa.cef.web.service.mapper.EmissionsProcessMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class EmissionsProcessServiceImpl implements EmissionsProcessService {

    @Autowired
    private EmissionsReportRepository reportRepo;

    @Autowired
    private EmissionsProcessRepository processRepo;

    @Autowired
    private LookupService lookupService;

    @Autowired
    private EmissionsProcessMapper emissionsProcessMapper;

    @Autowired
    private EmissionsReportStatusServiceImpl reportStatusService;
    
    @Autowired
    private BulkUploadMapper bulkUploadMapper;


    public EmissionsProcessDto create(EmissionsProcessSaveDto dto) {

        EmissionsProcess process = emissionsProcessMapper.fromSaveDto(dto);

        process.getReportingPeriods().forEach(period -> {
            if (period.getCalculationMaterialCode() != null) {
                period.setCalculationMaterialCode(lookupService.retrieveCalcMaterialCodeEntityByCode(period.getCalculationMaterialCode().getCode()));
            }

            if (period.getCalculationParameterTypeCode() != null) {
                period.setCalculationParameterTypeCode(lookupService.retrieveCalcParamTypeCodeEntityByCode(period.getCalculationParameterTypeCode().getCode()));
            }

            if (period.getCalculationParameterUom() != null) {
                period.setCalculationParameterUom(lookupService.retrieveUnitMeasureCodeEntityByCode(period.getCalculationParameterUom().getCode()));
            }

            if (period.getEmissionsOperatingTypeCode() != null) {
                period.setEmissionsOperatingTypeCode(lookupService.retrieveEmissionsOperatingTypeCodeEntityByCode(period.getEmissionsOperatingTypeCode().getCode()));
            }

            if (period.getReportingPeriodTypeCode() != null) {
                period.setReportingPeriodTypeCode(lookupService.retrieveReportingPeriodCodeEntityByCode(period.getReportingPeriodTypeCode().getCode()));
            }
            
            if (period.getFuelUseMaterialCode() != null) {
                period.setFuelUseMaterialCode(lookupService.retrieveCalcMaterialCodeEntityByCode(period.getFuelUseMaterialCode().getCode()));
            }
            
            if (period.getFuelUseUom() != null) {
                period.setFuelUseUom(lookupService.retrieveUnitMeasureCodeEntityByCode(period.getFuelUseUom().getCode()));
            }
            
            if (period.getHeatContentUom() != null) {
                period.setHeatContentUom(lookupService.retrieveUnitMeasureCodeEntityByCode(period.getHeatContentUom().getCode()));
            }

            period.setEmissionsProcess(process);

            period.getOperatingDetails().forEach(od -> {
                od.setReportingPeriod(period);
            });
        });

        process.getReleasePointAppts().forEach(appt -> {
            appt.clearId();
            appt.setEmissionsProcess(process);
        });

        EmissionsProcessDto result = emissionsProcessMapper.emissionsProcessToEmissionsProcessDto(processRepo.save(process));
        reportStatusService.resetEmissionsReportForEntity(Collections.singletonList(result.getId()), EmissionsProcessRepository.class);
        return result;
    }

    public EmissionsProcessDto update(EmissionsProcessSaveDto dto) {

        EmissionsProcess process = processRepo.findById(dto.getId()).orElse(null);
        emissionsProcessMapper.updateFromSaveDto(dto, process);

        EmissionsProcessDto result = emissionsProcessMapper.emissionsProcessToEmissionsProcessDto(processRepo.save(process));
        reportStatusService.resetEmissionsReportForEntity(Collections.singletonList(result.getId()), EmissionsProcessRepository.class);
        return result;
    }

    /* (non-Javadoc)
     * @see gov.epa.cef.web.service.impl.EmissionsProcessService#retrieveById(java.lang.Long)
     */
    @Override
    public EmissionsProcessDto retrieveById(Long id) {
        EmissionsProcess result = processRepo
            .findById(id)
            .orElse(null);
        return emissionsProcessMapper.emissionsProcessToEmissionsProcessDto(result);
    }

    /**
     * Retrieve versions of this process from the last year reported
     * @param id
     * @return
     */
    public EmissionsProcessDto retrievePreviousById(Long id) {
        EmissionsProcess process= processRepo
                .findById(id)
                .orElse(null);

        Long mfrId = processRepo.retrieveMasterFacilityRecordIdById(id).orElse(null);

        EmissionsProcess result = this.findPrevious(mfrId, 
                    process.getEmissionsUnit().getFacilitySite().getEmissionsReport().getYear(), 
                    process.getEmissionsProcessIdentifier(), 
                    process.getEmissionsUnit().getUnitIdentifier())
                .stream()
                .findFirst()
                .orElse(null);

        return emissionsProcessMapper.emissionsProcessToEmissionsProcessDto(result);
    }

    /* (non-Javadoc)
     * @see gov.epa.cef.web.service.impl.EmissionsProcessService#retrieveForReleasePoint(java.lang.Long)
     */
    @Override
    public List<EmissionsProcessDto> retrieveForReleasePoint(Long pointId) {
        List<EmissionsProcess> result = processRepo.findByReleasePointApptsReleasePointIdOrderByEmissionsProcessIdentifier(pointId);
        return emissionsProcessMapper.emissionsProcessesToEmissionsProcessDtos(result);
    }


    /**
     * Retrieve Emissions Processes for an Emissions Unit
     * @param emissionsUnitId
     * @return
     */
    public List<EmissionsProcessDto> retrieveForEmissionsUnit(Long emissionsUnitId) {
        List<EmissionsProcess> result = processRepo.findByEmissionsUnitIdOrderByEmissionsProcessIdentifier(emissionsUnitId);
        return emissionsProcessMapper.emissionsProcessesToEmissionsProcessDtos(result);
    }

    /**
     * Delete an Emissions Process for a given id
     * @param id
     */
    public void delete(Long id) {
        EmissionsProcess process = processRepo
                .findById(id)
                .orElse(null);

        Long mfrId = processRepo.retrieveMasterFacilityRecordIdById(id).orElse(null);

        this.findPrevious(mfrId, 
                process.getEmissionsUnit().getFacilitySite().getEmissionsReport().getYear(), 
                process.getEmissionsProcessIdentifier(), 
                process.getEmissionsUnit().getUnitIdentifier())
            .stream()
            .findFirst()
            .ifPresent(oldUnit -> {
                throw new AppValidationException("This Process has been submitted on previous years' facility reports, so it cannot be deleted. "
                        + "If this Process is no longer operational, please use the \"Operating Status\" field to mark this Process as \"Permanently Shutdown\".");
            });

        reportStatusService.resetEmissionsReportForEntity(Collections.singletonList(id), EmissionsProcessRepository.class);
        processRepo.deleteById(id);
    }

    /**
     * Find versions of this Process from the last year reported
     * @param mfrId
     * @param year
     * @param processIdentifier
     * @param unitIdentifier
     * @return
     */
    private List<EmissionsProcess> findPrevious(Long mfrId, Short year, String processIdentifier, String unitIdentifier) {

        // find the last year reported
        Optional<EmissionsReport> lastReport = reportRepo.findFirstByMasterFacilityRecordIdAndYearLessThanOrderByYearDesc(mfrId,
                year);

        // check if the emissions process was reported last year
        if (lastReport.isPresent()) {
            return processRepo.retrieveByIdentifierParentFacilityYear(processIdentifier,
                    unitIdentifier, 
                    mfrId, 
                    lastReport.get().getYear());
        }

        return Collections.emptyList();
    }


    /**
     * Retrieve a list of emissions processes for the given program system code and emissions report year
     * @param programSystemCode
     * @param emissionsReportYear
     * @return
     */     
    public List<EmissionsProcessBulkUploadDto> retrieveEmissionsProcesses(String programSystemCode, Short emissionsReportYear) {
    	List<EmissionsProcess> processes = processRepo.findByPscAndEmissionsReportYear(programSystemCode, emissionsReportYear);
    	return bulkUploadMapper.emissionsProcessToDtoList(processes);
    }

}
