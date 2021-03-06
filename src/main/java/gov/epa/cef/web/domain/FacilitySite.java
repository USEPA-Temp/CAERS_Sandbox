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
package gov.epa.cef.web.domain;

import gov.epa.cef.web.domain.common.BaseAuditEntity;
import gov.epa.cef.web.util.ConstantUtils;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Facility entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "facility_site")
public class FacilitySite extends BaseAuditEntity {

    private static final long serialVersionUID = 1L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_code")
    private FacilityCategoryCode facilityCategoryCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_type_code")
    private FacilitySourceTypeCode facilitySourceTypeCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "program_system_code")
    private ProgramSystemCode programSystemCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "status_code", nullable = false)
    private OperatingStatusCode operatingStatusCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_id", nullable = false)
    private EmissionsReport emissionsReport;

    @Column(name = "alt_site_identifier", nullable = false, length = 30)
    private String altSiteIdentifier;

    @Column(name = "name", nullable = false, length = 80)
    private String name;

    @Column(name = "description", length = 100)
    private String description;

    @Column(name = "status_year")
    private Short statusYear;

    @Column(name = "street_address", nullable = false, length = 100)
    private String streetAddress;

    @Column(name = "city", nullable = false, length = 60)
    private String city;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "county_code")
    private FipsCounty countyCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "state_code", nullable = false)
    private FipsStateCode stateCode;

    @Column(name = "country_code", length = 10)
    private String countryCode;

    @Column(name = "postal_code", length = 10)
    private String postalCode;

    @Column(name = "mailing_street_address", length = 100)
    private String mailingStreetAddress;

    @Column(name = "mailing_city", length = 60)
    private String mailingCity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mailing_state_code", nullable = false)
    private FipsStateCode mailingStateCode;

    @Column(name = "mailing_postal_code", length = 10)
    private String mailingPostalCode;

    @Column(name = "latitude", precision = 10, scale = 6)
    private BigDecimal latitude;

    @Column(name = "longitude", precision = 10, scale = 6)
    private BigDecimal longitude;
    
    @Column(name = "comments", length = 400)
    private String comments;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tribal_code")
    private TribalCode tribalCode;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "facilitySite")
    private List<FacilityNAICSXref> facilityNAICS = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "facilitySite")
    private List<EmissionsUnit> emissionsUnits = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "facilitySite")
    private List<ReleasePoint> releasePoints = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "facilitySite")
    private List<FacilitySiteContact> contacts = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "facilitySite")
    private List<Control> controls = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "facilitySite")
    private List<ControlPath> controlPaths = new ArrayList<>();


    /***
     * Default constructor
     */
    public FacilitySite() {}


    /***
     * Copy constructor
     * @param emissionsReport The emissions report object that this facility site should be associated with
     * @param originalFacilitySite The facility site object being copied
     */
    public FacilitySite(EmissionsReport emissionsReport, FacilitySite originalFacilitySite) {
		this.id = originalFacilitySite.getId();
        this.facilityCategoryCode = originalFacilitySite.getFacilityCategoryCode();
        this.facilitySourceTypeCode = originalFacilitySite.getFacilitySourceTypeCode();
        this.programSystemCode = originalFacilitySite.getProgramSystemCode();
        this.operatingStatusCode = originalFacilitySite.getOperatingStatusCode();
        this.emissionsReport = emissionsReport;
        this.altSiteIdentifier = originalFacilitySite.getAltSiteIdentifier();
        this.name = originalFacilitySite.getName();
        this.description = originalFacilitySite.getDescription();
        this.statusYear = originalFacilitySite.getStatusYear();
        this.streetAddress = originalFacilitySite.getStreetAddress();
        this.city = originalFacilitySite.getCity();
        this.countyCode = originalFacilitySite.getCountyCode();
        this.stateCode = originalFacilitySite.getStateCode();
        this.countryCode = originalFacilitySite.getCountryCode();
        this.postalCode = originalFacilitySite.getPostalCode();
        this.mailingStreetAddress = originalFacilitySite.getMailingStreetAddress();
        this.mailingCity = originalFacilitySite.getMailingCity();
        this.mailingStateCode = originalFacilitySite.getMailingStateCode();
        this.mailingPostalCode = originalFacilitySite.getMailingPostalCode();
        this.latitude = originalFacilitySite.getLatitude();
        this.longitude = originalFacilitySite.getLongitude();
        this.tribalCode = originalFacilitySite.getTribalCode();
        this.comments = originalFacilitySite.getComments();

        for (FacilityNAICSXref naicsXref : originalFacilitySite.getFacilityNAICS()) {
        	this.facilityNAICS.add(new FacilityNAICSXref(this, naicsXref));
        }
        for (ReleasePoint releasePoint : originalFacilitySite.getReleasePoints()) {
        	if (!releasePoint.getOperatingStatusCode().getCode().equals(ConstantUtils.STATUS_PERMANENTLY_SHUTDOWN)) {
        		this.releasePoints.add(new ReleasePoint(this, releasePoint));
        	}
        }
        
        //controls need to be before emission unit so that emission process and release point apportionments
        //underneath the units can association themselves with the appropriate controls
        for (Control control : originalFacilitySite.getControls()) {
        	if (!control.getOperatingStatusCode().getCode().equals(ConstantUtils.STATUS_PERMANENTLY_SHUTDOWN)) {
        		this.controls.add(new Control(this, control));
        	}
        }
        for (ControlPath controlPath : originalFacilitySite.getControlPaths()) {
            this.controlPaths.add(new ControlPath(this, controlPath));
        }
        
        for (ControlPath newControlPath: this.controlPaths){
        	for (ControlPath originalControlPaths : originalFacilitySite.getControlPaths()) {
        		for (ControlAssignment originalControlAssignment : originalControlPaths.getAssignments()) {
        			Control c = null;
    	        	ControlPath cpc = null;
        		
		        	for(Control newControl : this.controls) {
		        		if (originalControlAssignment.getControl() != null && newControl.getId().equals(originalControlAssignment.getControl().getId())
		        				&& originalControlAssignment.getControlPath().getPathId().equals(newControlPath.getPathId())) {
		        			c = newControl;
		        			break;
		        		}
		        	}
		        	
		        	//if the original control assignment has a child control path, then loop through the
		        	//control paths associated with the new facility, find the appropriate one, and
		        	//associate it to this control assignment - otherwise leave child path as null
		        	if (originalControlAssignment.getControlPathChild() != null) {
		            	for(ControlPath newControlPathChild : this.controlPaths) {
		            		if (newControlPathChild.getPathId().equals(originalControlAssignment.getControlPathChild().getPathId())
		            				&& newControlPath.getPathId().equals(originalControlAssignment.getControlPath().getPathId())) {
		            			cpc = newControlPathChild;
		            			break;
		            		}
		            	}
		        	}
		        	if(c != null || cpc != null){
		        		newControlPath.getAssignments().add(new ControlAssignment(newControlPath, c, cpc, originalControlAssignment));
		        	}
        		}
        	}
        }
        
        for (EmissionsUnit emissionsUnit : originalFacilitySite.getEmissionsUnits()) {
        	if (!emissionsUnit.getOperatingStatusCode().getCode().equals(ConstantUtils.STATUS_PERMANENTLY_SHUTDOWN)) {
        			
        		this.emissionsUnits.add(new EmissionsUnit(this, emissionsUnit));
        	}
        	
        	if (emissionsUnit.getOperatingStatusCode().getCode().equals(ConstantUtils.STATUS_PERMANENTLY_SHUTDOWN)
        			&& originalFacilitySite.getEmissionsReport().getMasterFacilityRecord().getFacilitySourceTypeCode() != null
        			&& originalFacilitySite.getEmissionsReport().getMasterFacilityRecord().getFacilitySourceTypeCode().getCode().equals(ConstantUtils.FACILITY_SOURCE_LANDFILL_CODE)) {
        		
        		if (emissionsUnit.getEmissionsProcesses().stream()
                        .filter(emissionsProcess -> !emissionsProcess.getOperatingStatusCode().getCode().contentEquals(ConstantUtils.STATUS_PERMANENTLY_SHUTDOWN))
                        .collect(Collectors.toList()).size() > 0) {
        			
            			this.emissionsUnits.add(new EmissionsUnit(this, emissionsUnit));
        		}
        	}
        }
        
        for (FacilitySiteContact siteContact : originalFacilitySite.getContacts()) {
        	this.contacts.add(new FacilitySiteContact(this, siteContact));
        }
    }


    public FacilityCategoryCode getFacilityCategoryCode() {
        return this.facilityCategoryCode;
    }

    public void setFacilityCategoryCode(FacilityCategoryCode facilityCategoryCode) {
        this.facilityCategoryCode = facilityCategoryCode;
    }

    public FacilitySourceTypeCode getFacilitySourceTypeCode() {
        return this.facilitySourceTypeCode;
    }

    public void setFacilitySourceTypeCode(FacilitySourceTypeCode facilitySourceTypeCode) {
        this.facilitySourceTypeCode = facilitySourceTypeCode;
    }

    public ProgramSystemCode getProgramSystemCode() {
        return this.programSystemCode;
    }

    public void setProgramSystemCode(ProgramSystemCode programSystemCode) {
        this.programSystemCode = programSystemCode;
    }

    public OperatingStatusCode getOperatingStatusCode() {
        return this.operatingStatusCode;
    }

    public void setOperatingStatusCode(OperatingStatusCode operatingStatusCode) {
        this.operatingStatusCode = operatingStatusCode;
    }

    public EmissionsReport getEmissionsReport() {
        return this.emissionsReport;
    }

    public void setEmissionsReport(EmissionsReport emissionsReport) {
        this.emissionsReport = emissionsReport;
    }

    public String getAltSiteIdentifier() {
        return this.altSiteIdentifier;
    }

    public void setAltSiteIdentifier(String altSiteIdentifier) {
        this.altSiteIdentifier = altSiteIdentifier;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Short getStatusYear() {
        return this.statusYear;
    }

    public void setStatusYear(Short statusYear) {
        this.statusYear = statusYear;
    }

    public String getStreetAddress() {
        return this.streetAddress;
    }

    public void setStreetAddress(String streetAddress) {
        this.streetAddress = streetAddress;
    }

    public String getCity() {
        return this.city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public FipsCounty getCountyCode() {
        return countyCode;
    }

    public void setCountyCode(FipsCounty countyCode) {
        this.countyCode = countyCode;
    }

    public FipsStateCode getStateCode() {
        return this.stateCode;
    }

    public void setStateCode(FipsStateCode stateCode) {
        this.stateCode = stateCode;
    }

    public String getCountryCode() {
        return this.countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getPostalCode() {
        return this.postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getMailingStreetAddress() {
        return mailingStreetAddress;
    }

    public void setMailingStreetAddress(String mailingStreetAddress) {
        this.mailingStreetAddress = mailingStreetAddress;
    }

    public String getMailingCity() {
        return mailingCity;
    }

    public void setMailingCity(String mailingCity) {
        this.mailingCity = mailingCity;
    }

    public FipsStateCode getMailingStateCode() {
        return mailingStateCode;
    }

    public void setMailingStateCode(FipsStateCode mailingStateCode) {
        this.mailingStateCode = mailingStateCode;
    }

    public String getMailingPostalCode() {
        return mailingPostalCode;
    }

    public void setMailingPostalCode(String mailingPostalCode) {
        this.mailingPostalCode = mailingPostalCode;
    }

    public BigDecimal getLatitude() {
        return this.latitude;
    }

    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }

    public BigDecimal getLongitude() {
        return this.longitude;
    }

    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }
    
    public String getComments() {
    	return comments;
    }
    
    public void setComments(String comments) {
    	this.comments = comments;
    }

    public TribalCode getTribalCode() {
        return tribalCode;
    }

    public void setTribalCode(TribalCode tribalCode) {
        this.tribalCode = tribalCode;
    }

    public List<FacilityNAICSXref> getFacilityNAICS() {
        return facilityNAICS;
    }

    public void setFacilityNAICS(List<FacilityNAICSXref> facilityNAICS) {

        this.facilityNAICS.clear();
        if (facilityNAICS != null) {
            this.facilityNAICS.addAll(facilityNAICS);
        }
    }

    public List<EmissionsUnit> getEmissionsUnits() {
        return this.emissionsUnits;
    }

    public void setEmissionsUnits(List<EmissionsUnit> emissionsUnits) {

        this.emissionsUnits.clear();
        if (emissionsUnits != null) {
            this.emissionsUnits.addAll(emissionsUnits);
        }
    }

    public List<ReleasePoint> getReleasePoints() {
        return this.releasePoints;
    }

    public void setReleasePoints(List<ReleasePoint> releasePoints) {

        this.releasePoints.clear();
        if (releasePoints != null) {
            this.releasePoints.addAll(releasePoints);
        };
    }

    public List<FacilitySiteContact> getContacts() {
        return this.contacts;
    }

    public void setContacts(List<FacilitySiteContact> contacts) {

        this.contacts.clear();
        if (contacts != null) {
            this.contacts.addAll(contacts);
        }
    }

    public List<Control> getControls() {
        return controls;
    }

    public void setControls(List<Control> controls) {
        this.controls.clear();
        if (controls != null) {
            this.controls.addAll(controls);
        }
    }

    public List<ControlPath> getControlPaths() {
        return controlPaths;
    }

    public void setControlPaths(List<ControlPath> controlPaths) {
        this.controlPaths.clear();
        if (controlPaths != null) {
            this.controlPaths.addAll(controlPaths);
        }
    }


    /***
     * Set the id property to null for this object and the id for it's direct children.  This method is useful to INSERT the updated object instead of UPDATE.
     */
    public void clearId() {
    	this.id = null;

        for (FacilityNAICSXref naicsXref : this.facilityNAICS) {
        	naicsXref.clearId();
        }
		for (ReleasePoint releasePoint : this.releasePoints) {
			releasePoint.clearId();
		}
		for (EmissionsUnit emissionsUnit : this.emissionsUnits) {
			emissionsUnit.clearId();
		}
        for (FacilitySiteContact contact : this.contacts) {
        	contact.clearId();
        }
		for (Control control : this.controls) {
			control.clearId();
		}
		for (ControlPath controlPath : this.controlPaths) {
			controlPath.clearId();
		}
    }
}
