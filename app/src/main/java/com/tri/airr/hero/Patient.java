package com.tri.airr.hero;

public class Patient {
    private String id;
    private String name;
    public String metric1;
    private String metric2;
    private String metric3;

    public Patient(String id, String name, String metric1, String metric2, String metric3) {
        this.id = id;
        this.name = name;
        this.metric1 = metric1;
        this.metric2 = metric2;
        this.metric3 = metric3;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMetric1() {
        return metric1;
    }

    public void setMetric1(String metric1) {
        this.metric1 = metric1;
    }

    public String getMetric2() {
        return metric2;
    }

    public void setMetric2(String metric2) {
        this.metric2 = metric2;
    }

    public String getMetric3() {
        return metric3;
    }

    public void setMetric3(String metric3) {
        this.metric3 = metric3;
    }
}
