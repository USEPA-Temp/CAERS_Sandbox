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
package gov.epa.cef.web.service.mapper;

import gov.epa.cef.web.domain.AircraftEngineTypeCode;
import gov.epa.cef.web.domain.CalculationMaterialCode;
import gov.epa.cef.web.domain.CalculationMethodCode;
import gov.epa.cef.web.domain.ContactTypeCode;
import gov.epa.cef.web.domain.ControlMeasureCode;
import gov.epa.cef.web.domain.EisLatLongToleranceLookup;
import gov.epa.cef.web.domain.EmissionFormulaVariableCode;
import gov.epa.cef.web.domain.EmissionsOperatingTypeCode;
import gov.epa.cef.web.domain.EnergyConversionFactor;
import gov.epa.cef.web.domain.FacilityCategoryCode;
import gov.epa.cef.web.domain.FacilitySourceTypeCode;
import gov.epa.cef.web.domain.FipsCounty;
import gov.epa.cef.web.domain.FipsStateCode;
import gov.epa.cef.web.domain.NaicsCode;
import gov.epa.cef.web.domain.OperatingStatusCode;
import gov.epa.cef.web.domain.PointSourceSccCode;
import gov.epa.cef.web.domain.Pollutant;
import gov.epa.cef.web.domain.ProgramSystemCode;
import gov.epa.cef.web.domain.ReleasePointTypeCode;
import gov.epa.cef.web.domain.ReportingPeriodCode;
import gov.epa.cef.web.domain.TribalCode;
import gov.epa.cef.web.domain.UnitMeasureCode;
import gov.epa.cef.web.domain.UnitTypeCode;
import gov.epa.cef.web.domain.common.BaseLookupEntity;
import gov.epa.cef.web.repository.LookupRepositories;
import gov.epa.cef.web.service.dto.AircraftEngineTypeCodeDto;
import gov.epa.cef.web.service.dto.CalculationMaterialCodeDto;
import gov.epa.cef.web.service.dto.CalculationMethodCodeDto;
import gov.epa.cef.web.service.dto.CodeLookupDto;
import gov.epa.cef.web.service.dto.EisLatLongToleranceLookupDto;
import gov.epa.cef.web.service.dto.EmissionFormulaVariableCodeDto;
import gov.epa.cef.web.service.dto.EnergyConversionFactorDto;
import gov.epa.cef.web.service.dto.FacilityCategoryCodeDto;
import gov.epa.cef.web.service.dto.FipsCountyDto;
import gov.epa.cef.web.service.dto.FipsStateCodeDto;
import gov.epa.cef.web.service.dto.PointSourceSccCodeDto;
import gov.epa.cef.web.service.dto.PollutantDto;
import gov.epa.cef.web.service.dto.UnitMeasureCodeDto;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;


@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR, uses = {})
public abstract class LookupEntityMapper {

    @Autowired
    private LookupRepositories repos;

    public abstract CodeLookupDto toDto(BaseLookupEntity source);

    public abstract List<CodeLookupDto> toDtoList(List<BaseLookupEntity> source);

    public abstract CodeLookupDto naicsCodeToDto(NaicsCode code);

    public abstract List<CodeLookupDto> naicsCodeToDtoList(List<NaicsCode> source);

    public abstract CodeLookupDto reportingPeriodCodeToDto(ReportingPeriodCode source);

    public abstract EmissionFormulaVariableCodeDto emissionFactorVariableCodeToDto(EmissionFormulaVariableCode source);

    public abstract CodeLookupDto emissionsOperatingTypeCodeToDto(EmissionsOperatingTypeCode source);

    public abstract CalculationMethodCodeDto calculationMethodCodeToDto(CalculationMethodCode source);

    public abstract CodeLookupDto controlMeasureCodeToDto(ControlMeasureCode source);

    public abstract List<CodeLookupDto> controlMeasureCodeToDtoList(List<ControlMeasureCode> source);
    
    public abstract List<CalculationMaterialCodeDto> fuelUseCalculationMaterialToDtoList(List<CalculationMaterialCode> source);
    
    public abstract CodeLookupDto releasePointTypCodeToDto(ReleasePointTypeCode source);
    
    public abstract List<CodeLookupDto> releasePointTypCodeToDtoList(List<ReleasePointTypeCode> source);

    public abstract FacilityCategoryCodeDto facilityCategoryCodeToDto(FacilityCategoryCode code);

    public abstract UnitMeasureCodeDto unitMeasureCodeToDto(UnitMeasureCode source);

    public abstract List<UnitMeasureCodeDto> unitMeasureCodeToDtoList(List<UnitMeasureCode> source);

    public abstract PollutantDto pollutantToDto(Pollutant source);

    public abstract List<PollutantDto> pollutantToDtoList(List<Pollutant> source);

    public abstract FipsCountyDto fipsCountyToDto(FipsCounty source);

    public abstract List<FipsCountyDto> fipsCountyToDtoList(List<FipsCounty> source);

    public abstract FipsStateCodeDto fipsStateCodeToDto(FipsStateCode source);

