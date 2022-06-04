package execution.common;


import execution.ResultsData;

public interface DataBinder {
    public void addResults(ResultsData resultsData);

    public void addProgress(int progress);
}
