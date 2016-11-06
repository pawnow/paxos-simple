package pl.edu.agh.iosr.utils;

public enum ApplicationEndpoints {
    ACCEPTOR_PROPOSE_URL("/acceptor/propose"),
    ACCEPTOR_ACCEPT_URL("/acceptor/accept"),
    LERNER_URL("/learner/learn"),
    ONLINE_URL("/utils/status"),
    PROPOSER_PROPOSE_URL("/proposer/propose"),
    PROPOSER_ACCEPT_URL("/proposer/accept");

    private final String endpoint;

    ApplicationEndpoints(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getEndpoint() {
        return endpoint;
    }

    @Override
    public String toString() {
        return endpoint;
    }
}
