package krs.erp.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import krs.erp.entity.ErpPlan;
import krs.erp.repository.ErpPlanRepository;

/**
 * Service for loading ERP plan definitions from XML configuration
 */
@Service
public class ErpPlanXmlLoaderService {

    private static final Logger logger = LoggerFactory.getLogger(ErpPlanXmlLoaderService.class);

    @Autowired
    private ErpPlanRepository erpPlanRepository;

    private static final String XML_FILE_PATTERN = "classpath:data/erp_plans.xml";

    /**
     * Load all plans from XML configuration
     */
    public void loadPlansFromXml() {
        try {
            logger.info("Loading ERP plan definitions from XML: {}", XML_FILE_PATTERN);

            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            Resource[] resources = resolver.getResources(XML_FILE_PATTERN);

            if (resources.length == 0) {
                logger.error("No XML files found matching pattern: {}", XML_FILE_PATTERN);
                return;
            }

            for (Resource resource : resources) {
                loadPlansFromResource(resource);
            }

        } catch (Exception e) {
            logger.error("Error loading plans from XML", e);
        }
    }

    /**
     * Load plans from a single XML resource
     */
    private void loadPlansFromResource(Resource resource) {
        try {
            logger.info("Loading plans from: {}", resource.getFilename());

            if (!resource.exists()) {
                logger.error("XML resource not found: {}", resource.getFilename());
                return;
            }

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(resource.getInputStream());
            document.getDocumentElement().normalize();

            NodeList planNodes = document.getElementsByTagName("plan");
            List<ErpPlan> plansToSave = new ArrayList<>();
            int updatedCount = 0;

            for (int i = 0; i < planNodes.getLength(); i++) {
                Element planElement = (Element) planNodes.item(i);
                ErpPlan plan = createOrUpdatePlanFromXml(planElement);
                if (plan != null) {
                    plansToSave.add(plan);
                    updatedCount++;
                }
            }

            if (!plansToSave.isEmpty()) {
                erpPlanRepository.saveAll(plansToSave);
                logger.info("Processed {} plans from {}", updatedCount, resource.getFilename());
            }

        } catch (Exception e) {
            logger.error("Error loading ERP plans from XML: {}", e.getMessage(), e);
        }
    }

    /**
     * Create or update ErpPlan entity from XML element
     */
    private ErpPlan createOrUpdatePlanFromXml(Element planElement) {
        try {
            String name = getElementText(planElement, "name");

            Optional<ErpPlan> existingPlanOpt = erpPlanRepository.findByName(name);
            ErpPlan plan = existingPlanOpt.orElse(new ErpPlan());

            plan.setName(name);
            plan.setType(getElementText(planElement, "type"));

            String amountStr = getElementText(planElement, "amount");
            if (amountStr != null) {
                plan.setAmount(new BigDecimal(amountStr));
            }

            plan.setCurrency(getElementText(planElement, "currency"));
            plan.setDescription(getElementText(planElement, "description"));
            plan.setIsActive(getElementTextAsBoolean(planElement, "is_active", true));

            return plan;

        } catch (Exception e) {
            logger.error("Error creating/updating plan from XML element: {}", e.getMessage());
            return null;
        }
    }

    private String getElementText(Element parent, String tagName) {
        NodeList nodeList = parent.getElementsByTagName(tagName);
        if (nodeList.getLength() > 0) {
            return nodeList.item(0).getTextContent().trim();
        }
        return null;
    }

    private Boolean getElementTextAsBoolean(Element parent, String tagName, boolean defaultValue) {
        String text = getElementText(parent, tagName);
        if (text != null && !text.isEmpty()) {
            return Boolean.parseBoolean(text);
        }
        return defaultValue;
    }
}
