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
package gov.epa.cef.web.security;


import gov.epa.cdx.shared.security.ApplicationUser;
import gov.epa.cef.web.config.CacheName;
import gov.epa.cef.web.domain.MasterFacilityRecord;
import gov.epa.cef.web.domain.UserFacilityAssociation;
import gov.epa.cef.web.exception.ApplicationErrorCode;
import gov.epa.cef.web.exception.ApplicationException;
import gov.epa.cef.web.repository.UserFacilityAssociationRepository;
import gov.epa.cef.web.security.enforcer.FacilityAccessEnforcer;
import gov.epa.cef.web.security.enforcer.FacilityAccessEnforcerImpl;
import gov.epa.cef.web.security.enforcer.ProgramIdRepoLocator;
import gov.epa.cef.web.security.enforcer.ReviewerFacilityAccessEnforcerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class SecurityService {

    private static final String FACILITY_ROLE_PREFIX = "{EIS}";

    private final CacheManager cacheManager;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final ProgramIdRepoLocator programIdRepoLocator;

    private final UserFacilityAssociationRepository ufaRepo;

    @Autowired
    SecurityService(UserFacilityAssociationRepository ufaRepo,
                    CacheManager cacheManager,
                    ProgramIdRepoLocator programIdRepoLocator) {

        this.ufaRepo = ufaRepo;

        this.cacheManager = cacheManager;

        this.programIdRepoLocator = programIdRepoLocator;

    }

    public List<GrantedAuthority> createUserRoles(AppRole.RoleType role, Long userRoleId) {

        List<GrantedAuthority> roles = new ArrayList<>();

        if (role != null) {
            roles.add(new SimpleGrantedAuthority(role.grantedRoleName()));
        } else {

            logger.warn("RoleId is null.");
        }

        if (userRoleId != null) {
            try {
                roles.addAll(this.ufaRepo.findByUserRoleId(userRoleId).stream()
                    .map(UserFacilityAssociation::getMasterFacilityRecord)
                    .map(new SecurityService.FacilityToRoleTransform())
                    .collect(Collectors.toList()));
            } catch (Exception e) {
                logger.warn(e.getMessage());
            }
        } else {

            logger.warn("UserRoleId is null.");
        }

        return roles;
    }

    public void evictUserCachedItems() {

        if (hasSecurityContext()) {

            Long userRoleId = getCurrentApplicationUser().getUserRoleId();
            evictUserCachedItems(userRoleId);

        } else {

            logger.warn("No user logged in. No cache items were evicted.");
        }
    }

    public FacilityAccessEnforcer facilityEnforcer() {

        if (hasRole(AppRole.RoleType.REVIEWER)||hasRole(AppRole.RoleType.CAERS_ADMIN)) {

            return new ReviewerFacilityAccessEnforcerImpl();
        }

        return new FacilityAccessEnforcerImpl(this.programIdRepoLocator, getCurrentUserMasterFacilityIds());
    }

    public ApplicationUser getCurrentApplicationUser() {

        return (ApplicationUser) getCurrentPrincipal();
    }

    public String getCurrentUserId() {

        return getCurrentApplicationUser().getUsername();
    }

    public String getCurrentProgramSystemCode() {

        return getCurrentApplicationUser().getClientId();
    }

    /**
     * Check if the current user has any of the provided roles
     *
     * @param role
     * @return
     */
    public boolean hasRole(AppRole.RoleType role) {

        return getCurrentRoles().stream().anyMatch(r -> r.getAuthority().equals(role.grantedRoleName()));
    }

    public boolean hasSecurityContext() {

        return SecurityContextHolder.getContext() != null
            && SecurityContextHolder.getContext().getAuthentication() != null
            && SecurityContextHolder.getContext().getAuthentication().getPrincipal() != null;
    }

    List<GrantedAuthority> createUserRoles(Long roleId, Long userRoleId) {

        AppRole.RoleType role = null;
        if (roleId != null) {
            try {
                role = AppRole.RoleType.fromId(roleId);
            } catch (IllegalArgumentException e) {
                logger.warn(e.getMessage());
            }
        } else {

            logger.warn("RoleId is null.");
        }

        return createUserRoles(role, userRoleId);
    }

    void evictUserCachedItems(long userRoleId) {

        this.cacheManager.getCache(CacheName.UserProgramFacilities).evict(userRoleId);
        this.cacheManager.getCache(CacheName.UserMasterFacilityIds).evict(userRoleId);

        logger.info("Program Facilities for UserRoleId-[{}] were evicted from cache.", userRoleId);
    }

    ApplicationUser getCurrentApplicationUser(Authentication authentication) {

        return (ApplicationUser) getCurrentPrincipal();
    }

    /**
     * Check if the security context is empty
     *
     * @throws ApplicationException
     */
    private void checkSecurityContext() {

        if (hasSecurityContext() == false) {
            throw new ApplicationException(
                ApplicationErrorCode.E_AUTHORIZATION,
                "Security Context, authentication or principal is empty.");
        }
    }

    /**
     * Return the current security principal
     *
     * @return
     */
    private Object getCurrentPrincipal() {
        // checking security context
        checkSecurityContext();
        return SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    /**
     * Get the security roles of the current user
     *
     * @return
     */
    private Collection<? extends GrantedAuthority> getCurrentRoles() {

        checkSecurityContext();
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities();
    }

    private List<Long> getCurrentUserMasterFacilityIds() {

        return this.ufaRepo.retrieveMasterFacilityRecordIds(getCurrentApplicationUser().getUserRoleId());
    }

    static class FacilityToRoleTransform implements Function<MasterFacilityRecord, GrantedAuthority> {

        @Override
        public GrantedAuthority apply(MasterFacilityRecord mfr) {

            return new SimpleGrantedAuthority(
                String.format("%s%d", FACILITY_ROLE_PREFIX, mfr.getId()));
        }
    }
}
