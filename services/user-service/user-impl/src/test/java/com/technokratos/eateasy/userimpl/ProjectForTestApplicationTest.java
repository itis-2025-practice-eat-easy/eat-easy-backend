package com.technokratos.eateasy.userimpl;

import com.technokratos.eateasy.userimpl.config.TestRestTemplateConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles(profiles = "test")
public class ProjectForTestApplicationTest {

    @Test
    void contextLoads(){

    }
}
