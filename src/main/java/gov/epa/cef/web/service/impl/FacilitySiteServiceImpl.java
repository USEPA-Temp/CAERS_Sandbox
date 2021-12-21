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

import gov.epa.cef.web.domain.FacilityNAICSXref;
import gov.epa.cef.web.domain.FacilitySite;
import gov.epa.cef.web.domain.NaicsCode;
import gov.epa.cef.web.domain.OperatingStatusCode;
import gov.epa.cef.web.repository.FacilityNAICSXrefRepository;
import gov.epa.cef.web.repository.FacilitySiteRepository;
import gov.epa.cef.web.repository.NaicsCodeRepository;
import gov.epa.cef.web.service.FacilitySiteService;
import gov.epa.cef.web.service.dto.FacilityNAICSDto;
import gov.epa.cef.web.service.dto.FacilitySiteDto;
import gov.epa.cef.web.service.dto.bulkUpload.FacilityNAICSBulkUploadDto;
import gov.epa.cef.web.service.dto.bulkUpload.FacilitySiteBulkUploadDto;
import gov.epa.cef.web.service.mapper.BulkUploadMapper;
import gov.epa.cef.web.service.mapper.FacilityNAICSMapper;
import gov.epa.cef.web.service.mapper.FacilitySiteMapper;
import gov.epa.cef.web.util.ConstantUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@Transactional(propagation = Propagation.REQUIRED)
public class FacilitySiteServiceImpl implements FacilitySiteService {

    private final FacilitySiteRepository facSiteRepo;

    private final FacilityNAICSXrefRepository facilityNaicsXrefRepo;
    
    private final NaicsCodeRepository naicsCodeRepo;

    private final EmissionsReportStatusServiceImpl reportStatusService;

    private final FacilitySiteMapper facilitySiteMapper;

    private final FacilityNAICSMapper facilityNaicsMapper;
    
    private BulkUploadMapper bulkUploadMapper;

    @Autowired
    FacilitySiteServiceImpl(FacilitySiteRepository facSiteRepo,
                            FacilitySiteMapper facilitySiteMapper,
                            FacilityNAICSXrefRepository facilityNaicsXrefRepo,
                            NaicsCodeRepository naicsCodeRepo,
                            FacilityNAICSMapper facilityNaicsMapper,
                            EmissionsReportStatusServiceImpl reportStatusService,
                            BulkUploadMapper bulkUploadMapper) {

        this.facSiteRepo = facSiteRepo;
        this.facilitySiteMapper = facilitySiteMapper;
        this.facilityNaicsXrefRepo = facilityNaicsXrefRepo;
        this.naicsCodeRepo = naicsCodeRepo;
        this.facilityNaicsMapper = facilityNaicsMapper;
        this.reportStatusService = reportStatusService;
        this.bulkUploadMapper = bulkUploadMapper;
    }

    @Override
    public FacilitySiteDto findByReportId(Long emissionsReportId) {

        return facSiteRepo.findByEmissionsReportId(emissionsReportId)
            .stream()
            .findFirst()
            .map(facilitySiteMapper::toDto)
            .orElse(null);
    }

    @Override
    public FacilitySiteDto findById(Long id) {

        return facSiteRepo.findById(id)
            .map(facilitySiteMapper::toDto)
            .orElse(null);
    }

    @Override
    public FacilitySite transform(FacilitySiteDto dto) {

        return this.facilitySiteMapper.fromDto(dto);
    }

    public FacilitySiteDto update(FacilitySiteDto dto) {

    	FacilitySite facilitySite = facSiteRepo.findById(dto.getId()).orElse(null);
    	
    	boolean isLandfill = (facilitySite.getFacilitySourceTypeCode() != null
    			&& facilitySite.getFacilitySourceTypeCode().getCode().equals(ConstantUtils.FACILITY_SOURCE_LANDFILL_CODE)) ? true : false;
    	
    	if(!(dto.getOperatingStatusCode().getCode().equals(facilitySite.getOperatingStatusCode().getCode())) && !isLandfill) {
    		
			OperatingStatusCode tempOperatingStatusCode = new OperatingStatusCode();
			tempOperatingStatusCode.setCode(dto.getOperatingStatusCode().getCode());
			
        	Short tempStatusYear = dto.getStatusYear();
        	
        	if(dto.getOperatingStatusCode().getCode().contentEquals(ConstantUtils.STATUS_OPERATING)
        			|| dto.getOperatingStatusCode().getCode().contentEquals(ConstantUtils.STATUS_PERMANENTLY_SHUTDOWN)
        			|| dto.getOperatingStatusCode().getCode().contentEquals(ConstantUtils.STATUS_TEMPORARILY_SHUTDOWN)) {
        		
	        	facilitySite.getEmissionsUnits().forEach(unit -> {
	        		if(!unit.getOperatingStatusCode().getCode().contentEquals("PS")){
	    	        	unit.setOperatingStatusCode(tempOperatingStatusCode);
	    	        	unit.setStatusYear(tempStatusYear);
	    	        	unit.getEmissionsProcesses().forEach(process -> {
	    	        		if(!process.getOperatingStatusCode().getCode().contentEquals("PS")){
	            	        	process.setOperatingStatusCode(tempOperatingStatusCode);
	            	        	process.setStatusYear(tempStatusYear);
	    	        		}
	    	        	});
	        		}
	        	});
	        	
	        	facilitySite.getControls().forEach(control -> {
	        		if(!control.getOperatingStatusCode().getCode().contentEquals("PS")){
	        			control.setOperatingStatusCode(tempOperatingStatusCode);
	        		}
	        	});
	        	
	        	facilitySite.getReleasePoints().forEach(releasePoint -> {
	        		if(!releasePoint.getOperatingStatusCode().getCode().contentEquals("PS")){
	    	        	releasePoint.setOperatingStatusCode(tempOperatingStatusCode);
	    	        	releasePoint.setStatusYear(tempStatusYear);
	        		}
	        	});
        	}
        }
   
    	facilitySiteMapper.updateFromDto(dto, facilitySite);
    	
    	FacilitySiteDto result = facilitySiteMapper.toDto(facSiteRepo.save(facilitySite));
    	reportStatusService.resetEmissionsReportForEntity(Collections.singletonList(result.getId()), FacilitySiteRepository.class);

    	return result;
    }

