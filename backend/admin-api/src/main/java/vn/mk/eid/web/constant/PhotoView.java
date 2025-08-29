package vn.mk.eid.web.constant;

public enum PhotoView {
    LEFT_PROFILE(0, "LEFT_PROFILE"),
    FRONT(1, "FRONT"),
    RIGHT_PROFILE(2, "RIGHT_PROFILE");

    private final Integer id;
    private final String value;
    PhotoView(Integer id, String value) {
        this.id = id;
        this.value = value;
    }

    public Integer getId() {
        return id;
    }
    public String getValue() {
        return value;
    }

    public static String getValueById(Integer id) {
        for (PhotoView view : PhotoView.values()) {
            if (view.id.equals(id)) {
                return view.value;
            }
        }
        throw new IllegalArgumentException("Invalid PhotoView id: " + id);
    }

    public static PhotoView fromValue(String value) {
        for (PhotoView view : PhotoView.values()) {
            if (view.value.equalsIgnoreCase(value)) {
                return view;
            }
        }
        throw new IllegalArgumentException("Invalid PhotoView value: " + value);
    }
    public static PhotoView fromId(Integer id) {
        for (PhotoView view : PhotoView.values()) {
            if (view.id.equals(id)) {
                return view;
            }
        }
        throw new IllegalArgumentException("Invalid PhotoView id: " + id);
    }
}
