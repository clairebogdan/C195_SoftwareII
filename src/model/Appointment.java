package model;


public class Appointment {
    
    String id, name, title, description, location, contact, type, url, date, start, end;
    
    public Appointment() {}
    
    public Appointment(String id, String name, String title, String description, String location, String contact, String type, String url, String date, String start, String end) {
        this.id = id;
        this.name = name;
        this.title = title;
        this.description = description;
        this.location = location;
        this.contact = contact;
        this.type = type;
        this.url = url;
        this.date = date;
        this.start = start;
        this.end = end;
    }
    
    
    

    //USED FOR CALENDAR / SIMPLE APPOINTMENT VIEW FOR MAIN SCREEN (does not require URL or Contact)
    public Appointment(String id, String name, String title, String description, String location, String type, String date, String start, String end) {
        this.id = id;
        this.name = name;
        this.title = title;
        this.description = description;
        this.location = location;
        this.type = type;
        this.date = date;
        this.start = start;
        this.end = end;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }
}