package com.bushmaster.architecture;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ArchitectureApplication.class)
@WebAppConfiguration
public class ScriptControllerTests {
    @Autowired
    private WebApplicationContext context;
    private MockMvc mock;

    @Before
    public void before() {
        mock = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @After
    public void after() {}
}
