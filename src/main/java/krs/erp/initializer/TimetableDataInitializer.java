package krs.erp.initializer;

import java.io.InputStream;
import java.time.LocalTime;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import krs.erp.model.Timetable;
import krs.erp.model.Timetable.DayOfWeek;
import krs.erp.repository.TimetableRepository;

// @Component - Disabled for multi-tenant system
@Order(11)
public class TimetableDataInitializer implements CommandLineRunner {

    @Autowired
    private TimetableRepository timetableRepository;

    @Override
    public void run(String... args) throws Exception {
        if (timetableRepository.count() == 0) {
            loadTimetablesFromXML();
            System.out.println("Timetable data initialized successfully!");
        } else {
            System.out.println("Timetable data already exists. Skipping initialization.");
        }
    }

    private void loadTimetablesFromXML() throws Exception {
        InputStream xmlFile = getClass().getClassLoader().getResourceAsStream("data/timetable/timetables.xml");

        if (xmlFile == null) {
            System.err.println("timetables.xml file not found!");
            return;
        }

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(xmlFile);
        doc.getDocumentElement().normalize();

        NodeList timetableList = doc.getElementsByTagName("timetable");

        for (int i = 0; i < timetableList.getLength(); i++) {
            Element timetableElement = (Element) timetableList.item(i);

            Timetable timetable = new Timetable();
            timetable.setTimetableCode(getElementTextContent(timetableElement, "timetableCode"));
            timetable.setClassName(getElementTextContent(timetableElement, "className"));
            timetable.setGradeLevel(getElementTextContent(timetableElement, "gradeLevel"));
            timetable.setAcademicYear(getElementTextContent(timetableElement, "academicYear"));
            timetable.setSemester(getElementTextContent(timetableElement, "semester"));

            String dayOfWeekStr = getElementTextContent(timetableElement, "dayOfWeek");
            timetable.setDayOfWeek(DayOfWeek.valueOf(dayOfWeekStr));

            String startTimeStr = getElementTextContent(timetableElement, "startTime");
            timetable.setStartTime(LocalTime.parse(startTimeStr));

            String endTimeStr = getElementTextContent(timetableElement, "endTime");
            timetable.setEndTime(LocalTime.parse(endTimeStr));

            timetable.setSubjectName(getElementTextContent(timetableElement, "subjectName"));
            timetable.setSubjectCode(getElementTextContent(timetableElement, "subjectCode"));
            timetable.setTeacherName(getElementTextContent(timetableElement, "teacherName"));
            timetable.setTeacherId(getElementTextContent(timetableElement, "teacherId"));
            timetable.setRoomNumber(getElementTextContent(timetableElement, "roomNumber"));
            timetable.setBuilding(getElementTextContent(timetableElement, "building"));

            String periodNumberStr = getElementTextContent(timetableElement, "periodNumber");
            if (periodNumberStr != null && !periodNumberStr.isEmpty()) {
                timetable.setPeriodNumber(Integer.parseInt(periodNumberStr));
            }

            timetable.setNotes(getElementTextContent(timetableElement, "notes"));

            String isLabSessionStr = getElementTextContent(timetableElement, "isLabSession");
            if (isLabSessionStr != null && !isLabSessionStr.isEmpty()) {
                timetable.setIsLabSession(Boolean.parseBoolean(isLabSessionStr));
            }

            timetable.markAsActive();
            timetableRepository.save(timetable);
        }

        System.out.println("Loaded " + timetableList.getLength() + " timetable entries from XML.");
    }

    private String getElementTextContent(Element parent, String tagName) {
        NodeList nodeList = parent.getElementsByTagName(tagName);
        if (nodeList.getLength() > 0) {
            return nodeList.item(0).getTextContent();
        }
        return null;
    }
}
