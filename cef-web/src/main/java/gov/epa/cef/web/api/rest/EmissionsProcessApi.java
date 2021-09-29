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
package gov.epa.cef.web.api.rest;

import gov.epa.cef.web.repository.EmissionsProcessRepository;
import gov.epa.cef.web.repository.EmissionsUnitRepository;
import gov.epa.cef.web.repository.ReleasePointRepository;
import gov.epa.cef.web.security.SecurityService;
import gov.epa.cef.web.service.EmissionsProcessService;
import gov.epa.cef.web.service.dto.EmissionsProcessDto;
import gov.epa.cef.web.service.dto.EmissionsProcessSaveDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;
import java.util.Collection;

@RestController
@RequestMapping("/api/emissionsProcess")
public class EmissionsProcessApi {

    private final EmissionsProcessService processService;

    private final SecurityService securityService;

    @Autowired
    EmissionsProcessApi(SecurityService securityService,
                        EmissionsProcessService processService) {

        this.securityService = securityService;
        this.processService = processService;
    }

    /**
     * Create a new Emissions Process
     * @param dto
     * @return
     */
    @PostMapping
    public ResponseEntity<EmissionsProcessDto> createEmissionsProcess(
        @NotNull @RequestBody EmissionsProcessSaveDto dto) {

        this.securityService.facilityEnforcer()
            .enforceEntity(dto.getEmissionsUnitId(), EmissionsUnitRepository.class);

        EmissionsProcessDto result = processService.create(dto);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    /**
     * Update an Emissions Process
     * @param id
     * @param dto
     * @return
     */
    @PutMapping(value = "/{id}")
    public ResponseEntity<EmissionsProcessDto> updateEmissionsProcess(
        @NotNull @PathVariable Long id, @NotNull @RequestBody EmissionsProcessSaveDto dto) {

        this.securityService.facilityEnforcer().enforceEntity(id, EmissionsProcessRepository.class);

        EmissionsProcessDto result = processService.update(dto.withId(id));

        return new ResponseEntity<EmissionsProcessDto>(result, HttpStatus.OK);
    }

    /**
     * Retrieve Emissions Process by id
     * @param id
     * @return
     */
    @GetMapping(value = "/{id}")
    public ResponseEntity<EmissionsProcessDto> retrieveEmissionsProcess(@NotNull @PathVariable Long id) {

        this.securityService.facilityEnforcer().enforceEntity(id, EmissionsProcessRepository.class);

        EmissionsProcessDto result = processService.retrieveById(id);

        return new ResponseEntity<EmissionsProcessDto>(result, HttpStatus.OK);
    }

    /**
     * Retrieve versions of this process from the last year reported
     * @param id
     * @return
     */
    @GetMapping(value = "/{id}/previous")
    public ResponseEntity<EmissionsProcessDto> retrievePreviousEmissionsProcess(@NotNull @PathVariable Long id) {

        this.securityService.facilityEnforcer().enforceEntity(id, EmissionsProcessRepository.class);

        EmissionsProcessDto result = processService.retrievePreviousById(id);

        return new ResponseEntity<EmissionsProcessDto>(result, HttpStatus.OK);
    }

    /**
     * Retrieve Emissions Processes for a release point
     * @param releasePointId
     * @return
     */
    @GetMapping(value = "/releasePoint/{releasePointId}")
    public ResponseEntity<Collection<EmissionsProcessDto>> retrieveEmissionsProcessesForReleasePoint(
        @NotNull @PathVariable Long releasePointId) {

        this.securityService.facilityEnforcer().enforceEntity(releasePointId, ReleasePointRepository.class);

        Collection<EmissionsProcessDto> result = processService.retrieveForReleasePoint(releasePointId);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    /**
     * Retrieve Emissions Processes for an emissions unit
     * @param emissionsUnitId
     * @return
     */
    @GetMapping(value = "/emissionsUnit/{emissionsUnitId}")
    public ResponseEntity<Collection<EmissionsProcessDto>> retrieveEmissionsProcessesForEmissionsUnit(
        @NotNull @PathVariable Long emissionsUnitId) {

        this.securityService.facilityEnforcer().enforceEntity(emissionsUnitId, EmissionsUnitRepository.class);

        Collection<EmissionsProcessDto> result = processService.retrieveForEmissionsUnit(emissionsUnitId);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    /**
     * Delete an Emissions Processes for given id
     * @param id
     * @return
     */
    @DeleteMapping(value = "/{id}")
    public void deleteEmissionsProcess(@PathVariable Long id) {

        this.securityService.facilityEnforcer().enforceEntity(id, EmissionsProcessRepository.class);

        processService.delete(id);
    }
}