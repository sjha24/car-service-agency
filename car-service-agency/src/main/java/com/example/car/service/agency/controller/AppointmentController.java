package com.example.car.service.agency.controller;
import com.example.car.service.agency.model.Appointment;
import com.example.car.service.agency.model.TimeSlot;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/appointments")
@AllArgsConstructor
public class AppointmentController {


    // In-memory database to store appointments
    private Map<String, List<Appointment>> appointmentsByOperator = new HashMap<>();

    // Endpoint to book an appointment
    @PostMapping("/book")
    public String bookAppointment(@RequestParam String operatorId, @RequestParam int startHour, @RequestParam int endHour) {
        Appointment appointment = new Appointment(operatorId, startHour, endHour);
        if (!appointmentsByOperator.containsKey(operatorId)) {
            appointmentsByOperator.put(operatorId, new ArrayList<>());
        }
        List<Appointment> appointments = appointmentsByOperator.get(operatorId);
        for (Appointment existingAppointment : appointments) {
            if (existingAppointment.overlaps(appointment)) {
                return "Appointment overlaps with existing appointment";
            }
        }
        appointments.add(appointment);
        return "Appointment booked successfully";
    }

    // Endpoint to reschedule an appointment
    @PutMapping("/reschedule/{appointmentId}")
    public String rescheduleAppointment(@PathVariable String appointmentId, @RequestParam int startHour, @RequestParam int endHour) {
        for (List<Appointment> appointments : appointmentsByOperator.values()) {
            for (Appointment appointment : appointments) {
                if (appointment.getId().equals(appointmentId)) {
                    appointment.setStartHour(startHour);
                    appointment.setEndHour(endHour);
                    return "Appointment rescheduled successfully";
                }
            }
        }
        return "Appointment not found";
    }

    // Endpoint to cancel an appointment
    @DeleteMapping("/cancel/{appointmentId}")
    public String cancelAppointment(@PathVariable String appointmentId) {
        for (List<Appointment> appointments : appointmentsByOperator.values()) {
            Iterator<Appointment> iterator = appointments.iterator();
            while (iterator.hasNext()) {
                Appointment appointment = iterator.next();
                if (appointment.getId().equals(appointmentId)) {
                    iterator.remove();
                    return "Appointment canceled successfully";
                }
            }
        }
        return "Appointment not found";
    }

    // Endpoint to get all appointments for a specific operator
    @GetMapping("/operator/{operatorId}")
    public List<Appointment> getAppointmentsByOperator(@PathVariable String operatorId) {
        return appointmentsByOperator.getOrDefault(operatorId, new ArrayList<>());
    }

    // Endpoint to get open appointment slots for a specific operator
    @GetMapping("/operator/{operatorId}/open-slots")
    public List<TimeSlot> getOpenSlotsByOperator(@PathVariable String operatorId) {
        List<TimeSlot> openSlots = new ArrayList<>();
        List<Appointment> appointments = appointmentsByOperator.getOrDefault(operatorId, new ArrayList<>());
        if (appointments.isEmpty()) {
            openSlots.add(new TimeSlot(0, 24));
        } else {
            int start = 0;
            for (Appointment appointment : appointments) {
                if (start < appointment.getStartHour()) {
                    openSlots.add(new TimeSlot(start, appointment.getStartHour()));
                }
                start = appointment.getEndHour();
            }
            if (start < 24) {
                openSlots.add(new TimeSlot(start, 24));
            }
        }
        return openSlots;
    }
}