    public abstract AircraftEngineTypeCodeDto aircraftEngCodeToDto(AircraftEngineTypeCode source);

    public abstract List<AircraftEngineTypeCodeDto> aircraftEngCodeToDtoList(List<AircraftEngineTypeCode> source);

    public abstract PointSourceSccCodeDto pointSourceSccCodeToDto(PointSourceSccCode source);
    
    public abstract List<PointSourceSccCodeDto> pointSourceSccCodeToDtoList(List<PointSourceSccCode> source);
    
    public abstract EisLatLongToleranceLookupDto EisLatLongToleranceLookupToDto(EisLatLongToleranceLookup source);

    public abstract CodeLookupDto facilitySourceTypeCodeToDto(FacilitySourceTypeCode source);
    
    public abstract List<CodeLookupDto> facilitySourceTypeCodeToDtoList(List<FacilitySourceTypeCode> source);
    
    public abstract EnergyConversionFactorDto energyConversionFactorToDto(EnergyConversionFactor source);

    @Named("CalculationMethodCode")
    public CalculationMethodCode dtoToCalculationMethodCode(CodeLookupDto source) {
        if (source != null) {
            return repos.methodCodeRepo().findById(source.getCode()).orElse(null);
        }
        return null;
    }

    @Named("OperatingStatusCode")
    public OperatingStatusCode dtoToOperatingStatusCode(CodeLookupDto source) {
        if (source != null) {
            return repos.operatingStatusRepo().findById(source.getCode()).orElse(null);
        }
        return null;
    }

    @Named("UnitTypeCode")
    public UnitTypeCode dtoToUnitTypeCode(CodeLookupDto source) {
        if (source != null) {
            return repos.UnitTypeCodeRepo().findById(source.getCode()).orElse(null);
        }
        return null;
    }

    public Pollutant pollutantDtoToPollutant(PollutantDto source) {
        if (source != null) {
            return repos.pollutantRepo().findById(source.getPollutantCode()).orElse(null);
        }
        return null;
    }

    @Named("UnitMeasureCode")
    public UnitMeasureCode dtoToUnitMeasureCode(CodeLookupDto source) {
        if (source != null) {
            return repos.uomRepo().findById(source.getCode()).orElse(null);
        }
        return null;
    }

    @Named("ContactTypeCode")
    public ContactTypeCode dtoToContactTypeCode(CodeLookupDto source) {
        if (source != null) {
            return repos.contactTypeRepo().findById(source.getCode()).orElse(null);
        }
        return null;
    }

    @Named("FipsCounty")
    public FipsCounty dtoToFipsStateCode(FipsCountyDto source) {
        if (source != null) {
            return repos.countyRepo().findById(source.getCode()).orElse(null);
        }
        return null;
    }

    @Named("FipsStateCode")
    public FipsStateCode dtoToFipsStateCode(FipsStateCodeDto source) {
        if (source != null) {

            FipsStateCode result = null;
            if (source.getCode() != null) {
                result = repos.stateCodeRepo().findById(source.getCode()).orElse(null);
            }
            if (result == null && source.getUspsCode() != null) {
                result = repos.stateCodeRepo().findByUspsCode(source.getUspsCode()).orElse(null);
            }

            return result;
        }
        return null;
    }

    @Named("ReleasePointTypeCode")
    public ReleasePointTypeCode dtoToReleasePointTypeCode(CodeLookupDto source) {
        if (source != null) {
            return repos.releasePtCodeRepo().findById(source.getCode()).orElse(null);
        }
        return null;
    }

    @Named("ProgramSystemCode")
    public ProgramSystemCode dtoToProgramSystemCode(CodeLookupDto source) {
        if (source != null) {
            return repos.programSystemCodeRepo().findById(source.getCode()).orElse(null);
        }
        return null;
    }

    @Named("ControlMeasureCode")
    public ControlMeasureCode dtoToControlMeasureCode(CodeLookupDto source) {
        if (source != null) {
            return repos.controlMeasureCodeRepo().findById(source.getCode()).orElse(null);
        }
        return null;
    }

    @Named("TribalCode")
    public TribalCode dtoToTribalCode(CodeLookupDto source) {
        if (source != null) {
            return repos.tribalCodeRepo().findById(source.getCode()).orElse(null);
        }
        return null;
    }

    @Named("AircraftEngineTypeCode")
    public AircraftEngineTypeCode dtoToAircraftEngCode(AircraftEngineTypeCodeDto source) {
        if (source != null) {
            return repos.aircraftEngCodeRepo().findById(source.getCode()).orElse(null);
        }
        return null;
    }

    @Named("FacilitySourceTypeCode")
    public FacilitySourceTypeCode dtoToFacilitySourceTypeCode(CodeLookupDto source) {
        if (source != null) {
            return repos.facilitySourceTypeCodeRepo().findById(source.getCode()).orElse(null);
        }
        return null;
    }

    @Named("FacilityCategoryCode")
    public FacilityCategoryCode dtoToFacilityCategoryCode(CodeLookupDto source) {
        if (source != null) {
            return repos.facilityCategoryCodeRepo().findById(source.getCode()).orElse(null);
        }
        return null;
    }

}
