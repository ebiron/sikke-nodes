package sikke.cli;

public enum RequestMethods {

	GET("GET"),
    POST("POST"),
    PUT("PUT"),
    DELETE("DELETE");
 
    private String url;
 
    RequestMethods(String requestMethod) {
        this.url = requestMethod;
    }
 
    public String getUrl() {
        return url;
    }

}
