package de.hhu.thesis_jensclicker.helper.arch;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaMember;
import com.tngtech.archunit.core.domain.JavaModifier;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;
import org.springframework.util.StringUtils;

public class ClassHasSetterRule extends ArchCondition<JavaClass> {

    public static final ClassHasSetterRule THIS_CLASS_HAS_A_SETTER =
            new ClassHasSetterRule("This Class has a Setter");


    public ClassHasSetterRule(String description, Object... args) {
        super(description, args);
    }

    private static boolean isNotFinal(JavaMember member) {
        return !member.getModifiers().contains(JavaModifier.FINAL);
    }


    @Override
    public void check(JavaClass javaClass, ConditionEvents events) {
        javaClass.getFields().stream()
                .filter(ClassHasSetterRule::isNotFinal)
                .forEach(f -> {
                    var methodName = "set" + StringUtils.capitalize(f.getName());
                    var parameterClass = f.getRawType().reflect();
                    var setter = javaClass.tryGetMethod(methodName, parameterClass);
                    if (!setter.isPresent()) {
                        events.add(SimpleConditionEvent.violated(f, "Field " + f.getFullName() + " has no Setter"));
                    } else {
                        events.add(SimpleConditionEvent.satisfied(f, "Field " + f.getFullName() + " has a Setter"));
                    }
                });
    }
}