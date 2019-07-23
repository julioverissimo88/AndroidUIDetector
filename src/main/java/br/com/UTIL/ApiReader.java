package br.com.UTIL;

import java.util.List;

public class ApiReader {

    private String api;
    private String version;

    private List<String> classesInterfacesExceptionsEnum;

    public String getApi() {
        return api;
    }

    public void setApi(String api) {
        this.api = api;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public List<String> getClassesInterfacesExceptionsEnum() {
        return classesInterfacesExceptionsEnum;
    }

    public void setClassesInterfacesExceptionsEnum(List<String> classesInterfacesExceptionsEnum) {
        this.classesInterfacesExceptionsEnum = classesInterfacesExceptionsEnum;
    }
}
