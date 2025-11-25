package krs.erp.service;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.fasterxml.jackson.databind.ObjectMapper;

import krs.erp.model.PricingPlan;
import krs.erp.model.enums.PlanType;
import krs.erp.repository.PricingPlanRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Data loader for pricing plans from XML file
 * Runs on application startup to populate pricing plans
 */
@Component
@Order(100) // Run after other loaders
@RequiredArgsConstructor
@Slf4j
public class PricingPlanDataLoader implements CommandLineRunner {

    private final PricingPlanRepository pricingPlanRepository;
    private final ObjectMapper objectMapper;

    @Override
    public void run(String... args) throws Exception {
        log.info("=== Starting Pricing Plans Data Loader ===");

        // Check if plans already exist
        long existingCount = pricingPlanRepository.count();
        if (existingCount > 0) {
            log.info("Pricing plans already exist ({}). Skipping XML import.", existingCount);
            return;
        }

        try {
            loadPricingPlansFromXml();
            log.info("=== Pricing Plans Data Loader Completed Successfully ===");
        } catch (Exception e) {
            log.error("Failed to load pricing plans from XML", e);
            throw e;
        }
    }

    private void loadPricingPlansFromXml() throws Exception {
        log.info("Loading pricing plans from XML file...");

        InputStream inputStream = getClass().getClassLoader()
                .getResourceAsStream("data/subscription/pricing-plans.xml");

        if (inputStream == null) {
            log.error("Could not find pricing-plans.xml in classpath");
            return;
        }

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(inputStream);
        document.getDocumentElement().normalize();

        NodeList planNodes = document.getElementsByTagName("plan");
        log.info("Found {} pricing plans to load", planNodes.getLength());

        int loadedCount = 0;
        for (int i = 0; i < planNodes.getLength(); i++) {
            Element planElement = (Element) planNodes.item(i);
            
            try {
                PricingPlan plan = parsePlanFromElement(planElement);
                pricingPlanRepository.save(plan);
                loadedCount++;
                log.info("Loaded pricing plan: {} ({})", plan.getDisplayName(), plan.getPlanType());
            } catch (Exception e) {
                log.error("Failed to parse plan at index {}", i, e);
            }
        }

        log.info("Successfully loaded {} pricing plans", loadedCount);
    }

    private PricingPlan parsePlanFromElement(Element element) throws Exception {
        PricingPlan plan = new PricingPlan();

        plan.setPlanName(getTextContent(element, "planName"));
        plan.setPlanType(PlanType.valueOf(getTextContent(element, "planType")));
        plan.setDisplayName(getTextContent(element, "displayName"));
        plan.setDescription(getTextContent(element, "description"));
        
        plan.setPriceMonthly(new BigDecimal(getTextContent(element, "priceMonthly")));
        plan.setPriceYearly(new BigDecimal(getTextContent(element, "priceYearly")));
        
        String maxUsersStr = getTextContent(element, "maxUsers");
        plan.setMaxUsers("-1".equals(maxUsersStr) ? null : Integer.parseInt(maxUsersStr));
        
        String maxStorageStr = getTextContent(element, "maxStorageGb");
        plan.setMaxStorageGb("-1".equals(maxStorageStr) ? null : Integer.parseInt(maxStorageStr));
        
        plan.setIsActive(Boolean.parseBoolean(getTextContent(element, "isActive")));
        plan.setIsTrialEligible(Boolean.parseBoolean(getTextContent(element, "isTrialEligible")));
        plan.setTrialDays(Integer.parseInt(getTextContent(element, "trialDays")));

        // Parse features as JSON array
        List<String> features = parseFeatures(element);
        plan.setFeatures(objectMapper.writeValueAsString(features));

        return plan;
    }

    private List<String> parseFeatures(Element planElement) {
        List<String> features = new ArrayList<>();
        
        NodeList featuresNodes = planElement.getElementsByTagName("features");
        if (featuresNodes.getLength() > 0) {
            Element featuresElement = (Element) featuresNodes.item(0);
            NodeList featureNodes = featuresElement.getElementsByTagName("feature");
            
            for (int i = 0; i < featureNodes.getLength(); i++) {
                String feature = featureNodes.item(i).getTextContent().trim();
                if (!feature.isEmpty()) {
                    features.add(feature);
                }
            }
        }
        
        return features;
    }

    private String getTextContent(Element element, String tagName) {
        NodeList nodeList = element.getElementsByTagName(tagName);
        if (nodeList.getLength() > 0) {
            return nodeList.item(0).getTextContent().trim();
        }
        return "";
    }
}
