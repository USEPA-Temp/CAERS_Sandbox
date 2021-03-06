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

import com.baidu.unbiz.fluentvalidator.ValidationError;
import com.baidu.unbiz.fluentvalidator.ValidatorChain;

import gov.epa.cef.web.domain.CalculationMethodCode;
import gov.epa.cef.web.domain.Control;
import gov.epa.cef.web.domain.ControlAssignment;
import gov.epa.cef.web.domain.ControlPath;
import gov.epa.cef.web.domain.Emission;
import gov.epa.cef.web.domain.EmissionsProcess;
import gov.epa.cef.web.domain.EmissionsReport;
import gov.epa.cef.web.domain.EmissionsUnit;
import gov.epa.cef.web.domain.FacilityNAICSXref;
import gov.epa.cef.web.domain.FacilitySite;
import gov.epa.cef.web.domain.MasterFacilityRecord;
import gov.epa.cef.web.domain.NaicsCode;
import gov.epa.cef.web.domain.NaicsCodeType;
import gov.epa.cef.web.domain.OperatingDetail;
import gov.epa.cef.web.domain.OperatingStatusCode;
import gov.epa.cef.web.domain.Pollutant;
import gov.epa.cef.web.domain.ReportHistory;
import gov.epa.cef.web.domain.ReportingPeriod;
import gov.epa.cef.web.repository.ControlAssignmentRepository;
import gov.epa.cef.web.repository.EmissionRepository;
import gov.epa.cef.web.repository.EmissionsProcessRepository;
import gov.epa.cef.web.repository.EmissionsReportRepository;
import gov.epa.cef.web.repository.ReportHistoryRepository;
import gov.epa.cef.web.service.validation.ValidationRegistry;
import gov.epa.cef.web.service.validation.ValidationResult;
import gov.epa.cef.web.service.validation.validator.IEmissionsReportValidator;
import gov.epa.cef.web.service.validation.validator.federal.ControlPathPollutantValidator;
import gov.epa.cef.web.service.validation.validator.federal.ControlPathValidator;
import gov.epa.cef.web.service.validation.validator.federal.ControlPollutantValidator;
import gov.epa.cef.web.service.validation.validator.federal.ControlValidator;
import gov.epa.cef.web.service.validation.validator.federal.EmissionValidator;
import gov.epa.cef.web.service.validation.validator.federal.EmissionsProcessValidator;
import gov.epa.cef.web.service.validation.validator.federal.EmissionsReportValidator;
import gov.epa.cef.web.service.validation.validator.federal.EmissionsUnitValidator;
import gov.epa.cef.web.service.validation.validator.federal.FacilitySiteValidator;
import gov.epa.cef.web.service.validation.validator.federal.OperatingDetailValidator;
import gov.epa.cef.web.service.validation.validator.federal.ReleasePointValidator;
import gov.epa.cef.web.service.validation.validator.federal.ReportingPeriodValidator;
import gov.epa.cef.web.service.validation.validator.state.GeorgiaValidator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class EmissionsReportValidationServiceImplTest {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Mock
    private ValidationRegistry validationRegistry;

    @Spy
    private EmissionRepository emissionRepo;
    
    @Spy
    private ReportHistoryRepository historyRepo;
    
    @Spy
    private ControlAssignmentRepository assignmentRepo;
    
    @Spy
    private EmissionsReportRepository reportRepo;
    
    @Spy
    private EmissionsProcessRepository processRepo;
    
    @Spy
    @InjectMocks
    private EmissionsProcessValidator processValidator;
    
    @Spy
    @InjectMocks
    private EmissionsReportValidator erValidator;
    
    @Spy
    @InjectMocks
    private ControlPathValidator cpValidator;
    
    @InjectMocks
    private EmissionsReportValidationServiceImpl validationService;

    @Before
    public void _onJunitBeginTest() {
    	
    	List<Emission> eList = new ArrayList<Emission>();
    	Emission e = new Emission();
        e.setId(1L);
        e.setTotalManualEntry(true);
        CalculationMethodCode cmc = new CalculationMethodCode();
        cmc.setTotalDirectEntry(false);
        e.setEmissionsCalcMethodCode(cmc);
        eList.add(e);
        
        List<ReportHistory> raList = new ArrayList<ReportHistory>();
        ReportHistory ra = new ReportHistory();
        ra.setId(1L);
        ra.setUserRole("Preparer");
        ra.setReportAttachmentId(1L);
        ra.setFileDeleted(false);
        raList.add(ra);
        
        List<ControlAssignment> caList = new ArrayList<ControlAssignment>();
        ControlAssignment ca = new ControlAssignment();
        ControlPath cp1 = new ControlPath();
        ControlPath cp2 = new ControlPath();
        cp1.setId(1L);
        cp2.setId(2L);
        ca.setId(1L);
        ca.setControlPath(cp2);
        ca.setControlPathChild(cp1);
        caList.add(ca);
        
        List<EmissionsReport> erList = new ArrayList<EmissionsReport>();
        MasterFacilityRecord mfr = new MasterFacilityRecord();
        mfr.setId(1L);
        EmissionsReport er1 = new EmissionsReport();
        er1.setId(1L);
        er1.setYear((short) 2018);
        er1.setEisProgramId("1");
        er1.setMasterFacilityRecord(mfr);
        erList.add(er1);
        
        OperatingStatusCode os = new OperatingStatusCode();
        os.setCode("PS");
        
        EmissionsProcess p1 = new EmissionsProcess();
        p1.setEmissionsProcessIdentifier("Boiler 001");
        p1.setId(2L);
        p1.setOperatingStatusCode(os);
        p1.setStatusYear((short) 2017);
        
        Pollutant pollutant = new Pollutant();
    	pollutant.setPollutantCode("NO3");
        
        List<Emission> eList2 = new ArrayList<Emission>();
        Emission previousE1 = new Emission();
        previousE1.setPollutant(pollutant);
        previousE1.setTotalEmissions(BigDecimal.valueOf(130.00));
        eList2.add(previousE1);
        
        when(reportRepo.findByMasterFacilityRecordId(1L)).thenReturn(erList);
        when(processRepo.retrieveByIdentifierParentFacilityYear(
          		"Boiler 001","test_unit",1L,(short) 2018)).thenReturn(Collections.singletonList(p1));
        when(emissionRepo.findAllByProcessIdReportId(2L,1L)).thenReturn(eList2);
        when(emissionRepo.findAllByReportId(1L)).thenReturn(eList);
        when(historyRepo.findByEmissionsReportIdOrderByActionDate(1L)).thenReturn(raList);
        when(assignmentRepo.findByControlPathChildId(1L)).thenReturn(caList);
        
        when(validationRegistry.findOneByType(FacilitySiteValidator.class))
            .thenReturn(new FacilitySiteValidator());

        when(validationRegistry.findOneByType(ReleasePointValidator.class))
            .thenReturn(new ReleasePointValidator());

        when(validationRegistry.findOneByType(EmissionsUnitValidator.class))
            .thenReturn(new EmissionsUnitValidator());
        
        when(validationRegistry.findOneByType(ControlValidator.class))
        .thenReturn(new ControlValidator());
        
        when(validationRegistry.findOneByType(ControlPollutantValidator.class))
        .thenReturn(new ControlPollutantValidator());

        when(validationRegistry.findOneByType(EmissionsProcessValidator.class))
            .thenReturn(processValidator);

        when(validationRegistry.findOneByType(ReportingPeriodValidator.class))
            .thenReturn(new ReportingPeriodValidator());

        when(validationRegistry.findOneByType(OperatingDetailValidator.class))
            .thenReturn(new OperatingDetailValidator());

        when(validationRegistry.findOneByType(EmissionValidator.class))
            .thenReturn(new EmissionValidator());
        
        when(validationRegistry.findOneByType(ControlPathValidator.class))
            .thenReturn(cpValidator);
        
        when(validationRegistry.findOneByType(ControlPathPollutantValidator.class))
        .thenReturn(new ControlPathPollutantValidator());

        ValidatorChain reportChain = new ValidatorChain();
        reportChain.setValidators(Arrays.asList(erValidator, new GeorgiaValidator()));

        when(validationRegistry.createValidatorChain(IEmissionsReportValidator.class))
            .thenReturn(reportChain);
    }

    @Test
    public void simpleValidateFailureTest() {

        EmissionsReport report = new EmissionsReport();
        OperatingStatusCode opStatCode = new OperatingStatusCode();
        opStatCode.setCode("OP");
        report.setId(1L);
        report.setYear((short) 2020);
        MasterFacilityRecord mfr = new MasterFacilityRecord();
        mfr.setId(1L);
        mfr.setEisProgramId("123");
        report.setMasterFacilityRecord(mfr);
        FacilitySite facilitySite = new FacilitySite();
        facilitySite.setStatusYear((short) 2019);
        EmissionsUnit emissionsUnit = new EmissionsUnit();
        EmissionsProcess emissionsProcess = new EmissionsProcess();
        ReportingPeriod reportingPeriod = new ReportingPeriod();
        OperatingDetail detail = new OperatingDetail();
        Emission emission = new Emission();
        emission.setTotalEmissions(new BigDecimal(10));
        ControlPath controlPath = new ControlPath();
        controlPath.setId(1L);
        Control control = new Control(); 
        control.setIdentifier("control_Identifier");
        control.setOperatingStatusCode(opStatCode);
        control.setPercentControl(new BigDecimal(50.0));
        control.setFacilitySite(facilitySite);
        controlPath.setFacilitySite(facilitySite);
        facilitySite.getControls().add(control);
        facilitySite.getControlPaths().add(controlPath);
        
        List<FacilityNAICSXref> naicsList = new ArrayList<FacilityNAICSXref>();
        FacilityNAICSXref facilityNaics = new FacilityNAICSXref();
      	
        NaicsCode naics = new NaicsCode();
        naics.setCode(332116);
        naics.setDescription("Metal Stamping");
        
        facilityNaics.setNaicsCode(naics);
        facilityNaics.setNaicsCodeType(NaicsCodeType.PRIMARY);
        naicsList.add(facilityNaics);
        
        facilitySite.setFacilityNAICS(naicsList);
        facilitySite.setOperatingStatusCode(opStatCode);

        reportingPeriod.getEmissions().add(emission);
        reportingPeriod.setEmissionsProcess(emissionsProcess);
        reportingPeriod.getOperatingDetails().add(detail);
        detail.setReportingPeriod(reportingPeriod);
        emission.setReportingPeriod(reportingPeriod);
        emissionsProcess.getReportingPeriods().add(reportingPeriod);
        emissionsProcess.setEmissionsUnit(emissionsUnit);
        emissionsProcess.setOperatingStatusCode(opStatCode);
        emissionsProcess.setStatusYear((short)2019);
        emissionsUnit.getEmissionsProcesses().add(emissionsProcess);
        emissionsUnit.setOperatingStatusCode(opStatCode);
        emissionsUnit.setFacilitySite(facilitySite);
        emissionsUnit.setStatusYear((short)2000);
        facilitySite.getEmissionsUnits().add(emissionsUnit);
        facilitySite.setEmissionsReport(report);
        report.getFacilitySites().add(facilitySite);
        
        ValidationResult result = this.validationService.validate(report);
        assertFalse(result.isValid());

        Map<String, ValidationError> federalErrors = result.getFederalErrors().stream()
            .collect(Collectors.toMap(ValidationError::getField, re -> re));

        logger.debug("Failures {}", String.join(", ", federalErrors.keySet()));
        assertTrue(federalErrors.containsKey("report.programSystemCode"));
        assertTrue(federalErrors.containsKey("report.facilitySite.countyCode"));
        assertTrue(federalErrors.containsKey("report.facilitySite.emissionsUnit.emissionsProcess.reportingPeriod.calculationParameterValue"));
        assertTrue(federalErrors.containsKey("report.facilitySite.emissionsUnit.emissionsProcess.reportingPeriod.emission.emissionsCalcMethodCode"));
    }
}
