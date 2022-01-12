package poseteam.pose.domain;

//이미지 태그 추천을 위한 객체로 추천태그값과 분류제목을 포함한다.
public class HashTag {
    String tag;
    String title;

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return "HashTag{" +
                "tag='" + tag + '\'' +
                ", title='" + title + '\'' +
                '}';
    }
}
