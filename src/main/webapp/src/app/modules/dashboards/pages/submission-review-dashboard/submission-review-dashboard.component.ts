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
import { Component, OnInit, ViewChild } from '@angular/core';
import { SubmissionsReviewDashboardService } from 'src/app/core/services/submissions-review-dashboard.service';
import { SubmissionUnderReview } from 'src/app/shared/models/submission-under-review';
import { SubmissionReviewListComponent } from 'src/app/modules/dashboards/components/submission-review-list/submission-review-list.component';
import { EmissionsReportingService } from 'src/app/core/services/emissions-reporting.service';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { SubmissionReviewModalComponent } from 'src/app/modules/dashboards/components/submission-review-modal/submission-review-modal.component';
import {SharedService} from 'src/app/core/services/shared.service';
import { FileAttachmentModalComponent } from 'src/app/modules/shared/components/file-attachment-modal/file-attachment-modal.component';
import { ReportStatus } from 'src/app/shared/enums/report-status';
import { UserContextService } from 'src/app/core/services/user-context.service';
import { EmissionsReportAgencyData } from 'src/app/shared/models/emissions-report-agency-data';
import { User } from 'src/app/shared/models/user';

@Component( {
    selector: 'app-submission-review-dashboard',
    templateUrl: './submission-review-dashboard.component.html',
    styleUrls: ['./submission-review-dashboard.component.scss']
} )
export class SubmissionReviewDashboardComponent implements OnInit {

    @ViewChild(SubmissionReviewListComponent, {static: true})

    private listComponent: SubmissionReviewListComponent;

    allSubmissions: SubmissionUnderReview[] = [];
    submissions: SubmissionUnderReview[] = [];
    user: User;
    hideButtons = false;
    invalidSelection = false;
    currentYear: number;
    selectedYear: number;
    selectedReportStatus = ReportStatus.SUBMITTED;
    selectedAgency: EmissionsReportAgencyData;
    selectedIndustrySector: string;

    agencyDataValues: EmissionsReportAgencyData[];
    yearValues: number[] = [];
    industrySectors: string[] = [];

    reportStatus = ReportStatus;

    submittedCount: 0;
    inProgressCount: 0;
    returnedCount: 0;
    advancedQACount: 0;
    approvedCount: 0;

    constructor(
        private userContext: UserContextService,
        private emissionReportService: EmissionsReportingService,
        private submissionsReviewDashboardService: SubmissionsReviewDashboardService,
        private modalService: NgbModal,
        private sharedService: SharedService) { }

    ngOnInit() {

        this.userContext.getUser()
        .subscribe(user => {

            this.user = user;

            this.emissionReportService.getAgencyReportedYears()
            .subscribe(result => {

                this.agencyDataValues = result.sort((a, b) => (a.programSystemCode.code > b.programSystemCode.code) ? 1 : -1);

                if (this.user.isReviewer()) {
                    const userAgency = this.agencyDataValues.find(item => item.programSystemCode.code === this.user.programSystemCode);
                    this.selectedAgency = userAgency;
                    this.selectedYear = this.selectedAgency?.years[0];
                    this.refreshFacilityReports();
                } else if (this.user.isAdmin()) {

                }
            });

            this.currentYear = new Date().getFullYear() - 1;

            if (this.user.isReviewer()) {
                this.submissionsReviewDashboardService.retrieveReviewerSubmissions(this.currentYear, null)
                .subscribe(submissions => {
                    this.filterAndCountSubmissions(submissions);
                });
            }
			if (this.user.isAdmin()) {
                this.submissionsReviewDashboardService.retrieveSubmissions(this.currentYear, null, null)
                .subscribe(submissions => {
                    this.filterAndCountSubmissions(submissions);
                });
            }

        });
    }

    onBeginAdvancedQA() {
        const selectedSubmissions = this.listComponent.tableData.filter(item => item.checked).map(item => item.emissionsReportId);

        if (!selectedSubmissions.length) {
            this.invalidSelection = true;
        } else {
            this.invalidSelection = false;
            this.emissionReportService.beginAdvancedQA(selectedSubmissions)
            .subscribe(() => {
                this.refreshFacilityReports();
				this.submissionsReviewDashboardService.retrieveReviewerSubmissions(this.currentYear, null)
                .subscribe(submissions => {
                    this.filterAndCountSubmissions(submissions);
                });
            });
        }
    }

    onApprove() {
        const selectedSubmissions = this.listComponent.tableData.filter(item => item.checked).map(item => item.emissionsReportId);

        if (!selectedSubmissions.length) {
            this.invalidSelection = true;
        } else {
            this.invalidSelection = false;
            const modalRef = this.modalService.open(SubmissionReviewModalComponent, { size: 'lg', backdrop: 'static' });
            modalRef.componentInstance.title = 'Accept Submissions';
            modalRef.componentInstance.message = 'Would you like to accept the selected submissions?';

            modalRef.result.then((comments) => {
                this.emissionReportService.acceptReports(selectedSubmissions, comments)
                .subscribe(() => {
                    this.refreshFacilityReports();
					this.submissionsReviewDashboardService.retrieveReviewerSubmissions(this.currentYear, null)
	                .subscribe(submissions => {
	                    this.filterAndCountSubmissions(submissions);
	                });
                });
            }, () => {
                // needed for dismissing without errors
            });
        }
    }

