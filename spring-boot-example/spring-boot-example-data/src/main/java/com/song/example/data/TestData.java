package com.song.example.data;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.RequestScope;

@Service
@RequestScope
public class TestData {

    private List<String> s;
    public List<String> getS() {
        return s;
    }

    public TestData() {
        s = new ArrayList<>();
        s.add("Added in the constructor");
    }
}
