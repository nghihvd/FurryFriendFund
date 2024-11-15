package org.example.furryfriendfund.appointments;

import java.util.List;

public interface IAppointmentsService {
    public Appointments save(Appointments appointments);

    public void delete(Appointments appointments);

    public Appointments findById(String id);

    List<Appointments> findByAccountIDAndAdoptStatus(String accountID, boolean adopt_status);

    List<Appointments> findByStatus(boolean status);

    List<Appointments> findByApproveStatus(boolean approve_status);

    List<Appointments> findByAdoptStatus(boolean adopt_status);

    List<Appointments> findByPetID(String petID);

    List<Appointments> findByAccountIDAndApproveStatus(String accountID, boolean approve_status);
}
