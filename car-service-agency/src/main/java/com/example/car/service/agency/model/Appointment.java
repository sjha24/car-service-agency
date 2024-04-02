package com.example.car.service.agency.model;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

@NoArgsConstructor
@Data
public class Appointment {
    private String id;
    private String operatorId;
    private int startHour;
    private int endHour;

    public Appointment(String operatorId, int startHour, int endHour) {
        if (endHour <= startHour) {
            throw new IllegalArgumentException("End hour must be greater than start hour");
        }
        this.id = UUID.randomUUID().toString();
        this.operatorId = operatorId;
        this.startHour = startHour;
        this.endHour = endHour;
    }

    public boolean overlaps(Appointment other) {
        return this.startHour < other.endHour && other.startHour < this.endHour;
    }
}
