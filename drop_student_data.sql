-- Drop student related data (respecting foreign key constraints)
DELETE FROM parent_student_relations;
DELETE FROM student_guardian_info;
DELETE FROM student_medical_info;
DELETE FROM students;
