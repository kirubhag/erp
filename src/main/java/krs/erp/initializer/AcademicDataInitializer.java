package krs.erp.initializer;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import krs.erp.model.academic.AcademicYear;
import krs.erp.model.academic.GradingScale;
import krs.erp.model.academic.Term;
import krs.erp.repository.academic.AcademicYearRepository;
import krs.erp.repository.academic.GradingScaleRepository;
import krs.erp.repository.academic.TermRepository;

/**
 * Data initializer for Academic entities
 * Loads academic years, terms, and grading scales from XML files
 */
@Component
@Order(8)
public class AcademicDataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(AcademicDataInitializer.class);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Autowired
    private AcademicYearRepository academicYearRepository;

    @Autowired
    private TermRepository termRepository;

    @Autowired
    private GradingScaleRepository gradingScaleRepository;

    @Override
    public void run(String... args) throws Exception {
        logger.info("Starting Academic data initialization...");

        loadAcademicYears();
        loadTerms();
        loadGradingScales();

        logger.info("Academic data initialization completed.");
    }

    /**
     * Load academic years from XML file
     */
    private void loadAcademicYears() {
        try {
            InputStream inputStream = getClass().getClassLoader()
                    .getResourceAsStream("data/academic/academic-years.xml");

            if (inputStream == null) {
                logger.warn("Academic years XML file not found. Skipping academic years initialization.");
                return;
            }

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(inputStream);
            document.getDocumentElement().normalize();

            NodeList yearList = document.getElementsByTagName("academicYear");
            int totalCount = yearList.getLength();
            int loadedCount = 0;
            int skippedCount = 0;

            Set<String> processedKeys = new HashSet<>();

            for (int i = 0; i < yearList.getLength(); i++) {
                Element yearElement = (Element) yearList.item(i);

                String name = yearElement.getElementsByTagName("name").item(0).getTextContent();
                String startDateStr = yearElement.getElementsByTagName("startDate").item(0).getTextContent();
                String endDateStr = yearElement.getElementsByTagName("endDate").item(0).getTextContent();
                String isActiveStr = yearElement.getElementsByTagName("isActive").item(0).getTextContent();
                String organizationIdStr = yearElement.getElementsByTagName("organizationId").item(0).getTextContent();

                Long organizationId = Long.parseLong(organizationIdStr);
                String uniqueKey = name + "_" + organizationId;

                if (processedKeys.contains(uniqueKey)) {
                    logger.debug("Skipping duplicate academic year: {}", name);
                    skippedCount++;
                    continue;
                }

                // Check if academic year already exists
                Optional<AcademicYear> existingYear = academicYearRepository
                        .findByOrganizationIdOrderByStartDateDesc(organizationId)
                        .stream()
                        .filter(year -> year.getName().equals(name))
                        .findFirst();

                if (existingYear.isPresent()) {
                    logger.debug("Academic year already exists: {}", name);
                    skippedCount++;
                    processedKeys.add(uniqueKey);
                    continue;
                }

                AcademicYear academicYear = new AcademicYear();
                academicYear.setName(name);
                academicYear.setStartDate(LocalDate.parse(startDateStr, DATE_FORMATTER));
                academicYear.setEndDate(LocalDate.parse(endDateStr, DATE_FORMATTER));
                academicYear.setIsActive(Boolean.parseBoolean(isActiveStr));
                academicYear.setOrganizationId(organizationId);

                academicYearRepository.save(academicYear);
                processedKeys.add(uniqueKey);
                loadedCount++;
                logger.debug("Loaded academic year: {}", name);
            }

            logger.info("Academic Years - Total: {}, Loaded: {}, Skipped: {}", totalCount, loadedCount, skippedCount);

        } catch (Exception e) {
            logger.error("Error loading academic years: {}", e.getMessage(), e);
        }
    }

    /**
     * Load terms from XML file
     */
    private void loadTerms() {
        try {
            InputStream inputStream = getClass().getClassLoader()
                    .getResourceAsStream("data/academic/terms.xml");

            if (inputStream == null) {
                logger.warn("Terms XML file not found. Skipping terms initialization.");
                return;
            }

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(inputStream);
            document.getDocumentElement().normalize();

            NodeList termList = document.getElementsByTagName("term");
            int totalCount = termList.getLength();
            int loadedCount = 0;
            int skippedCount = 0;

            Set<String> processedKeys = new HashSet<>();

            for (int i = 0; i < termList.getLength(); i++) {
                Element termElement = (Element) termList.item(i);

                String name = termElement.getElementsByTagName("name").item(0).getTextContent();
                String startDateStr = termElement.getElementsByTagName("startDate").item(0).getTextContent();
                String endDateStr = termElement.getElementsByTagName("endDate").item(0).getTextContent();
                String academicYearName = termElement.getElementsByTagName("academicYearName").item(0).getTextContent();
                String organizationIdStr = termElement.getElementsByTagName("organizationId").item(0).getTextContent();

                Long organizationId = Long.parseLong(organizationIdStr);

                // Find academic year by name and organization
                Optional<AcademicYear> academicYear = academicYearRepository
                        .findByOrganizationIdOrderByStartDateDesc(organizationId)
                        .stream()
                        .filter(year -> year.getName().equals(academicYearName))
                        .findFirst();

                if (!academicYear.isPresent()) {
                    logger.warn("Academic year not found for term: {}. Skipping.", name);
                    skippedCount++;
                    continue;
                }

                String uniqueKey = name + "_" + academicYear.get().getId();

                if (processedKeys.contains(uniqueKey)) {
                    logger.debug("Skipping duplicate term: {}", name);
                    skippedCount++;
                    continue;
                }

                // Check if term already exists
                Optional<Term> existingTerm = termRepository
                        .findByAcademicYearIdOrderByStartDateAsc(academicYear.get().getId())
                        .stream()
                        .filter(term -> term.getName().equals(name))
                        .findFirst();

                if (existingTerm.isPresent()) {
                    logger.debug("Term already exists: {}", name);
                    skippedCount++;
                    processedKeys.add(uniqueKey);
                    continue;
                }

                Term term = new Term();
                term.setName(name);
                term.setStartDate(LocalDate.parse(startDateStr, DATE_FORMATTER));
                term.setEndDate(LocalDate.parse(endDateStr, DATE_FORMATTER));
                term.setAcademicYearId(academicYear.get().getId());
                term.setOrganizationId(organizationId);

                termRepository.save(term);
                processedKeys.add(uniqueKey);
                loadedCount++;
                logger.debug("Loaded term: {}", name);
            }

            logger.info("Terms - Total: {}, Loaded: {}, Skipped: {}", totalCount, loadedCount, skippedCount);

        } catch (Exception e) {
            logger.error("Error loading terms: {}", e.getMessage(), e);
        }
    }

    /**
     * Load grading scales from XML file
     */
    private void loadGradingScales() {
        try {
            InputStream inputStream = getClass().getClassLoader()
                    .getResourceAsStream("data/academic/grading-scales.xml");

            if (inputStream == null) {
                logger.warn("Grading scales XML file not found. Skipping grading scales initialization.");
                return;
            }

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(inputStream);
            document.getDocumentElement().normalize();

            NodeList scaleList = document.getElementsByTagName("gradingScale");
            int totalCount = scaleList.getLength();
            int loadedCount = 0;
            int skippedCount = 0;

            Set<String> processedKeys = new HashSet<>();

            for (int i = 0; i < scaleList.getLength(); i++) {
                Element scaleElement = (Element) scaleList.item(i);

                String name = scaleElement.getElementsByTagName("name").item(0).getTextContent();
                String letterGrade = scaleElement.getElementsByTagName("letterGrade").item(0).getTextContent();
                String minPercentageStr = scaleElement.getElementsByTagName("minPercentage").item(0).getTextContent();
                String maxPercentageStr = scaleElement.getElementsByTagName("maxPercentage").item(0).getTextContent();
                String gradePointStr = scaleElement.getElementsByTagName("gradePoint").item(0).getTextContent();
                String organizationIdStr = scaleElement.getElementsByTagName("organizationId").item(0).getTextContent();

                Long organizationId = Long.parseLong(organizationIdStr);
                String uniqueKey = letterGrade + "_" + organizationId;

                if (processedKeys.contains(uniqueKey)) {
                    logger.debug("Skipping duplicate grading scale: {}", letterGrade);
                    skippedCount++;
                    continue;
                }

                // Check if grading scale already exists
                Optional<GradingScale> existingScale = gradingScaleRepository
                        .findByOrganizationIdOrderByMinPercentageDesc(organizationId)
                        .stream()
                        .filter(scale -> scale.getLetterGrade().equals(letterGrade))
                        .findFirst();

                if (existingScale.isPresent()) {
                    logger.debug("Grading scale already exists: {}", letterGrade);
                    skippedCount++;
                    processedKeys.add(uniqueKey);
                    continue;
                }

                GradingScale gradingScale = new GradingScale();
                gradingScale.setName(name);
                gradingScale.setLetterGrade(letterGrade);
                gradingScale.setMinPercentage(Double.parseDouble(minPercentageStr));
                gradingScale.setMaxPercentage(Double.parseDouble(maxPercentageStr));
                gradingScale.setGradePoint(Double.parseDouble(gradePointStr));
                gradingScale.setOrganizationId(organizationId);

                gradingScaleRepository.save(gradingScale);
                processedKeys.add(uniqueKey);
                loadedCount++;
                logger.debug("Loaded grading scale: {}", letterGrade);
            }

            logger.info("Grading Scales - Total: {}, Loaded: {}, Skipped: {}", totalCount, loadedCount, skippedCount);

        } catch (Exception e) {
            logger.error("Error loading grading scales: {}", e.getMessage(), e);
        }
    }
}
