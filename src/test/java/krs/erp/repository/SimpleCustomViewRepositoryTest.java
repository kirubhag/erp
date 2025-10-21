package krs.erp.repository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import krs.erp.enums.EntityType;
import krs.erp.model.CustomView;

@DataJpaTest
@ActiveProfiles("test")
class SimpleCustomViewRepositoryTest {

    @Autowired
    private CustomViewRepository customViewRepository;

    @Test
    void testSaveAndFindCustomView() {
        // Given
        CustomView view = new CustomView();
        view.setViewName("Test View");
        view.setDescription("Test Description");
        view.setEntityType(EntityType.STUDENT);
        view.setSelectedFields(Arrays.asList("name", "email"));
        view.setCreatedByUser("testuser");
        view.setIsDefault(false);
        view.setIsPublic(true);

        // When
        CustomView savedView = customViewRepository.save(view);
        
        // Then
        assertThat(savedView.getId()).isNotNull();
        assertThat(savedView.getViewName()).isEqualTo("Test View");
        assertThat(savedView.getEntityType()).isEqualTo(EntityType.STUDENT);
        assertThat(savedView.getSelectedFields()).containsExactly("name", "email");
    }

    @Test
    void testFindByEntityType() {
        // Given
        CustomView view1 = new CustomView();
        view1.setViewName("Student View 1");
        view1.setEntityType(EntityType.STUDENT);
        view1.setSelectedFields(Arrays.asList("name"));
        view1.setCreatedByUser("user1");
        
        CustomView view2 = new CustomView();
        view2.setViewName("Student View 2");
        view2.setEntityType(EntityType.STUDENT);
        view2.setSelectedFields(Arrays.asList("email"));
        view2.setCreatedByUser("user2");
        
        CustomView view3 = new CustomView();
        view3.setViewName("Parent View");
        view3.setEntityType(EntityType.PARENT);
        view3.setSelectedFields(Arrays.asList("phone"));
        view3.setCreatedByUser("user3");

        customViewRepository.save(view1);
        customViewRepository.save(view2);
        customViewRepository.save(view3);

        // When
        List<CustomView> studentViews = customViewRepository.findByEntityType(EntityType.STUDENT);
        List<CustomView> parentViews = customViewRepository.findByEntityType(EntityType.PARENT);

        // Then
        assertThat(studentViews).hasSize(2);
        assertThat(parentViews).hasSize(1);
        assertThat(studentViews.get(0).getEntityType()).isEqualTo(EntityType.STUDENT);
        assertThat(parentViews.get(0).getEntityType()).isEqualTo(EntityType.PARENT);
    }

    @Test
    void testFindByEntityTypeAndIsDefaultTrue() {
        // Given
        CustomView defaultView = new CustomView();
        defaultView.setViewName("Default Student View");
        defaultView.setEntityType(EntityType.STUDENT);
        defaultView.setSelectedFields(Arrays.asList("name"));
        defaultView.setCreatedByUser("admin");
        defaultView.setIsDefault(true);
        
        CustomView customView = new CustomView();
        customView.setViewName("Custom Student View");
        customView.setEntityType(EntityType.STUDENT);
        customView.setSelectedFields(Arrays.asList("email"));
        customView.setCreatedByUser("user1");
        customView.setIsDefault(false);

        customViewRepository.save(defaultView);
        customViewRepository.save(customView);

        // When
        Optional<CustomView> defaultStudentView = customViewRepository.findByEntityTypeAndIsDefaultTrue(EntityType.STUDENT);

        // Then
        assertThat(defaultStudentView).isPresent();
        assertThat(defaultStudentView.get().getViewName()).isEqualTo("Default Student View");
        assertThat(defaultStudentView.get().getIsDefault()).isTrue();
    }
}