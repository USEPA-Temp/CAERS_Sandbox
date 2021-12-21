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
package gov.epa.cef.web.service.validation.validator.federal;

import gov.epa.cef.web.domain.AircraftEngineTypeCode;
import gov.epa.cef.web.domain.Emission;
import gov.epa.cef.web.domain.EmissionsProcess;
import gov.epa.cef.web.domain.EmissionsReport;
import gov.epa.cef.web.domain.EmissionsUnit;
import gov.epa.cef.web.domain.PointSourceSccCode;
import gov.epa.cef.web.domain.ReleasePointAppt;
import gov.epa.cef.web.domain.ReportingPeriod;
import gov.epa.cef.web.repository.AircraftEngineTypeCodeRepository;
import gov.epa.cef.web.repository.EmissionRepository;
import gov.epa.cef.web.repository.EmissionsProcessRepository;
import gov.epa.cef.web.repository.EmissionsReportRepository;
import gov.epa.cef.web.repository.PointSourceSccCodeRepository;
import gov.epa.cef.web.service.dto.EntityType;
import gov.epa.cef.web.service.dto.ValidationDetailDto;
import gov.epa.cef.web.service.validation.CefValidatorContext;
import gov.epa.cef.web.service.validation.ValidationField;
import gov.epa.cef.web.service.validation.ValidationRegistry;
import gov.epa.cef.web.service.validation.validator.BaseValidator;
import gov.epa.cef.web.util.ConstantUtils;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.baidu.unbiz.fluentvalidator.FluentValidator;
import com.baidu.unbiz.fluentvalidator.ValidatorContext;
import com.google.common.base.Strings;

@Component
public class EmissionsProcessValidator extends BaseValidator<EmissionsProcess> {
	
	@Autowired
	private PointSourceSccCodeRepository sccRepo;
	
	@Autowired
	private AircraftEngineTypeCodeRepository aircraftEngCodeRepo;
	
	@Autowired
	private EmissionsReportRepository reportRepo;
	
	@Autowired
	private EmissionsProcessRepository processRepo;
	
	@Autowired
	private EmissionRepository emissionRepo;
    
    @Override
    public void compose(FluentValidator validator,
                        ValidatorContext validatorContext,
                        EmissionsProcess emissionsProcess) {

        ValidationRegistry registry = getCefValidatorContext(validatorContext).getValidationRegistry();

        // add more validators as needed
        validator.onEach(emissionsProcess.getReportingPeriods(),
            registry.findOneByType(ReportingPeriodValidator.class));
    }

