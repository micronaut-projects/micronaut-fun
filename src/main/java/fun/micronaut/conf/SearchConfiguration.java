package fun.micronaut.conf;

import io.micronaut.context.annotation.ConfigurationProperties;

@ConfigurationProperties("search")
public class SearchConfiguration {
    private Integer resultsPerPage = 10;

    public Integer getResultsPerPage() {
        return resultsPerPage;
    }

    public void setResultsPerPage(Integer resultsPerPage) {
        this.resultsPerPage = resultsPerPage;
    }
}
