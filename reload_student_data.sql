-- Script to drop and reload student data
-- Run this script to clean up problematic student data and reload fresh data

-- Drop student related data (respecting foreign key constraints)
DELETE FROM student_medical_info;
DELETE FROM student_guardian;
DELETE FROM students;

-- Note: After running this script, restart the Spring Boot application
-- to reload student data from the XML files in src/main/resources/data/student/
