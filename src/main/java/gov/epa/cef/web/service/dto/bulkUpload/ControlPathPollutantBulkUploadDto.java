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
package gov.epa.cef.web.service.dto.bulkUpload;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import gov.epa.cef.web.annotation.CsvColumn;
import gov.epa.cef.web.annotation.CsvFileName;

import java.io.Serializable;

@CsvFileName(name = "control_path_pollutants.csv")
public class ControlPathPollutantBulkUploadDto extends BaseWorksheetDto implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "Control Path Pollutant ID is required.")
    private Long id;

    @NotNull(message = "Control Path ID is required.")
    private Long controlPathId;
    
    private String pathName;

    @NotBlank(message = "Pollutant Code is required.")
    @Size(max = 12, message = "Pollutant Code can not exceed {max} chars; found '${validatedValue}'.")
    private String pollutantCode;
    
    private String pollutantName;

    @NotBlank(message = "Percent Reduction is required.")
    @Pattern(regexp = "^\\d{0,3}(\\.\\d{1})?$",
        message = "Percent Reduction Efficiency is not in expected numeric format: '{3}.{1}' digits; found '${validatedValue}'.")
    private String percentReduction;

    public ControlPathPollutantBulkUploadDto() {

        super(WorksheetName.ControlPathPollutant);
    }

    @CsvColumn(name = "ID", order = 1)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @CsvColumn(name = "Control Path ID", order = 2)
    public Long getControlPathId() {
        return controlPathId;
    }

    public void setControlPathId(Long controlPathId) {
        this.controlPathId = controlPathId;
    }

    @CsvColumn(name = "Control Path Name", order = 2)
    public String getPathName() {
    	return pathName;
    }
    public void setPathName(String pathName) {
    	this.pathName = pathName;
    }

    @CsvColumn(name = "Pollutant Code", order = 3)
    public String getPollutantCode() {
        return pollutantCode;
    }

    public void setPollutantCode(String pollutant) {
        this.pollutantCode = pollutant;
    }

    @CsvColumn(name = "Pollutant Name", order = 3)
    public String getPollutantName() {
    	return pollutantName;
    }
    public void setPollutantName(String pollutantName) {
    	this.pollutantName = pollutantName;
    }

    @CsvColumn(name = "Percent Reduction", order = 4)
    public String getPercentReduction() {
        return percentReduction;
    }

    public void setPercentReduction(String percentReduction) {
        this.percentReduction = percentReduction;
    }

}
