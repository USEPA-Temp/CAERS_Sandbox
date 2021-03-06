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

@Entity
@Table(name = "control_path")
public class ControlPath extends BaseAuditEntity {

    private static final long serialVersionUID = 1L;

    @Column(name = "description", nullable = false, length = 200)
    private String description;
    
    @Column(name = "path_id", nullable = false, length = 20)
    private String pathId;
    
    //percentControl maps to Percent Path Effectiveness in the UI
    @Column(name = "percent_control", precision = 6, scale = 3)
    private BigDecimal percentControl;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "controlPath")
    private List<ControlAssignment> assignments = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "controlPathChild")
    private List<ControlAssignment> childAssignments = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "facility_site_id", nullable = false)
    private FacilitySite facilitySite;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "controlPath")
    private List<ReleasePointAppt> releasePointAppts = new ArrayList<>();
    
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "controlPath")
    private List<ControlPathPollutant> pollutants = new ArrayList<>();


    /**
     * Default constructor
     */
    public ControlPath() {}


    /**
     * Copy constructor
     * @param originalControlPath
     */
    public ControlPath(FacilitySite facilitySite, ControlPath originalControlPath) {
    	this.id = originalControlPath.getId();
    	this.facilitySite = facilitySite;
    	this.description = originalControlPath.getDescription();
    	this.pathId = originalControlPath.getPathId();
    	this.percentControl = originalControlPath.getPercentControl();
    	for (ControlPathPollutant pollutant : originalControlPath.getPollutants()) {
    		this.pollutants.add(new ControlPathPollutant(this, pollutant));
    	}
//    	this.assignments = new HashSet<ControlAssignment>();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getPathId() {
        return pathId;
    }

    public void setPathId(String pathId) {
        this.pathId = pathId;
    }
    
    public BigDecimal getPercentControl() {
        return percentControl;
    }

    public void setPercentControl(BigDecimal percentControl) {
        this.percentControl = percentControl;
    }

    public List<ControlAssignment> getAssignments() {
        return assignments;
    }

    public void setAssignments(List<ControlAssignment> assignments) {

        this.assignments.clear();
        if (assignments != null) {
            this.assignments.addAll(assignments);
        }
    }

    public List<ControlAssignment> getChildAssignments() {
        return childAssignments;
    }

    public void setChildAssignments(List<ControlAssignment> childAssignments) {

        this.childAssignments.clear();
        if (childAssignments != null) {
            this.childAssignments.addAll(childAssignments);
        }
    }

    public FacilitySite getFacilitySite() {
        return facilitySite;
    }

    public void setFacilitySite(FacilitySite facilitySite) {
        this.facilitySite = facilitySite;
    }

    public List<ReleasePointAppt> getReleasePointAppts() {
        return releasePointAppts;
    }

    public void setReleasePointAppts(List<ReleasePointAppt> releasePointAppts) {

        this.releasePointAppts.clear();
        if (releasePointAppts != null) {
            this.releasePointAppts.addAll(releasePointAppts);
        }
    }
    
    public List<ControlPathPollutant> getPollutants() {
        return pollutants;
    }
    
    public void setPollutants(List<ControlPathPollutant> pollutants) {

        this.pollutants.clear();
        if (pollutants != null) {
            this.pollutants.addAll(pollutants);
        }
    }


    /***
     * Set the id property to null for this object and the id for it's direct children.  This method is useful to INSERT the updated object instead of UPDATE.
     */
    public void clearId() {
    	this.id = null;

    	//clear the ids for the child control assignments
        for (ControlAssignment controlAssignment : this.assignments) {
            controlAssignment.clearId();
        }
        
        for (ControlPathPollutant pollutant : this.pollutants) {
    		pollutant.clearId();
    	}
    }

}
