package org.example.furryfriendfund.appointments;

import java.util.List;

public interface IAppointmentsService {
    public Appointments save(Appointments appointments);
    public void delete(Appointments appointments);
    public Appointments findById(String id);
    List<Appointments> findByAccountIDAndStatus(String accountID, boolean status);
}
