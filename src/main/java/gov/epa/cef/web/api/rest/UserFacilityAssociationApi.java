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

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import gov.epa.cef.web.security.AppRole;
import gov.epa.cef.web.security.SecurityService;
import gov.epa.cef.web.service.dto.MasterFacilityRecordDto;
import gov.epa.cef.web.service.dto.UserFacilityAssociationDto;
import gov.epa.cef.web.service.impl.UserFacilityAssociationServiceImpl;

@RestController
@RequestMapping("/api/userFacilityAssociation")
public class UserFacilityAssociationApi {

    private final UserFacilityAssociationServiceImpl ufaService;

    private final SecurityService securityService;

    @Autowired
    UserFacilityAssociationApi(SecurityService securityService,
                               UserFacilityAssociationServiceImpl mfrService) {

        this.securityService = securityService;
        this.ufaService = mfrService;
    }


    @GetMapping(value = "/{id}")
    public ResponseEntity<UserFacilityAssociationDto> retrieveAssociation(@NotNull @PathVariable Long id) {

        UserFacilityAssociationDto result = this.ufaService.findById(id);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @DeleteMapping(value = "/{id}")
    public void deleteAssociation(@NotNull @PathVariable Long id) {

        this.ufaService.deleteById(id);
    }

    @PostMapping(value = "/request")
    @RolesAllowed(value = {AppRole.ROLE_PREPARER, AppRole.ROLE_NEI_CERTIFIER})
    public ResponseEntity<UserFacilityAssociationDto> createAssociationRequest(@NotNull @RequestBody MasterFacilityRecordDto facility) {

        UserFacilityAssociationDto result = this.ufaService.requestFacilityAssociation(facility, this.securityService.getCurrentApplicationUser());

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping(value = "/approve")
    public ResponseEntity<List<UserFacilityAssociationDto>> approveAssociationRequests(@NotNull @RequestBody List<UserFacilityAssociationDto> associations) {

        List<UserFacilityAssociationDto> result = this.ufaService.approveAssociations(associations);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping(value = "/reject")
    public ResponseEntity<List<UserFacilityAssociationDto>> rejectAssociationRequests(@NotNull @RequestBody RejectDto dto) {

        List<UserFacilityAssociationDto> result = this.ufaService.rejectAssociations(dto.getAssociations(), dto.getComments());

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping(value = "/user/{userRoleId}")
    public ResponseEntity<List<UserFacilityAssociationDto>> retrieveAssociationsForUser(
        @NotNull @PathVariable Long userRoleId) {

        List<UserFacilityAssociationDto> result =
            this.ufaService.findByUserRoleId(userRoleId);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping(value = "/facility/{masterFacilityRecordId}/details")
    public ResponseEntity<List<UserFacilityAssociationDto>> retrieveAssociationDetailsForFacility(
        @NotNull @PathVariable Long masterFacilityRecordId) {

        List<UserFacilityAssociationDto> result =
            this.ufaService.findDetailsByMasterFacilityRecordId(masterFacilityRecordId);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping(value = "/facility/{masterFacilityRecordId}/approved/details")
    public ResponseEntity<List<UserFacilityAssociationDto>> retrieveApprovedAssociationDetailsForFacility(
        @NotNull @PathVariable Long masterFacilityRecordId) {

        List<UserFacilityAssociationDto> result =
            this.ufaService.findDetailsByMasterFacilityRecordIdAndApproved(masterFacilityRecordId, true);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping(value = "/pending/details")
    public ResponseEntity<List<UserFacilityAssociationDto>> retrievePendingAssociationDetailsForCurrentProgram() {

        List<UserFacilityAssociationDto> result =
            this.ufaService.findDetailsByProgramSystemCodeAndApproved(this.securityService.getCurrentProgramSystemCode(), false);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping(value = "/my")
    public ResponseEntity<List<UserFacilityAssociationDto>> retrieveAssociationsForCurrentUser() {

        List<UserFacilityAssociationDto> result =
            this.ufaService.findByUserRoleId(this.securityService.getCurrentApplicationUser().getUserRoleId());

        return new ResponseEntity<>(result, HttpStatus.OK);
    }
    
    @GetMapping(value = "/myPrograms")
    public ResponseEntity<List<String>> retrieveProgramSystemCodeAssociationsForCurrentUser() {

        List<String> result =
            this.ufaService.findProgramSystemCodesByUserRoleId(this.securityService.getCurrentApplicationUser().getUserRoleId());

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping(value = "/migrate")
    @RolesAllowed(value = {AppRole.ROLE_CAERS_ADMIN})
    public ResponseEntity<List<UserFacilityAssociationDto>> migrateUserAssociations() {

        List<UserFacilityAssociationDto> result =
            this.ufaService.migrateAssociations();

        return new ResponseEntity<>(result, HttpStatus.OK);
    }


    public static class RejectDto {

        private List<UserFacilityAssociationDto> associations;

        private String comments;

        public List<UserFacilityAssociationDto> getAssociations() {

            return associations;
        }

        public void setAssociations(List<UserFacilityAssociationDto> associations) {

            this.associations = associations;
        }

        public String getComments() {

            return comments;
        }

        public void setComments(String comments) {

            this.comments = comments;
        }

    }
}