    @Override
    public boolean validate(ValidatorContext validatorContext, EmissionsProcess emissionsProcess) {

        boolean result = true;

        CefValidatorContext context = getCefValidatorContext(validatorContext);
        boolean isProcessOperating = ConstantUtils.STATUS_OPERATING.contentEquals(emissionsProcess.getOperatingStatusCode().getCode());

        BigDecimal totalReleasePointPercent = emissionsProcess.getReleasePointAppts().stream()
        		.map(ReleasePointAppt::getPercent)
        		.reduce(BigDecimal.ZERO,BigDecimal::add);
        Map<Object, List<ReleasePointAppt>> rpaMap = emissionsProcess.getReleasePointAppts().stream()
            .filter(rpa -> rpa.getReleasePoint() != null)
            .collect(Collectors.groupingBy(e -> e.getReleasePoint().getId()));
	     
        for (List<ReleasePointAppt> rpa: rpaMap.values()) {
        	// release point can be used only once per rp appt collection
        	if (rpa.size() > 1) {
        		
        		result = false;
	        	context.addFederalError(
	        			ValidationField.PROCESS_RP.value(),
	        			"emissionsProcess.releasePointAppts.duplicate",
	        			createValidationDetails(emissionsProcess),
	        			rpa.get(0).getReleasePoint().getReleasePointIdentifier());
	        }
        	
        	// If release point operation is apportioned to a process, operation status must be operating
        	if (isProcessOperating && !ConstantUtils.STATUS_OPERATING.contentEquals(rpa.get(0).getReleasePoint().getOperatingStatusCode().getCode())) {
        		
        		result = false;
	        	context.addFederalError(
	        			ValidationField.PROCESS_RP.value(),
	        			"emissionsProcess.releasePointAppts.statusTypeCode",
	        			createValidationDetails(emissionsProcess),
	        			rpa.get(0).getReleasePoint().getReleasePointIdentifier(),
	        			rpa.get(0).getReleasePoint().getOperatingStatusCode().getDescription());
        	}
	      }
        
        // Release Point Apportionments Emission Percentage for the process must be between 1 and 100.
        if (emissionsProcess.getReleasePointAppts() != null) {
        	for(ReleasePointAppt rpa: emissionsProcess.getReleasePointAppts()){
        		  if(rpa.getPercent().compareTo(BigDecimal.ONE) == -1 || rpa.getPercent().compareTo(BigDecimal.valueOf(100)) == 1){
  	        		result = false;
		        	context.addFederalError(
		        			ValidationField.PROCESS_RP_PCT.value(),
		        			"emissionsProcess.releasePointAppts.percent.range",
		        			createValidationDetails(emissionsProcess),
		        			rpa.getReleasePoint().getReleasePointIdentifier());  
        		  }
        	}
        }
        
        if (isProcessOperating) { 
        	// Might need to add a rounding tolerance.
            if (totalReleasePointPercent.compareTo(BigDecimal.valueOf(100)) != 0) {

            	result = false;
            	context.addFederalError(
                      ValidationField.PROCESS_RP_PCT.value(),
                      "emissionsProcess.releasePointAppts.percent.total",
                      createValidationDetails(emissionsProcess));
            }
            
            // Process must go to at least one release point
            if (CollectionUtils.sizeIsEmpty(rpaMap)) {

            	result = false;
            	context.addFederalError(
            			ValidationField.PROCESS_RP.value(),
            			"emissionsProcess.releasePointAppts.required",
            			createValidationDetails(emissionsProcess));
            }
        	
            // Check for valid SCC Code
            if (Strings.emptyToNull(emissionsProcess.getSccCode()) != null) {

            	PointSourceSccCode isPointSourceSccCode = sccRepo.findById(emissionsProcess.getSccCode()).orElse(null);
            	short reportYear = emissionsProcess.getEmissionsUnit().getFacilitySite().getEmissionsReport().getYear();
            	
            	if (isPointSourceSccCode == null) {
            		
            		result = false;
            		context.addFederalError(
                        ValidationField.PROCESS_INFO_SCC.value(),
                        "emissionsProcess.information.scc.invalid",
                        createValidationDetails(emissionsProcess),
                        emissionsProcess.getSccCode());
                
            	} else if (isPointSourceSccCode.getLastInventoryYear() != null
            		&& isPointSourceSccCode.getLastInventoryYear() < reportYear) {
            		
            		result = false;
            		context.addFederalError(
            			ValidationField.PROCESS_INFO_SCC.value(),
            			"emissionsProcess.information.scc.expired",
            			createValidationDetails(emissionsProcess),
            			emissionsProcess.getSccCode(),
            			isPointSourceSccCode.getLastInventoryYear().toString());
            		
            	} else if (isPointSourceSccCode.getLastInventoryYear() != null
            		&& isPointSourceSccCode.getLastInventoryYear() >= reportYear) {
            		
            		result = false;
            		context.addFederalWarning(
            			ValidationField.PROCESS_INFO_SCC.value(),
            			"emissionsProcess.information.scc.retired",
            			createValidationDetails(emissionsProcess),
            			emissionsProcess.getSccCode(),
            			isPointSourceSccCode.getLastInventoryYear().toString());
              }
            	
              List<String> aircraftEngineScc = new ArrayList<String>();
              Collections.addAll(aircraftEngineScc, "2275001000", "2275020000", "2275050011", "2275050012", "2275060011", "2275060012");
            	
              // if SCC is an aircraft engine type, then aircraft engine code is required 
              for (String code: aircraftEngineScc) {
              	if (code.contentEquals(emissionsProcess.getSccCode()) && emissionsProcess.getAircraftEngineTypeCode() == null) {
              		
              		result = false;
              		context.addFederalError(
              				ValidationField.PROCESS_AIRCRAFT_CODE.value(),
              				"emissionsProcess.aircraftCode.required",
              				createValidationDetails(emissionsProcess));
              		
              	} 
              }
            }
            
            // Check for unique SCC and AircraftEngineType code combination within a facility site
            if ((emissionsProcess.getSccCode() != null) && (emissionsProcess.getAircraftEngineTypeCode() != null)) {
            	String testingCombination = emissionsProcess.getAircraftEngineTypeCode().getCode() + emissionsProcess.getSccCode();
                List<String> combinationList = new ArrayList<String>();
                for(EmissionsUnit eu: emissionsProcess.getEmissionsUnit().getFacilitySite().getEmissionsUnits()){
                	if(eu.getEmissionsProcesses() != null){
                		for(EmissionsProcess ep: eu.getEmissionsProcesses()){
                			if ((ep.getSccCode() != null) && (ep.getAircraftEngineTypeCode() != null) && (!ep.getId().equals(emissionsProcess.getId()))){
                    			String combination = ep.getAircraftEngineTypeCode().getCode().toString() + ep.getSccCode().toString();
                    			combinationList.add(combination);
                			}
                		}
                	}
                }
                for(String combination: combinationList){
            		if(combination.equals(testingCombination)){
                    	context.addFederalError(
                    			ValidationField.PROCESS_AIRCRAFT_CODE_AND_SCC_CODE.value(),
                    			"emissionsProcess.aircraftCodeAndSccCombination.duplicate",
                    			createValidationDetails(emissionsProcess));
                    	result = false;
            		}
                }
            }
      
			  // aircraft engine code must match assigned SCC
			  if (emissionsProcess.getSccCode() != null && emissionsProcess.getAircraftEngineTypeCode() != null) {
			  	AircraftEngineTypeCode sccHasEngineType = aircraftEngCodeRepo.findById(emissionsProcess.getAircraftEngineTypeCode().getCode()).orElse(null);
			  	
			  	if (sccHasEngineType != null && !emissionsProcess.getSccCode().contentEquals(sccHasEngineType.getScc())) {
			  		
			  		result = false;
			  		context.addFederalError(
			  				ValidationField.PROCESS_AIRCRAFT_CODE.value(),
			  				"emissionsProcess.aircraftCode.valid",
			  				createValidationDetails(emissionsProcess));
					}
				}
			
			  if (emissionsProcess.getAircraftEngineTypeCode() != null && emissionsProcess.getAircraftEngineTypeCode().getLastInventoryYear() != null
			          && emissionsProcess.getAircraftEngineTypeCode().getLastInventoryYear() < getReportYear(emissionsProcess)) {
			
			      result = false;
			      context.addFederalError(
			              ValidationField.PROCESS_AIRCRAFT_CODE.value(),
			              "emissionsProcess.aircraftCode.legacy", 
			              createValidationDetails(emissionsProcess),
			              emissionsProcess.getAircraftEngineTypeCode().getFaaAircraftType(),
			              emissionsProcess.getAircraftEngineTypeCode().getEngine());
			  }
        	
        	
            // At least one emission is recorded for the Reporting Period when Process Status is "Operating.
        	if (emissionsProcess.getReportingPeriods() != null) {
        		for (ReportingPeriod rp : emissionsProcess.getReportingPeriods()) {
        			if (rp.getEmissions() == null || rp.getEmissions().size() == 0) {
        				
        				result = false;
        				context.addFederalError(
        						ValidationField.PROCESS_PERIOD_EMISSION.value(),
        						"emissionsProcess.emission.required",
        						createValidationDetails(emissionsProcess));
      				}
      			}
      		}
        
        }
        
    	// check current year's total emissions against previous year's total emissions warning
    	EmissionsReport currentReport = emissionsProcess.getEmissionsUnit().getFacilitySite().getEmissionsReport();
    		
    	boolean sourceTypeLandfill = false;
		
    	if (currentReport.getMasterFacilityRecord().getFacilitySourceTypeCode() != null 
	    			&& ConstantUtils.FACILITY_SOURCE_LANDFILL_CODE.contentEquals(currentReport.getMasterFacilityRecord().getFacilitySourceTypeCode().getCode())) {
    		sourceTypeLandfill = true;
    	}
    	
		// find previous report
		List<EmissionsReport> erList = reportRepo.findByMasterFacilityRecordId(currentReport.getMasterFacilityRecord().getId()).stream()
				.filter(var -> (var.getYear() != null && var.getYear() < currentReport.getYear()))
				.sorted(Comparator.comparing(EmissionsReport::getYear))
				.collect(Collectors.toList());

		boolean pyProcessExists = false;

		if (!erList.isEmpty()) {
			Short previousReportYr = erList.get(erList.size()-1).getYear();
			Long previousReportId = erList.get(erList.size()-1).getId();

			List<EmissionsProcess> previousProcesses = processRepo.retrieveByIdentifierParentFacilityYear(emissionsProcess.getEmissionsProcessIdentifier(), 
			        emissionsProcess.getEmissionsUnit().getUnitIdentifier(), 
			        currentReport.getMasterFacilityRecord().getId(), 
			        previousReportYr);

			// check if previous report exists then check if this process exists in that report
			if (!previousProcesses.isEmpty()) {

			    pyProcessExists = true;

			    // loop through all processes, if the same report was uploaded twice for a previous year this should only work once
			    // if the previous report had the same process multiple times this may return duplicate messages
			    for (EmissionsProcess previousProcess : previousProcesses) {
			    	
			    	// check PS/TS status year of current report to OP status year of previous report
			    	if (!isProcessOperating && previousProcess.getStatusYear() != null
			    			&& ConstantUtils.STATUS_OPERATING.contentEquals(previousProcess.getOperatingStatusCode().getCode())
			    			&& (emissionsProcess.getStatusYear() == null || emissionsProcess.getStatusYear() <= previousProcess.getStatusYear())
			    			&& !sourceTypeLandfill) {
			    		result = false;
        				context.addFederalError(
        						ValidationField.PROCESS_STATUS_YEAR.value(),
        						"emissionsProcess.statusYear.invalid",
        						createValidationDetails(emissionsProcess),
        						emissionsProcess.getOperatingStatusCode().getDescription(),
        						emissionsProcess.getStatusYear() != null ? emissionsProcess.getStatusYear().toString():emissionsProcess.getStatusYear());
			    	}
			    	
			    	if (isProcessOperating) {
        				List<Emission> currentEmissionsList = emissionRepo.findAllByProcessIdReportId(emissionsProcess.getId(), currentReport.getId());
        				List<Emission> previousEmissionsList = emissionRepo.findAllByProcessIdReportId(previousProcess.getId(), previousReportId);
      					
      					if (!currentEmissionsList.isEmpty() && !previousEmissionsList.isEmpty()) {
	      					for (Emission ce: currentEmissionsList) {
	      						for (Emission pe: previousEmissionsList) {
	      							// check if pollutant code the same and total emissions are equal in value and scale 
	      							if ((ce.getPollutant().getPollutantCode().contentEquals(pe.getPollutant().getPollutantCode()))
	      									&& ce.getTotalEmissions().equals(pe.getTotalEmissions())) {
	  
	      								result = false;
	      								context.addFederalWarning(
	      										ValidationField.EMISSION_TOTAL_EMISSIONS.value(),
	      										"emission.totalEmissions.copied",
	      										createEmissionValidationDetails(ce),
	      										previousReportYr.toString());
	      							}
	      						}
	      					}
      					}
			    	}
			    }
			}
		}
		
		// if emissions process was PS in previous year report and is not PS in this report
        if (emissionsProcess.getPreviousYearOperatingStatusCode() != null) {
	        if (ConstantUtils.STATUS_PERMANENTLY_SHUTDOWN.contentEquals(emissionsProcess.getPreviousYearOperatingStatusCode().getCode()) &&
	        	!ConstantUtils.STATUS_PERMANENTLY_SHUTDOWN.contentEquals(emissionsProcess.getOperatingStatusCode().getCode())) {
	        	
	            result = false;
	            context.addFederalError(
	                    ValidationField.PROCESS_STATUS_CODE.value(),
	                    "emissionsProcess.statusTypeCode.psPreviousYear",
	                    createValidationDetails(emissionsProcess));
	        }
        }
		
		if (emissionsProcess.getStatusYear() != null) {
			// Status year must be between 1900 and the report year
	        if (emissionsProcess.getStatusYear() < 1900 || emissionsProcess.getStatusYear() > emissionsProcess.getEmissionsUnit().getFacilitySite().getEmissionsReport().getYear()) {
	            
	        	result = false;
	            context.addFederalError(
	                ValidationField.PROCESS_STATUS_YEAR.value(), "emissionsProcess.statusYear.range",
	                createValidationDetails(emissionsProcess),
	                emissionsProcess.getEmissionsUnit().getFacilitySite().getEmissionsReport().getYear().toString());
	        }
			
			// process operating status year cannot be before unit operating status year when both are OP.
			// check does not apply to landfills, process can be operating while unit is TS/PS.
			if (isProcessOperating && !sourceTypeLandfill && emissionsProcess.getEmissionsUnit().getStatusYear() != null
					&& ConstantUtils.STATUS_OPERATING.contentEquals(emissionsProcess.getEmissionsUnit().getOperatingStatusCode().getCode())
					&& emissionsProcess.getStatusYear() < emissionsProcess.getEmissionsUnit().getStatusYear()) {
				
				result = false;
				context.addFederalError(
						ValidationField.PROCESS_STATUS_YEAR.value(),
						"emissionsProcess.statusYear.beforeUnitYear",
						createValidationDetails(emissionsProcess),
						emissionsProcess.getEmissionsUnit().getUnitIdentifier());
			}
		}
        
        
		if (!pyProcessExists && !isProcessOperating) {
			
		    // process is new, but is PS/TS
            result = false;
            context.addFederalError(
                    ValidationField.PROCESS_STATUS_CODE.value(),
                    "emissionsProcess.statusTypeCode.newShutdown",
                    createValidationDetails(emissionsProcess));
		}
		
        return result;
    }

