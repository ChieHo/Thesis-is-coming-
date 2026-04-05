package de.hhu.thesis_jensclicker;

import de.hhu.thesis_jensclicker.helper.TestcontainersConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest()
@Import(TestcontainersConfiguration.class)
class ThesisJensclickerApplicationTests {

    @Test
    void contextLoads() {
    }

}
