package io.github.tolik2509.simplemenu.button;

public enum BaseButtonType implements ButtonKey {
    CONFIRM("base.confirm"),
    DENY("base.deny"),
    BACK("base.back"),
    NEXT("base.next");

    private final String path;

    BaseButtonType(String path) {
        this.path = path;
    }

    public String getPath() {
        return this.path;
    }
}
