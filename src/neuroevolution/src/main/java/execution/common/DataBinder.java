package execution.common;


import execution.ResultsData;

public interface DataBinder {
    void addResults(ResultsData resultsData);

    void addProgress(int progress);
}