    private String getEmissionsUnitIdentifier(EmissionsProcess process) {
        if (process.getEmissionsUnit() != null) {
            return process.getEmissionsUnit().getUnitIdentifier();
        }
        return null;
    }

    private int getReportYear(EmissionsProcess process) {
        return process.getEmissionsUnit().getFacilitySite().getEmissionsReport().getYear().intValue();
    }

    private ValidationDetailDto createValidationDetails(EmissionsProcess source) {

        String description = MessageFormat.format("Emission Unit: {0}, Emission Process: {1}", 
                getEmissionsUnitIdentifier(source),
                source.getEmissionsProcessIdentifier());

        ValidationDetailDto dto = new ValidationDetailDto(source.getId(), source.getEmissionsProcessIdentifier(), EntityType.EMISSIONS_PROCESS, description);
        return dto;
    }
    
    private String getEmissionsUnitIdentifier(Emission emission) {
      if (emission.getReportingPeriod() != null && emission.getReportingPeriod().getEmissionsProcess() != null 
              && emission.getReportingPeriod().getEmissionsProcess().getEmissionsUnit() != null) {
          return emission.getReportingPeriod().getEmissionsProcess().getEmissionsUnit().getUnitIdentifier();
      }
      return null;
	}
    
    private String getEmissionsProcessIdentifier(Emission emission) {
  	if (emission.getReportingPeriod() != null && emission.getReportingPeriod().getEmissionsProcess() != null) {
  		return emission.getReportingPeriod().getEmissionsProcess().getEmissionsProcessIdentifier();
  	}
  	return null;
    }
    
    private String getPollutantName(Emission emission) {
  	if (emission.getPollutant() != null) {
  		return emission.getPollutant().getPollutantName();
  	}
  	return null;
	}

    private ValidationDetailDto createEmissionValidationDetails(Emission source) {

      String description = MessageFormat.format("Emission Unit: {0}, Emission Process: {1}, Pollutant: {2}", 
              getEmissionsUnitIdentifier(source),
              getEmissionsProcessIdentifier(source),
              getPollutantName(source));

      ValidationDetailDto dto = new ValidationDetailDto(source.getId(), getPollutantName(source), EntityType.EMISSION, description);
      if (source.getReportingPeriod() != null) {
          dto.getParents().add(new ValidationDetailDto(source.getReportingPeriod().getId(), null, EntityType.REPORTING_PERIOD));
      }
      return dto;
  }

}
