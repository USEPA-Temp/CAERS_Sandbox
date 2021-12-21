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
package gov.epa.cef.web.service;

import gov.epa.cef.web.domain.CalculationMaterialCode;
import gov.epa.cef.web.domain.CalculationParameterTypeCode;
import gov.epa.cef.web.domain.ContactTypeCode;
import gov.epa.cef.web.domain.ControlMeasureCode;
import gov.epa.cef.web.domain.EmissionsOperatingTypeCode;
import gov.epa.cef.web.domain.FipsCounty;
import gov.epa.cef.web.domain.FipsStateCode;
import gov.epa.cef.web.domain.OperatingStatusCode;
import gov.epa.cef.web.domain.ProgramSystemCode;
import gov.epa.cef.web.domain.ReleasePointTypeCode;
import gov.epa.cef.web.domain.ReportingPeriodCode;
import gov.epa.cef.web.domain.TribalCode;
import gov.epa.cef.web.domain.UnitMeasureCode;
import gov.epa.cef.web.service.dto.AircraftEngineTypeCodeDto;
import gov.epa.cef.web.service.dto.CalculationMaterialCodeDto;
import gov.epa.cef.web.service.dto.CalculationMethodCodeDto;
import gov.epa.cef.web.service.dto.CodeLookupDto;
import gov.epa.cef.web.service.dto.EisLatLongToleranceLookupDto;
import gov.epa.cef.web.service.dto.EnergyConversionFactorDto;
import gov.epa.cef.web.service.dto.FacilityCategoryCodeDto;
import gov.epa.cef.web.service.dto.FipsCountyDto;
import gov.epa.cef.web.service.dto.FipsStateCodeDto;
import gov.epa.cef.web.service.dto.PointSourceSccCodeDto;
import gov.epa.cef.web.service.dto.PollutantDto;
import gov.epa.cef.web.service.dto.UnitMeasureCodeDto;

import java.util.List;

public interface LookupService {

    /**
     * Retrieve Calculation Material codes
     * @return
     */
    List<CodeLookupDto> retrieveCalcMaterialCodes();
    
    /**
     * Retrieve Fuel Use Calculation Material codes
     * @return
     */
    List<CalculationMaterialCodeDto> retrieveFuelUseMaterialCodes();

    /**
     * Retrieve Calculation Material code database object by code
     * @param code
     * @return
     */
    CalculationMaterialCode retrieveCalcMaterialCodeEntityByCode(String code);

    /**
     * Retrieve Calculation Method codes
     * @return
     */
    List<CalculationMethodCodeDto> retrieveCalcMethodCodes();

    /**
     * Retrieve Calculation Parameter Type codes
     * @return
     */
    List<CodeLookupDto> retrieveCalcParamTypeCodes();

    /**
     * Retrieve Calculation Parameter Type code database object by code
     * @param code
     * @return
     */
    CalculationParameterTypeCode retrieveCalcParamTypeCodeEntityByCode(String code);

    /**
     * Retrieve Operating Status codes for sub-facility components
     * @return
     */
    List<CodeLookupDto> retrieveSubFacilityOperatingStatusCodes();

    /**
     * Retrieve Operating Status codes for facilities
     * @return
     */
    List<CodeLookupDto> retrieveFacilityOperatingStatusCodes();

    /**
     * Retrieve Pollutants
     * @return
     */
    List<PollutantDto> retrievePollutants();

    /**
     * Retrieve non-legacy Pollutants
     * @param year
     * @return
     */
    List<PollutantDto> retrieveCurrentPollutants(Integer year);

    /**
     * Retrieve Operating Status code database object by code
     * @param code
     * @return
     */
    OperatingStatusCode retrieveOperatingStatusCodeEntityByCode(String code);

    /**
     * Retrieve Reporting Period codes
     * @return
     */
    List<CodeLookupDto> retrieveReportingPeriodCodes();

    /**
     * Retrieve Reporting Period code database object by code
     * @param code
     * @return
     */
    ReportingPeriodCode retrieveReportingPeriodCodeEntityByCode(String code);

    /**
     * Retrieve UoM codes
     * @return
     */
    List<UnitMeasureCodeDto> retrieveUnitMeasureCodes();

    /**
     * Retrieve non-legacy UoM codes
     * @return
     */
    List<UnitMeasureCodeDto> retrieveCurrentUnitMeasureCodes();
    
    /**
     * Retrieve Fuel Use UoM codes 
     * @return
     */
    List<UnitMeasureCodeDto> retrieveFuelUseUnitMeasureCodes();

    /**
     * Retrieve UoM code database object by code
     * @param code
     * @return
     */
    UnitMeasureCode retrieveUnitMeasureCodeEntityByCode(String code);

    /**
     * Retrieve the list of Emission Operating Type Codes
     * @return
     */
    List<CodeLookupDto> retrieveEmissionOperatingTypeCodes();
    
    /**
     * Retrieve the Emissions Operating Type Code entity by code
     * 
     * @param code
     * @return
     */
    EmissionsOperatingTypeCode retrieveEmissionsOperatingTypeCodeEntityByCode(String code);
    
    /**
     * Retrieve Contact Type codes
     * @return
     */
    List<CodeLookupDto> retrieveContactTypeCodes();
    
