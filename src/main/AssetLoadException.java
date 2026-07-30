package main;

public class AssetLoadException extends Exception {
    private final String resourcePath;
    private final String requester;

    public AssetLoadException(String message, String resourcePath, String requester) {
        super(message);
        this.resourcePath = resourcePath;
        this.requester = requester;
    }

    public AssetLoadException(String message, String resourcePath, String requester, Throwable cause) {
        super(message, cause);
        this.resourcePath = resourcePath;
        this.requester = requester;
    }

    public String resourcePath() {
        return resourcePath;
    }

    public String requester() {
        return requester;
    }
}