    onReject() {
        const selectedSubmissions = this.listComponent.tableData.filter(item => item.checked).map(item => item.emissionsReportId);

        if (!selectedSubmissions.length) {
            this.invalidSelection = true;
        } else {
            this.invalidSelection = false;
            const modalRef = this.modalService.open(FileAttachmentModalComponent, { size: 'lg', backdrop: 'static' });
            modalRef.componentInstance.reportId = selectedSubmissions[0];
            modalRef.componentInstance.title = 'Reject Submissions';
            modalRef.componentInstance.message = 'Would you like to reject the selected submissions?';

            modalRef.result.then((resp) => {
                this.emissionReportService.rejectReports(selectedSubmissions, resp.comments, resp.id)
                .subscribe(() => {
                    this.refreshFacilityReports();
	                this.submissionsReviewDashboardService.retrieveReviewerSubmissions(this.currentYear, null)
	                .subscribe(submissions => {
	                    this.filterAndCountSubmissions(submissions);
	                });
                });
            }, () => {
                // needed for dismissing without errors
            });
        }
    }

    refreshFacilityReports(): void {
        if (this.user.isReviewer()) {
            this.submissionsReviewDashboardService.retrieveReviewerSubmissions(this.selectedYear, this.selectedReportStatus)
            .subscribe((submissions) => {
                this.sortSubmissions(submissions);
            });
        } else if (this.user.isAdmin() && this.selectedAgency) {
            this.submissionsReviewDashboardService.retrieveSubmissions(
                this.selectedYear,
                this.selectedReportStatus,
                this.selectedAgency.programSystemCode.code)
            .subscribe((submissions) => {
                this.sortSubmissions(submissions);
            });
        } else {
            this.sortSubmissions([]);
		}
    }

    sortSubmissions(submissions: SubmissionUnderReview[]) {
        this.allSubmissions = submissions.sort((a, b) => (a.facilityName > b.facilityName) ? 1 : -1);
        // map down to industry values, convert to a Set to get distinct values, remove nulls, and then sort
        this.industrySectors = [...new Set(this.allSubmissions.map(item => item.industry))].filter(item => item != null).sort();
        this.filterSubmissions();
    }

    filterSubmissions() {
        // reset selected sector if the current selection is no longer in the dropdown
        if (this.selectedIndustrySector && !this.industrySectors.includes(this.selectedIndustrySector)) {
            this.selectedIndustrySector = null;
        }
        if (this.selectedIndustrySector) {
            this.submissions = this.allSubmissions.filter(item => item.industry === this.selectedIndustrySector);
        } else {
            this.submissions = this.allSubmissions;
        }
    }

    onStatusSelected() {

        if (this.selectedReportStatus === ReportStatus.SUBMITTED || this.selectedReportStatus === ReportStatus.ADVANCED_QA) {
            this.hideButtons = false;
        } else {
            this.hideButtons = true;
        }
        this.refreshFacilityReports();
    }

    onIndustrySelected(industry: string) {
        this.selectedIndustrySector = industry;
        this.filterSubmissions();
    }

    onAgencySelected() {

        if (this.selectedAgency?.years && !(this.selectedYear && this.selectedAgency.years.includes(+this.selectedYear))) {
            this.selectedYear = this.selectedAgency.years[0];
        }
        this.refreshFacilityReports();
		if (this.selectedAgency.programSystemCode) {
          this.submissionsReviewDashboardService.retrieveSubmissions(this.currentYear, null, this.selectedAgency.programSystemCode.code)
          .subscribe(submissions => {
              this.filterAndCountSubmissions(submissions);
          });
	    } else {
          this.filterAndCountSubmissions([]);
		}
    }

    filterAndCountSubmissions(submissions){
      this.approvedCount = this.advancedQACount = this.submittedCount = this.inProgressCount = this.returnedCount = 0;
      submissions.forEach(submission => {
        if (submission.reportStatus === ReportStatus.APPROVED) {
          this.approvedCount++;
        }
        if (submission.reportStatus === ReportStatus.SUBMITTED) {
          this.submittedCount++;
        }
        if (submission.reportStatus === ReportStatus.IN_PROGRESS){
          this.inProgressCount++;
        }
        if (submission.reportStatus === ReportStatus.RETURNED){
          this.returnedCount++;
        }
        if (submission.reportStatus === ReportStatus.ADVANCED_QA){
          this.advancedQACount++;
        }
      });
    }

    onFilter(reportStatus: ReportStatus) {
      this.selectedReportStatus = reportStatus;
      this.selectedYear = this.currentYear;
	  this.onStatusSelected();
    }

	onSubmissionDeleted() {
		this.submissionsReviewDashboardService.retrieveSubmissions(this.currentYear, null, this.selectedAgency.programSystemCode.code)
            .subscribe(submissions => {
				this.refreshFacilityReports();
                this.filterAndCountSubmissions(submissions);
            });
	}
}
