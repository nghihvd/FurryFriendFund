package org.example.furryfriendfund.appointments;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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

    @Override
    public Appointments findById(String id) {
        return appointmentsRepository.findById(id).orElse(null);
    }

    @Override
    public List<Appointments> findByAccountIDAndAdoptStatus(String accountID, boolean adopt_status) {
        List<Appointments> appointments = appointmentsRepository.findByAccountIDAndAdoptStatus(accountID, adopt_status);
        return appointments;
    }

    @Override
    public List<Appointments> findByStatus(boolean status) {
        return appointmentsRepository.findByStatus(status);
    }

    @Override
    public List<Appointments> findByAdoptStatus(boolean adopt_status) {
        return appointmentsRepository.findByAdoptStatus(adopt_status);
    }
}
