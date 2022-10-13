package com.vercator.correvate.multifileapi;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
public class APIControllerTest {


    @Autowired
    private WebApplicationContext webApplicationContext;

    @Test
    public void testPost() throws Exception {

        // given - precondition or setup
        Resource test1 = new ClassPathResource("test1.txt");
        Resource test2 = new ClassPathResource("test2.txt");
        MockMultipartFile firstFile = new MockMultipartFile(
                "file", test1.getFilename(),
                MediaType.MULTIPART_FORM_DATA_VALUE,
                test1.getInputStream());

        MockMultipartFile secondFile = new MockMultipartFile(
                "file", test2.getFilename(),
                MediaType.MULTIPART_FORM_DATA_VALUE,
                test2.getInputStream());

        assertNotNull(firstFile);
        assertNotNull(secondFile);

        MockMvc mockMvc = MockMvcBuilders.
                webAppContextSetup(webApplicationContext).build();

        MvcResult andReturn = mockMvc.perform(MockMvcRequestBuilders
                .multipart("/zip")
                .file(firstFile)
                .file(secondFile))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/zip"))
                .andDo(MockMvcResultHandlers.print())
                .andReturn();
        System.out.println("andReturn = " + andReturn);
        // when - action or behaviour that we are going test


    }
}