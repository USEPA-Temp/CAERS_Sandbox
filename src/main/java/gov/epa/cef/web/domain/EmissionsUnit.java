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


/**
 * EmissionsUnit entity
 */
@Entity
@Table(name = "emissions_unit")
public class EmissionsUnit extends BaseAuditEntity {

    private static final long serialVersionUID = 1L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type_code", nullable = false)
    private UnitTypeCode unitTypeCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "status_code", nullable = false)
    private OperatingStatusCode operatingStatusCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "facility_site_id", nullable = false)
    private FacilitySite facilitySite;

    @Column(name = "unit_identifier", nullable = false, length = 20)
    private String unitIdentifier;

    @Column(name = "description", length = 100)
    private String description;

    @Column(name = "status_year")
    private Short statusYear;

    @Column(name = "design_capacity")
    private BigDecimal designCapacity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unit_measure_cd")
    private UnitMeasureCode unitOfMeasureCode;

    @Column(name = "comments", length = 400)
    private String comments;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "previous_year_status_code", nullable = true)
    private OperatingStatusCode previousYearOperatingStatusCode;

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "emissionsUnit")
    private List<EmissionsProcess> emissionsProcesses = new ArrayList<>();

    /***
     * Default constructor
     */
    public EmissionsUnit() {}


    /***
     * Copy constructor
     * @param facilitySite The facility site object that this unit should be associated with
     * @param originalUnit The unit object that is being copied
     */
    public EmissionsUnit(FacilitySite facilitySite, EmissionsUnit originalUnit) {
		this.id = originalUnit.getId();
        this.facilitySite = facilitySite;
        this.unitTypeCode = originalUnit.getUnitTypeCode();
        this.operatingStatusCode = originalUnit.getOperatingStatusCode();
        this.unitIdentifier = originalUnit.getUnitIdentifier();
        this.description = originalUnit.getDescription();
        this.statusYear = originalUnit.getStatusYear();
        this.designCapacity = originalUnit.getDesignCapacity();
        this.unitOfMeasureCode = originalUnit.getUnitOfMeasureCode();
        this.comments = originalUnit.getComments();

        for (EmissionsProcess process : originalUnit.getEmissionsProcesses()) {
        	if (!process.getOperatingStatusCode().getCode().equals(ConstantUtils.STATUS_PERMANENTLY_SHUTDOWN)) {
        		this.emissionsProcesses.add(new EmissionsProcess(this, process));
        	}
        }
    }

    public UnitTypeCode getUnitTypeCode() {
        return this.unitTypeCode;
    }
    public void setUnitTypeCode(UnitTypeCode unitTypeCode) {
        this.unitTypeCode = unitTypeCode;
    }

    public OperatingStatusCode getOperatingStatusCode() {
        return this.operatingStatusCode;
    }
    public void setOperatingStatusCode(OperatingStatusCode operatingStatusCode) {
        this.operatingStatusCode = operatingStatusCode;
    }

    public FacilitySite getFacilitySite() {
        return this.facilitySite;
    }
    public void setFacilitySite(FacilitySite facilitySite) {
        this.facilitySite = facilitySite;
    }

    public String getUnitIdentifier() {
        return this.unitIdentifier;
    }
    public void setUnitIdentifier(String unitIdentifier) {
        this.unitIdentifier = unitIdentifier;
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

    public UnitMeasureCode getUnitOfMeasureCode() {
        return this.unitOfMeasureCode;
    }
    public void setUnitOfMeasureCode(UnitMeasureCode unitOfMeasureCode) {
        this.unitOfMeasureCode = unitOfMeasureCode;
    }

    public BigDecimal getDesignCapacity() {
        return designCapacity;
    }


    public void setDesignCapacity(BigDecimal designCapacity) {
        this.designCapacity = designCapacity;
    }


    public String getComments() {
        return comments;
    }


    public void setComments(String comments) {
        this.comments = comments;
    }

    public OperatingStatusCode getPreviousYearOperatingStatusCode() {
		return previousYearOperatingStatusCode;
	}

	public void setPreviousYearOperatingStatusCode(OperatingStatusCode previousYearOperatingStatusCode) {
		this.previousYearOperatingStatusCode = previousYearOperatingStatusCode;
	}
	
    public List<EmissionsProcess> getEmissionsProcesses() {
        return this.emissionsProcesses;
    }
    public void setEmissionsProcesses(List<EmissionsProcess> emissionsProcesses) {
        this.emissionsProcesses.clear();
        if (emissionsProcesses != null) {
            this.emissionsProcesses.addAll(emissionsProcesses);
        }
    }


    /***
     * Set the id property to null for this object and the id for it's direct children.  This method is useful to INSERT the updated object instead of UPDATE.
     */
    public void clearId() {
    	this.id = null;

		for (EmissionsProcess process : this.emissionsProcesses) {
			process.clearId();
		}
    }

}
