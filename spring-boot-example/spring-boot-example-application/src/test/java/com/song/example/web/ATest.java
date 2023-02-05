package com.song.example.web;

import java.util.ArrayList;
import java.util.List;

import com.song.example.data.TestData;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import io.restassured.RestAssured;
import io.restassured.response.Response;

@ExtendWith(SpringExtension.class)
@TestPropertySource(locations = "classpath:application.properties")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ATest {

    @LocalServerPort
    private int port;

    @MockBean(name = "testData")
    private TestData testData;

    @Test
    void basicTest() {

        final List<String> s = new ArrayList<>();
        s.add("B");

        Mockito.when(testData.getS()).thenReturn(s);

        final String URL = "http://localhost:" + this.port + "/test/";

        System.out.println(URL);
        Response res = RestAssured.get(URL);

        System.out.println(res.asString());
        var result = res.as(List.class);

        
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals("B", result.get(0));

    }

}
