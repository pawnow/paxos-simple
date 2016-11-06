package pl.edu.agh.iosr.utils;

public enum ApplicationEndpoints {
    ACCEPTOR_URL("/acceptor/accept"),
    LERNER_URL("/learner/learn"),
    ONLINE_URL("/utils/status"),
    PROPOSER_URL("/proposer/propose");

    private final String endpoint;

    ApplicationEndpoints(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getEndpoint() {
        return endpoint;
    }
}