    /**
     * Create Facility Site
     * @param facilitySite
     */
	public FacilitySiteDto create(FacilitySite facilitySite){

    	FacilitySiteDto result = facilitySiteMapper.toDto(facSiteRepo.save(facilitySite));

    	return result;
	}

    /**
     * Create Facility NAICS
     * @param dto
     */
    public FacilityNAICSDto createNaics(FacilityNAICSDto dto) {
    	FacilityNAICSXref facilityNaics = facilityNaicsMapper.fromDto(dto);

    	FacilityNAICSDto result = facilityNaicsMapper.facilityNAICSXrefToFacilityNAICSDto(facilityNaicsXrefRepo.save(facilityNaics));
			reportStatusService.resetEmissionsReportForEntity(Collections.singletonList(result.getId()), FacilityNAICSXrefRepository.class);

    	return result;
    }

    /**
     * Update existing Facility NAICS
     */
    public FacilityNAICSDto updateNaics(FacilityNAICSDto dto) {

    	FacilityNAICSXref facilityNaics = facilityNaicsXrefRepo.findById(dto.getId()).orElse(null);
    	facilityNaicsMapper.updateFromDto(dto, facilityNaics);

    	NaicsCode naicsCode = naicsCodeRepo.findById(Integer.parseInt(dto.getCode())).orElse(null);
    	facilityNaics.setNaicsCode(naicsCode);
    	
    	FacilityNAICSDto result = facilityNaicsMapper.facilityNAICSXrefToFacilityNAICSDto(facilityNaicsXrefRepo.save(facilityNaics));
    	reportStatusService.resetEmissionsReportForEntity(Collections.singletonList(result.getId()), FacilityNAICSXrefRepository.class);

    	return result;
    }

    /**
     * Delete Facility NAICS by id
     * @param facilityNaicsId
     */
    public void deleteFacilityNaics(Long facilityNaicsId) {
    	reportStatusService.resetEmissionsReportForEntity(Collections.singletonList(facilityNaicsId), FacilityNAICSXrefRepository.class);
    	facilityNaicsXrefRepo.deleteById(facilityNaicsId);
    }


    /**
     * Retrieve a list of facility site IDs for the given program system code and emissions report year
     * @param programSystemCode
     * @param emissionsReportYear
     * @return
     */ 
    public List<Long> getFacilityIds(String programSystemCode, Short year) {
    	List<Long> facilityIds = facSiteRepo.findFacilityIds(programSystemCode, year);
    	return facilityIds;
    }


    /**
     * Retrieve a list of facilities for the given program system code and emissions report year
     * @param programSystemCode
     * @param emissionsReportYear
     * @return
     */ 
    public List<FacilitySiteBulkUploadDto> retrieveFacilities(String programSystemCode, Short emissionsReportYear) {
    	List<FacilitySite> facilities = facSiteRepo.findByPscAndEmissionsReportYear(programSystemCode, emissionsReportYear);
    	return bulkUploadMapper.facilitySiteToDtoList(facilities);
    }


    /**
     * Retrieve a list of facility NAICS codes for the given program system code and emissions report year
     * @param programSystemCode
     * @param emissionsReportYear
     * @return
     */ 
    public List<FacilityNAICSBulkUploadDto> retrieveFacilityNaics(String programSystemCode, Short emissionsReportYear) {
    	List<FacilityNAICSXref> naics = facilityNaicsXrefRepo.findByPscAndEmissionsReportYear(programSystemCode, emissionsReportYear);
    	return bulkUploadMapper.faciliytNAICSToDtoList(naics);
    }
}
