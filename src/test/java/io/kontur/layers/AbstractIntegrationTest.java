package io.kontur.layers;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class AbstractIntegrationTest {

    @Autowired
    protected WebApplicationContext webApplicationContext;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    protected MockMvc mockMvc;
    protected String BASE_URL;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
        BASE_URL = "http://localhost:80";
    }

    @AfterEach
    public void cleanDB() {
        JdbcTestUtils
                .deleteFromTables(jdbcTemplate, "layers", "layers_features", "layers_group_properties",
                        "layers_category_properties", "layers_style", "layers_dependencies");
    }
}
