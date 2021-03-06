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
export class SideNavItem {
  baseUrl: string;
  id: number;
  description: string;
  children: SideNavItem[];

  constructor(id: number, description: string, baseUrl: string, children: SideNavItem[]) {
    this.id = id;
    this.description = description;
    this.baseUrl = baseUrl;
    this.children = children;
  }

  get url(): string {
    if (this.id) {
      return `${this.baseUrl}/${this.id}`;
    }
    return this.baseUrl;
  }
}