    /**
     * Retrieve Contact Type code database object by code
     * @param code
     * @return
     */
    ContactTypeCode retrieveContactTypeEntityByCode(String code);
    /**
     * Retrieve Unit Type codes
     * @return
     */
    List<CodeLookupDto> retrieveUnitTypeCodes();

    /**
     * Retrieve County codes
     * @return
     */
    List<FipsCountyDto> retrieveCountyCodes();
    
    /**
     * Retrieve non-legacy County codes
     * @param year
     * @return
     */
    List<FipsCountyDto> retrieveCurrentCounties(Integer year);

    /**
     * Retrieve County codes for a state
     * @param stateCode
     * @return
     */
    List<FipsCountyDto> retrieveCountyCodesByState(String stateCode);
    
    /**
     * Retrieve non-legacy County codes for a state
     * @param stateCode
     * @param year
     * @return
     */
    List<FipsCountyDto> retrieveCurrentCountyCodesByState(String stateCode, Integer year);


    /**
     * Retrieve County code database object by code
     * @param code
     * @return
     */
    FipsCounty retrieveCountyEntityByCode(String code);

    /**
     * Retrieve Fips State codes
     * @return
     */
    List<FipsStateCodeDto> retrieveStateCodes();
    
    /**
     * Retrieve Fips State code database object by code
     * @param code
     * @return
     */
    FipsStateCode retrieveStateCodeEntityByCode(String code);
    
    /**
     * Retrieve the list of Release Point Type Codes
     * @return
     */
    List<CodeLookupDto> retrieveReleasePointTypeCodes();
    
    /**
     * Retrieve Release Point Type code database object by code
     * @param code
     * @return
     */
    ReleasePointTypeCode retrieveReleasePointTypeCodeEntityByCode(String code);
    
    /**
     * Retrieve non-legacy Release Point Type codes
     * @param year
     * @return
     */
    List<CodeLookupDto> retrieveCurrentReleasePointTypeCodes(Integer year);
    
    /**
     * Retrieve the list of Program System Type Codes
     * @return
     */
    List<CodeLookupDto> retrieveProgramSystemTypeCodes();
    
    /**
     * Retrieve Program System code by description
     * @param description
     * @return
     */
    CodeLookupDto retrieveProgramSystemCodeByDescription(String description);
    
    /**
     * Retrieve Program System Type code database object by code
     * @param code
     * @return
     */
    ProgramSystemCode retrieveProgramSystemTypeCodeEntityByCode(String code);
    
    /**
     * Retrieve Control Measure codes
     * @return
     */
    List<CodeLookupDto> retrieveControlMeasureCodes();
    
    /**
     * Retrieve non-legacy Control Measure codes
     * @param year
     * @return
     */
    List<CodeLookupDto> retrieveCurrentControlMeasureCodes(Integer year);
    
    /**
    * Retrieve Control Measure code database object by code
    * @param code
    * @return
    */
    ControlMeasureCode retrieveControlMeasureCodeEntityByCode(String code);
    
    /**
     * Retrieve Tribal Codes
     * @return
     */
    List<CodeLookupDto> retrieveTribalCodes();
    
    /**
     * Retrieve Tribal code database object by code
     * @param code
     * @return
     */
    TribalCode retrieveTribalCodeEntityByCode(String code);
    
    /**
     * Retrieve Facility NAICS Codes
     * @return
     */
    List<CodeLookupDto> retrieveNaicsCode();

    /**
     * Retrieve non-legacy Facility NAICS codes
     * @param year
     * @return
     */
    List<CodeLookupDto> retrieveCurrentNaicsCodes(Integer year);
    
    /**
     * Retrieve Aircraft Engine Type Codes
     * @return
     */
    List<AircraftEngineTypeCodeDto> retrieveAircraftEngineCodes(String scc);
    
    /**
     * Retrieve non-legacy Aircraft Engine Type Codes
     * @param scc
     * @param year
     * @return
     */
    List<AircraftEngineTypeCodeDto> retrieveCurrentAircraftEngineCodes(String scc, Integer year);
    
    /**
    * Retrieve Point Source SCC code database object by code
    * @param code
    * @return
    */
    PointSourceSccCodeDto retrievePointSourceSccCode(String code);
    
    /**
     * Retrieve EIS latitude/longitude tolerance by EIS program id
     * @param eisProgramId
     * @return
     */
    EisLatLongToleranceLookupDto retrieveLatLongTolerance(String eisProgramId);
    
    /**
     * Retrieve Facility Category codes
     * @return
     */
    List<FacilityCategoryCodeDto> retrieveFacilityCategoryCodes();
    
    /**
     * Retrieve Facility Source Type codes
     * @return
     */
    List<CodeLookupDto> retrieveFacilitySourceTypeCodes();
    
    /**
     * Retrieve non-legacy Facility Source Type codes
     * @param year
     * @return
     */
    List<CodeLookupDto> retrieveCurrentFacilitySourceTypeCodes(Integer year);
    
    
    /**
     * Retrieve point source scc codes by search term
     * @param searchTerm
     * @return
     */
    List<PointSourceSccCodeDto> retrieveSearchSccCodes(String searchTerm);
    
    /**
	 * Retrieve Energy Conversion Factor by throughput calculation material
	 * @param calcMaterial
	 * @return
	 */
    EnergyConversionFactorDto findByCalculationMaterialCode(String calcMaterial);
}
