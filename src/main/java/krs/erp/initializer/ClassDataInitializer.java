package krs.erp.initializer;

import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import krs.erp.model.ErpClass;
import krs.erp.repository.ErpClassRepository;

/**
 * Initializer for loading Class sample data from XML
 */
public class ClassDataInitializer {

    private static final Logger logger = LoggerFactory.getLogger(ClassDataInitializer.class);

    @Autowired
    private ErpClassRepository classRepository;

    /**
     * Load class data from XML file
     * 
     * @param xmlFilePath Path to the XML file in classpath
     */
    public void loadClassData(String xmlFilePath) {
        try {
            logger.info("Loading class data from: {}", xmlFilePath);

            // Check if data already exists
            long existingCount = classRepository.count();
            if (existingCount > 0) {
                logger.info("Class data already exists ({} records). Skipping import.", existingCount);
                return;
            }

            // Load and parse XML
            ClassPathResource resource = new ClassPathResource(xmlFilePath);
            InputStream inputStream = resource.getInputStream();

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(inputStream);
            doc.getDocumentElement().normalize();

            NodeList nodeList = doc.getElementsByTagName("class");
            int importedCount = 0;

            for (int i = 0; i < nodeList.getLength(); i++) {
                Element element = (Element) nodeList.item(i);

                ErpClass erpClass = new ErpClass();
                erpClass.setClassCode(getElementValue(element, "classCode"));
                erpClass.setClassName(getElementValue(element, "className"));
                erpClass.setGradeLevel(getElementValue(element, "gradeLevel"));
                erpClass.setSection(getElementValue(element, "section"));
                erpClass.setAcademicYear(getElementValue(element, "academicYear"));

                String capacityStr = getElementValue(element, "capacity");
                if (capacityStr != null && !capacityStr.isEmpty()) {
                    erpClass.setCapacity(Integer.parseInt(capacityStr));
                }

                erpClass.setRoomNumber(getElementValue(element, "roomNumber"));
                erpClass.setDescription(getElementValue(element, "description"));

                String isActiveStr = getElementValue(element, "isActive");
                if ("true".equalsIgnoreCase(isActiveStr)) {
                    erpClass.markAsActive();
                } else {
                    erpClass.markAsDeleted();
                }

                classRepository.save(erpClass);
                importedCount++;
            }

            logger.info("Successfully imported {} classes from {}", importedCount, xmlFilePath);

        } catch (Exception e) {
            logger.error("Error loading class data from {}: {}", xmlFilePath, e.getMessage(), e);
            throw new RuntimeException("Failed to load class data", e);
        }
    }

    /**
     * Get element value from XML element
     */
    private String getElementValue(Element parent, String tagName) {
        NodeList nodeList = parent.getElementsByTagName(tagName);
        if (nodeList != null && nodeList.getLength() > 0) {
            Element element = (Element) nodeList.item(0);
            if (element != null && element.getFirstChild() != null) {
                return element.getFirstChild().getNodeValue();
            }
        }
        return null;
    }
}
