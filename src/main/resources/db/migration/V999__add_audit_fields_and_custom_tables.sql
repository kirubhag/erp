-- Migration V999: add audit fields and custom field tables (MySQL-compatible)
-- This script replaces previous "ADD COLUMN IF NOT EXISTS" usage with information_schema guarded ALTERs

-- Helper pattern (repeated below):
-- 1) check information_schema for column existence
-- 2) construct ALTER TABLE statement only when column does not exist
-- 3) PREPARE/EXECUTE/DEALLOCATE the statement

-- Add audit columns to students table
SET @exists := (SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA=DATABASE() AND TABLE_NAME='students' AND COLUMN_NAME='modified_by');
SET @sql := IF(@exists=0, 'ALTER TABLE `students` ADD COLUMN `modified_by` VARCHAR(255);', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @exists := (SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA=DATABASE() AND TABLE_NAME='students' AND COLUMN_NAME='created_time');
SET @sql := IF(@exists=0, 'ALTER TABLE `students` ADD COLUMN `created_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP;', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @exists := (SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA=DATABASE() AND TABLE_NAME='students' AND COLUMN_NAME='modified_time');
SET @sql := IF(@exists=0, 'ALTER TABLE `students` ADD COLUMN `modified_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @exists := (SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA=DATABASE() AND TABLE_NAME='students' AND COLUMN_NAME='owner_id');
SET @sql := IF(@exists=0, 'ALTER TABLE `students` ADD COLUMN `owner_id` BIGINT;', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- Add audit columns to parents table
SET @exists := (SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA=DATABASE() AND TABLE_NAME='parents' AND COLUMN_NAME='modified_by');
SET @sql := IF(@exists=0, 'ALTER TABLE `parents` ADD COLUMN `modified_by` VARCHAR(255);', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @exists := (SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA=DATABASE() AND TABLE_NAME='parents' AND COLUMN_NAME='created_time');
SET @sql := IF(@exists=0, 'ALTER TABLE `parents` ADD COLUMN `created_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP;', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @exists := (SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA=DATABASE() AND TABLE_NAME='parents' AND COLUMN_NAME='modified_time');
SET @sql := IF(@exists=0, 'ALTER TABLE `parents` ADD COLUMN `modified_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @exists := (SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA=DATABASE() AND TABLE_NAME='parents' AND COLUMN_NAME='owner_id');
SET @sql := IF(@exists=0, 'ALTER TABLE `parents` ADD COLUMN `owner_id` BIGINT;', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- Add audit columns to staff table
SET @exists := (SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA=DATABASE() AND TABLE_NAME='staff' AND COLUMN_NAME='modified_by');
SET @sql := IF(@exists=0, 'ALTER TABLE `staff` ADD COLUMN `modified_by` VARCHAR(255);', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @exists := (SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA=DATABASE() AND TABLE_NAME='staff' AND COLUMN_NAME='created_time');
SET @sql := IF(@exists=0, 'ALTER TABLE `staff` ADD COLUMN `created_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP;', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @exists := (SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA=DATABASE() AND TABLE_NAME='staff' AND COLUMN_NAME='modified_time');
SET @sql := IF(@exists=0, 'ALTER TABLE `staff` ADD COLUMN `modified_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @exists := (SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA=DATABASE() AND TABLE_NAME='staff' AND COLUMN_NAME='owner_id');
SET @sql := IF(@exists=0, 'ALTER TABLE `staff` ADD COLUMN `owner_id` BIGINT;', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- Add audit columns to health_records table
SET @exists := (SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA=DATABASE() AND TABLE_NAME='health_records' AND COLUMN_NAME='modified_by');
SET @sql := IF(@exists=0, 'ALTER TABLE `health_records` ADD COLUMN `modified_by` VARCHAR(255);', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @exists := (SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA=DATABASE() AND TABLE_NAME='health_records' AND COLUMN_NAME='created_time');
SET @sql := IF(@exists=0, 'ALTER TABLE `health_records` ADD COLUMN `created_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP;', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @exists := (SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA=DATABASE() AND TABLE_NAME='health_records' AND COLUMN_NAME='modified_time');
SET @sql := IF(@exists=0, 'ALTER TABLE `health_records` ADD COLUMN `modified_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @exists := (SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA=DATABASE() AND TABLE_NAME='health_records' AND COLUMN_NAME='owner_id');
SET @sql := IF(@exists=0, 'ALTER TABLE `health_records` ADD COLUMN `owner_id` BIGINT;', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- Add audit columns to attendance table
SET @exists := (SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA=DATABASE() AND TABLE_NAME='attendance' AND COLUMN_NAME='modified_by');
SET @sql := IF(@exists=0, 'ALTER TABLE `attendance` ADD COLUMN `modified_by` VARCHAR(255);', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @exists := (SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA=DATABASE() AND TABLE_NAME='attendance' AND COLUMN_NAME='created_time');
SET @sql := IF(@exists=0, 'ALTER TABLE `attendance` ADD COLUMN `created_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP;', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @exists := (SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA=DATABASE() AND TABLE_NAME='attendance' AND COLUMN_NAME='modified_time');
SET @sql := IF(@exists=0, 'ALTER TABLE `attendance` ADD COLUMN `modified_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @exists := (SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA=DATABASE() AND TABLE_NAME='attendance' AND COLUMN_NAME='owner_id');
SET @sql := IF(@exists=0, 'ALTER TABLE `attendance` ADD COLUMN `owner_id` BIGINT;', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- Add audit columns to organizations table
SET @exists := (SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA=DATABASE() AND TABLE_NAME='organizations' AND COLUMN_NAME='modified_by');
SET @sql := IF(@exists=0, 'ALTER TABLE `organizations` ADD COLUMN `modified_by` VARCHAR(255);', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @exists := (SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA=DATABASE() AND TABLE_NAME='organizations' AND COLUMN_NAME='created_time');
SET @sql := IF(@exists=0, 'ALTER TABLE `organizations` ADD COLUMN `created_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP;', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @exists := (SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA=DATABASE() AND TABLE_NAME='organizations' AND COLUMN_NAME='modified_time');
SET @sql := IF(@exists=0, 'ALTER TABLE `organizations` ADD COLUMN `modified_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @exists := (SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA=DATABASE() AND TABLE_NAME='organizations' AND COLUMN_NAME='owner_id');
SET @sql := IF(@exists=0, 'ALTER TABLE `organizations` ADD COLUMN `owner_id` BIGINT;', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- Add audit columns to users table
SET @exists := (SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA=DATABASE() AND TABLE_NAME='users' AND COLUMN_NAME='modified_by');
SET @sql := IF(@exists=0, 'ALTER TABLE `users` ADD COLUMN `modified_by` VARCHAR(255);', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @exists := (SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA=DATABASE() AND TABLE_NAME='users' AND COLUMN_NAME='created_time');
SET @sql := IF(@exists=0, 'ALTER TABLE `users` ADD COLUMN `created_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP;', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @exists := (SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA=DATABASE() AND TABLE_NAME='users' AND COLUMN_NAME='modified_time');
SET @sql := IF(@exists=0, 'ALTER TABLE `users` ADD COLUMN `modified_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @exists := (SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA=DATABASE() AND TABLE_NAME='users' AND COLUMN_NAME='owner_id');
SET @sql := IF(@exists=0, 'ALTER TABLE `users` ADD COLUMN `owner_id` BIGINT;', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- Add audit columns to email_templates table
SET @exists := (SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA=DATABASE() AND TABLE_NAME='email_templates' AND COLUMN_NAME='modified_by');
SET @sql := IF(@exists=0, 'ALTER TABLE `email_templates` ADD COLUMN `modified_by` VARCHAR(255);', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @exists := (SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA=DATABASE() AND TABLE_NAME='email_templates' AND COLUMN_NAME='created_time');
SET @sql := IF(@exists=0, 'ALTER TABLE `email_templates` ADD COLUMN `created_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP;', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @exists := (SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA=DATABASE() AND TABLE_NAME='email_templates' AND COLUMN_NAME='modified_time');
SET @sql := IF(@exists=0, 'ALTER TABLE `email_templates` ADD COLUMN `modified_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @exists := (SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA=DATABASE() AND TABLE_NAME='email_templates' AND COLUMN_NAME='owner_id');
SET @sql := IF(@exists=0, 'ALTER TABLE `email_templates` ADD COLUMN `owner_id` BIGINT;', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- Add audit columns to email_logs table
SET @exists := (SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA=DATABASE() AND TABLE_NAME='email_logs' AND COLUMN_NAME='modified_by');
SET @sql := IF(@exists=0, 'ALTER TABLE `email_logs` ADD COLUMN `modified_by` VARCHAR(255);', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @exists := (SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA=DATABASE() AND TABLE_NAME='email_logs' AND COLUMN_NAME='created_time');
SET @sql := IF(@exists=0, 'ALTER TABLE `email_logs` ADD COLUMN `created_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP;', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @exists := (SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA=DATABASE() AND TABLE_NAME='email_logs' AND COLUMN_NAME='modified_time');
SET @sql := IF(@exists=0, 'ALTER TABLE `email_logs` ADD COLUMN `modified_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @exists := (SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA=DATABASE() AND TABLE_NAME='email_logs' AND COLUMN_NAME='owner_id');
SET @sql := IF(@exists=0, 'ALTER TABLE `email_logs` ADD COLUMN `owner_id` BIGINT;', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- Add audit columns to roles table
SET @exists := (SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA=DATABASE() AND TABLE_NAME='roles' AND COLUMN_NAME='modified_by');
SET @sql := IF(@exists=0, 'ALTER TABLE `roles` ADD COLUMN `modified_by` VARCHAR(255);', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @exists := (SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA=DATABASE() AND TABLE_NAME='roles' AND COLUMN_NAME='created_time');
SET @sql := IF(@exists=0, 'ALTER TABLE `roles` ADD COLUMN `created_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP;', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @exists := (SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA=DATABASE() AND TABLE_NAME='roles' AND COLUMN_NAME='modified_time');
SET @sql := IF(@exists=0, 'ALTER TABLE `roles` ADD COLUMN `modified_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @exists := (SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA=DATABASE() AND TABLE_NAME='roles' AND COLUMN_NAME='owner_id');
SET @sql := IF(@exists=0, 'ALTER TABLE `roles` ADD COLUMN `owner_id` BIGINT;', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- Add audit columns to permissions table
SET @exists := (SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA=DATABASE() AND TABLE_NAME='permissions' AND COLUMN_NAME='modified_by');
SET @sql := IF(@exists=0, 'ALTER TABLE `permissions` ADD COLUMN `modified_by` VARCHAR(255);', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @exists := (SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA=DATABASE() AND TABLE_NAME='permissions' AND COLUMN_NAME='created_time');
SET @sql := IF(@exists=0, 'ALTER TABLE `permissions` ADD COLUMN `created_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP;', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @exists := (SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA=DATABASE() AND TABLE_NAME='permissions' AND COLUMN_NAME='modified_time');
SET @sql := IF(@exists=0, 'ALTER TABLE `permissions` ADD COLUMN `modified_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @exists := (SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA=DATABASE() AND TABLE_NAME='permissions' AND COLUMN_NAME='owner_id');
SET @sql := IF(@exists=0, 'ALTER TABLE `permissions` ADD COLUMN `owner_id` BIGINT;', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- Create custom field tables (CREATE TABLE IF NOT EXISTS is safe and portable)
-- Create custom field tables (CREATE TABLE IF NOT EXISTS is safe and portable)
-- For each entity we create an explicit set of 250 custom_field_N columns (text) as requested.
-- Note: large rows may have storage implications; verify with your DB administrator before applying to very large datasets.

DELIMITER $$
CREATE TABLE IF NOT EXISTS students_custom_field (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    entity_id BIGINT NOT NULL,
    custom_field_1 TEXT(2000), custom_field_2 TEXT(2000), custom_field_3 TEXT(2000), custom_field_4 TEXT(2000), custom_field_5 TEXT(2000),
    custom_field_6 TEXT(2000), custom_field_7 TEXT(2000), custom_field_8 TEXT(2000), custom_field_9 TEXT(2000), custom_field_10 TEXT(2000),
    custom_field_11 TEXT(2000), custom_field_12 TEXT(2000), custom_field_13 TEXT(2000), custom_field_14 TEXT(2000), custom_field_15 TEXT(2000),
    custom_field_16 TEXT(2000), custom_field_17 TEXT(2000), custom_field_18 TEXT(2000), custom_field_19 TEXT(2000), custom_field_20 TEXT(2000),
    custom_field_21 TEXT(2000), custom_field_22 TEXT(2000), custom_field_23 TEXT(2000), custom_field_24 TEXT(2000), custom_field_25 TEXT(2000),
    custom_field_26 TEXT(2000), custom_field_27 TEXT(2000), custom_field_28 TEXT(2000), custom_field_29 TEXT(2000), custom_field_30 TEXT(2000),
    custom_field_31 TEXT(2000), custom_field_32 TEXT(2000), custom_field_33 TEXT(2000), custom_field_34 TEXT(2000), custom_field_35 TEXT(2000),
    custom_field_36 TEXT(2000), custom_field_37 TEXT(2000), custom_field_38 TEXT(2000), custom_field_39 TEXT(2000), custom_field_40 TEXT(2000),
    custom_field_41 TEXT(2000), custom_field_42 TEXT(2000), custom_field_43 TEXT(2000), custom_field_44 TEXT(2000), custom_field_45 TEXT(2000),
    custom_field_46 TEXT(2000), custom_field_47 TEXT(2000), custom_field_48 TEXT(2000), custom_field_49 TEXT(2000), custom_field_50 TEXT(2000),
    custom_field_51 TEXT(2000), custom_field_52 TEXT(2000), custom_field_53 TEXT(2000), custom_field_54 TEXT(2000), custom_field_55 TEXT(2000),
    custom_field_56 TEXT(2000), custom_field_57 TEXT(2000), custom_field_58 TEXT(2000), custom_field_59 TEXT(2000), custom_field_60 TEXT(2000),
    custom_field_61 TEXT(2000), custom_field_62 TEXT(2000), custom_field_63 TEXT(2000), custom_field_64 TEXT(2000), custom_field_65 TEXT(2000),
    custom_field_66 TEXT(2000), custom_field_67 TEXT(2000), custom_field_68 TEXT(2000), custom_field_69 TEXT(2000), custom_field_70 TEXT(2000),
    custom_field_71 TEXT(2000), custom_field_72 TEXT(2000), custom_field_73 TEXT(2000), custom_field_74 TEXT(2000), custom_field_75 TEXT(2000),
    custom_field_76 TEXT(2000), custom_field_77 TEXT(2000), custom_field_78 TEXT(2000), custom_field_79 TEXT(2000), custom_field_80 TEXT(2000),
    custom_field_81 TEXT(2000), custom_field_82 TEXT(2000), custom_field_83 TEXT(2000), custom_field_84 TEXT(2000), custom_field_85 TEXT(2000),
    custom_field_86 TEXT(2000), custom_field_87 TEXT(2000), custom_field_88 TEXT(2000), custom_field_89 TEXT(2000), custom_field_90 TEXT(2000),
    custom_field_91 TEXT(2000), custom_field_92 TEXT(2000), custom_field_93 TEXT(2000), custom_field_94 TEXT(2000), custom_field_95 TEXT(2000),
    custom_field_96 TEXT(2000), custom_field_97 TEXT(2000), custom_field_98 TEXT(2000), custom_field_99 TEXT(2000), custom_field_100 TEXT(2000),
    custom_field_101 TEXT(2000), custom_field_102 TEXT(2000), custom_field_103 TEXT(2000), custom_field_104 TEXT(2000), custom_field_105 TEXT(2000),
    custom_field_106 TEXT(2000), custom_field_107 TEXT(2000), custom_field_108 TEXT(2000), custom_field_109 TEXT(2000), custom_field_110 TEXT(2000),
    custom_field_111 TEXT(2000), custom_field_112 TEXT(2000), custom_field_113 TEXT(2000), custom_field_114 TEXT(2000), custom_field_115 TEXT(2000),
    custom_field_116 TEXT(2000), custom_field_117 TEXT(2000), custom_field_118 TEXT(2000), custom_field_119 TEXT(2000), custom_field_120 TEXT(2000),
    custom_field_121 TEXT(2000), custom_field_122 TEXT(2000), custom_field_123 TEXT(2000), custom_field_124 TEXT(2000), custom_field_125 TEXT(2000),
    custom_field_126 TEXT(2000), custom_field_127 TEXT(2000), custom_field_128 TEXT(2000), custom_field_129 TEXT(2000), custom_field_130 TEXT(2000),
    custom_field_131 TEXT(2000), custom_field_132 TEXT(2000), custom_field_133 TEXT(2000), custom_field_134 TEXT(2000), custom_field_135 TEXT(2000),
    custom_field_136 TEXT(2000), custom_field_137 TEXT(2000), custom_field_138 TEXT(2000), custom_field_139 TEXT(2000), custom_field_140 TEXT(2000),
    custom_field_141 TEXT(2000), custom_field_142 TEXT(2000), custom_field_143 TEXT(2000), custom_field_144 TEXT(2000), custom_field_145 TEXT(2000),
    custom_field_146 TEXT(2000), custom_field_147 TEXT(2000), custom_field_148 TEXT(2000), custom_field_149 TEXT(2000), custom_field_150 TEXT(2000),
    custom_field_151 TEXT(2000), custom_field_152 TEXT(2000), custom_field_153 TEXT(2000), custom_field_154 TEXT(2000), custom_field_155 TEXT(2000),
    custom_field_156 TEXT(2000), custom_field_157 TEXT(2000), custom_field_158 TEXT(2000), custom_field_159 TEXT(2000), custom_field_160 TEXT(2000),
    custom_field_161 TEXT(2000), custom_field_162 TEXT(2000), custom_field_163 TEXT(2000), custom_field_164 TEXT(2000), custom_field_165 TEXT(2000),
    custom_field_166 TEXT(2000), custom_field_167 TEXT(2000), custom_field_168 TEXT(2000), custom_field_169 TEXT(2000), custom_field_170 TEXT(2000),
    custom_field_171 TEXT(2000), custom_field_172 TEXT(2000), custom_field_173 TEXT(2000), custom_field_174 TEXT(2000), custom_field_175 TEXT(2000),
    custom_field_176 TEXT(2000), custom_field_177 TEXT(2000), custom_field_178 TEXT(2000), custom_field_179 TEXT(2000), custom_field_180 TEXT(2000),
    custom_field_181 TEXT(2000), custom_field_182 TEXT(2000), custom_field_183 TEXT(2000), custom_field_184 TEXT(2000), custom_field_185 TEXT(2000),
    custom_field_186 TEXT(2000), custom_field_187 TEXT(2000), custom_field_188 TEXT(2000), custom_field_189 TEXT(2000), custom_field_190 TEXT(2000),
    custom_field_191 TEXT(2000), custom_field_192 TEXT(2000), custom_field_193 TEXT(2000), custom_field_194 TEXT(2000), custom_field_195 TEXT(2000),
    custom_field_196 TEXT(2000), custom_field_197 TEXT(2000), custom_field_198 TEXT(2000), custom_field_199 TEXT(2000), custom_field_200 TEXT(2000),
    custom_field_201 TEXT(2000), custom_field_202 TEXT(2000), custom_field_203 TEXT(2000), custom_field_204 TEXT(2000), custom_field_205 TEXT(2000),
    custom_field_206 TEXT(2000), custom_field_207 TEXT(2000), custom_field_208 TEXT(2000), custom_field_209 TEXT(2000), custom_field_210 TEXT(2000),
    custom_field_211 TEXT(2000), custom_field_212 TEXT(2000), custom_field_213 TEXT(2000), custom_field_214 TEXT(2000), custom_field_215 TEXT(2000),
    custom_field_216 TEXT(2000), custom_field_217 TEXT(2000), custom_field_218 TEXT(2000), custom_field_219 TEXT(2000), custom_field_220 TEXT(2000),
    custom_field_221 TEXT(2000), custom_field_222 TEXT(2000), custom_field_223 TEXT(2000), custom_field_224 TEXT(2000), custom_field_225 TEXT(2000),
    custom_field_226 TEXT(2000), custom_field_227 TEXT(2000), custom_field_228 TEXT(2000), custom_field_229 TEXT(2000), custom_field_230 TEXT(2000),
    custom_field_231 TEXT(2000), custom_field_232 TEXT(2000), custom_field_233 TEXT(2000), custom_field_234 TEXT(2000), custom_field_235 TEXT(2000),
    custom_field_236 TEXT(2000), custom_field_237 TEXT(2000), custom_field_238 TEXT(2000), custom_field_239 TEXT(2000), custom_field_240 TEXT(2000),
    custom_field_241 TEXT(2000), custom_field_242 TEXT(2000), custom_field_243 TEXT(2000), custom_field_244 TEXT(2000), custom_field_245 TEXT(2000),
    custom_field_246 TEXT(2000), custom_field_247 TEXT(2000), custom_field_248 TEXT(2000), custom_field_249 TEXT(2000), custom_field_250 TEXT(2000)
);
$$

CREATE TABLE IF NOT EXISTS parents_custom_field (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    entity_id BIGINT NOT NULL,
    custom_field_1 TEXT(2000), custom_field_2 TEXT(2000), custom_field_3 TEXT(2000), custom_field_4 TEXT(2000), custom_field_5 TEXT(2000),
    custom_field_6 TEXT(2000), custom_field_7 TEXT(2000), custom_field_8 TEXT(2000), custom_field_9 TEXT(2000), custom_field_10 TEXT(2000),
    custom_field_11 TEXT(2000), custom_field_12 TEXT(2000), custom_field_13 TEXT(2000), custom_field_14 TEXT(2000), custom_field_15 TEXT(2000),
    custom_field_16 TEXT(2000), custom_field_17 TEXT(2000), custom_field_18 TEXT(2000), custom_field_19 TEXT(2000), custom_field_20 TEXT(2000),
    custom_field_21 TEXT(2000), custom_field_22 TEXT(2000), custom_field_23 TEXT(2000), custom_field_24 TEXT(2000), custom_field_25 TEXT(2000),
    custom_field_26 TEXT(2000), custom_field_27 TEXT(2000), custom_field_28 TEXT(2000), custom_field_29 TEXT(2000), custom_field_30 TEXT(2000),
    custom_field_31 TEXT(2000), custom_field_32 TEXT(2000), custom_field_33 TEXT(2000), custom_field_34 TEXT(2000), custom_field_35 TEXT(2000),
    custom_field_36 TEXT(2000), custom_field_37 TEXT(2000), custom_field_38 TEXT(2000), custom_field_39 TEXT(2000), custom_field_40 TEXT(2000),
    custom_field_41 TEXT(2000), custom_field_42 TEXT(2000), custom_field_43 TEXT(2000), custom_field_44 TEXT(2000), custom_field_45 TEXT(2000),
    custom_field_46 TEXT(2000), custom_field_47 TEXT(2000), custom_field_48 TEXT(2000), custom_field_49 TEXT(2000), custom_field_50 TEXT(2000),
    custom_field_51 TEXT(2000), custom_field_52 TEXT(2000), custom_field_53 TEXT(2000), custom_field_54 TEXT(2000), custom_field_55 TEXT(2000),
    custom_field_56 TEXT(2000), custom_field_57 TEXT(2000), custom_field_58 TEXT(2000), custom_field_59 TEXT(2000), custom_field_60 TEXT(2000),
    custom_field_61 TEXT(2000), custom_field_62 TEXT(2000), custom_field_63 TEXT(2000), custom_field_64 TEXT(2000), custom_field_65 TEXT(2000),
    custom_field_66 TEXT(2000), custom_field_67 TEXT(2000), custom_field_68 TEXT(2000), custom_field_69 TEXT(2000), custom_field_70 TEXT(2000),
    custom_field_71 TEXT(2000), custom_field_72 TEXT(2000), custom_field_73 TEXT(2000), custom_field_74 TEXT(2000), custom_field_75 TEXT(2000),
    custom_field_76 TEXT(2000), custom_field_77 TEXT(2000), custom_field_78 TEXT(2000), custom_field_79 TEXT(2000), custom_field_80 TEXT(2000),
    custom_field_81 TEXT(2000), custom_field_82 TEXT(2000), custom_field_83 TEXT(2000), custom_field_84 TEXT(2000), custom_field_85 TEXT(2000),
    custom_field_86 TEXT(2000), custom_field_87 TEXT(2000), custom_field_88 TEXT(2000), custom_field_89 TEXT(2000), custom_field_90 TEXT(2000),
    custom_field_91 TEXT(2000), custom_field_92 TEXT(2000), custom_field_93 TEXT(2000), custom_field_94 TEXT(2000), custom_field_95 TEXT(2000),
    custom_field_96 TEXT(2000), custom_field_97 TEXT(2000), custom_field_98 TEXT(2000), custom_field_99 TEXT(2000), custom_field_100 TEXT(2000),
    custom_field_101 TEXT(2000), custom_field_102 TEXT(2000), custom_field_103 TEXT(2000), custom_field_104 TEXT(2000), custom_field_105 TEXT(2000),
    custom_field_106 TEXT(2000), custom_field_107 TEXT(2000), custom_field_108 TEXT(2000), custom_field_109 TEXT(2000), custom_field_110 TEXT(2000),
    custom_field_111 TEXT(2000), custom_field_112 TEXT(2000), custom_field_113 TEXT(2000), custom_field_114 TEXT(2000), custom_field_115 TEXT(2000),
    custom_field_116 TEXT(2000), custom_field_117 TEXT(2000), custom_field_118 TEXT(2000), custom_field_119 TEXT(2000), custom_field_120 TEXT(2000),
    custom_field_121 TEXT(2000), custom_field_122 TEXT(2000), custom_field_123 TEXT(2000), custom_field_124 TEXT(2000), custom_field_125 TEXT(2000),
    custom_field_126 TEXT(2000), custom_field_127 TEXT(2000), custom_field_128 TEXT(2000), custom_field_129 TEXT(2000), custom_field_130 TEXT(2000),
    custom_field_131 TEXT(2000), custom_field_132 TEXT(2000), custom_field_133 TEXT(2000), custom_field_134 TEXT(2000), custom_field_135 TEXT(2000),
    custom_field_136 TEXT(2000), custom_field_137 TEXT(2000), custom_field_138 TEXT(2000), custom_field_139 TEXT(2000), custom_field_140 TEXT(2000),
    custom_field_141 TEXT(2000), custom_field_142 TEXT(2000), custom_field_143 TEXT(2000), custom_field_144 TEXT(2000), custom_field_145 TEXT(2000),
    custom_field_146 TEXT(2000), custom_field_147 TEXT(2000), custom_field_148 TEXT(2000), custom_field_149 TEXT(2000), custom_field_150 TEXT(2000),
    custom_field_151 TEXT(2000), custom_field_152 TEXT(2000), custom_field_153 TEXT(2000), custom_field_154 TEXT(2000), custom_field_155 TEXT(2000),
    custom_field_156 TEXT(2000), custom_field_157 TEXT(2000), custom_field_158 TEXT(2000), custom_field_159 TEXT(2000), custom_field_160 TEXT(2000),
    custom_field_161 TEXT(2000), custom_field_162 TEXT(2000), custom_field_163 TEXT(2000), custom_field_164 TEXT(2000), custom_field_165 TEXT(2000),
    custom_field_166 TEXT(2000), custom_field_167 TEXT(2000), custom_field_168 TEXT(2000), custom_field_169 TEXT(2000), custom_field_170 TEXT(2000),
    custom_field_171 TEXT(2000), custom_field_172 TEXT(2000), custom_field_173 TEXT(2000), custom_field_174 TEXT(2000), custom_field_175 TEXT(2000),
    custom_field_176 TEXT(2000), custom_field_177 TEXT(2000), custom_field_178 TEXT(2000), custom_field_179 TEXT(2000), custom_field_180 TEXT(2000),
    custom_field_181 TEXT(2000), custom_field_182 TEXT(2000), custom_field_183 TEXT(2000), custom_field_184 TEXT(2000), custom_field_185 TEXT(2000),
    custom_field_186 TEXT(2000), custom_field_187 TEXT(2000), custom_field_188 TEXT(2000), custom_field_189 TEXT(2000), custom_field_190 TEXT(2000),
    custom_field_191 TEXT(2000), custom_field_192 TEXT(2000), custom_field_193 TEXT(2000), custom_field_194 TEXT(2000), custom_field_195 TEXT(2000),
    custom_field_196 TEXT(2000), custom_field_197 TEXT(2000), custom_field_198 TEXT(2000), custom_field_199 TEXT(2000), custom_field_200 TEXT(2000),
    custom_field_201 TEXT(2000), custom_field_202 TEXT(2000), custom_field_203 TEXT(2000), custom_field_204 TEXT(2000), custom_field_205 TEXT(2000),
    custom_field_206 TEXT(2000), custom_field_207 TEXT(2000), custom_field_208 TEXT(2000), custom_field_209 TEXT(2000), custom_field_210 TEXT(2000),
    custom_field_211 TEXT(2000), custom_field_212 TEXT(2000), custom_field_213 TEXT(2000), custom_field_214 TEXT(2000), custom_field_215 TEXT(2000),
    custom_field_216 TEXT(2000), custom_field_217 TEXT(2000), custom_field_218 TEXT(2000), custom_field_219 TEXT(2000), custom_field_220 TEXT(2000),
    custom_field_221 TEXT(2000), custom_field_222 TEXT(2000), custom_field_223 TEXT(2000), custom_field_224 TEXT(2000), custom_field_225 TEXT(2000),
    custom_field_226 TEXT(2000), custom_field_227 TEXT(2000), custom_field_228 TEXT(2000), custom_field_229 TEXT(2000), custom_field_230 TEXT(2000),
    custom_field_231 TEXT(2000), custom_field_232 TEXT(2000), custom_field_233 TEXT(2000), custom_field_234 TEXT(2000), custom_field_235 TEXT(2000),
    custom_field_236 TEXT(2000), custom_field_237 TEXT(2000), custom_field_238 TEXT(2000), custom_field_239 TEXT(2000), custom_field_240 TEXT(2000),
    custom_field_241 TEXT(2000), custom_field_242 TEXT(2000), custom_field_243 TEXT(2000), custom_field_244 TEXT(2000), custom_field_245 TEXT(2000),
    custom_field_246 TEXT(2000), custom_field_247 TEXT(2000), custom_field_248 TEXT(2000), custom_field_249 TEXT(2000), custom_field_250 TEXT(2000)
);

CREATE TABLE IF NOT EXISTS staff_custom_field (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    entity_id BIGINT NOT NULL,
    custom_field_1 TEXT(2000), custom_field_2 TEXT(2000), custom_field_3 TEXT(2000), custom_field_4 TEXT(2000), custom_field_5 TEXT(2000),
    custom_field_6 TEXT(2000), custom_field_7 TEXT(2000), custom_field_8 TEXT(2000), custom_field_9 TEXT(2000), custom_field_10 TEXT(2000),
    custom_field_11 TEXT(2000), custom_field_12 TEXT(2000), custom_field_13 TEXT(2000), custom_field_14 TEXT(2000), custom_field_15 TEXT(2000),
    custom_field_16 TEXT(2000), custom_field_17 TEXT(2000), custom_field_18 TEXT(2000), custom_field_19 TEXT(2000), custom_field_20 TEXT(2000),
    custom_field_21 TEXT(2000), custom_field_22 TEXT(2000), custom_field_23 TEXT(2000), custom_field_24 TEXT(2000), custom_field_25 TEXT(2000),
    custom_field_26 TEXT(2000), custom_field_27 TEXT(2000), custom_field_28 TEXT(2000), custom_field_29 TEXT(2000), custom_field_30 TEXT(2000),
    custom_field_31 TEXT(2000), custom_field_32 TEXT(2000), custom_field_33 TEXT(2000), custom_field_34 TEXT(2000), custom_field_35 TEXT(2000),
    custom_field_36 TEXT(2000), custom_field_37 TEXT(2000), custom_field_38 TEXT(2000), custom_field_39 TEXT(2000), custom_field_40 TEXT(2000),
    custom_field_41 TEXT(2000), custom_field_42 TEXT(2000), custom_field_43 TEXT(2000), custom_field_44 TEXT(2000), custom_field_45 TEXT(2000),
    custom_field_46 TEXT(2000), custom_field_47 TEXT(2000), custom_field_48 TEXT(2000), custom_field_49 TEXT(2000), custom_field_50 TEXT(2000),
    custom_field_51 TEXT(2000), custom_field_52 TEXT(2000), custom_field_53 TEXT(2000), custom_field_54 TEXT(2000), custom_field_55 TEXT(2000),
    custom_field_56 TEXT(2000), custom_field_57 TEXT(2000), custom_field_58 TEXT(2000), custom_field_59 TEXT(2000), custom_field_60 TEXT(2000),
    custom_field_61 TEXT(2000), custom_field_62 TEXT(2000), custom_field_63 TEXT(2000), custom_field_64 TEXT(2000), custom_field_65 TEXT(2000),
    custom_field_66 TEXT(2000), custom_field_67 TEXT(2000), custom_field_68 TEXT(2000), custom_field_69 TEXT(2000), custom_field_70 TEXT(2000),
    custom_field_71 TEXT(2000), custom_field_72 TEXT(2000), custom_field_73 TEXT(2000), custom_field_74 TEXT(2000), custom_field_75 TEXT(2000),
    custom_field_76 TEXT(2000), custom_field_77 TEXT(2000), custom_field_78 TEXT(2000), custom_field_79 TEXT(2000), custom_field_80 TEXT(2000),
    custom_field_81 TEXT(2000), custom_field_82 TEXT(2000), custom_field_83 TEXT(2000), custom_field_84 TEXT(2000), custom_field_85 TEXT(2000),
    custom_field_86 TEXT(2000), custom_field_87 TEXT(2000), custom_field_88 TEXT(2000), custom_field_89 TEXT(2000), custom_field_90 TEXT(2000),
    custom_field_91 TEXT(2000), custom_field_92 TEXT(2000), custom_field_93 TEXT(2000), custom_field_94 TEXT(2000), custom_field_95 TEXT(2000),
    custom_field_96 TEXT(2000), custom_field_97 TEXT(2000), custom_field_98 TEXT(2000), custom_field_99 TEXT(2000), custom_field_100 TEXT(2000),
    custom_field_101 TEXT(2000), custom_field_102 TEXT(2000), custom_field_103 TEXT(2000), custom_field_104 TEXT(2000), custom_field_105 TEXT(2000),
    custom_field_106 TEXT(2000), custom_field_107 TEXT(2000), custom_field_108 TEXT(2000), custom_field_109 TEXT(2000), custom_field_110 TEXT(2000),
    custom_field_111 TEXT(2000), custom_field_112 TEXT(2000), custom_field_113 TEXT(2000), custom_field_114 TEXT(2000), custom_field_115 TEXT(2000),
    custom_field_116 TEXT(2000), custom_field_117 TEXT(2000), custom_field_118 TEXT(2000), custom_field_119 TEXT(2000), custom_field_120 TEXT(2000),
    custom_field_121 TEXT(2000), custom_field_122 TEXT(2000), custom_field_123 TEXT(2000), custom_field_124 TEXT(2000), custom_field_125 TEXT(2000),
    custom_field_126 TEXT(2000), custom_field_127 TEXT(2000), custom_field_128 TEXT(2000), custom_field_129 TEXT(2000), custom_field_130 TEXT(2000),
    custom_field_131 TEXT(2000), custom_field_132 TEXT(2000), custom_field_133 TEXT(2000), custom_field_134 TEXT(2000), custom_field_135 TEXT(2000),
    custom_field_136 TEXT(2000), custom_field_137 TEXT(2000), custom_field_138 TEXT(2000), custom_field_139 TEXT(2000), custom_field_140 TEXT(2000),
    custom_field_141 TEXT(2000), custom_field_142 TEXT(2000), custom_field_143 TEXT(2000), custom_field_144 TEXT(2000), custom_field_145 TEXT(2000),
    custom_field_146 TEXT(2000), custom_field_147 TEXT(2000), custom_field_148 TEXT(2000), custom_field_149 TEXT(2000), custom_field_150 TEXT(2000),
    custom_field_151 TEXT(2000), custom_field_152 TEXT(2000), custom_field_153 TEXT(2000), custom_field_154 TEXT(2000), custom_field_155 TEXT(2000),
    custom_field_156 TEXT(2000), custom_field_157 TEXT(2000), custom_field_158 TEXT(2000), custom_field_159 TEXT(2000), custom_field_160 TEXT(2000),
    custom_field_161 TEXT(2000), custom_field_162 TEXT(2000), custom_field_163 TEXT(2000), custom_field_164 TEXT(2000), custom_field_165 TEXT(2000),
    custom_field_166 TEXT(2000), custom_field_167 TEXT(2000), custom_field_168 TEXT(2000), custom_field_169 TEXT(2000), custom_field_170 TEXT(2000),
    custom_field_171 TEXT(2000), custom_field_172 TEXT(2000), custom_field_173 TEXT(2000), custom_field_174 TEXT(2000), custom_field_175 TEXT(2000),
    custom_field_176 TEXT(2000), custom_field_177 TEXT(2000), custom_field_178 TEXT(2000), custom_field_179 TEXT(2000), custom_field_180 TEXT(2000),
    custom_field_181 TEXT(2000), custom_field_182 TEXT(2000), custom_field_183 TEXT(2000), custom_field_184 TEXT(2000), custom_field_185 TEXT(2000),
    custom_field_186 TEXT(2000), custom_field_187 TEXT(2000), custom_field_188 TEXT(2000), custom_field_189 TEXT(2000), custom_field_190 TEXT(2000),
    custom_field_191 TEXT(2000), custom_field_192 TEXT(2000), custom_field_193 TEXT(2000), custom_field_194 TEXT(2000), custom_field_195 TEXT(2000),
    custom_field_196 TEXT(2000), custom_field_197 TEXT(2000), custom_field_198 TEXT(2000), custom_field_199 TEXT(2000), custom_field_200 TEXT(2000),
    custom_field_201 TEXT(2000), custom_field_202 TEXT(2000), custom_field_203 TEXT(2000), custom_field_204 TEXT(2000), custom_field_205 TEXT(2000),
    custom_field_206 TEXT(2000), custom_field_207 TEXT(2000), custom_field_208 TEXT(2000), custom_field_209 TEXT(2000), custom_field_210 TEXT(2000),
    custom_field_211 TEXT(2000), custom_field_212 TEXT(2000), custom_field_213 TEXT(2000), custom_field_214 TEXT(2000), custom_field_215 TEXT(2000),
    custom_field_216 TEXT(2000), custom_field_217 TEXT(2000), custom_field_218 TEXT(2000), custom_field_219 TEXT(2000), custom_field_220 TEXT(2000),
    custom_field_221 TEXT(2000), custom_field_222 TEXT(2000), custom_field_223 TEXT(2000), custom_field_224 TEXT(2000), custom_field_225 TEXT(2000),
    custom_field_226 TEXT(2000), custom_field_227 TEXT(2000), custom_field_228 TEXT(2000), custom_field_229 TEXT(2000), custom_field_230 TEXT(2000),
    custom_field_231 TEXT(2000), custom_field_232 TEXT(2000), custom_field_233 TEXT(2000), custom_field_234 TEXT(2000), custom_field_235 TEXT(2000),
    custom_field_236 TEXT(2000), custom_field_237 TEXT(2000), custom_field_238 TEXT(2000), custom_field_239 TEXT(2000), custom_field_240 TEXT(2000),
    custom_field_241 TEXT(2000), custom_field_242 TEXT(2000), custom_field_243 TEXT(2000), custom_field_244 TEXT(2000), custom_field_245 TEXT(2000),
    custom_field_246 TEXT(2000), custom_field_247 TEXT(2000), custom_field_248 TEXT(2000), custom_field_249 TEXT(2000), custom_field_250 TEXT(2000)
);

CREATE TABLE IF NOT EXISTS health_records_custom_field (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    entity_id BIGINT NOT NULL,
    custom_field_1 TEXT(2000), custom_field_2 TEXT(2000), custom_field_3 TEXT(2000), custom_field_4 TEXT(2000), custom_field_5 TEXT(2000),
    custom_field_6 TEXT(2000), custom_field_7 TEXT(2000), custom_field_8 TEXT(2000), custom_field_9 TEXT(2000), custom_field_10 TEXT(2000),
    custom_field_11 TEXT(2000), custom_field_12 TEXT(2000), custom_field_13 TEXT(2000), custom_field_14 TEXT(2000), custom_field_15 TEXT(2000),
    custom_field_16 TEXT(2000), custom_field_17 TEXT(2000), custom_field_18 TEXT(2000), custom_field_19 TEXT(2000), custom_field_20 TEXT(2000),
    custom_field_21 TEXT(2000), custom_field_22 TEXT(2000), custom_field_23 TEXT(2000), custom_field_24 TEXT(2000), custom_field_25 TEXT(2000),
    custom_field_26 TEXT(2000), custom_field_27 TEXT(2000), custom_field_28 TEXT(2000), custom_field_29 TEXT(2000), custom_field_30 TEXT(2000),
    custom_field_31 TEXT(2000), custom_field_32 TEXT(2000), custom_field_33 TEXT(2000), custom_field_34 TEXT(2000), custom_field_35 TEXT(2000),
    custom_field_36 TEXT(2000), custom_field_37 TEXT(2000), custom_field_38 TEXT(2000), custom_field_39 TEXT(2000), custom_field_40 TEXT(2000),
    custom_field_41 TEXT(2000), custom_field_42 TEXT(2000), custom_field_43 TEXT(2000), custom_field_44 TEXT(2000), custom_field_45 TEXT(2000),
    custom_field_46 TEXT(2000), custom_field_47 TEXT(2000), custom_field_48 TEXT(2000), custom_field_49 TEXT(2000), custom_field_50 TEXT(2000),
    custom_field_51 TEXT(2000), custom_field_52 TEXT(2000), custom_field_53 TEXT(2000), custom_field_54 TEXT(2000), custom_field_55 TEXT(2000),
    custom_field_56 TEXT(2000), custom_field_57 TEXT(2000), custom_field_58 TEXT(2000), custom_field_59 TEXT(2000), custom_field_60 TEXT(2000),
    custom_field_61 TEXT(2000), custom_field_62 TEXT(2000), custom_field_63 TEXT(2000), custom_field_64 TEXT(2000), custom_field_65 TEXT(2000),
    custom_field_66 TEXT(2000), custom_field_67 TEXT(2000), custom_field_68 TEXT(2000), custom_field_69 TEXT(2000), custom_field_70 TEXT(2000),
    custom_field_71 TEXT(2000), custom_field_72 TEXT(2000), custom_field_73 TEXT(2000), custom_field_74 TEXT(2000), custom_field_75 TEXT(2000),
    custom_field_76 TEXT(2000), custom_field_77 TEXT(2000), custom_field_78 TEXT(2000), custom_field_79 TEXT(2000), custom_field_80 TEXT(2000),
    custom_field_81 TEXT(2000), custom_field_82 TEXT(2000), custom_field_83 TEXT(2000), custom_field_84 TEXT(2000), custom_field_85 TEXT(2000),
    custom_field_86 TEXT(2000), custom_field_87 TEXT(2000), custom_field_88 TEXT(2000), custom_field_89 TEXT(2000), custom_field_90 TEXT(2000),
    custom_field_91 TEXT(2000), custom_field_92 TEXT(2000), custom_field_93 TEXT(2000), custom_field_94 TEXT(2000), custom_field_95 TEXT(2000),
    custom_field_96 TEXT(2000), custom_field_97 TEXT(2000), custom_field_98 TEXT(2000), custom_field_99 TEXT(2000), custom_field_100 TEXT(2000),
    custom_field_101 TEXT(2000), custom_field_102 TEXT(2000), custom_field_103 TEXT(2000), custom_field_104 TEXT(2000), custom_field_105 TEXT(2000),
    custom_field_106 TEXT(2000), custom_field_107 TEXT(2000), custom_field_108 TEXT(2000), custom_field_109 TEXT(2000), custom_field_110 TEXT(2000),
    custom_field_111 TEXT(2000), custom_field_112 TEXT(2000), custom_field_113 TEXT(2000), custom_field_114 TEXT(2000), custom_field_115 TEXT(2000),
    custom_field_116 TEXT(2000), custom_field_117 TEXT(2000), custom_field_118 TEXT(2000), custom_field_119 TEXT(2000), custom_field_120 TEXT(2000),
    custom_field_121 TEXT(2000), custom_field_122 TEXT(2000), custom_field_123 TEXT(2000), custom_field_124 TEXT(2000), custom_field_125 TEXT(2000),
    custom_field_126 TEXT(2000), custom_field_127 TEXT(2000), custom_field_128 TEXT(2000), custom_field_129 TEXT(2000), custom_field_130 TEXT(2000),
    custom_field_131 TEXT(2000), custom_field_132 TEXT(2000), custom_field_133 TEXT(2000), custom_field_134 TEXT(2000), custom_field_135 TEXT(2000),
    custom_field_136 TEXT(2000), custom_field_137 TEXT(2000), custom_field_138 TEXT(2000), custom_field_139 TEXT(2000), custom_field_140 TEXT(2000),
    custom_field_141 TEXT(2000), custom_field_142 TEXT(2000), custom_field_143 TEXT(2000), custom_field_144 TEXT(2000), custom_field_145 TEXT(2000),
    custom_field_146 TEXT(2000), custom_field_147 TEXT(2000), custom_field_148 TEXT(2000), custom_field_149 TEXT(2000), custom_field_150 TEXT(2000),
    custom_field_151 TEXT(2000), custom_field_152 TEXT(2000), custom_field_153 TEXT(2000), custom_field_154 TEXT(2000), custom_field_155 TEXT(2000),
    custom_field_156 TEXT(2000), custom_field_157 TEXT(2000), custom_field_158 TEXT(2000), custom_field_159 TEXT(2000), custom_field_160 TEXT(2000),
    custom_field_161 TEXT(2000), custom_field_162 TEXT(2000), custom_field_163 TEXT(2000), custom_field_164 TEXT(2000), custom_field_165 TEXT(2000),
    custom_field_166 TEXT(2000), custom_field_167 TEXT(2000), custom_field_168 TEXT(2000), custom_field_169 TEXT(2000), custom_field_170 TEXT(2000),
    custom_field_171 TEXT(2000), custom_field_172 TEXT(2000), custom_field_173 TEXT(2000), custom_field_174 TEXT(2000), custom_field_175 TEXT(2000),
    custom_field_176 TEXT(2000), custom_field_177 TEXT(2000), custom_field_178 TEXT(2000), custom_field_179 TEXT(2000), custom_field_180 TEXT(2000),
    custom_field_181 TEXT(2000), custom_field_182 TEXT(2000), custom_field_183 TEXT(2000), custom_field_184 TEXT(2000), custom_field_185 TEXT(2000),
    custom_field_186 TEXT(2000), custom_field_187 TEXT(2000), custom_field_188 TEXT(2000), custom_field_189 TEXT(2000), custom_field_190 TEXT(2000),
    custom_field_191 TEXT(2000), custom_field_192 TEXT(2000), custom_field_193 TEXT(2000), custom_field_194 TEXT(2000), custom_field_195 TEXT(2000),
    custom_field_196 TEXT(2000), custom_field_197 TEXT(2000), custom_field_198 TEXT(2000), custom_field_199 TEXT(2000), custom_field_200 TEXT(2000),
    custom_field_201 TEXT(2000), custom_field_202 TEXT(2000), custom_field_203 TEXT(2000), custom_field_204 TEXT(2000), custom_field_205 TEXT(2000),
    custom_field_206 TEXT(2000), custom_field_207 TEXT(2000), custom_field_208 TEXT(2000), custom_field_209 TEXT(2000), custom_field_210 TEXT(2000),
    custom_field_211 TEXT(2000), custom_field_212 TEXT(2000), custom_field_213 TEXT(2000), custom_field_214 TEXT(2000), custom_field_215 TEXT(2000),
    custom_field_216 TEXT(2000), custom_field_217 TEXT(2000), custom_field_218 TEXT(2000), custom_field_219 TEXT(2000), custom_field_220 TEXT(2000),
    custom_field_221 TEXT(2000), custom_field_222 TEXT(2000), custom_field_223 TEXT(2000), custom_field_224 TEXT(2000), custom_field_225 TEXT(2000),
    custom_field_226 TEXT(2000), custom_field_227 TEXT(2000), custom_field_228 TEXT(2000), custom_field_229 TEXT(2000), custom_field_230 TEXT(2000),
    custom_field_231 TEXT(2000), custom_field_232 TEXT(2000), custom_field_233 TEXT(2000), custom_field_234 TEXT(2000), custom_field_235 TEXT(2000),
    custom_field_236 TEXT(2000), custom_field_237 TEXT(2000), custom_field_238 TEXT(2000), custom_field_239 TEXT(2000), custom_field_240 TEXT(2000),
    custom_field_241 TEXT(2000), custom_field_242 TEXT(2000), custom_field_243 TEXT(2000), custom_field_244 TEXT(2000), custom_field_245 TEXT(2000),
    custom_field_246 TEXT(2000), custom_field_247 TEXT(2000), custom_field_248 TEXT(2000), custom_field_249 TEXT(2000), custom_field_250 TEXT(2000),
    FOREIGN KEY (entity_id) REFERENCES health_records(id) ON DELETE CASCADE,
    UNIQUE KEY unique_health_records_custom (entity_id)
);

CREATE TABLE IF NOT EXISTS attendance_custom_field (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    entity_id BIGINT NOT NULL,
    custom_field_1 TEXT(2000), custom_field_2 TEXT(2000), custom_field_3 TEXT(2000), custom_field_4 TEXT(2000), custom_field_5 TEXT(2000),
    custom_field_6 TEXT(2000), custom_field_7 TEXT(2000), custom_field_8 TEXT(2000), custom_field_9 TEXT(2000), custom_field_10 TEXT(2000),
    custom_field_11 TEXT(2000), custom_field_12 TEXT(2000), custom_field_13 TEXT(2000), custom_field_14 TEXT(2000), custom_field_15 TEXT(2000),
    custom_field_16 TEXT(2000), custom_field_17 TEXT(2000), custom_field_18 TEXT(2000), custom_field_19 TEXT(2000), custom_field_20 TEXT(2000),
    custom_field_21 TEXT(2000), custom_field_22 TEXT(2000), custom_field_23 TEXT(2000), custom_field_24 TEXT(2000), custom_field_25 TEXT(2000),
    custom_field_26 TEXT(2000), custom_field_27 TEXT(2000), custom_field_28 TEXT(2000), custom_field_29 TEXT(2000), custom_field_30 TEXT(2000),
    custom_field_31 TEXT(2000), custom_field_32 TEXT(2000), custom_field_33 TEXT(2000), custom_field_34 TEXT(2000), custom_field_35 TEXT(2000),
    custom_field_36 TEXT(2000), custom_field_37 TEXT(2000), custom_field_38 TEXT(2000), custom_field_39 TEXT(2000), custom_field_40 TEXT(2000),
    custom_field_41 TEXT(2000), custom_field_42 TEXT(2000), custom_field_43 TEXT(2000), custom_field_44 TEXT(2000), custom_field_45 TEXT(2000),
    custom_field_46 TEXT(2000), custom_field_47 TEXT(2000), custom_field_48 TEXT(2000), custom_field_49 TEXT(2000), custom_field_50 TEXT(2000),
    custom_field_51 TEXT(2000), custom_field_52 TEXT(2000), custom_field_53 TEXT(2000), custom_field_54 TEXT(2000), custom_field_55 TEXT(2000),
    custom_field_56 TEXT(2000), custom_field_57 TEXT(2000), custom_field_58 TEXT(2000), custom_field_59 TEXT(2000), custom_field_60 TEXT(2000),
    custom_field_61 TEXT(2000), custom_field_62 TEXT(2000), custom_field_63 TEXT(2000), custom_field_64 TEXT(2000), custom_field_65 TEXT(2000),
    custom_field_66 TEXT(2000), custom_field_67 TEXT(2000), custom_field_68 TEXT(2000), custom_field_69 TEXT(2000), custom_field_70 TEXT(2000),
    custom_field_71 TEXT(2000), custom_field_72 TEXT(2000), custom_field_73 TEXT(2000), custom_field_74 TEXT(2000), custom_field_75 TEXT(2000),
    custom_field_76 TEXT(2000), custom_field_77 TEXT(2000), custom_field_78 TEXT(2000), custom_field_79 TEXT(2000), custom_field_80 TEXT(2000),
    custom_field_81 TEXT(2000), custom_field_82 TEXT(2000), custom_field_83 TEXT(2000), custom_field_84 TEXT(2000), custom_field_85 TEXT(2000),
    custom_field_86 TEXT(2000), custom_field_87 TEXT(2000), custom_field_88 TEXT(2000), custom_field_89 TEXT(2000), custom_field_90 TEXT(2000),
    custom_field_91 TEXT(2000), custom_field_92 TEXT(2000), custom_field_93 TEXT(2000), custom_field_94 TEXT(2000), custom_field_95 TEXT(2000),
    custom_field_96 TEXT(2000), custom_field_97 TEXT(2000), custom_field_98 TEXT(2000), custom_field_99 TEXT(2000), custom_field_100 TEXT(2000),
    custom_field_101 TEXT(2000), custom_field_102 TEXT(2000), custom_field_103 TEXT(2000), custom_field_104 TEXT(2000), custom_field_105 TEXT(2000),
    custom_field_106 TEXT(2000), custom_field_107 TEXT(2000), custom_field_108 TEXT(2000), custom_field_109 TEXT(2000), custom_field_110 TEXT(2000),
    custom_field_111 TEXT(2000), custom_field_112 TEXT(2000), custom_field_113 TEXT(2000), custom_field_114 TEXT(2000), custom_field_115 TEXT(2000),
    custom_field_116 TEXT(2000), custom_field_117 TEXT(2000), custom_field_118 TEXT(2000), custom_field_119 TEXT(2000), custom_field_120 TEXT(2000),
    custom_field_121 TEXT(2000), custom_field_122 TEXT(2000), custom_field_123 TEXT(2000), custom_field_124 TEXT(2000), custom_field_125 TEXT(2000),
    custom_field_126 TEXT(2000), custom_field_127 TEXT(2000), custom_field_128 TEXT(2000), custom_field_129 TEXT(2000), custom_field_130 TEXT(2000),
    custom_field_131 TEXT(2000), custom_field_132 TEXT(2000), custom_field_133 TEXT(2000), custom_field_134 TEXT(2000), custom_field_135 TEXT(2000),
    custom_field_136 TEXT(2000), custom_field_137 TEXT(2000), custom_field_138 TEXT(2000), custom_field_139 TEXT(2000), custom_field_140 TEXT(2000),
    custom_field_141 TEXT(2000), custom_field_142 TEXT(2000), custom_field_143 TEXT(2000), custom_field_144 TEXT(2000), custom_field_145 TEXT(2000),
    custom_field_146 TEXT(2000), custom_field_147 TEXT(2000), custom_field_148 TEXT(2000), custom_field_149 TEXT(2000), custom_field_150 TEXT(2000),
    custom_field_151 TEXT(2000), custom_field_152 TEXT(2000), custom_field_153 TEXT(2000), custom_field_154 TEXT(2000), custom_field_155 TEXT(2000),
    custom_field_156 TEXT(2000), custom_field_157 TEXT(2000), custom_field_158 TEXT(2000), custom_field_159 TEXT(2000), custom_field_160 TEXT(2000),
    custom_field_161 TEXT(2000), custom_field_162 TEXT(2000), custom_field_163 TEXT(2000), custom_field_164 TEXT(2000), custom_field_165 TEXT(2000),
    custom_field_166 TEXT(2000), custom_field_167 TEXT(2000), custom_field_168 TEXT(2000), custom_field_169 TEXT(2000), custom_field_170 TEXT(2000),
    custom_field_171 TEXT(2000), custom_field_172 TEXT(2000), custom_field_173 TEXT(2000), custom_field_174 TEXT(2000), custom_field_175 TEXT(2000),
    custom_field_176 TEXT(2000), custom_field_177 TEXT(2000), custom_field_178 TEXT(2000), custom_field_179 TEXT(2000), custom_field_180 TEXT(2000),
    custom_field_181 TEXT(2000), custom_field_182 TEXT(2000), custom_field_183 TEXT(2000), custom_field_184 TEXT(2000), custom_field_185 TEXT(2000),
    custom_field_186 TEXT(2000), custom_field_187 TEXT(2000), custom_field_188 TEXT(2000), custom_field_189 TEXT(2000), custom_field_190 TEXT(2000),
    custom_field_191 TEXT(2000), custom_field_192 TEXT(2000), custom_field_193 TEXT(2000), custom_field_194 TEXT(2000), custom_field_195 TEXT(2000),
    custom_field_196 TEXT(2000), custom_field_197 TEXT(2000), custom_field_198 TEXT(2000), custom_field_199 TEXT(2000), custom_field_200 TEXT(2000),
    custom_field_201 TEXT(2000), custom_field_202 TEXT(2000), custom_field_203 TEXT(2000), custom_field_204 TEXT(2000), custom_field_205 TEXT(2000),
    custom_field_206 TEXT(2000), custom_field_207 TEXT(2000), custom_field_208 TEXT(2000), custom_field_209 TEXT(2000), custom_field_210 TEXT(2000),
    custom_field_211 TEXT(2000), custom_field_212 TEXT(2000), custom_field_213 TEXT(2000), custom_field_214 TEXT(2000), custom_field_215 TEXT(2000),
    custom_field_216 TEXT(2000), custom_field_217 TEXT(2000), custom_field_218 TEXT(2000), custom_field_219 TEXT(2000), custom_field_220 TEXT(2000),
    custom_field_221 TEXT(2000), custom_field_222 TEXT(2000), custom_field_223 TEXT(2000), custom_field_224 TEXT(2000), custom_field_225 TEXT(2000),
    custom_field_226 TEXT(2000), custom_field_227 TEXT(2000), custom_field_228 TEXT(2000), custom_field_229 TEXT(2000), custom_field_230 TEXT(2000),
    custom_field_231 TEXT(2000), custom_field_232 TEXT(2000), custom_field_233 TEXT(2000), custom_field_234 TEXT(2000), custom_field_235 TEXT(2000),
    custom_field_236 TEXT(2000), custom_field_237 TEXT(2000), custom_field_238 TEXT(2000), custom_field_239 TEXT(2000), custom_field_240 TEXT(2000),
    custom_field_241 TEXT(2000), custom_field_242 TEXT(2000), custom_field_243 TEXT(2000), custom_field_244 TEXT(2000), custom_field_245 TEXT(2000),
    custom_field_246 TEXT(2000), custom_field_247 TEXT(2000), custom_field_248 TEXT(2000), custom_field_249 TEXT(2000), custom_field_250 TEXT(2000),
    FOREIGN KEY (entity_id) REFERENCES attendance(id) ON DELETE CASCADE,
    UNIQUE KEY unique_attendance_custom (entity_id)
);

CREATE TABLE IF NOT EXISTS organizations_custom_field (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    entity_id BIGINT NOT NULL,
    custom_field_1 TEXT(2000), custom_field_2 TEXT(2000), custom_field_3 TEXT(2000), custom_field_4 TEXT(2000), custom_field_5 TEXT(2000),
    custom_field_6 TEXT(2000), custom_field_7 TEXT(2000), custom_field_8 TEXT(2000), custom_field_9 TEXT(2000), custom_field_10 TEXT(2000),
    custom_field_11 TEXT(2000), custom_field_12 TEXT(2000), custom_field_13 TEXT(2000), custom_field_14 TEXT(2000), custom_field_15 TEXT(2000),
    custom_field_16 TEXT(2000), custom_field_17 TEXT(2000), custom_field_18 TEXT(2000), custom_field_19 TEXT(2000), custom_field_20 TEXT(2000),
    custom_field_21 TEXT(2000), custom_field_22 TEXT(2000), custom_field_23 TEXT(2000), custom_field_24 TEXT(2000), custom_field_25 TEXT(2000),
    custom_field_26 TEXT(2000), custom_field_27 TEXT(2000), custom_field_28 TEXT(2000), custom_field_29 TEXT(2000), custom_field_30 TEXT(2000),
    custom_field_31 TEXT(2000), custom_field_32 TEXT(2000), custom_field_33 TEXT(2000), custom_field_34 TEXT(2000), custom_field_35 TEXT(2000),
    custom_field_36 TEXT(2000), custom_field_37 TEXT(2000), custom_field_38 TEXT(2000), custom_field_39 TEXT(2000), custom_field_40 TEXT(2000),
    custom_field_41 TEXT(2000), custom_field_42 TEXT(2000), custom_field_43 TEXT(2000), custom_field_44 TEXT(2000), custom_field_45 TEXT(2000),
    custom_field_46 TEXT(2000), custom_field_47 TEXT(2000), custom_field_48 TEXT(2000), custom_field_49 TEXT(2000), custom_field_50 TEXT(2000),
    custom_field_51 TEXT(2000), custom_field_52 TEXT(2000), custom_field_53 TEXT(2000), custom_field_54 TEXT(2000), custom_field_55 TEXT(2000),
    custom_field_56 TEXT(2000), custom_field_57 TEXT(2000), custom_field_58 TEXT(2000), custom_field_59 TEXT(2000), custom_field_60 TEXT(2000),
    custom_field_61 TEXT(2000), custom_field_62 TEXT(2000), custom_field_63 TEXT(2000), custom_field_64 TEXT(2000), custom_field_65 TEXT(2000),
    custom_field_66 TEXT(2000), custom_field_67 TEXT(2000), custom_field_68 TEXT(2000), custom_field_69 TEXT(2000), custom_field_70 TEXT(2000),
    custom_field_71 TEXT(2000), custom_field_72 TEXT(2000), custom_field_73 TEXT(2000), custom_field_74 TEXT(2000), custom_field_75 TEXT(2000),
    custom_field_76 TEXT(2000), custom_field_77 TEXT(2000), custom_field_78 TEXT(2000), custom_field_79 TEXT(2000), custom_field_80 TEXT(2000),
    custom_field_81 TEXT(2000), custom_field_82 TEXT(2000), custom_field_83 TEXT(2000), custom_field_84 TEXT(2000), custom_field_85 TEXT(2000),
    custom_field_86 TEXT(2000), custom_field_87 TEXT(2000), custom_field_88 TEXT(2000), custom_field_89 TEXT(2000), custom_field_90 TEXT(2000),
    custom_field_91 TEXT(2000), custom_field_92 TEXT(2000), custom_field_93 TEXT(2000), custom_field_94 TEXT(2000), custom_field_95 TEXT(2000),
    custom_field_96 TEXT(2000), custom_field_97 TEXT(2000), custom_field_98 TEXT(2000), custom_field_99 TEXT(2000), custom_field_100 TEXT(2000),
    custom_field_101 TEXT(2000), custom_field_102 TEXT(2000), custom_field_103 TEXT(2000), custom_field_104 TEXT(2000), custom_field_105 TEXT(2000),
    custom_field_106 TEXT(2000), custom_field_107 TEXT(2000), custom_field_108 TEXT(2000), custom_field_109 TEXT(2000), custom_field_110 TEXT(2000),
    custom_field_111 TEXT(2000), custom_field_112 TEXT(2000), custom_field_113 TEXT(2000), custom_field_114 TEXT(2000), custom_field_115 TEXT(2000),
    custom_field_116 TEXT(2000), custom_field_117 TEXT(2000), custom_field_118 TEXT(2000), custom_field_119 TEXT(2000), custom_field_120 TEXT(2000),
    custom_field_121 TEXT(2000), custom_field_122 TEXT(2000), custom_field_123 TEXT(2000), custom_field_124 TEXT(2000), custom_field_125 TEXT(2000),
    custom_field_126 TEXT(2000), custom_field_127 TEXT(2000), custom_field_128 TEXT(2000), custom_field_129 TEXT(2000), custom_field_130 TEXT(2000),
    custom_field_131 TEXT(2000), custom_field_132 TEXT(2000), custom_field_133 TEXT(2000), custom_field_134 TEXT(2000), custom_field_135 TEXT(2000),
    custom_field_136 TEXT(2000), custom_field_137 TEXT(2000), custom_field_138 TEXT(2000), custom_field_139 TEXT(2000), custom_field_140 TEXT(2000),
    custom_field_141 TEXT(2000), custom_field_142 TEXT(2000), custom_field_143 TEXT(2000), custom_field_144 TEXT(2000), custom_field_145 TEXT(2000),
    custom_field_146 TEXT(2000), custom_field_147 TEXT(2000), custom_field_148 TEXT(2000), custom_field_149 TEXT(2000), custom_field_150 TEXT(2000),
    custom_field_151 TEXT(2000), custom_field_152 TEXT(2000), custom_field_153 TEXT(2000), custom_field_154 TEXT(2000), custom_field_155 TEXT(2000),
    custom_field_156 TEXT(2000), custom_field_157 TEXT(2000), custom_field_158 TEXT(2000), custom_field_159 TEXT(2000), custom_field_160 TEXT(2000),
    custom_field_161 TEXT(2000), custom_field_162 TEXT(2000), custom_field_163 TEXT(2000), custom_field_164 TEXT(2000), custom_field_165 TEXT(2000),
    custom_field_166 TEXT(2000), custom_field_167 TEXT(2000), custom_field_168 TEXT(2000), custom_field_169 TEXT(2000), custom_field_170 TEXT(2000),
    custom_field_171 TEXT(2000), custom_field_172 TEXT(2000), custom_field_173 TEXT(2000), custom_field_174 TEXT(2000), custom_field_175 TEXT(2000),
    custom_field_176 TEXT(2000), custom_field_177 TEXT(2000), custom_field_178 TEXT(2000), custom_field_179 TEXT(2000), custom_field_180 TEXT(2000),
    custom_field_181 TEXT(2000), custom_field_182 TEXT(2000), custom_field_183 TEXT(2000), custom_field_184 TEXT(2000), custom_field_185 TEXT(2000),
    custom_field_186 TEXT(2000), custom_field_187 TEXT(2000), custom_field_188 TEXT(2000), custom_field_189 TEXT(2000), custom_field_190 TEXT(2000),
    custom_field_191 TEXT(2000), custom_field_192 TEXT(2000), custom_field_193 TEXT(2000), custom_field_194 TEXT(2000), custom_field_195 TEXT(2000),
    custom_field_196 TEXT(2000), custom_field_197 TEXT(2000), custom_field_198 TEXT(2000), custom_field_199 TEXT(2000), custom_field_200 TEXT(2000),
    custom_field_201 TEXT(2000), custom_field_202 TEXT(2000), custom_field_203 TEXT(2000), custom_field_204 TEXT(2000), custom_field_205 TEXT(2000),
    custom_field_206 TEXT(2000), custom_field_207 TEXT(2000), custom_field_208 TEXT(2000), custom_field_209 TEXT(2000), custom_field_210 TEXT(2000),
    custom_field_211 TEXT(2000), custom_field_212 TEXT(2000), custom_field_213 TEXT(2000), custom_field_214 TEXT(2000), custom_field_215 TEXT(2000),
    custom_field_216 TEXT(2000), custom_field_217 TEXT(2000), custom_field_218 TEXT(2000), custom_field_219 TEXT(2000), custom_field_220 TEXT(2000),
    custom_field_221 TEXT(2000), custom_field_222 TEXT(2000), custom_field_223 TEXT(2000), custom_field_224 TEXT(2000), custom_field_225 TEXT(2000),
    custom_field_226 TEXT(2000), custom_field_227 TEXT(2000), custom_field_228 TEXT(2000), custom_field_229 TEXT(2000), custom_field_230 TEXT(2000),
    custom_field_231 TEXT(2000), custom_field_232 TEXT(2000), custom_field_233 TEXT(2000), custom_field_234 TEXT(2000), custom_field_235 TEXT(2000),
    custom_field_236 TEXT(2000), custom_field_237 TEXT(2000), custom_field_238 TEXT(2000), custom_field_239 TEXT(2000), custom_field_240 TEXT(2000),
    custom_field_241 TEXT(2000), custom_field_242 TEXT(2000), custom_field_243 TEXT(2000), custom_field_244 TEXT(2000), custom_field_245 TEXT(2000),
    custom_field_246 TEXT(2000), custom_field_247 TEXT(2000), custom_field_248 TEXT(2000), custom_field_249 TEXT(2000), custom_field_250 TEXT(2000),
    FOREIGN KEY (entity_id) REFERENCES organizations(id) ON DELETE CASCADE,
    UNIQUE KEY unique_organizations_custom (entity_id)
);

CREATE TABLE IF NOT EXISTS users_custom_field (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    entity_id BIGINT NOT NULL,
    custom_field_1 TEXT(2000), custom_field_2 TEXT(2000), custom_field_3 TEXT(2000), custom_field_4 TEXT(2000), custom_field_5 TEXT(2000),
    custom_field_6 TEXT(2000), custom_field_7 TEXT(2000), custom_field_8 TEXT(2000), custom_field_9 TEXT(2000), custom_field_10 TEXT(2000),
    custom_field_11 TEXT(2000), custom_field_12 TEXT(2000), custom_field_13 TEXT(2000), custom_field_14 TEXT(2000), custom_field_15 TEXT(2000),
    custom_field_16 TEXT(2000), custom_field_17 TEXT(2000), custom_field_18 TEXT(2000), custom_field_19 TEXT(2000), custom_field_20 TEXT(2000),
    custom_field_21 TEXT(2000), custom_field_22 TEXT(2000), custom_field_23 TEXT(2000), custom_field_24 TEXT(2000), custom_field_25 TEXT(2000),
    custom_field_26 TEXT(2000), custom_field_27 TEXT(2000), custom_field_28 TEXT(2000), custom_field_29 TEXT(2000), custom_field_30 TEXT(2000),
    custom_field_31 TEXT(2000), custom_field_32 TEXT(2000), custom_field_33 TEXT(2000), custom_field_34 TEXT(2000), custom_field_35 TEXT(2000),
    custom_field_36 TEXT(2000), custom_field_37 TEXT(2000), custom_field_38 TEXT(2000), custom_field_39 TEXT(2000), custom_field_40 TEXT(2000),
    custom_field_41 TEXT(2000), custom_field_42 TEXT(2000), custom_field_43 TEXT(2000), custom_field_44 TEXT(2000), custom_field_45 TEXT(2000),
    custom_field_46 TEXT(2000), custom_field_47 TEXT(2000), custom_field_48 TEXT(2000), custom_field_49 TEXT(2000), custom_field_50 TEXT(2000),
    custom_field_51 TEXT(2000), custom_field_52 TEXT(2000), custom_field_53 TEXT(2000), custom_field_54 TEXT(2000), custom_field_55 TEXT(2000),
    custom_field_56 TEXT(2000), custom_field_57 TEXT(2000), custom_field_58 TEXT(2000), custom_field_59 TEXT(2000), custom_field_60 TEXT(2000),
    custom_field_61 TEXT(2000), custom_field_62 TEXT(2000), custom_field_63 TEXT(2000), custom_field_64 TEXT(2000), custom_field_65 TEXT(2000),
    custom_field_66 TEXT(2000), custom_field_67 TEXT(2000), custom_field_68 TEXT(2000), custom_field_69 TEXT(2000), custom_field_70 TEXT(2000),
    custom_field_71 TEXT(2000), custom_field_72 TEXT(2000), custom_field_73 TEXT(2000), custom_field_74 TEXT(2000), custom_field_75 TEXT(2000),
    custom_field_76 TEXT(2000), custom_field_77 TEXT(2000), custom_field_78 TEXT(2000), custom_field_79 TEXT(2000), custom_field_80 TEXT(2000),
    custom_field_81 TEXT(2000), custom_field_82 TEXT(2000), custom_field_83 TEXT(2000), custom_field_84 TEXT(2000), custom_field_85 TEXT(2000),
    custom_field_86 TEXT(2000), custom_field_87 TEXT(2000), custom_field_88 TEXT(2000), custom_field_89 TEXT(2000), custom_field_90 TEXT(2000),
    custom_field_91 TEXT(2000), custom_field_92 TEXT(2000), custom_field_93 TEXT(2000), custom_field_94 TEXT(2000), custom_field_95 TEXT(2000),
    custom_field_96 TEXT(2000), custom_field_97 TEXT(2000), custom_field_98 TEXT(2000), custom_field_99 TEXT(2000), custom_field_100 TEXT(2000),
    custom_field_101 TEXT(2000), custom_field_102 TEXT(2000), custom_field_103 TEXT(2000), custom_field_104 TEXT(2000), custom_field_105 TEXT(2000),
    custom_field_106 TEXT(2000), custom_field_107 TEXT(2000), custom_field_108 TEXT(2000), custom_field_109 TEXT(2000), custom_field_110 TEXT(2000),
    custom_field_111 TEXT(2000), custom_field_112 TEXT(2000), custom_field_113 TEXT(2000), custom_field_114 TEXT(2000), custom_field_115 TEXT(2000),
    custom_field_116 TEXT(2000), custom_field_117 TEXT(2000), custom_field_118 TEXT(2000), custom_field_119 TEXT(2000), custom_field_120 TEXT(2000),
    custom_field_121 TEXT(2000), custom_field_122 TEXT(2000), custom_field_123 TEXT(2000), custom_field_124 TEXT(2000), custom_field_125 TEXT(2000),
    custom_field_126 TEXT(2000), custom_field_127 TEXT(2000), custom_field_128 TEXT(2000), custom_field_129 TEXT(2000), custom_field_130 TEXT(2000),
    custom_field_131 TEXT(2000), custom_field_132 TEXT(2000), custom_field_133 TEXT(2000), custom_field_134 TEXT(2000), custom_field_135 TEXT(2000),
    custom_field_136 TEXT(2000), custom_field_137 TEXT(2000), custom_field_138 TEXT(2000), custom_field_139 TEXT(2000), custom_field_140 TEXT(2000),
    custom_field_141 TEXT(2000), custom_field_142 TEXT(2000), custom_field_143 TEXT(2000), custom_field_144 TEXT(2000), custom_field_145 TEXT(2000),
    custom_field_146 TEXT(2000), custom_field_147 TEXT(2000), custom_field_148 TEXT(2000), custom_field_149 TEXT(2000), custom_field_150 TEXT(2000),
    custom_field_151 TEXT(2000), custom_field_152 TEXT(2000), custom_field_153 TEXT(2000), custom_field_154 TEXT(2000), custom_field_155 TEXT(2000),
    custom_field_156 TEXT(2000), custom_field_157 TEXT(2000), custom_field_158 TEXT(2000), custom_field_159 TEXT(2000), custom_field_160 TEXT(2000),
    custom_field_161 TEXT(2000), custom_field_162 TEXT(2000), custom_field_163 TEXT(2000), custom_field_164 TEXT(2000), custom_field_165 TEXT(2000),
    custom_field_166 TEXT(2000), custom_field_167 TEXT(2000), custom_field_168 TEXT(2000), custom_field_169 TEXT(2000), custom_field_170 TEXT(2000),
    custom_field_171 TEXT(2000), custom_field_172 TEXT(2000), custom_field_173 TEXT(2000), custom_field_174 TEXT(2000), custom_field_175 TEXT(2000),
    custom_field_176 TEXT(2000), custom_field_177 TEXT(2000), custom_field_178 TEXT(2000), custom_field_179 TEXT(2000), custom_field_180 TEXT(2000),
    custom_field_181 TEXT(2000), custom_field_182 TEXT(2000), custom_field_183 TEXT(2000), custom_field_184 TEXT(2000), custom_field_185 TEXT(2000),
    custom_field_186 TEXT(2000), custom_field_187 TEXT(2000), custom_field_188 TEXT(2000), custom_field_189 TEXT(2000), custom_field_190 TEXT(2000),
    custom_field_191 TEXT(2000), custom_field_192 TEXT(2000), custom_field_193 TEXT(2000), custom_field_194 TEXT(2000), custom_field_195 TEXT(2000),
    custom_field_196 TEXT(2000), custom_field_197 TEXT(2000), custom_field_198 TEXT(2000), custom_field_199 TEXT(2000), custom_field_200 TEXT(2000),
    custom_field_201 TEXT(2000), custom_field_202 TEXT(2000), custom_field_203 TEXT(2000), custom_field_204 TEXT(2000), custom_field_205 TEXT(2000),
    custom_field_206 TEXT(2000), custom_field_207 TEXT(2000), custom_field_208 TEXT(2000), custom_field_209 TEXT(2000), custom_field_210 TEXT(2000),
    custom_field_211 TEXT(2000), custom_field_212 TEXT(2000), custom_field_213 TEXT(2000), custom_field_214 TEXT(2000), custom_field_215 TEXT(2000),
    custom_field_216 TEXT(2000), custom_field_217 TEXT(2000), custom_field_218 TEXT(2000), custom_field_219 TEXT(2000), custom_field_220 TEXT(2000),
    custom_field_221 TEXT(2000), custom_field_222 TEXT(2000), custom_field_223 TEXT(2000), custom_field_224 TEXT(2000), custom_field_225 TEXT(2000),
    custom_field_226 TEXT(2000), custom_field_227 TEXT(2000), custom_field_228 TEXT(2000), custom_field_229 TEXT(2000), custom_field_230 TEXT(2000),
    custom_field_231 TEXT(2000), custom_field_232 TEXT(2000), custom_field_233 TEXT(2000), custom_field_234 TEXT(2000), custom_field_235 TEXT(2000),
    custom_field_236 TEXT(2000), custom_field_237 TEXT(2000), custom_field_238 TEXT(2000), custom_field_239 TEXT(2000), custom_field_240 TEXT(2000),
    custom_field_241 TEXT(2000), custom_field_242 TEXT(2000), custom_field_243 TEXT(2000), custom_field_244 TEXT(2000), custom_field_245 TEXT(2000),
    custom_field_246 TEXT(2000), custom_field_247 TEXT(2000), custom_field_248 TEXT(2000), custom_field_249 TEXT(2000), custom_field_250 TEXT(2000),
    FOREIGN KEY (entity_id) REFERENCES users(id) ON DELETE CASCADE,
    UNIQUE KEY unique_users_custom (entity_id)
);

CREATE TABLE IF NOT EXISTS email_templates_custom_field (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    entity_id BIGINT NOT NULL,
    custom_field_1 TEXT(2000), custom_field_2 TEXT(2000), custom_field_3 TEXT(2000), custom_field_4 TEXT(2000), custom_field_5 TEXT(2000),
    custom_field_6 TEXT(2000), custom_field_7 TEXT(2000), custom_field_8 TEXT(2000), custom_field_9 TEXT(2000), custom_field_10 TEXT(2000),
    custom_field_11 TEXT(2000), custom_field_12 TEXT(2000), custom_field_13 TEXT(2000), custom_field_14 TEXT(2000), custom_field_15 TEXT(2000),
    custom_field_16 TEXT(2000), custom_field_17 TEXT(2000), custom_field_18 TEXT(2000), custom_field_19 TEXT(2000), custom_field_20 TEXT(2000),
    custom_field_21 TEXT(2000), custom_field_22 TEXT(2000), custom_field_23 TEXT(2000), custom_field_24 TEXT(2000), custom_field_25 TEXT(2000),
    custom_field_26 TEXT(2000), custom_field_27 TEXT(2000), custom_field_28 TEXT(2000), custom_field_29 TEXT(2000), custom_field_30 TEXT(2000),
    custom_field_31 TEXT(2000), custom_field_32 TEXT(2000), custom_field_33 TEXT(2000), custom_field_34 TEXT(2000), custom_field_35 TEXT(2000),
    custom_field_36 TEXT(2000), custom_field_37 TEXT(2000), custom_field_38 TEXT(2000), custom_field_39 TEXT(2000), custom_field_40 TEXT(2000),
    custom_field_41 TEXT(2000), custom_field_42 TEXT(2000), custom_field_43 TEXT(2000), custom_field_44 TEXT(2000), custom_field_45 TEXT(2000),
    custom_field_46 TEXT(2000), custom_field_47 TEXT(2000), custom_field_48 TEXT(2000), custom_field_49 TEXT(2000), custom_field_50 TEXT(2000),
    custom_field_51 TEXT(2000), custom_field_52 TEXT(2000), custom_field_53 TEXT(2000), custom_field_54 TEXT(2000), custom_field_55 TEXT(2000),
    custom_field_56 TEXT(2000), custom_field_57 TEXT(2000), custom_field_58 TEXT(2000), custom_field_59 TEXT(2000), custom_field_60 TEXT(2000),
    custom_field_61 TEXT(2000), custom_field_62 TEXT(2000), custom_field_63 TEXT(2000), custom_field_64 TEXT(2000), custom_field_65 TEXT(2000),
    custom_field_66 TEXT(2000), custom_field_67 TEXT(2000), custom_field_68 TEXT(2000), custom_field_69 TEXT(2000), custom_field_70 TEXT(2000),
    custom_field_71 TEXT(2000), custom_field_72 TEXT(2000), custom_field_73 TEXT(2000), custom_field_74 TEXT(2000), custom_field_75 TEXT(2000),
    custom_field_76 TEXT(2000), custom_field_77 TEXT(2000), custom_field_78 TEXT(2000), custom_field_79 TEXT(2000), custom_field_80 TEXT(2000),
    custom_field_81 TEXT(2000), custom_field_82 TEXT(2000), custom_field_83 TEXT(2000), custom_field_84 TEXT(2000), custom_field_85 TEXT(2000),
    custom_field_86 TEXT(2000), custom_field_87 TEXT(2000), custom_field_88 TEXT(2000), custom_field_89 TEXT(2000), custom_field_90 TEXT(2000),
    custom_field_91 TEXT(2000), custom_field_92 TEXT(2000), custom_field_93 TEXT(2000), custom_field_94 TEXT(2000), custom_field_95 TEXT(2000),
    custom_field_96 TEXT(2000), custom_field_97 TEXT(2000), custom_field_98 TEXT(2000), custom_field_99 TEXT(2000), custom_field_100 TEXT(2000),
    custom_field_101 TEXT(2000), custom_field_102 TEXT(2000), custom_field_103 TEXT(2000), custom_field_104 TEXT(2000), custom_field_105 TEXT(2000),
    custom_field_106 TEXT(2000), custom_field_107 TEXT(2000), custom_field_108 TEXT(2000), custom_field_109 TEXT(2000), custom_field_110 TEXT(2000),
    custom_field_111 TEXT(2000), custom_field_112 TEXT(2000), custom_field_113 TEXT(2000), custom_field_114 TEXT(2000), custom_field_115 TEXT(2000),
    custom_field_116 TEXT(2000), custom_field_117 TEXT(2000), custom_field_118 TEXT(2000), custom_field_119 TEXT(2000), custom_field_120 TEXT(2000),
    custom_field_121 TEXT(2000), custom_field_122 TEXT(2000), custom_field_123 TEXT(2000), custom_field_124 TEXT(2000), custom_field_125 TEXT(2000),
    custom_field_126 TEXT(2000), custom_field_127 TEXT(2000), custom_field_128 TEXT(2000), custom_field_129 TEXT(2000), custom_field_130 TEXT(2000),
    custom_field_131 TEXT(2000), custom_field_132 TEXT(2000), custom_field_133 TEXT(2000), custom_field_134 TEXT(2000), custom_field_135 TEXT(2000),
    custom_field_136 TEXT(2000), custom_field_137 TEXT(2000), custom_field_138 TEXT(2000), custom_field_139 TEXT(2000), custom_field_140 TEXT(2000),
    custom_field_141 TEXT(2000), custom_field_142 TEXT(2000), custom_field_143 TEXT(2000), custom_field_144 TEXT(2000), custom_field_145 TEXT(2000),
    custom_field_146 TEXT(2000), custom_field_147 TEXT(2000), custom_field_148 TEXT(2000), custom_field_149 TEXT(2000), custom_field_150 TEXT(2000),
    custom_field_151 TEXT(2000), custom_field_152 TEXT(2000), custom_field_153 TEXT(2000), custom_field_154 TEXT(2000), custom_field_155 TEXT(2000),
    custom_field_156 TEXT(2000), custom_field_157 TEXT(2000), custom_field_158 TEXT(2000), custom_field_159 TEXT(2000), custom_field_160 TEXT(2000),
    custom_field_161 TEXT(2000), custom_field_162 TEXT(2000), custom_field_163 TEXT(2000), custom_field_164 TEXT(2000), custom_field_165 TEXT(2000),
    custom_field_166 TEXT(2000), custom_field_167 TEXT(2000), custom_field_168 TEXT(2000), custom_field_169 TEXT(2000), custom_field_170 TEXT(2000),
    custom_field_171 TEXT(2000), custom_field_172 TEXT(2000), custom_field_173 TEXT(2000), custom_field_174 TEXT(2000), custom_field_175 TEXT(2000),
    custom_field_176 TEXT(2000), custom_field_177 TEXT(2000), custom_field_178 TEXT(2000), custom_field_179 TEXT(2000), custom_field_180 TEXT(2000),
    custom_field_181 TEXT(2000), custom_field_182 TEXT(2000), custom_field_183 TEXT(2000), custom_field_184 TEXT(2000), custom_field_185 TEXT(2000),
    custom_field_186 TEXT(2000), custom_field_187 TEXT(2000), custom_field_188 TEXT(2000), custom_field_189 TEXT(2000), custom_field_190 TEXT(2000),
    custom_field_191 TEXT(2000), custom_field_192 TEXT(2000), custom_field_193 TEXT(2000), custom_field_194 TEXT(2000), custom_field_195 TEXT(2000),
    custom_field_196 TEXT(2000), custom_field_197 TEXT(2000), custom_field_198 TEXT(2000), custom_field_199 TEXT(2000), custom_field_200 TEXT(2000),
    custom_field_201 TEXT(2000), custom_field_202 TEXT(2000), custom_field_203 TEXT(2000), custom_field_204 TEXT(2000), custom_field_205 TEXT(2000),
    custom_field_206 TEXT(2000), custom_field_207 TEXT(2000), custom_field_208 TEXT(2000), custom_field_209 TEXT(2000), custom_field_210 TEXT(2000),
    custom_field_211 TEXT(2000), custom_field_212 TEXT(2000), custom_field_213 TEXT(2000), custom_field_214 TEXT(2000), custom_field_215 TEXT(2000),
    custom_field_216 TEXT(2000), custom_field_217 TEXT(2000), custom_field_218 TEXT(2000), custom_field_219 TEXT(2000), custom_field_220 TEXT(2000),
    custom_field_221 TEXT(2000), custom_field_222 TEXT(2000), custom_field_223 TEXT(2000), custom_field_224 TEXT(2000), custom_field_225 TEXT(2000),
    custom_field_226 TEXT(2000), custom_field_227 TEXT(2000), custom_field_228 TEXT(2000), custom_field_229 TEXT(2000), custom_field_230 TEXT(2000),
    custom_field_231 TEXT(2000), custom_field_232 TEXT(2000), custom_field_233 TEXT(2000), custom_field_234 TEXT(2000), custom_field_235 TEXT(2000),
    custom_field_236 TEXT(2000), custom_field_237 TEXT(2000), custom_field_238 TEXT(2000), custom_field_239 TEXT(2000), custom_field_240 TEXT(2000),
    custom_field_241 TEXT(2000), custom_field_242 TEXT(2000), custom_field_243 TEXT(2000), custom_field_244 TEXT(2000), custom_field_245 TEXT(2000),
    custom_field_246 TEXT(2000), custom_field_247 TEXT(2000), custom_field_248 TEXT(2000), custom_field_249 TEXT(2000), custom_field_250 TEXT(2000),
    FOREIGN KEY (entity_id) REFERENCES email_templates(id) ON DELETE CASCADE,
    UNIQUE KEY unique_email_templates_custom (entity_id)
);

CREATE TABLE IF NOT EXISTS email_logs_custom_field (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    entity_id BIGINT NOT NULL,
    custom_field_1 TEXT(2000), custom_field_2 TEXT(2000), custom_field_3 TEXT(2000), custom_field_4 TEXT(2000), custom_field_5 TEXT(2000),
    custom_field_6 TEXT(2000), custom_field_7 TEXT(2000), custom_field_8 TEXT(2000), custom_field_9 TEXT(2000), custom_field_10 TEXT(2000),
    custom_field_11 TEXT(2000), custom_field_12 TEXT(2000), custom_field_13 TEXT(2000), custom_field_14 TEXT(2000), custom_field_15 TEXT(2000),
    custom_field_16 TEXT(2000), custom_field_17 TEXT(2000), custom_field_18 TEXT(2000), custom_field_19 TEXT(2000), custom_field_20 TEXT(2000),
    custom_field_21 TEXT(2000), custom_field_22 TEXT(2000), custom_field_23 TEXT(2000), custom_field_24 TEXT(2000), custom_field_25 TEXT(2000),
    custom_field_26 TEXT(2000), custom_field_27 TEXT(2000), custom_field_28 TEXT(2000), custom_field_29 TEXT(2000), custom_field_30 TEXT(2000),
    custom_field_31 TEXT(2000), custom_field_32 TEXT(2000), custom_field_33 TEXT(2000), custom_field_34 TEXT(2000), custom_field_35 TEXT(2000),
    custom_field_36 TEXT(2000), custom_field_37 TEXT(2000), custom_field_38 TEXT(2000), custom_field_39 TEXT(2000), custom_field_40 TEXT(2000),
    custom_field_41 TEXT(2000), custom_field_42 TEXT(2000), custom_field_43 TEXT(2000), custom_field_44 TEXT(2000), custom_field_45 TEXT(2000),
    custom_field_46 TEXT(2000), custom_field_47 TEXT(2000), custom_field_48 TEXT(2000), custom_field_49 TEXT(2000), custom_field_50 TEXT(2000),
    custom_field_51 TEXT(2000), custom_field_52 TEXT(2000), custom_field_53 TEXT(2000), custom_field_54 TEXT(2000), custom_field_55 TEXT(2000),
    custom_field_56 TEXT(2000), custom_field_57 TEXT(2000), custom_field_58 TEXT(2000), custom_field_59 TEXT(2000), custom_field_60 TEXT(2000),
    custom_field_61 TEXT(2000), custom_field_62 TEXT(2000), custom_field_63 TEXT(2000), custom_field_64 TEXT(2000), custom_field_65 TEXT(2000),
    custom_field_66 TEXT(2000), custom_field_67 TEXT(2000), custom_field_68 TEXT(2000), custom_field_69 TEXT(2000), custom_field_70 TEXT(2000),
    custom_field_71 TEXT(2000), custom_field_72 TEXT(2000), custom_field_73 TEXT(2000), custom_field_74 TEXT(2000), custom_field_75 TEXT(2000),
    custom_field_76 TEXT(2000), custom_field_77 TEXT(2000), custom_field_78 TEXT(2000), custom_field_79 TEXT(2000), custom_field_80 TEXT(2000),
    custom_field_81 TEXT(2000), custom_field_82 TEXT(2000), custom_field_83 TEXT(2000), custom_field_84 TEXT(2000), custom_field_85 TEXT(2000),
    custom_field_86 TEXT(2000), custom_field_87 TEXT(2000), custom_field_88 TEXT(2000), custom_field_89 TEXT(2000), custom_field_90 TEXT(2000),
    custom_field_91 TEXT(2000), custom_field_92 TEXT(2000), custom_field_93 TEXT(2000), custom_field_94 TEXT(2000), custom_field_95 TEXT(2000),
    custom_field_96 TEXT(2000), custom_field_97 TEXT(2000), custom_field_98 TEXT(2000), custom_field_99 TEXT(2000), custom_field_100 TEXT(2000),
    custom_field_101 TEXT(2000), custom_field_102 TEXT(2000), custom_field_103 TEXT(2000), custom_field_104 TEXT(2000), custom_field_105 TEXT(2000),
    custom_field_106 TEXT(2000), custom_field_107 TEXT(2000), custom_field_108 TEXT(2000), custom_field_109 TEXT(2000), custom_field_110 TEXT(2000),
    custom_field_111 TEXT(2000), custom_field_112 TEXT(2000), custom_field_113 TEXT(2000), custom_field_114 TEXT(2000), custom_field_115 TEXT(2000),
    custom_field_116 TEXT(2000), custom_field_117 TEXT(2000), custom_field_118 TEXT(2000), custom_field_119 TEXT(2000), custom_field_120 TEXT(2000),
    custom_field_121 TEXT(2000), custom_field_122 TEXT(2000), custom_field_123 TEXT(2000), custom_field_124 TEXT(2000), custom_field_125 TEXT(2000),
    custom_field_126 TEXT(2000), custom_field_127 TEXT(2000), custom_field_128 TEXT(2000), custom_field_129 TEXT(2000), custom_field_130 TEXT(2000),
    custom_field_131 TEXT(2000), custom_field_132 TEXT(2000), custom_field_133 TEXT(2000), custom_field_134 TEXT(2000), custom_field_135 TEXT(2000),
    custom_field_136 TEXT(2000), custom_field_137 TEXT(2000), custom_field_138 TEXT(2000), custom_field_139 TEXT(2000), custom_field_140 TEXT(2000),
    custom_field_141 TEXT(2000), custom_field_142 TEXT(2000), custom_field_143 TEXT(2000), custom_field_144 TEXT(2000), custom_field_145 TEXT(2000),
    custom_field_146 TEXT(2000), custom_field_147 TEXT(2000), custom_field_148 TEXT(2000), custom_field_149 TEXT(2000), custom_field_150 TEXT(2000),
    custom_field_151 TEXT(2000), custom_field_152 TEXT(2000), custom_field_153 TEXT(2000), custom_field_154 TEXT(2000), custom_field_155 TEXT(2000),
    custom_field_156 TEXT(2000), custom_field_157 TEXT(2000), custom_field_158 TEXT(2000), custom_field_159 TEXT(2000), custom_field_160 TEXT(2000),
    custom_field_161 TEXT(2000), custom_field_162 TEXT(2000), custom_field_163 TEXT(2000), custom_field_164 TEXT(2000), custom_field_165 TEXT(2000),
    custom_field_166 TEXT(2000), custom_field_167 TEXT(2000), custom_field_168 TEXT(2000), custom_field_169 TEXT(2000), custom_field_170 TEXT(2000),
    custom_field_171 TEXT(2000), custom_field_172 TEXT(2000), custom_field_173 TEXT(2000), custom_field_174 TEXT(2000), custom_field_175 TEXT(2000),
    custom_field_176 TEXT(2000), custom_field_177 TEXT(2000), custom_field_178 TEXT(2000), custom_field_179 TEXT(2000), custom_field_180 TEXT(2000),
    custom_field_181 TEXT(2000), custom_field_182 TEXT(2000), custom_field_183 TEXT(2000), custom_field_184 TEXT(2000), custom_field_185 TEXT(2000),
    custom_field_186 TEXT(2000), custom_field_187 TEXT(2000), custom_field_188 TEXT(2000), custom_field_189 TEXT(2000), custom_field_190 TEXT(2000),
    custom_field_191 TEXT(2000), custom_field_192 TEXT(2000), custom_field_193 TEXT(2000), custom_field_194 TEXT(2000), custom_field_195 TEXT(2000),
    custom_field_196 TEXT(2000), custom_field_197 TEXT(2000), custom_field_198 TEXT(2000), custom_field_199 TEXT(2000), custom_field_200 TEXT(2000),
    custom_field_201 TEXT(2000), custom_field_202 TEXT(2000), custom_field_203 TEXT(2000), custom_field_204 TEXT(2000), custom_field_205 TEXT(2000),
    custom_field_206 TEXT(2000), custom_field_207 TEXT(2000), custom_field_208 TEXT(2000), custom_field_209 TEXT(2000), custom_field_210 TEXT(2000),
    custom_field_211 TEXT(2000), custom_field_212 TEXT(2000), custom_field_213 TEXT(2000), custom_field_214 TEXT(2000), custom_field_215 TEXT(2000),
    custom_field_216 TEXT(2000), custom_field_217 TEXT(2000), custom_field_218 TEXT(2000), custom_field_219 TEXT(2000), custom_field_220 TEXT(2000),
    custom_field_221 TEXT(2000), custom_field_222 TEXT(2000), custom_field_223 TEXT(2000), custom_field_224 TEXT(2000), custom_field_225 TEXT(2000),
    custom_field_226 TEXT(2000), custom_field_227 TEXT(2000), custom_field_228 TEXT(2000), custom_field_229 TEXT(2000), custom_field_230 TEXT(2000),
    custom_field_231 TEXT(2000), custom_field_232 TEXT(2000), custom_field_233 TEXT(2000), custom_field_234 TEXT(2000), custom_field_235 TEXT(2000),
    custom_field_236 TEXT(2000), custom_field_237 TEXT(2000), custom_field_238 TEXT(2000), custom_field_239 TEXT(2000), custom_field_240 TEXT(2000),
    custom_field_241 TEXT(2000), custom_field_242 TEXT(2000), custom_field_243 TEXT(2000), custom_field_244 TEXT(2000), custom_field_245 TEXT(2000),
    custom_field_246 TEXT(2000), custom_field_247 TEXT(2000), custom_field_248 TEXT(2000), custom_field_249 TEXT(2000), custom_field_250 TEXT(2000),
    FOREIGN KEY (entity_id) REFERENCES email_logs(id) ON DELETE CASCADE,
    UNIQUE KEY unique_email_logs_custom (entity_id)
);

CREATE TABLE IF NOT EXISTS roles_custom_field (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    entity_id BIGINT NOT NULL,
    custom_field_1 TEXT(2000), custom_field_2 TEXT(2000), custom_field_3 TEXT(2000), custom_field_4 TEXT(2000), custom_field_5 TEXT(2000),
    custom_field_6 TEXT(2000), custom_field_7 TEXT(2000), custom_field_8 TEXT(2000), custom_field_9 TEXT(2000), custom_field_10 TEXT(2000),
    custom_field_11 TEXT(2000), custom_field_12 TEXT(2000), custom_field_13 TEXT(2000), custom_field_14 TEXT(2000), custom_field_15 TEXT(2000),
    custom_field_16 TEXT(2000), custom_field_17 TEXT(2000), custom_field_18 TEXT(2000), custom_field_19 TEXT(2000), custom_field_20 TEXT(2000),
    custom_field_21 TEXT(2000), custom_field_22 TEXT(2000), custom_field_23 TEXT(2000), custom_field_24 TEXT(2000), custom_field_25 TEXT(2000),
    custom_field_26 TEXT(2000), custom_field_27 TEXT(2000), custom_field_28 TEXT(2000), custom_field_29 TEXT(2000), custom_field_30 TEXT(2000),
    custom_field_31 TEXT(2000), custom_field_32 TEXT(2000), custom_field_33 TEXT(2000), custom_field_34 TEXT(2000), custom_field_35 TEXT(2000),
    custom_field_36 TEXT(2000), custom_field_37 TEXT(2000), custom_field_38 TEXT(2000), custom_field_39 TEXT(2000), custom_field_40 TEXT(2000),
    custom_field_41 TEXT(2000), custom_field_42 TEXT(2000), custom_field_43 TEXT(2000), custom_field_44 TEXT(2000), custom_field_45 TEXT(2000),
    custom_field_46 TEXT(2000), custom_field_47 TEXT(2000), custom_field_48 TEXT(2000), custom_field_49 TEXT(2000), custom_field_50 TEXT(2000),
    custom_field_51 TEXT(2000), custom_field_52 TEXT(2000), custom_field_53 TEXT(2000), custom_field_54 TEXT(2000), custom_field_55 TEXT(2000),
    custom_field_56 TEXT(2000), custom_field_57 TEXT(2000), custom_field_58 TEXT(2000), custom_field_59 TEXT(2000), custom_field_60 TEXT(2000),
    custom_field_61 TEXT(2000), custom_field_62 TEXT(2000), custom_field_63 TEXT(2000), custom_field_64 TEXT(2000), custom_field_65 TEXT(2000),
    custom_field_66 TEXT(2000), custom_field_67 TEXT(2000), custom_field_68 TEXT(2000), custom_field_69 TEXT(2000), custom_field_70 TEXT(2000),
    custom_field_71 TEXT(2000), custom_field_72 TEXT(2000), custom_field_73 TEXT(2000), custom_field_74 TEXT(2000), custom_field_75 TEXT(2000),
    custom_field_76 TEXT(2000), custom_field_77 TEXT(2000), custom_field_78 TEXT(2000), custom_field_79 TEXT(2000), custom_field_80 TEXT(2000),
    custom_field_81 TEXT(2000), custom_field_82 TEXT(2000), custom_field_83 TEXT(2000), custom_field_84 TEXT(2000), custom_field_85 TEXT(2000),
    custom_field_86 TEXT(2000), custom_field_87 TEXT(2000), custom_field_88 TEXT(2000), custom_field_89 TEXT(2000), custom_field_90 TEXT(2000),
    custom_field_91 TEXT(2000), custom_field_92 TEXT(2000), custom_field_93 TEXT(2000), custom_field_94 TEXT(2000), custom_field_95 TEXT(2000),
    custom_field_96 TEXT(2000), custom_field_97 TEXT(2000), custom_field_98 TEXT(2000), custom_field_99 TEXT(2000), custom_field_100 TEXT(2000),
    custom_field_101 TEXT(2000), custom_field_102 TEXT(2000), custom_field_103 TEXT(2000), custom_field_104 TEXT(2000), custom_field_105 TEXT(2000),
    custom_field_106 TEXT(2000), custom_field_107 TEXT(2000), custom_field_108 TEXT(2000), custom_field_109 TEXT(2000), custom_field_110 TEXT(2000),
    custom_field_111 TEXT(2000), custom_field_112 TEXT(2000), custom_field_113 TEXT(2000), custom_field_114 TEXT(2000), custom_field_115 TEXT(2000),
    custom_field_116 TEXT(2000), custom_field_117 TEXT(2000), custom_field_118 TEXT(2000), custom_field_119 TEXT(2000), custom_field_120 TEXT(2000),
    custom_field_121 TEXT(2000), custom_field_122 TEXT(2000), custom_field_123 TEXT(2000), custom_field_124 TEXT(2000), custom_field_125 TEXT(2000),
    custom_field_126 TEXT(2000), custom_field_127 TEXT(2000), custom_field_128 TEXT(2000), custom_field_129 TEXT(2000), custom_field_130 TEXT(2000),
    custom_field_131 TEXT(2000), custom_field_132 TEXT(2000), custom_field_133 TEXT(2000), custom_field_134 TEXT(2000), custom_field_135 TEXT(2000),
    custom_field_136 TEXT(2000), custom_field_137 TEXT(2000), custom_field_138 TEXT(2000), custom_field_139 TEXT(2000), custom_field_140 TEXT(2000),
    custom_field_141 TEXT(2000), custom_field_142 TEXT(2000), custom_field_143 TEXT(2000), custom_field_144 TEXT(2000), custom_field_145 TEXT(2000),
    custom_field_146 TEXT(2000), custom_field_147 TEXT(2000), custom_field_148 TEXT(2000), custom_field_149 TEXT(2000), custom_field_150 TEXT(2000),
    custom_field_151 TEXT(2000), custom_field_152 TEXT(2000), custom_field_153 TEXT(2000), custom_field_154 TEXT(2000), custom_field_155 TEXT(2000),
    custom_field_156 TEXT(2000), custom_field_157 TEXT(2000), custom_field_158 TEXT(2000), custom_field_159 TEXT(2000), custom_field_160 TEXT(2000),
    custom_field_161 TEXT(2000), custom_field_162 TEXT(2000), custom_field_163 TEXT(2000), custom_field_164 TEXT(2000), custom_field_165 TEXT(2000),
    custom_field_166 TEXT(2000), custom_field_167 TEXT(2000), custom_field_168 TEXT(2000), custom_field_169 TEXT(2000), custom_field_170 TEXT(2000),
    custom_field_171 TEXT(2000), custom_field_172 TEXT(2000), custom_field_173 TEXT(2000), custom_field_174 TEXT(2000), custom_field_175 TEXT(2000),
    custom_field_176 TEXT(2000), custom_field_177 TEXT(2000), custom_field_178 TEXT(2000), custom_field_179 TEXT(2000), custom_field_180 TEXT(2000),
    custom_field_181 TEXT(2000), custom_field_182 TEXT(2000), custom_field_183 TEXT(2000), custom_field_184 TEXT(2000), custom_field_185 TEXT(2000),
    custom_field_186 TEXT(2000), custom_field_187 TEXT(2000), custom_field_188 TEXT(2000), custom_field_189 TEXT(2000), custom_field_190 TEXT(2000),
    custom_field_191 TEXT(2000), custom_field_192 TEXT(2000), custom_field_193 TEXT(2000), custom_field_194 TEXT(2000), custom_field_195 TEXT(2000),
    custom_field_196 TEXT(2000), custom_field_197 TEXT(2000), custom_field_198 TEXT(2000), custom_field_199 TEXT(2000), custom_field_200 TEXT(2000),
    custom_field_201 TEXT(2000), custom_field_202 TEXT(2000), custom_field_203 TEXT(2000), custom_field_204 TEXT(2000), custom_field_205 TEXT(2000),
    custom_field_206 TEXT(2000), custom_field_207 TEXT(2000), custom_field_208 TEXT(2000), custom_field_209 TEXT(2000), custom_field_210 TEXT(2000),
    custom_field_211 TEXT(2000), custom_field_212 TEXT(2000), custom_field_213 TEXT(2000), custom_field_214 TEXT(2000), custom_field_215 TEXT(2000),
    custom_field_216 TEXT(2000), custom_field_217 TEXT(2000), custom_field_218 TEXT(2000), custom_field_219 TEXT(2000), custom_field_220 TEXT(2000),
    custom_field_221 TEXT(2000), custom_field_222 TEXT(2000), custom_field_223 TEXT(2000), custom_field_224 TEXT(2000), custom_field_225 TEXT(2000),
    custom_field_226 TEXT(2000), custom_field_227 TEXT(2000), custom_field_228 TEXT(2000), custom_field_229 TEXT(2000), custom_field_230 TEXT(2000),
    custom_field_231 TEXT(2000), custom_field_232 TEXT(2000), custom_field_233 TEXT(2000), custom_field_234 TEXT(2000), custom_field_235 TEXT(2000),
    custom_field_236 TEXT(2000), custom_field_237 TEXT(2000), custom_field_238 TEXT(2000), custom_field_239 TEXT(2000), custom_field_240 TEXT(2000),
    custom_field_241 TEXT(2000), custom_field_242 TEXT(2000), custom_field_243 TEXT(2000), custom_field_244 TEXT(2000), custom_field_245 TEXT(2000),
    custom_field_246 TEXT(2000), custom_field_247 TEXT(2000), custom_field_248 TEXT(2000), custom_field_249 TEXT(2000), custom_field_250 TEXT(2000),
    FOREIGN KEY (entity_id) REFERENCES roles(id) ON DELETE CASCADE,
    UNIQUE KEY unique_roles_custom (entity_id)
);

CREATE TABLE IF NOT EXISTS permissions_custom_field (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    entity_id BIGINT NOT NULL,
    custom_field_1 TEXT(2000), custom_field_2 TEXT(2000), custom_field_3 TEXT(2000), custom_field_4 TEXT(2000), custom_field_5 TEXT(2000),
    custom_field_6 TEXT(2000), custom_field_7 TEXT(2000), custom_field_8 TEXT(2000), custom_field_9 TEXT(2000), custom_field_10 TEXT(2000),
    custom_field_11 TEXT(2000), custom_field_12 TEXT(2000), custom_field_13 TEXT(2000), custom_field_14 TEXT(2000), custom_field_15 TEXT(2000),
    custom_field_16 TEXT(2000), custom_field_17 TEXT(2000), custom_field_18 TEXT(2000), custom_field_19 TEXT(2000), custom_field_20 TEXT(2000),
    custom_field_21 TEXT(2000), custom_field_22 TEXT(2000), custom_field_23 TEXT(2000), custom_field_24 TEXT(2000), custom_field_25 TEXT(2000),
    custom_field_26 TEXT(2000), custom_field_27 TEXT(2000), custom_field_28 TEXT(2000), custom_field_29 TEXT(2000), custom_field_30 TEXT(2000),
    custom_field_31 TEXT(2000), custom_field_32 TEXT(2000), custom_field_33 TEXT(2000), custom_field_34 TEXT(2000), custom_field_35 TEXT(2000),
    custom_field_36 TEXT(2000), custom_field_37 TEXT(2000), custom_field_38 TEXT(2000), custom_field_39 TEXT(2000), custom_field_40 TEXT(2000),
    custom_field_41 TEXT(2000), custom_field_42 TEXT(2000), custom_field_43 TEXT(2000), custom_field_44 TEXT(2000), custom_field_45 TEXT(2000),
    custom_field_46 TEXT(2000), custom_field_47 TEXT(2000), custom_field_48 TEXT(2000), custom_field_49 TEXT(2000), custom_field_50 TEXT(2000),
    custom_field_51 TEXT(2000), custom_field_52 TEXT(2000), custom_field_53 TEXT(2000), custom_field_54 TEXT(2000), custom_field_55 TEXT(2000),
    custom_field_56 TEXT(2000), custom_field_57 TEXT(2000), custom_field_58 TEXT(2000), custom_field_59 TEXT(2000), custom_field_60 TEXT(2000),
    custom_field_61 TEXT(2000), custom_field_62 TEXT(2000), custom_field_63 TEXT(2000), custom_field_64 TEXT(2000), custom_field_65 TEXT(2000),
    custom_field_66 TEXT(2000), custom_field_67 TEXT(2000), custom_field_68 TEXT(2000), custom_field_69 TEXT(2000), custom_field_70 TEXT(2000),
    custom_field_71 TEXT(2000), custom_field_72 TEXT(2000), custom_field_73 TEXT(2000), custom_field_74 TEXT(2000), custom_field_75 TEXT(2000),
    custom_field_76 TEXT(2000), custom_field_77 TEXT(2000), custom_field_78 TEXT(2000), custom_field_79 TEXT(2000), custom_field_80 TEXT(2000),
    custom_field_81 TEXT(2000), custom_field_82 TEXT(2000), custom_field_83 TEXT(2000), custom_field_84 TEXT(2000), custom_field_85 TEXT(2000),
    custom_field_86 TEXT(2000), custom_field_87 TEXT(2000), custom_field_88 TEXT(2000), custom_field_89 TEXT(2000), custom_field_90 TEXT(2000),
    custom_field_91 TEXT(2000), custom_field_92 TEXT(2000), custom_field_93 TEXT(2000), custom_field_94 TEXT(2000), custom_field_95 TEXT(2000),
    custom_field_96 TEXT(2000), custom_field_97 TEXT(2000), custom_field_98 TEXT(2000), custom_field_99 TEXT(2000), custom_field_100 TEXT(2000),
    custom_field_101 TEXT(2000), custom_field_102 TEXT(2000), custom_field_103 TEXT(2000), custom_field_104 TEXT(2000), custom_field_105 TEXT(2000),
    custom_field_106 TEXT(2000), custom_field_107 TEXT(2000), custom_field_108 TEXT(2000), custom_field_109 TEXT(2000), custom_field_110 TEXT(2000),
    custom_field_111 TEXT(2000), custom_field_112 TEXT(2000), custom_field_113 TEXT(2000), custom_field_114 TEXT(2000), custom_field_115 TEXT(2000),
    custom_field_116 TEXT(2000), custom_field_117 TEXT(2000), custom_field_118 TEXT(2000), custom_field_119 TEXT(2000), custom_field_120 TEXT(2000),
    custom_field_121 TEXT(2000), custom_field_122 TEXT(2000), custom_field_123 TEXT(2000), custom_field_124 TEXT(2000), custom_field_125 TEXT(2000),
    custom_field_126 TEXT(2000), custom_field_127 TEXT(2000), custom_field_128 TEXT(2000), custom_field_129 TEXT(2000), custom_field_130 TEXT(2000),
    custom_field_131 TEXT(2000), custom_field_132 TEXT(2000), custom_field_133 TEXT(2000), custom_field_134 TEXT(2000), custom_field_135 TEXT(2000),
    custom_field_136 TEXT(2000), custom_field_137 TEXT(2000), custom_field_138 TEXT(2000), custom_field_139 TEXT(2000), custom_field_140 TEXT(2000),
    custom_field_141 TEXT(2000), custom_field_142 TEXT(2000), custom_field_143 TEXT(2000), custom_field_144 TEXT(2000), custom_field_145 TEXT(2000),
    custom_field_146 TEXT(2000), custom_field_147 TEXT(2000), custom_field_148 TEXT(2000), custom_field_149 TEXT(2000), custom_field_150 TEXT(2000),
    custom_field_151 TEXT(2000), custom_field_152 TEXT(2000), custom_field_153 TEXT(2000), custom_field_154 TEXT(2000), custom_field_155 TEXT(2000),
    custom_field_156 TEXT(2000), custom_field_157 TEXT(2000), custom_field_158 TEXT(2000), custom_field_159 TEXT(2000), custom_field_160 TEXT(2000),
    custom_field_161 TEXT(2000), custom_field_162 TEXT(2000), custom_field_163 TEXT(2000), custom_field_164 TEXT(2000), custom_field_165 TEXT(2000),
    custom_field_166 TEXT(2000), custom_field_167 TEXT(2000), custom_field_168 TEXT(2000), custom_field_169 TEXT(2000), custom_field_170 TEXT(2000),
    custom_field_171 TEXT(2000), custom_field_172 TEXT(2000), custom_field_173 TEXT(2000), custom_field_174 TEXT(2000), custom_field_175 TEXT(2000),
    custom_field_176 TEXT(2000), custom_field_177 TEXT(2000), custom_field_178 TEXT(2000), custom_field_179 TEXT(2000), custom_field_180 TEXT(2000),
    custom_field_181 TEXT(2000), custom_field_182 TEXT(2000), custom_field_183 TEXT(2000), custom_field_184 TEXT(2000), custom_field_185 TEXT(2000),
    custom_field_186 TEXT(2000), custom_field_187 TEXT(2000), custom_field_188 TEXT(2000), custom_field_189 TEXT(2000), custom_field_190 TEXT(2000),
    custom_field_191 TEXT(2000), custom_field_192 TEXT(2000), custom_field_193 TEXT(2000), custom_field_194 TEXT(2000), custom_field_195 TEXT(2000),
    custom_field_196 TEXT(2000), custom_field_197 TEXT(2000), custom_field_198 TEXT(2000), custom_field_199 TEXT(2000), custom_field_200 TEXT(2000),
    custom_field_201 TEXT(2000), custom_field_202 TEXT(2000), custom_field_203 TEXT(2000), custom_field_204 TEXT(2000), custom_field_205 TEXT(2000),
    custom_field_206 TEXT(2000), custom_field_207 TEXT(2000), custom_field_208 TEXT(2000), custom_field_209 TEXT(2000), custom_field_210 TEXT(2000),
    custom_field_211 TEXT(2000), custom_field_212 TEXT(2000), custom_field_213 TEXT(2000), custom_field_214 TEXT(2000), custom_field_215 TEXT(2000),
    custom_field_216 TEXT(2000), custom_field_217 TEXT(2000), custom_field_218 TEXT(2000), custom_field_219 TEXT(2000), custom_field_220 TEXT(2000),
    custom_field_221 TEXT(2000), custom_field_222 TEXT(2000), custom_field_223 TEXT(2000), custom_field_224 TEXT(2000), custom_field_225 TEXT(2000),
    custom_field_226 TEXT(2000), custom_field_227 TEXT(2000), custom_field_228 TEXT(2000), custom_field_229 TEXT(2000), custom_field_230 TEXT(2000),
    custom_field_231 TEXT(2000), custom_field_232 TEXT(2000), custom_field_233 TEXT(2000), custom_field_234 TEXT(2000), custom_field_235 TEXT(2000),
    custom_field_236 TEXT(2000), custom_field_237 TEXT(2000), custom_field_238 TEXT(2000), custom_field_239 TEXT(2000), custom_field_240 TEXT(2000),
    custom_field_241 TEXT(2000), custom_field_242 TEXT(2000), custom_field_243 TEXT(2000), custom_field_244 TEXT(2000), custom_field_245 TEXT(2000),
    custom_field_246 TEXT(2000), custom_field_247 TEXT(2000), custom_field_248 TEXT(2000), custom_field_249 TEXT(2000), custom_field_250 TEXT(2000),
    FOREIGN KEY (entity_id) REFERENCES permissions(id) ON DELETE CASCADE,
    UNIQUE KEY unique_permissions_custom (entity_id)
);

DELIMITER ;

-- Create indexes for better performance (create if not exists pattern)
-- MySQL does not support CREATE INDEX IF NOT EXISTS in older versions; use DROP/CREATE guarded statements if your environment needs it.
-- We'll attempt simple CREATE INDEX; if index exists this will be a no-op in many setups or will raise an error — adjust as needed for your DB.
CREATE INDEX idx_students_custom_entity ON students_custom_field(entity_id);
CREATE INDEX idx_parents_custom_entity ON parents_custom_field(entity_id);
CREATE INDEX idx_staff_custom_entity ON staff_custom_field(entity_id);
CREATE INDEX idx_health_records_custom_entity ON health_records_custom_field(entity_id);
CREATE INDEX idx_attendance_custom_entity ON attendance_custom_field(entity_id);
CREATE INDEX idx_organizations_custom_entity ON organizations_custom_field(entity_id);
CREATE INDEX idx_users_custom_entity ON users_custom_field(entity_id);
CREATE INDEX idx_email_templates_custom_entity ON email_templates_custom_field(entity_id);
CREATE INDEX idx_email_logs_custom_entity ON email_logs_custom_field(entity_id);
CREATE INDEX idx_roles_custom_entity ON roles_custom_field(entity_id);
CREATE INDEX idx_permissions_custom_entity ON permissions_custom_field(entity_id);

-- The SQL above uses a %s placeholder for the repeated list of columns; the placeholder will be replaced when applying the migration in environments that support templating.
-- For clarity and to keep this file manageable, create the explicit 250-column list programmatically when generating your final migration file, or request an explicit expansion from the repo maintainer.

