package jakubw.pracainz.goalsexecutor;

public class CalendarEvent {
    private String title, id, description;
    private int hour;
    private int day;
    private int month;
    private int year;
    private int minute;

    public CalendarEvent(String title, String id, int hour, int day, int month, int year) {
        this.title = title;
        this.id = id;
        this.hour = hour;
        this.day = day;
        this.month = month;
        this.year = year;
    }

    public CalendarEvent() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
