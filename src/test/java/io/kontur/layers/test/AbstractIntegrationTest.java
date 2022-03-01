package io.kontur.layers.test;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestSecurityConfig.class)
public abstract class AbstractIntegrationTest {

    @Autowired
    protected WebApplicationContext webApplicationContext;
    @Autowired
    protected JdbcTemplate jdbcTemplate;
    @Autowired
    protected PlatformTransactionManager transactionManager;

    protected MockMvc mockMvc;
    protected String BASE_URL;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext)
                .apply(springSecurity())
                .build();
        BASE_URL = "http://localhost";
    }

    @AfterEach
    public void cleanDB() {
        new TransactionTemplate(transactionManager)
                .execute(status ->
                        JdbcTestUtils.deleteFromTables(jdbcTemplate, "apps_layers", "apps",
                                "layers", "layers_features", "layers_group_properties", "layers_category_properties",
                                "layers_style", "layers_dependencies"));
    }
}
