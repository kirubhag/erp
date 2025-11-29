import{a as W}from"./chunk-MSNXGUWN.js";import{a as B}from"./chunk-4I2P2EEM.js";import{b as R,d as D,g as N,n as A,o as L,p as V,v as j}from"./chunk-NWWELG7D.js";import{$ as x,Aa as E,Ba as F,Ca as H,D as h,E as b,L as r,M as I,Ma as z,P as _,R as l,U as M,V as e,W as n,X as g,_ as y,aa as m,fa as i,ga as f,ha as C,ka as O,la as w,ma as S,oa as T,qa as k,z as v}from"./chunk-OXAEJMWT.js";var U=a=>({active:a});function G(a,d){if(a&1&&(e(0,"option",30),i(1),n()),a&2){let t=d.$implicit;l("value",t),r(),f(t)}}function Y(a,d){if(a&1){let t=y();e(0,"button",31),x("click",function(){h(t);let o=m();return b(o.clearFilters())}),g(1,"i",32),i(2,"Clear Filters "),n()}}function q(a,d){if(a&1&&(e(0,"div",33),g(1,"i",34),i(2),n()),a&2){let t=m();r(2),C(" ",t.errorMessage," ")}}function J(a,d){a&1&&(e(0,"div",35),g(1,"div",36),e(2,"p"),i(3,"Loading import history..."),n()())}function K(a,d){a&1&&(e(0,"div",37),g(1,"i",38),e(2,"h3"),i(3,"No Import History"),n(),e(4,"p"),i(5,"No data imports have been recorded yet."),n()())}function Q(a,d){if(a&1&&(e(0,"tr",44)(1,"td",45),g(2,"i"),e(3,"strong"),i(4),n()(),e(5,"td")(6,"span",46),i(7),n()(),e(8,"td")(9,"span",47),i(10),n()(),e(11,"td")(12,"span",48),g(13,"i"),i(14),n()(),e(15,"td"),i(16),n(),e(17,"td"),i(18),n(),e(19,"td"),i(20),n()()),a&2){let t=d.$implicit,s=m(2);l("ngClass","status-"+t.importStatus.toLowerCase()),r(2),M("fas fa-database me-2"),r(2),f(t.entityName),r(3),f(t.importType),r(3),f(t.recordCount),r(2),l("ngClass",s.getStatusBadgeClass(t.importStatus)),r(),M(s.getStatusIcon(t.importStatus)+" me-1"),r(),C(" ",t.importStatus," "),r(2),f(t.importedBy),r(2),f(s.formatDate(t.importStartTime)),r(2),f(s.getDurationFormatted(t))}}function X(a,d){if(a&1&&(e(0,"div",54)(1,"span",55),i(2,"Error:"),n(),e(3,"span",58),i(4),n()()),a&2){let t=m().$implicit;r(4),f(t.errorMessage)}}function Z(a,d){if(a&1&&(e(0,"div",49)(1,"div",50)(2,"div",51),g(3,"i",52),e(4,"h4"),i(5),n()(),e(6,"span",48),g(7,"i"),i(8),n()(),e(9,"div",53)(10,"div",54)(11,"span",55),i(12,"Type:"),n(),e(13,"span",46),i(14),n()(),e(15,"div",54)(16,"span",55),i(17,"Records:"),n(),e(18,"span",56),i(19),n()(),e(20,"div",54)(21,"span",55),i(22,"Imported By:"),n(),e(23,"span",56),i(24),n()(),e(25,"div",54)(26,"span",55),i(27,"Start Time:"),n(),e(28,"span",56),i(29),n()(),e(30,"div",54)(31,"span",55),i(32,"Duration:"),n(),e(33,"span",56),i(34),n()(),_(35,X,5,1,"div",57),n()()),a&2){let t=d.$implicit,s=m(2);l("ngClass","status-"+t.importStatus.toLowerCase()),r(5),f(t.entityName),r(),l("ngClass",s.getStatusBadgeClass(t.importStatus)),r(),M(s.getStatusIcon(t.importStatus)+" me-1"),r(),C(" ",t.importStatus," "),r(6),f(t.importType),r(5),f(t.recordCount),r(5),f(t.importedBy),r(5),f(s.formatDate(t.importStartTime)),r(5),f(s.getDurationFormatted(t)),r(),l("ngIf",t.errorMessage)}}function tt(a,d){if(a&1&&(e(0,"div",39)(1,"table",40)(2,"thead")(3,"tr")(4,"th"),i(5,"Entity"),n(),e(6,"th"),i(7,"Type"),n(),e(8,"th"),i(9,"Records"),n(),e(10,"th"),i(11,"Status"),n(),e(12,"th"),i(13,"Imported By"),n(),e(14,"th"),i(15,"Start Time"),n(),e(16,"th"),i(17,"Duration"),n()()(),e(18,"tbody"),_(19,Q,21,13,"tr",41),n()(),e(20,"div",42),_(21,Z,36,12,"div",43),n()()),a&2){let t=m();r(19),l("ngForOf",t.getDisplayRecords()),r(2),l("ngForOf",t.getDisplayRecords())}}function et(a,d){if(a&1){let t=y();e(0,"button",69),x("click",function(){h(t);let o=m(2);return b(o.goToPage(1))}),i(1," 1 "),n()}}function nt(a,d){a&1&&(e(0,"span",70),i(1,"..."),n())}function ot(a,d){if(a&1){let t=y();e(0,"button",71),x("click",function(){let o=h(t).$implicit,p=m(2);return b(p.goToPage(o))}),i(1),n()}if(a&2){let t=d.$implicit,s=m(2);l("ngClass",k(2,U,t===s.currentPage)),r(),C(" ",t," ")}}function it(a,d){a&1&&(e(0,"span",70),i(1,"..."),n())}function rt(a,d){if(a&1){let t=y();e(0,"button",69),x("click",function(){h(t);let o=m(2);return b(o.goToPage(o.totalPages))}),i(1),n()}if(a&2){let t=m(2);r(),C(" ",t.totalPages," ")}}function at(a,d){if(a&1){let t=y();e(0,"div",59)(1,"div",60),i(2),n(),e(3,"nav",61)(4,"button",62),x("click",function(){h(t);let o=m();return b(o.prevPage())}),g(5,"i",63),i(6,"Previous "),n(),e(7,"div",64),_(8,et,2,0,"button",65)(9,nt,2,0,"span",66)(10,ot,2,4,"button",67)(11,it,2,0,"span",66)(12,rt,2,1,"button",65),n(),e(13,"button",62),x("click",function(){h(t);let o=m();return b(o.nextPage())}),i(14," Next"),g(15,"i",68),n()()()}if(a&2){let t=m();r(2),C(" ",t.getRecordSummary()," "),r(2),l("disabled",t.currentPage===1),r(4),l("ngIf",t.currentPage>2),r(),l("ngIf",t.currentPage>3),r(),l("ngForOf",t.getPageNumbers()),r(),l("ngIf",t.currentPage<t.totalPages-2),r(),l("ngIf",t.currentPage<t.totalPages-1),r(),l("disabled",t.currentPage===t.totalPages)}}var ft=(()=>{class a{constructor(t){this.organizationService=t,this.importHistoryRecords=[],this.filteredRecords=[],this.loading=!1,this.errorMessage="",this.currentPage=1,this.pageSize=10,this.totalRecords=0,this.totalPages=0,this.searchTerm="",this.filterStatus="",this.filterType="",this.statusColors={SUCCESS:"#28a745",PENDING:"#ffc107",FAILED:"#dc3545",PARTIAL:"#fd7e14"},this.statusIcons={SUCCESS:"fas fa-check-circle",PENDING:"fas fa-hourglass-half",FAILED:"fas fa-times-circle",PARTIAL:"fas fa-exclamation-triangle"},this.importTypes=["SAMPLE_DATA","MANUAL_IMPORT","REGISTRATION","MIGRATION","SYNC"]}ngOnInit(){this.loadImportHistory()}loadImportHistory(){this.loading=!0,this.errorMessage="",this.organizationService.getImportHistory(this.currentPage-1,this.pageSize).subscribe({next:t=>{t.content?(this.importHistoryRecords=t.content,this.totalRecords=t.totalElements,this.totalPages=t.totalPages):Array.isArray(t)&&(this.importHistoryRecords=t,this.totalRecords=t.length,this.totalPages=Math.ceil(this.totalRecords/this.pageSize)),this.applyFilters(),this.loading=!1},error:t=>{this.loading=!1,this.errorMessage="Failed to load import history. Please try again.",console.error("Error loading import history:",t)}})}applyFilters(){let t=[...this.importHistoryRecords];if(this.searchTerm){let s=this.searchTerm.toLowerCase();t=t.filter(o=>o.entityName.toLowerCase().includes(s)||o.importType.toLowerCase().includes(s)||o.source?.toLowerCase().includes(s)||o.importedBy.toLowerCase().includes(s))}this.filterStatus&&(t=t.filter(s=>s.importStatus===this.filterStatus)),this.filterType&&(t=t.filter(s=>s.importType===this.filterType)),this.filteredRecords=t}onSearchChange(t){let s=t.target;this.searchTerm=s.value,this.currentPage=1,this.applyFilters()}onStatusFilterChange(t){this.filterStatus=t,this.currentPage=1,this.applyFilters()}onTypeFilterChange(t){this.filterType=t,this.currentPage=1,this.applyFilters()}clearFilters(){this.searchTerm="",this.filterStatus="",this.filterType="",this.currentPage=1,this.applyFilters()}getDurationFormatted(t){if(!t.importStartTime||!t.importEndTime)return"N/A";let s=new Date(t.importStartTime).getTime(),p=new Date(t.importEndTime).getTime()-s,c=Math.floor(p/1e3);return c<60?`${c}s`:`${Math.floor(c/60)}m ${c%60}s`}getStatusBadgeClass(t){return`badge-${t.toLowerCase()}`}getStatusIcon(t){return this.statusIcons[t]||"fas fa-info-circle"}getStatusColor(t){return this.statusColors[t]||"#6c757d"}formatDate(t){return new Date(t).toLocaleDateString("en-US",{year:"numeric",month:"short",day:"numeric",hour:"2-digit",minute:"2-digit"})}getPageNumbers(){let t=[],o=Math.max(1,this.currentPage-Math.floor(2.5)),p=Math.min(this.totalPages,o+5-1);p-o<4&&(o=Math.max(1,p-5+1));for(let c=o;c<=p;c++)t.push(c);return t}goToPage(t){t>=1&&t<=this.totalPages&&(this.currentPage=t,this.loadImportHistory())}nextPage(){this.currentPage<this.totalPages&&this.goToPage(this.currentPage+1)}prevPage(){this.currentPage>1&&this.goToPage(this.currentPage-1)}exportToCSV(){let t=["Entity Name","Import Type","Record Count","Status","Imported By","Start Time","End Time","Duration"],s=this.filteredRecords.map(u=>[u.entityName,u.importType,u.recordCount.toString(),u.importStatus,u.importedBy,this.formatDate(u.importStartTime),u.importEndTime?this.formatDate(u.importEndTime):"N/A",this.getDurationFormatted(u)]),o=t.join(",")+`
`;s.forEach(u=>{o+=u.map($=>`"${$}"`).join(",")+`
`});let p=new Blob([o],{type:"text/csv"}),c=window.URL.createObjectURL(p),P=document.createElement("a");P.href=c,P.download=`import-history-${new Date().toISOString().split("T")[0]}.csv`,P.click()}getDisplayRecords(){let t=(this.currentPage-1)*this.pageSize,s=t+this.pageSize;return this.filteredRecords.slice(t,s)}getRecordSummary(){let t=(this.currentPage-1)*this.pageSize+1,s=Math.min(t+this.pageSize-1,this.filteredRecords.length);return`Showing ${t} to ${s} of ${this.filteredRecords.length} records`}static{this.\u0275fac=function(s){return new(s||a)(I(B))}}static{this.\u0275cmp=v({type:a,selectors:[["app-import-history"]],standalone:!0,features:[T],decls:46,vars:11,consts:[[1,"setup-layout"],[1,"import-history-content"],[1,"import-history-wrapper"],[1,"page-header"],[1,"header-content"],[1,"fas","fa-history","me-2"],[1,"btn","btn-primary","export-btn",3,"click","disabled"],[1,"fas","fa-download","me-2"],[1,"filters-section"],[1,"search-box"],[1,"fas","fa-search","search-icon"],["type","text","placeholder","Search by entity, type, source, or user...",1,"search-input",3,"ngModelChange","input","ngModel"],[1,"filter-group"],[1,"filter-item"],["for","statusFilter"],["id","statusFilter",1,"filter-select",3,"ngModelChange","change","ngModel"],["value",""],["value","SUCCESS"],["value","PENDING"],["value","FAILED"],["value","PARTIAL"],["for","typeFilter"],["id","typeFilter",1,"filter-select",3,"ngModelChange","change","ngModel"],[3,"value",4,"ngFor","ngForOf"],["class","btn btn-outline-secondary clear-btn",3,"click",4,"ngIf"],["class","alert alert-danger",4,"ngIf"],["class","loading-state",4,"ngIf"],["class","empty-state",4,"ngIf"],["class","history-container",4,"ngIf"],["class","pagination-section",4,"ngIf"],[3,"value"],[1,"btn","btn-outline-secondary","clear-btn",3,"click"],[1,"fas","fa-times","me-2"],[1,"alert","alert-danger"],[1,"fas","fa-exclamation-circle","me-2"],[1,"loading-state"],[1,"spinner"],[1,"empty-state"],[1,"fas","fa-inbox"],[1,"history-container"],[1,"history-table","desktop-table"],["class","history-row",3,"ngClass",4,"ngFor","ngForOf"],[1,"mobile-cards"],["class","history-card",3,"ngClass",4,"ngFor","ngForOf"],[1,"history-row",3,"ngClass"],[1,"entity-name"],[1,"badge","badge-info"],[1,"record-count"],[1,"status-badge",3,"ngClass"],[1,"history-card",3,"ngClass"],[1,"card-header"],[1,"entity-header"],[1,"fas","fa-database","me-2"],[1,"card-body"],[1,"card-row"],[1,"label"],[1,"value"],["class","card-row",4,"ngIf"],[1,"value","error"],[1,"pagination-section"],[1,"records-summary"],[1,"pagination-nav"],[1,"btn","btn-outline-secondary","btn-sm",3,"click","disabled"],[1,"fas","fa-chevron-left","me-1"],[1,"page-buttons"],["class","page-btn",3,"click",4,"ngIf"],["class","page-ellipsis",4,"ngIf"],["class","page-btn",3,"ngClass","click",4,"ngFor","ngForOf"],[1,"fas","fa-chevron-right","ms-1"],[1,"page-btn",3,"click"],[1,"page-ellipsis"],[1,"page-btn",3,"click","ngClass"]],template:function(s,o){s&1&&(e(0,"div",0),g(1,"app-settings-sidebar"),e(2,"div",1)(3,"div",2)(4,"div",3)(5,"div",4)(6,"h1"),g(7,"i",5),i(8,"Import History"),n(),e(9,"p"),i(10,"Track all data imports and population events"),n()(),e(11,"button",6),x("click",function(){return o.exportToCSV()}),g(12,"i",7),i(13,"Export to CSV "),n()(),e(14,"div",8)(15,"div",9),g(16,"i",10),e(17,"input",11),S("ngModelChange",function(c){return w(o.searchTerm,c)||(o.searchTerm=c),c}),x("input",function(c){return o.onSearchChange(c)}),n()(),e(18,"div",12)(19,"div",13)(20,"label",14),i(21,"Status:"),n(),e(22,"select",15),S("ngModelChange",function(c){return w(o.filterStatus,c)||(o.filterStatus=c),c}),x("change",function(){return o.onStatusFilterChange(o.filterStatus)}),e(23,"option",16),i(24,"All Statuses"),n(),e(25,"option",17),i(26,"Success"),n(),e(27,"option",18),i(28,"Pending"),n(),e(29,"option",19),i(30,"Failed"),n(),e(31,"option",20),i(32,"Partial"),n()()(),e(33,"div",13)(34,"label",21),i(35,"Type:"),n(),e(36,"select",22),S("ngModelChange",function(c){return w(o.filterType,c)||(o.filterType=c),c}),x("change",function(){return o.onTypeFilterChange(o.filterType)}),e(37,"option",16),i(38,"All Types"),n(),_(39,G,2,2,"option",23),n()(),_(40,Y,3,0,"button",24),n()(),_(41,q,3,1,"div",25)(42,J,4,0,"div",26)(43,K,6,0,"div",27)(44,tt,22,2,"div",28)(45,at,16,8,"div",29),n()()()),s&2&&(r(11),l("disabled",o.filteredRecords.length===0),r(6),O("ngModel",o.searchTerm),r(5),O("ngModel",o.filterStatus),r(14),O("ngModel",o.filterType),r(3),l("ngForOf",o.importTypes),r(),l("ngIf",o.searchTerm||o.filterStatus||o.filterType),r(),l("ngIf",o.errorMessage),r(),l("ngIf",o.loading),r(),l("ngIf",!o.loading&&o.filteredRecords.length===0),r(),l("ngIf",!o.loading&&o.filteredRecords.length>0),r(),l("ngIf",!o.loading&&o.filteredRecords.length>0))},dependencies:[z,E,F,H,j,L,V,R,A,D,N,W],styles:[".import-history-page[_ngcontent-%COMP%]{display:flex;min-height:100vh}.import-history-sidebar[_ngcontent-%COMP%]{width:250px;background-color:#f8f9fa;border-right:1px solid #e9ecef;overflow-y:auto}.import-history-content[_ngcontent-%COMP%]{flex:1;overflow-y:auto;padding-top:80px}.import-history-wrapper[_ngcontent-%COMP%]{padding:30px;max-width:1200px;margin:0 auto}.page-header[_ngcontent-%COMP%]{display:flex;justify-content:space-between;align-items:center;margin-bottom:30px}.header-content[_ngcontent-%COMP%]   h1[_ngcontent-%COMP%]{font-size:2rem;font-weight:700;margin-bottom:5px;color:#333}.header-content[_ngcontent-%COMP%]   p[_ngcontent-%COMP%]{color:#6c757d;margin:0}.export-btn[_ngcontent-%COMP%]{background-color:#667eea;color:#fff;border:none;padding:10px 20px;border-radius:6px;cursor:pointer;transition:all .3s ease}.export-btn[_ngcontent-%COMP%]:hover:not(:disabled){background-color:#5568d3;transform:translateY(-2px)}.export-btn[_ngcontent-%COMP%]:disabled{opacity:.5;cursor:not-allowed}.filters-section[_ngcontent-%COMP%]{display:flex;gap:15px;margin-bottom:25px;flex-wrap:wrap}.search-box[_ngcontent-%COMP%]{position:relative;flex:1;min-width:250px}.search-icon[_ngcontent-%COMP%]{position:absolute;left:12px;top:50%;transform:translateY(-50%);color:#999}.search-input[_ngcontent-%COMP%]{width:100%;padding:10px 12px 10px 40px;border:1px solid #ddd;border-radius:6px;font-size:.95rem}.search-input[_ngcontent-%COMP%]:focus{outline:none;border-color:#667eea;box-shadow:0 0 0 3px #667eea1a}.filter-group[_ngcontent-%COMP%]{display:flex;gap:12px;flex-wrap:wrap;align-items:center}.filter-item[_ngcontent-%COMP%]{display:flex;gap:8px;align-items:center}.filter-item[_ngcontent-%COMP%]   label[_ngcontent-%COMP%]{font-weight:500;color:#333;white-space:nowrap}.filter-select[_ngcontent-%COMP%]{padding:8px 12px;border:1px solid #ddd;border-radius:6px;font-size:.95rem;background-color:#fff;cursor:pointer}.filter-select[_ngcontent-%COMP%]:focus{outline:none;border-color:#667eea;box-shadow:0 0 0 3px #667eea1a}.clear-btn[_ngcontent-%COMP%]{background-color:#fff;color:#333;border:1px solid #ddd;padding:8px 16px;border-radius:6px;cursor:pointer;transition:all .3s ease}.clear-btn[_ngcontent-%COMP%]:hover{background-color:#f8f9fa}.alert[_ngcontent-%COMP%]{padding:15px;border-radius:6px;margin-bottom:25px}.alert-danger[_ngcontent-%COMP%]{background-color:#f8d7da;color:#721c24;border:1px solid #f5c6cb}.loading-state[_ngcontent-%COMP%]{display:flex;flex-direction:column;align-items:center;justify-content:center;padding:60px 20px}.spinner[_ngcontent-%COMP%]{width:40px;height:40px;border:4px solid #f3f3f3;border-top:4px solid #667eea;border-radius:50%;animation:_ngcontent-%COMP%_spin 1s linear infinite}@keyframes _ngcontent-%COMP%_spin{0%{transform:rotate(0)}to{transform:rotate(360deg)}}.empty-state[_ngcontent-%COMP%]{text-align:center;padding:60px 20px;color:#6c757d}.empty-state[_ngcontent-%COMP%]   i[_ngcontent-%COMP%]{font-size:3rem;color:#ddd;margin-bottom:20px}.empty-state[_ngcontent-%COMP%]   h3[_ngcontent-%COMP%]{font-size:1.3rem;font-weight:600;margin-bottom:10px}.history-container[_ngcontent-%COMP%]{background:#fff;border-radius:8px;box-shadow:0 2px 10px #0000000d;overflow-x:auto;margin-bottom:25px}.desktop-table[_ngcontent-%COMP%]{width:100%;border-collapse:collapse}.desktop-table[_ngcontent-%COMP%]   thead[_ngcontent-%COMP%]{background-color:#f8f9fa;border-bottom:2px solid #ddd}.desktop-table[_ngcontent-%COMP%]   th[_ngcontent-%COMP%]{padding:15px;text-align:left;font-weight:600;color:#333;font-size:.9rem;text-transform:uppercase;letter-spacing:.5px}.desktop-table[_ngcontent-%COMP%]   tbody[_ngcontent-%COMP%]   tr[_ngcontent-%COMP%]{border-bottom:1px solid #e9ecef;transition:background-color .2s ease}.desktop-table[_ngcontent-%COMP%]   tbody[_ngcontent-%COMP%]   tr[_ngcontent-%COMP%]:hover{background-color:#f8f9fa}.history-row[_ngcontent-%COMP%]   td[_ngcontent-%COMP%]{padding:15px;color:#333;font-size:.95rem}.history-row.status-success[_ngcontent-%COMP%]{border-left:4px solid #28a745}.history-row.status-failed[_ngcontent-%COMP%]{border-left:4px solid #dc3545}.history-row.status-pending[_ngcontent-%COMP%]{border-left:4px solid #ffc107}.history-row.status-partial[_ngcontent-%COMP%]{border-left:4px solid #fd7e14}.entity-name[_ngcontent-%COMP%]{font-weight:600;color:#667eea}.record-count[_ngcontent-%COMP%]{background-color:#e7f3ff;color:#004085;padding:4px 12px;border-radius:4px;font-weight:600}.badge[_ngcontent-%COMP%]{display:inline-block;padding:4px 12px;border-radius:4px;font-size:.85rem;font-weight:600}.badge-info[_ngcontent-%COMP%]{background-color:#d1ecf1;color:#0c5460}.status-badge[_ngcontent-%COMP%]{display:inline-flex;align-items:center;padding:6px 12px;border-radius:4px;font-size:.85rem;font-weight:600;white-space:nowrap}.badge-success[_ngcontent-%COMP%]{background-color:#d4edda;color:#155724}.badge-failed[_ngcontent-%COMP%]{background-color:#f8d7da;color:#721c24}.badge-pending[_ngcontent-%COMP%]{background-color:#fff3cd;color:#856404}.badge-partial[_ngcontent-%COMP%]{background-color:#ffe5d9;color:#783100}.mobile-cards[_ngcontent-%COMP%]{display:none}.pagination-section[_ngcontent-%COMP%]{display:flex;justify-content:space-between;align-items:center;margin-top:25px;padding:20px;background-color:#f8f9fa;border-radius:8px;flex-wrap:wrap;gap:20px}.records-summary[_ngcontent-%COMP%]{color:#6c757d;font-size:.95rem}.pagination-nav[_ngcontent-%COMP%]{display:flex;gap:12px;align-items:center}.page-buttons[_ngcontent-%COMP%]{display:flex;gap:8px;align-items:center}.page-btn[_ngcontent-%COMP%]{width:36px;height:36px;padding:0;border:1px solid #ddd;background-color:#fff;color:#333;border-radius:4px;cursor:pointer;font-weight:500;transition:all .3s ease}.page-btn[_ngcontent-%COMP%]:hover{background-color:#f8f9fa;border-color:#667eea}.page-btn.active[_ngcontent-%COMP%]{background-color:#667eea;color:#fff;border-color:#667eea}.page-ellipsis[_ngcontent-%COMP%]{color:#999}.btn-sm[_ngcontent-%COMP%]{padding:8px 16px;font-size:.85rem}.btn-outline-secondary[_ngcontent-%COMP%]{background-color:#fff;color:#333;border:1px solid #ddd;border-radius:6px;cursor:pointer;transition:all .3s ease}.btn-outline-secondary[_ngcontent-%COMP%]:hover:not(:disabled){background-color:#f8f9fa}.btn-outline-secondary[_ngcontent-%COMP%]:disabled{opacity:.5;cursor:not-allowed}@media (max-width: 768px){.import-history-wrapper[_ngcontent-%COMP%]{padding:20px}.page-header[_ngcontent-%COMP%]{flex-direction:column;align-items:flex-start;gap:15px}.export-btn[_ngcontent-%COMP%]{width:100%}.filters-section[_ngcontent-%COMP%]{flex-direction:column}.filter-group[_ngcontent-%COMP%]{flex-direction:column;width:100%}.filter-item[_ngcontent-%COMP%], .filter-select[_ngcontent-%COMP%]{width:100%}.desktop-table[_ngcontent-%COMP%]{display:none}.mobile-cards[_ngcontent-%COMP%]{display:grid;grid-template-columns:1fr;gap:15px}.history-card[_ngcontent-%COMP%]{background:#fff;border:1px solid #e9ecef;border-radius:8px;overflow:hidden;box-shadow:0 2px 10px #0000000d}.history-card.status-success[_ngcontent-%COMP%]{border-left:4px solid #28a745}.history-card.status-failed[_ngcontent-%COMP%]{border-left:4px solid #dc3545}.history-card.status-pending[_ngcontent-%COMP%]{border-left:4px solid #ffc107}.history-card.status-partial[_ngcontent-%COMP%]{border-left:4px solid #fd7e14}.card-header[_ngcontent-%COMP%]{padding:15px;background-color:#f8f9fa;border-bottom:1px solid #e9ecef;display:flex;justify-content:space-between;align-items:center;gap:10px}.entity-header[_ngcontent-%COMP%]{display:flex;align-items:center;gap:8px;flex:1}.entity-header[_ngcontent-%COMP%]   h4[_ngcontent-%COMP%]{margin:0;font-size:1rem;color:#667eea;font-weight:600}.card-body[_ngcontent-%COMP%]{padding:15px}.card-row[_ngcontent-%COMP%]{display:flex;justify-content:space-between;margin-bottom:12px;font-size:.9rem}.card-row[_ngcontent-%COMP%]   .label[_ngcontent-%COMP%]{font-weight:600;color:#333}.card-row[_ngcontent-%COMP%]   .value[_ngcontent-%COMP%]{color:#6c757d;text-align:right}.card-row[_ngcontent-%COMP%]   .value.error[_ngcontent-%COMP%]{color:#dc3545;font-size:.85rem}.pagination-section[_ngcontent-%COMP%]{flex-direction:column;align-items:stretch;gap:15px}.pagination-nav[_ngcontent-%COMP%]{flex-direction:column;width:100%}.page-buttons[_ngcontent-%COMP%]{width:100%;justify-content:center;flex-wrap:wrap}.btn-sm[_ngcontent-%COMP%]{width:100%}}",`.import-history-wrapper[_ngcontent-%COMP%] {
    padding: 30px;
    max-width: 1200px;
    margin: 0 auto;
  }

  .page-header[_ngcontent-%COMP%] {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 30px;
  }

  .header-content[_ngcontent-%COMP%]   h1[_ngcontent-%COMP%] {
    font-size: 2rem;
    font-weight: 700;
    margin-bottom: 5px;
    color: #333;
  }

  .header-content[_ngcontent-%COMP%]   p[_ngcontent-%COMP%] {
    color: #6c757d;
    margin: 0;
  }

  .export-btn[_ngcontent-%COMP%] {
    background-color: #667eea;
    color: white;
    border: none;
    padding: 10px 20px;
    border-radius: 6px;
    cursor: pointer;
    transition: all 0.3s ease;
  }

  .export-btn[_ngcontent-%COMP%]:hover:not(:disabled) {
    background-color: #5568d3;
    transform: translateY(-2px);
  }

  .export-btn[_ngcontent-%COMP%]:disabled {
    opacity: 0.5;
    cursor: not-allowed;
  }

  .filters-section[_ngcontent-%COMP%] {
    display: flex;
    gap: 15px;
    margin-bottom: 25px;
    flex-wrap: wrap;
  }

  .search-box[_ngcontent-%COMP%] {
    position: relative;
    flex: 1;
    min-width: 250px;
  }

  .search-icon[_ngcontent-%COMP%] {
    position: absolute;
    left: 12px;
    top: 50%;
    transform: translateY(-50%);
    color: #999;
  }

  .search-input[_ngcontent-%COMP%] {
    width: 100%;
    padding: 10px 12px 10px 40px;
    border: 1px solid #ddd;
    border-radius: 6px;
    font-size: 0.95rem;
  }

  .search-input[_ngcontent-%COMP%]:focus {
    outline: none;
    border-color: #667eea;
    box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
  }

  .filter-group[_ngcontent-%COMP%] {
    display: flex;
    gap: 12px;
    flex-wrap: wrap;
    align-items: center;
  }

  .filter-item[_ngcontent-%COMP%] {
    display: flex;
    gap: 8px;
    align-items: center;
  }

  .filter-item[_ngcontent-%COMP%]   label[_ngcontent-%COMP%] {
    font-weight: 500;
    color: #333;
    white-space: nowrap;
  }

  .filter-select[_ngcontent-%COMP%] {
    padding: 8px 12px;
    border: 1px solid #ddd;
    border-radius: 6px;
    font-size: 0.95rem;
    background-color: white;
    cursor: pointer;
  }

  .filter-select[_ngcontent-%COMP%]:focus {
    outline: none;
    border-color: #667eea;
    box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
  }

  .clear-btn[_ngcontent-%COMP%] {
    background-color: white;
    color: #333;
    border: 1px solid #ddd;
    padding: 8px 16px;
    border-radius: 6px;
    cursor: pointer;
    transition: all 0.3s ease;
  }

  .clear-btn[_ngcontent-%COMP%]:hover {
    background-color: #f8f9fa;
  }

  .alert[_ngcontent-%COMP%] {
    padding: 15px;
    border-radius: 6px;
    margin-bottom: 25px;
  }

  .alert-danger[_ngcontent-%COMP%] {
    background-color: #f8d7da;
    color: #721c24;
    border: 1px solid #f5c6cb;
  }

  .loading-state[_ngcontent-%COMP%] {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding: 60px 20px;
  }

  .spinner[_ngcontent-%COMP%] {
    width: 40px;
    height: 40px;
    border: 4px solid #f3f3f3;
    border-top: 4px solid #667eea;
    border-radius: 50%;
    animation: _ngcontent-%COMP%_spin 1s linear infinite;
  }

  @keyframes _ngcontent-%COMP%_spin {
    0% { transform: rotate(0deg); }
    100% { transform: rotate(360deg); }
  }

  .empty-state[_ngcontent-%COMP%] {
    text-align: center;
    padding: 60px 20px;
    color: #6c757d;
  }

  .empty-state[_ngcontent-%COMP%]   i[_ngcontent-%COMP%] {
    font-size: 3rem;
    color: #ddd;
    margin-bottom: 20px;
  }

  .empty-state[_ngcontent-%COMP%]   h3[_ngcontent-%COMP%] {
    font-size: 1.3rem;
    font-weight: 600;
    margin-bottom: 10px;
  }

  .history-container[_ngcontent-%COMP%] {
    background: white;
    border-radius: 8px;
    box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
    overflow-x: auto;
    margin-bottom: 25px;
  }

  .desktop-table[_ngcontent-%COMP%] {
    width: 100%;
    border-collapse: collapse;
  }

  .desktop-table[_ngcontent-%COMP%]   thead[_ngcontent-%COMP%] {
    background-color: #f8f9fa;
    border-bottom: 2px solid #ddd;
  }

  .desktop-table[_ngcontent-%COMP%]   th[_ngcontent-%COMP%] {
    padding: 15px;
    text-align: left;
    font-weight: 600;
    color: #333;
    font-size: 0.9rem;
    text-transform: uppercase;
    letter-spacing: 0.5px;
  }

  .desktop-table[_ngcontent-%COMP%]   tbody[_ngcontent-%COMP%]   tr[_ngcontent-%COMP%] {
    border-bottom: 1px solid #e9ecef;
    transition: background-color 0.2s ease;
  }

  .desktop-table[_ngcontent-%COMP%]   tbody[_ngcontent-%COMP%]   tr[_ngcontent-%COMP%]:hover {
    background-color: #f8f9fa;
  }

  .history-row[_ngcontent-%COMP%]   td[_ngcontent-%COMP%] {
    padding: 15px;
    color: #333;
    font-size: 0.95rem;
  }

  .history-row.status-success[_ngcontent-%COMP%] {
    border-left: 4px solid #28a745;
  }

  .history-row.status-failed[_ngcontent-%COMP%] {
    border-left: 4px solid #dc3545;
  }

  .history-row.status-pending[_ngcontent-%COMP%] {
    border-left: 4px solid #ffc107;
  }

  .history-row.status-partial[_ngcontent-%COMP%] {
    border-left: 4px solid #fd7e14;
  }

  .entity-name[_ngcontent-%COMP%] {
    font-weight: 600;
    color: #667eea;
  }

  .record-count[_ngcontent-%COMP%] {
    background-color: #e7f3ff;
    color: #004085;
    padding: 4px 12px;
    border-radius: 4px;
    font-weight: 600;
  }

  .badge[_ngcontent-%COMP%] {
    display: inline-block;
    padding: 4px 12px;
    border-radius: 4px;
    font-size: 0.85rem;
    font-weight: 600;
  }

  .badge-info[_ngcontent-%COMP%] {
    background-color: #d1ecf1;
    color: #0c5460;
  }

  .status-badge[_ngcontent-%COMP%] {
    display: inline-flex;
    align-items: center;
    padding: 6px 12px;
    border-radius: 4px;
    font-size: 0.85rem;
    font-weight: 600;
    white-space: nowrap;
  }

  .badge-success[_ngcontent-%COMP%] {
    background-color: #d4edda;
    color: #155724;
  }

  .badge-failed[_ngcontent-%COMP%] {
    background-color: #f8d7da;
    color: #721c24;
  }

  .badge-pending[_ngcontent-%COMP%] {
    background-color: #fff3cd;
    color: #856404;
  }

  .badge-partial[_ngcontent-%COMP%] {
    background-color: #ffe5d9;
    color: #783100;
  }

  .mobile-cards[_ngcontent-%COMP%] {
    display: none;
  }

  .pagination-section[_ngcontent-%COMP%] {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-top: 25px;
    padding: 20px;
    background-color: #f8f9fa;
    border-radius: 8px;
    flex-wrap: wrap;
    gap: 20px;
  }

  .records-summary[_ngcontent-%COMP%] {
    color: #6c757d;
    font-size: 0.95rem;
  }

  .pagination-nav[_ngcontent-%COMP%] {
    display: flex;
    gap: 12px;
    align-items: center;
  }

  .page-buttons[_ngcontent-%COMP%] {
    display: flex;
    gap: 8px;
    align-items: center;
  }

  .page-btn[_ngcontent-%COMP%] {
    width: 36px;
    height: 36px;
    padding: 0;
    border: 1px solid #ddd;
    background-color: white;
    color: #333;
    border-radius: 4px;
    cursor: pointer;
    font-weight: 500;
    transition: all 0.3s ease;
  }

  .page-btn[_ngcontent-%COMP%]:hover {
    background-color: #f8f9fa;
    border-color: #667eea;
  }

  .page-btn.active[_ngcontent-%COMP%] {
    background-color: #667eea;
    color: white;
    border-color: #667eea;
  }

  .page-ellipsis[_ngcontent-%COMP%] {
    color: #999;
  }

  .btn-sm[_ngcontent-%COMP%] {
    padding: 8px 16px;
    font-size: 0.85rem;
  }

  .btn-outline-secondary[_ngcontent-%COMP%] {
    background-color: white;
    color: #333;
    border: 1px solid #ddd;
    border-radius: 6px;
    cursor: pointer;
    transition: all 0.3s ease;
  }

  .btn-outline-secondary[_ngcontent-%COMP%]:hover:not(:disabled) {
    background-color: #f8f9fa;
  }

  .btn-outline-secondary[_ngcontent-%COMP%]:disabled {
    opacity: 0.5;
    cursor: not-allowed;
  }

  @media (max-width: 768px) {
    .import-history-wrapper[_ngcontent-%COMP%] {
      padding: 20px;
    }

    .page-header[_ngcontent-%COMP%] {
      flex-direction: column;
      align-items: flex-start;
      gap: 15px;
    }

    .export-btn[_ngcontent-%COMP%] {
      width: 100%;
    }

    .filters-section[_ngcontent-%COMP%] {
      flex-direction: column;
    }

    .filter-group[_ngcontent-%COMP%] {
      flex-direction: column;
      width: 100%;
    }

    .filter-item[_ngcontent-%COMP%] {
      width: 100%;
    }

    .filter-select[_ngcontent-%COMP%] {
      width: 100%;
    }

    .desktop-table[_ngcontent-%COMP%] {
      display: none;
    }

    .mobile-cards[_ngcontent-%COMP%] {
      display: grid;
      grid-template-columns: 1fr;
      gap: 15px;
    }

    .history-card[_ngcontent-%COMP%] {
      background: white;
      border: 1px solid #e9ecef;
      border-radius: 8px;
      overflow: hidden;
      box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
    }

    .history-card.status-success[_ngcontent-%COMP%] {
      border-left: 4px solid #28a745;
    }

    .history-card.status-failed[_ngcontent-%COMP%] {
      border-left: 4px solid #dc3545;
    }

    .history-card.status-pending[_ngcontent-%COMP%] {
      border-left: 4px solid #ffc107;
    }

    .history-card.status-partial[_ngcontent-%COMP%] {
      border-left: 4px solid #fd7e14;
    }

    .card-header[_ngcontent-%COMP%] {
      padding: 15px;
      background-color: #f8f9fa;
      border-bottom: 1px solid #e9ecef;
      display: flex;
      justify-content: space-between;
      align-items: center;
      gap: 10px;
    }

    .entity-header[_ngcontent-%COMP%] {
      display: flex;
      align-items: center;
      gap: 8px;
      flex: 1;
    }

    .entity-header[_ngcontent-%COMP%]   h4[_ngcontent-%COMP%] {
      margin: 0;
      font-size: 1rem;
      color: #667eea;
      font-weight: 600;
    }

    .card-body[_ngcontent-%COMP%] {
      padding: 15px;
    }

    .card-row[_ngcontent-%COMP%] {
      display: flex;
      justify-content: space-between;
      margin-bottom: 12px;
      font-size: 0.9rem;
    }

    .card-row[_ngcontent-%COMP%]   .label[_ngcontent-%COMP%] {
      font-weight: 600;
      color: #333;
    }

    .card-row[_ngcontent-%COMP%]   .value[_ngcontent-%COMP%] {
      color: #6c757d;
      text-align: right;
    }

    .card-row[_ngcontent-%COMP%]   .value.error[_ngcontent-%COMP%] {
      color: #dc3545;
      font-size: 0.85rem;
    }

    .pagination-section[_ngcontent-%COMP%] {
      flex-direction: column;
      align-items: stretch;
      gap: 15px;
    }

    .pagination-nav[_ngcontent-%COMP%] {
      flex-direction: column;
      width: 100%;
    }

    .page-buttons[_ngcontent-%COMP%] {
      width: 100%;
      justify-content: center;
      flex-wrap: wrap;
    }

    .btn-sm[_ngcontent-%COMP%] {
      width: 100%;
    }
  }`]})}}return a})();export{ft as ImportHistoryComponent};
