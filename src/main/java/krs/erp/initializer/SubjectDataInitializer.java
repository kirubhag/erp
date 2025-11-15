package krs.erp.initializer;

import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import krs.erp.model.Subject;
import krs.erp.repository.SubjectRepository;

/**
 * Initializes Subject data from subjects.xml on application startup
 */
// @Component  // Disabled - schema mismatch issues
@Order(6)
public class SubjectDataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(SubjectDataInitializer.class);

    @Autowired
    private SubjectRepository subjectRepository;

    @Override
    public void run(String... args) throws Exception {
        // Check if subjects already exist
        if (subjectRepository.count() > 0) {
            logger.info("Subjects already exist in database. Skipping initialization.");
            return;
        }

        logger.info("Loading subjects from subjects.xml...");
        List<Subject> subjects = loadSubjectsFromXml();
        
        if (!subjects.isEmpty()) {
            subjectRepository.saveAll(subjects);
            logger.info("Successfully loaded {} subjects into database", subjects.size());
        } else {
            logger.warn("No subjects loaded from XML file");
        }
    }

    private List<Subject> loadSubjectsFromXml() {
        List<Subject> subjects = new ArrayList<>();
        
        try (var inputStream = new ClassPathResource("data/subject/subjects.xml").getInputStream()) {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(inputStream);
            document.getDocumentElement().normalize();

            NodeList subjectNodes = document.getElementsByTagName("subject");
            
            for (int i = 0; i < subjectNodes.getLength(); i++) {
                Element element = (Element) subjectNodes.item(i);
                Subject subject = parseSubject(element);
                if (subject != null) {
                    subjects.add(subject);
                }
            }
        } catch (Exception e) {
            logger.error("Error loading subjects from XML", e);
        }
        
        return subjects;
    }

    private Subject parseSubject(Element element) {
        try {
            Subject subject = new Subject();
            
            subject.setSubjectCode(getElementText(element, "subjectCode"));
            subject.setSubjectName(getElementText(element, "subjectName"));
            subject.setDescription(getElementText(element, "description"));
            subject.setGradeLevel(getElementText(element, "gradeLevel"));
            subject.setCategory(getElementText(element, "category"));
            
            String creditsStr = getElementText(element, "credits");
            if (creditsStr != null && !creditsStr.isEmpty()) {
                subject.setCredits(Integer.parseInt(creditsStr));
            }
            
            String isMandatoryStr = getElementText(element, "isMandatory");
            if (isMandatoryStr != null && !isMandatoryStr.isEmpty()) {
                subject.setIsMandatory(Boolean.parseBoolean(isMandatoryStr));
            }
            
            subject.setPrerequisites(getElementText(element, "prerequisites"));
            subject.setDifficultyLevel(getElementText(element, "difficultyLevel"));
            
            String isActiveStr = getElementText(element, "isActive");
            if (isActiveStr != null && !isActiveStr.isEmpty()) {
                subject.setIsActive(Boolean.parseBoolean(isActiveStr) ? 1 : 0);
            } else {
                subject.markAsActive(); // Default to active (1)
            }
            
            return subject;
        } catch (Exception e) {
            logger.error("Error parsing subject element", e);
            return null;
        }
    }

    private String getElementText(Element parent, String tagName) {
        NodeList nodeList = parent.getElementsByTagName(tagName);
        if (nodeList.getLength() > 0) {
            return nodeList.item(0).getTextContent();
        }
        return null;
    }
}
