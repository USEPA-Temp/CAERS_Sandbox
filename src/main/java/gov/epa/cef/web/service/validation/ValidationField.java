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
package gov.epa.cef.web.service.validation;

public enum ValidationField {
    REPORT_YEAR("report.year"),
    REPORT_FRS_ID("report.frsFacilityId"),
    REPORT_EIS_ID("report.eisProgramId"),
    REPORT_PROGRAM_SYSTEM_CODE("report.programSystemCode"),
    REPORT_ATTACHMENT("report.reportAttachment"),
    FACILITY_EIS_ID("report.facilitySite.eisProgramId"),
    FACILITY_COUNTY("report.facilitySite.countyCode"),
    FACILITY_CONTACT("report.facilitySite.contacts"),
    FACILITY_CONTACT_COUNTY("report.facilitySite.contacts.countyCode"),
    FACILITY_CONTACT_POSTAL("report.facilitySite.contacts.postalCode"),
    FACILITY_CONTACT_PHONE("report.facilitySite.contacts.phoneNumber"),
    FACILITY_EMAIL_ADDRESS("report.facilitySite.contacts.emailAddress"),
    FACILITY_STATUS("report.facilitySite.status"),
    FACILITY_NAICS("report.facilitySite.naics"),
    FACILITY_SOURCE_TYPE_CODE("report.facilitySite.sourceTypeCode"),
    FACILITY_EMISSION_REPORTED("report.facilitySite.reportedEmissions"),
    RP_IDENTIFIER("report.facilitySite.releasePoint.releasePointIdentifier"),
    RP_STATUS_CODE("report.facilitySite.releasePoint.statusTypeCode"),
    RP_STATUS_YEAR("report.facilitySite.releasePoint.statusYear"),
    RP_GAS_TEMP("report.facilitySite.releasePoint.exitGasTemperature"),
    RP_GAS_FLOW("report.facilitySite.releasePoint.exitGasFlowRate"),
    RP_GAS_VELOCITY("report.facilitySite.releasePoint.exitGasVelocity"),
    RP_FENCELINE("report.facilitySite.releasePoint.fenceLine"),
    RP_UOM_FT("report.facilitySite.releasePoint.uom"),
    RP_FUGITIVE("report.facilitySite.releasePoint.fugitive"),
    RP_STACK("report.facilitySite.releasePoint.stack"),
    RP_STACK_WARNING("report.facilitySite.releasePoint.stackWarning"),
    RP_COORDINATE("report.facilitySite.releasePoint.coordinate"),
    RP_TYPE_CODE("report.facilitySite.releasePoint.releasePointTypeCode"),
    CONTROL_IDENTIFIER("report.facilitySite.control.controlIdentifier"),
    CONTROL_PATH_ASSIGNMENT("report.facilitySite.controlPath.assignment"),
    CONTROL_PATH_ASSIGNMENT_SEQUENCE_NUMBER("report.facilitySite.controlPath.sequenceNumber"),
    CONTROL_PATH_NO_CONTROL_DEVICE_ASSIGNMENT("report.facilitySite.controlPath.notAssigned"),
    CONTROL_PATH_RPA_WARNING("report.facilitySite.controlPath.releasePointApportionment"),
    CONTROL_PATH_IDENTIFIER("report.facilitySite.controlPath.controlPathIdentifier"),
    CONTROL_PATH_PERCENT_CONTROL("report.facilitySite.controlPath.percentControl"),
    CONTROL_PATH_POLLUTANT("report.facilitySite.controlPath.controlPathPollutant"),
    CONTROL_PATH_POLLUTANT_PERCENT_REDUCTION("report.facilitySite.controlPath.controlPollutant.percentReduction"),
    CONTROL_PATH_WARNING("report.facilitySite.control.pathWarning"),
    CONTROL_STATUS_CODE("report.facilitySite.control.statusTypeCode"),
    CONTROL_PERCENT_CAPTURE("report.facilitySite.control.percentCapture"),
    CONTROL_PERCENT_CONTROL("report.facilitySite.control.percentControl"),
    CONTROL_NUMBER_OPERATING_MONTHS("report.facilitySite.control.numberOperatingMonths"),
    CONTROL_POLLUTANT("report.facilitySite.control.controlPollutant"),
    CONTROL_POLLUTANT_PERCENT_REDUCTION("report.facilitySite.control.controlPollutant.percentReduction"),
    CONTROL_MEASURE_CODE("report.facilitySite.control.controlMeasureCode"),
    CONTROL_STATUS_YEAR("report.facilitySite.control.statusYear"),
    CONTROL_DATE("report.facilitySite.control.date"),
    PROCESS_RP_PCT("report.facilitySite.emissionsUnit.emissionsProcess.releasePointAppts.percent"),
    PROCESS_INFO_SCC("report.facilitySite.emissionsUnit.emissionsProcess.information.scc"),
    PROCESS_RP("report.facilitySite.emissionsUnit.emissionsProcess.releasePointAppts.required"),
    PROCESS_AIRCRAFT_CODE("report.facilitySite.emissionsUnit.emissionsProcess.aircraftCode"),
    PROCESS_AIRCRAFT_CODE_AND_SCC_CODE("report.facilitySite.emissionsUnit.emissionsProcess.aircraftCodeAndSccCombination"),
    PROCESS_PERIOD_EMISSION("report.facilitySite.emissionsUnit.emissionsProcess.emission"),
    PROCESS_STATUS_CODE("report.facilitySite.emissionsUnit.emissionsProcess.statusTypeCode"),
    PROCESS_STATUS_YEAR("report.facilitySite.emissionsUnit.emissionsProcess.statusYear"),
    PERIOD_OPERATING_TYPE_CODE("report.facilitySite.emissionsUnit.emissionsProcess.reportingPeriod.operatingTypeCode"),
    PERIOD_CALC_VALUE("report.facilitySite.emissionsUnit.emissionsProcess.reportingPeriod.calculationParameterValue"),
    PERIOD_CALC_MAT_CODE("report.facilitySite.emissionsUnit.emissionsProcess.reportingPeriod.calculationMaterialCode"),
    PERIOD_CALC_TYPE_CODE("report.facilitySite.emissionsUnit.emissionsProcess.reportingPeriod.calculationParameterTypeCode"),
    PERIOD_CALC_UOM("report.facilitySite.emissionsUnit.emissionsProcess.reportingPeriod.calculationParameterUom"),
    PERIOD_FUEL_USE_VALUES("report.facilitySite.emissionsUnit.emissionsProcess.reportingPeriod.fuelUseValues"),
    PERIOD_SCC_FUEL_MATERIAL("report.facilitySite.emissionsUnit.emissionsProcess.reportingPeriod.fuelUseMaterial"),
    PERIOD_DUP_SCC_FUEL_USE("report.facilitySite.emissionsUnit.emissionsProcess.reportingPeriod.duplicateSccFuelUse"),
    PERIOD_HEAT_CONTENT_VALUES("report.facilitySite.emissionsUnit.emissionsProcess.reportingPeriod.heatContentValues"),
    PERIOD_EMISSION("report.facilitySite.emissionsUnit.emissionsProcess.reportingPeriod.emission"),
    PERIOD_FUEL_UOM("report.facilitySite.emissionsUnit.emissionsProcess.reportingPeriod.fuelUseUom"),
    DETAIL_AVG_HR_PER_DAY("report.facilitySite.emissionsUnit.emissionsProcess.reportingPeriod.operatingDetail.avgHoursPerDay"),
    DETAIL_AVG_DAY_PER_WEEK("report.facilitySite.emissionsUnit.emissionsProcess.reportingPeriod.operatingDetail.avgDaysPerWeek"),
    DETAIL_ACT_HR_PER_PERIOD("report.facilitySite.emissionsUnit.emissionsProcess.reportingPeriod.operatingDetail.actualHoursPerPeriod"),
    DETAIL_AVG_WEEK_PER_PERIOD("report.facilitySite.emissionsUnit.emissionsProcess.reportingPeriod.operatingDetail.avgWeeksPerPeriod"),
    DETAIL_PCT_SPRING("report.facilitySite.emissionsUnit.emissionsProcess.reportingPeriod.operatingDetail.percentSpring"),
    DETAIL_PCT_SUMMER("report.facilitySite.emissionsUnit.emissionsProcess.reportingPeriod.operatingDetail.percentSummer"),
    DETAIL_PCT_FALL("report.facilitySite.emissionsUnit.emissionsProcess.reportingPeriod.operatingDetail.percentFall"),
    DETAIL_PCT_WINTER("report.facilitySite.emissionsUnit.emissionsProcess.reportingPeriod.operatingDetail.percentWinter"),
    DETAIL_PCT("report.facilitySite.emissionsUnit.emissionsProcess.reportingPeriod.operatingDetail.percents"),
    EMISSION_POLLUTANT("report.facilitySite.emissionsUnit.emissionsProcess.reportingPeriod.emission.pollutant"),
    EMISSION_CALC_METHOD("report.facilitySite.emissionsUnit.emissionsProcess.reportingPeriod.emission.emissionsCalcMethodCode"),
    EMISSION_COMMENTS("report.facilitySite.emissionsUnit.emissionsProcess.reportingPeriod.emission.comments"),
    EMISSION_CALC_DESC("report.facilitySite.emissionsUnit.emissionsProcess.reportingPeriod.emission.calculationDescription"),
    EMISSION_EF("report.facilitySite.emissionsUnit.emissionsProcess.reportingPeriod.emission.emissionsFactor"),
    EMISSION_EF_TEXT("report.facilitySite.emissionsUnit.emissionsProcess.reportingPeriod.emission.emissionsFactorText"),
    EMISSION_TOTAL_EMISSIONS("report.facilitySite.emissionsUnit.emissionsProcess.reportingPeriod.emission.totalEmissions"),
    EMISSION_CONTROL_PERCENT("report.facilitySite.emissionsUnit.emissionsProcess.reportingPeriod.emission.controlPercent"),
    EMISSION_UOM("report.facilitySite.emissionsUnit.emissionsProcess.reportingPeriod.emission.emissionsUomCode"),
    EMISSION_CURIE_UOM("report.facilitySite.emissionsUnit.emissionsProcess.reportingPeriod.emission.emissionsCurieUom"),
    EMISSION_NUM_UOM("report.facilitySite.emissionsUnit.emissionsProcess.reportingPeriod.emission.emissionsNumeratorUom"),
    EMISSION_DENOM_UOM("report.facilitySite.emissionsUnit.emissionsProcess.reportingPeriod.emission.emissionsDenominatorUom"),
    EMISSION_FORMULA_VARIABLE("report.facilitySite.emissionsUnit.emissionsProcess.reportingPeriod.emission.formula.variable"),
    EMISSIONS_UNIT_STATUS_CODE("report.facilitySite.emissionsUnit.statusTypeCode"),
    EMISSIONS_UNIT_STATUS_YEAR("report.facilitySite.emissionsUnit.statusYear"),
    EMISSIONS_UNIT_CAPACITY("report.facilitySite.emissionsUnit.capacity"),
    EMISSIONS_UNIT_UOM("report.facilitySite.emissionsUnit.unitOfMeasureCode"),
    EMISSIONS_UNIT_PROCESS("report.facilitySite.emissionsUnit.emissionsProcess"),
    EMISSIONS_UNIT_IDENTIFIER("report.facilitySite.emissionsUnit.unitIdentifier"),
//    EMISSION_(""),
    ;

    private final String value;

    ValidationField(String value) {
        this.value = value;
    }

//    public String code() {
//        return this.name();
//    }

    public String value() {
        return this.value;
    }
}
