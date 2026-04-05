package de.hhu.thesis_jensclicker.helper.arch;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaMember;
import com.tngtech.archunit.core.domain.JavaModifier;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;
import org.springframework.util.StringUtils;

public class ClassHasGetterRule extends ArchCondition<JavaClass> {
    public static final ClassHasGetterRule THIS_CLASS_HAS_A_GETTER = new ClassHasGetterRule("This Class has a Getter");

    public ClassHasGetterRule(String description, Object... args) {
        super(description, args);
    }

    private static boolean isNotFinal(JavaMember member) {
        return !member.getModifiers().contains(JavaModifier.FINAL);
    }

    @Override
    public void check(JavaClass javaClass, ConditionEvents events) {
        javaClass.getFields().stream()
                .filter(ClassHasGetterRule::isNotFinal)
                .forEach(f -> {
                    var methodName = "get" + StringUtils.capitalize(f.getName());
                    var getter = javaClass.tryGetMethod(methodName);
                    if (!getter.isPresent()) {
                        events.add(SimpleConditionEvent.violated(f, "Field " + f.getFullName() + " has no Getter"));
                    } else {
                        events.add(SimpleConditionEvent.satisfied(f, "Field " + f.getFullName() + " has a Getter"));
                    }
                });
    }
}
