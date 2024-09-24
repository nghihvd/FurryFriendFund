package org.example.furryfriendfund.appointments;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AppointmentsService implements IAppointmentsService {

    @Autowired
    AppointmentsRepository appointmentsRepository;

    @Override
    public Appointments save(Appointments appointments) {
       return appointmentsRepository.save(appointments);
    }

    @Override
    public void delete(Appointments appointments) {
            appointmentsRepository.delete(appointments);
    }
}
