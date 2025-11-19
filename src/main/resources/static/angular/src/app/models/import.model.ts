/**
 * Import System Models
 * Defines data structures for the import wizard workflow
 */

export interface ImportFile {
  file: File;
  name: string;
  size: number;
  uploadedAt: Date;
  format: 'xlsx' | 'xls' | 'csv' | 'vcf';
}

export interface ImportSettings {
  importType: 'personal' | 'organization';
  enableManualApproval: boolean;
  duplicateAction: 'skip' | 'overwrite' | 'clone';
  skipEmptyFields: boolean;
  findDuplicatesBy: string; // 'Email', 'Phone', 'Name', etc.
}

export interface ImportColumn {
  index: number;
  name: string;
  value: string;
  isMapped: boolean;
}

export interface FieldMapping {
  sourceColumn: string;
  sourceIndex: number;
  targetField: string;
  targetFieldLabel: string;
  isRequired: boolean;
  dataType: string;
}

export interface ImportSession {
  id: string;
  fileName: string;
  fileFormat: string;
  totalRecords: number;
  uploadedAt: Date;
  importType: 'personal' | 'organization';
  enableManualApproval: boolean;
  duplicateAction: 'skip' | 'overwrite' | 'clone';
  skipEmptyFields: boolean;
  findDuplicatesBy: string;
  fieldMappings: FieldMapping[];
  unmappedColumns: string[];
  importedRecords?: ImportResult[];
  status: 'in-progress' | 'completed' | 'failed';
  statistics?: {
    totalRecords: number;
    addedRecords: number;
    updatedRecords: number;
    skippedRecords: number;
    failedRecords: number;
  };
}

export interface ImportResult {
  rowNumber: number;
  recordId?: string | number;
  status: 'added' | 'updated' | 'skipped' | 'failed';
  data: any;
  errors?: string[];
}

export interface ImportPreview {
  totalRows: number;
  headerRow: string[];
  sampleRows: any[];
  detectedFormat: string;
}

export interface FieldMappingTemplate {
  entityType: string;
  fields: {
    key: string;
    label: string;
    required: boolean;
    dataType: string;
    suggestions?: string[]; // Common column name variations
  }[];
}

export interface ImportStatistics {
  totalRecords: number;
  addedRecords: number;
  updatedRecords: number;
  skippedRecords: number;
  failedRecords: number;
  duplicateRecords: number;
  successRate: number;
}
