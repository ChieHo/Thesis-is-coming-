package de.hhu.thesis_jensclicker.test.archtest;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.library.GeneralCodingRules;
import de.hhu.thesis_jensclicker.helper.arch.ClassHasGetterRule;
import de.hhu.thesis_jensclicker.helper.arch.ClassHasSetterRule;
import de.propra.profil.ProfilApplication;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.Architectures.onionArchitecture;

@AnalyzeClasses(packagesOf = ProfilApplication.class, importOptions = ImportOption.DoNotIncludeTests.class)
public class ProfilArchUnitTest {

    @ArchTest
    ArchRule noMembersShouldBeAutowired = GeneralCodingRules.NO_CLASSES_SHOULD_USE_FIELD_INJECTION;

    @ArchTest
    ArchRule noMembersShouldBeAutowiredMetaAnnotation = classes().should().notBeMetaAnnotatedWith(Autowired.class);

    @ArchTest
    ArchRule everyMemberShouldBeServiced = classes().that().resideInAPackage("..application.service..").should().beAnnotatedWith(Service.class);


    @ArchTest
    ArchRule onlyControllerAccesControllersVarianteB = classes().that().areNotAnnotatedWith(Controller.class).should().dependOnClassesThat().areNotAnnotatedWith(Controller.class);

    @ArchTest
    ArchRule setterRule = classes().that().resideInAPackage("..domain.model..").should(ClassHasSetterRule.THIS_CLASS_HAS_A_SETTER);

    @ArchTest
    ArchRule getterRule = classes().that().resideInAPackage("..domain.model..").should(ClassHasGetterRule.THIS_CLASS_HAS_A_GETTER);

    @ArchTest
    ArchRule noSetterRule = noClasses().that().resideInAPackage("..persistence.dto..").should(ClassHasSetterRule.THIS_CLASS_HAS_A_SETTER);

    @ArchTest
    ArchRule noGetterRule = noClasses().that().resideInAPackage("..persistence.dto..").should(ClassHasGetterRule.THIS_CLASS_HAS_A_GETTER);

    @Test
    @DisplayName("Anwendung hat eine Onion-(Zwiebel)-Architektur")
    void onion_test() {
        ArchRule rule = onionArchitecture()
                .domainModels("..domain..")
                .applicationServices("..application..")
                .adapter("web", "..web..")
                .adapter("persistence", "..persistence..");
    }
}
