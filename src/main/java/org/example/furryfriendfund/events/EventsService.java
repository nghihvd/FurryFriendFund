package org.example.furryfriendfund.events;


import org.example.furryfriendfund.donations.IDonationsRepository;
import org.example.furryfriendfund.notification.Notification;
import org.example.furryfriendfund.notification.NotificationRepository;
import org.example.furryfriendfund.notification.NotificationService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class EventsService implements IEventsService {
    private static final Path CURRENT_FOLDER = Paths.get(System.getProperty("user.dir"));


    @Autowired
    private NotificationService notificationService;

    @Autowired
    private EventsRepository eventRepo;

    @Autowired
    private IDonationsRepository donationRepo;

    @Autowired
    private NotificationRepository notificationRepo;

    @Override
    public Events addEvent(EventsDTO eventsDTO) throws IOException {
        Path staticPath = Paths.get("static");
        Path imagePath = Paths.get("images");

        // Tạo thư mục nếu chưa tồn tại
        if (!Files.exists(CURRENT_FOLDER.resolve(staticPath).resolve(imagePath))) {
            Files.createDirectories(CURRENT_FOLDER.resolve(staticPath).resolve(imagePath));
        }

        // Lưu file ảnh vào thư mục
        Path file = CURRENT_FOLDER.resolve(staticPath).resolve(imagePath)
                .resolve(Objects.requireNonNull(eventsDTO.getImage().getOriginalFilename()));
        try (OutputStream outputStream = Files.newOutputStream(file)) {
            outputStream.write(eventsDTO.getImage().getBytes());
        }

        // Chuyển từ DTO sang Entity
        Events event = new Events();
        event.setEventID(UUID.randomUUID().toString().substring(0, 8));
        BeanUtils.copyProperties(eventsDTO, event, "image", "eventID");
        event.setImg_url(imagePath.resolve(eventsDTO.getImage().getOriginalFilename()).toString());

        // Thêm sự kiện mới vào database
        if (!eventRepo.existsById(event.getEventID())) {
            event.setStatus("Waiting");
            Events a_event = eventRepo.save(event);
            // send notification to admin accept/deny
            notificationService.eventStatusNotification(a_event);
            return a_event;
        }
        return null;
    }

    @Override
    public Events save(Events event) {
        return eventRepo.save(event);
    }

    @Override
    @Transactional
    public boolean deleteEvent(String eventID) {
        boolean success = false;
        Events eventDeleted = eventRepo.findById(eventID).orElse(null);
        if (eventDeleted != null) {
            if (donationRepo.findByEventID(eventID).isEmpty()) {
                eventRepo.deleteById(eventID);
                success = true;
            } else {
                eventDeleted.setStatus("Ending");
                eventRepo.save(eventDeleted);
                success = false;
            }
        }
        List<Notification> notificationsToDelete = notificationRepo.findAll()
                .stream()
                .filter(noti -> {
                    String extractedEventId = notificationService.extractEventIdFromMessage(noti);
                    return extractedEventId.equals(eventID);
                })
                .collect(Collectors.toList());

        if (!notificationsToDelete.isEmpty()) {
            notificationRepo.deleteAll(notificationsToDelete);
        }
        return success;
    }

    /**
     * Function giúp xóa định kì mỗi 12hPM mỗi ngày sẽ xóa Events tới end_Date
     * 0 seconds 0 minutes 0 hours * day of months * months ? not care about day of months
     */
    @Transactional
    @Scheduled(cron = "0 0 0 * * ?")
    public void deleteExpiredEvents() {
        //lấy time hiện tại theo kieu Date
        Date today = new Date();
        // search all event is ended
        List<Events> expiredEvents = eventRepo.findAll().stream()
                .filter(event -> event.getEnd_date() != null && event.getEnd_date().before(today))
                .toList();
        for (Events event : expiredEvents) {
            deleteEvent(event.getEventID());
        }
    }


    @Override
    public Events updateEvents(String eventID, EventsDTO eventsDTO) throws IOException {
        // Lấy sự kiện cũ từ cơ sở dữ liệu
        System.out.println("Updating event with ID: " + eventID);
        System.out.println("Received DTO: " + eventsDTO);

        Events eventUpdate = getEvent(eventID);
        if (eventUpdate == null) {
            return null;
        }

        // Kiểm tra trạng thái của sự kiện
        if (!"Waiting".equalsIgnoreCase(eventUpdate.getStatus()) && !"Updating".equalsIgnoreCase(eventUpdate.getStatus())) {

            throw new IllegalStateException("Event status is not allowed to update");
        }

        // Tạo một bản sao của sự kiện hiện tại để lưu thông tin cũ (nếu cần)
        Events oldEventInfo = new Events();
        String oldImageUrl = eventUpdate.getImg_url();
        BeanUtils.copyProperties(eventUpdate, oldEventInfo);

        // Sao chép thuộc tính từ DTO sang eventUpdate, trừ image và eventID
        BeanUtils.copyProperties(eventsDTO, eventUpdate, "image", "eventID", "img_url");

        // Kiểm tra và cập nhật hình ảnh nếu có
        MultipartFile imageFile = eventsDTO.getImage();
        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                Path staticPath = Paths.get("static");
                Path imagePath = Paths.get("images");

                // Xóa file cũ nếu tồn tại
                if (oldImageUrl != null && !oldImageUrl.isEmpty()) {
                    Path oldFilePath = CURRENT_FOLDER.resolve(staticPath)
                            .resolve(imagePath)
                            .resolve(Paths.get(oldImageUrl).getFileName());
                    Files.deleteIfExists(oldFilePath);
                }

                // Tạo thư mục nếu chưa tồn tại
                if (!Files.exists(CURRENT_FOLDER.resolve(staticPath).resolve(imagePath))) {
                    Files.createDirectories(CURRENT_FOLDER.resolve(staticPath).resolve(imagePath));
                }

                String originalFileName = imageFile.getOriginalFilename();

                // Kiểm tra định dạng file


                // Tạo tên file mới với UUID
                String newFileName = UUID.randomUUID() + "_" + originalFileName;

                // Lưu file mới
                Path file = CURRENT_FOLDER.resolve(staticPath)
                        .resolve(imagePath)
                        .resolve(newFileName);
                try (OutputStream outputStream = Files.newOutputStream(file)) {
                    outputStream.write(imageFile.getBytes());
                }

                // Cập nhật URL trong database - đồng nhất với cách lưu trong addEvent
                oldEventInfo.setImg_url(imagePath.resolve(newFileName).toString());
            } catch (IOException e) {
                throw new RuntimeException("Failed to process image file: " + e.getMessage());
            }
        }

        // So sánh và cập nhật các trường khác
        if (eventUpdate.getEvent_name() != null && !eventUpdate.getEvent_name().trim().isEmpty() && !Objects.equals(eventUpdate.getEvent_name(), oldEventInfo.getEvent_name())) {
            oldEventInfo.setEvent_name(eventUpdate.getEvent_name());
        }
        if (eventUpdate.getStart_date() != null && !Objects.equals(eventUpdate.getStart_date(), oldEventInfo.getStart_date())) {
            oldEventInfo.setStart_date(eventUpdate.getStart_date());
        }
        if (eventUpdate.getEnd_date() != null && !Objects.equals(eventUpdate.getEnd_date(), oldEventInfo.getEnd_date())) {
            oldEventInfo.setEnd_date(eventUpdate.getEnd_date());
        }
        if (eventUpdate.getDescription() != null && !eventUpdate.getDescription().trim().isEmpty() && !Objects.equals(eventUpdate.getDescription(), oldEventInfo.getDescription())) {
            oldEventInfo.setDescription(eventUpdate.getDescription());
        }
        if(eventUpdate.getLocation() != null && !eventUpdate.getLocation().trim().isEmpty() && !Objects.equals(eventUpdate.getLocation(), oldEventInfo.getLocation())) {
            oldEventInfo.setLocation(eventUpdate.getLocation());
        }
        // Nếu có thay đổi, lưu vào cơ sở dữ liệu

        eventUpdate.setStatus("Updating");
        Events savedEvent = eventRepo.save(oldEventInfo);
        notificationService.eventStatusNotification(savedEvent);
        return savedEvent;

    }

    @Override
    public List<Events> showEvents() {
        return eventRepo.findByEventStatusInIgnoreCase(Arrays.asList("Published","Ending"));
    }

    @Override
    public List<Events> showEventsAdmin() {
        return eventRepo.showAllEvents();
    }

    @Override
    public Events getEvent(String eventID) {

        return eventRepo.findById(eventID).orElse(null);
    }


    @Override
    public boolean rejectEvent(String eventID) {
        List<Notification> notificationsToDelete = notificationRepo.findAll()
                .stream()
                .filter(noti -> {
                    String extractedEventId = notificationService.extractEventIdFromMessage(noti);
                    return extractedEventId.equals(eventID);
                })
                .collect(Collectors.toList());

        if (!notificationsToDelete.isEmpty()) {
            Notification latestNoti = notificationsToDelete.get(0);
            latestNoti.setStatus(false);
            notificationRepo.save(latestNoti);
            notificationRepo.deleteAll(notificationsToDelete);

        }
        eventRepo.deleteById(eventID);
        Events events = getEvent(eventID);
        return events == null;
    }

    @Override
    public List<Events> showEventsByWaitingUpdating() {
        return eventRepo.showAllEvents().stream().filter(event -> event.getStatus().equalsIgnoreCase("Waiting")
                        || event.getStatus().equalsIgnoreCase("Updating"))
                .toList();
    }

    @Override
    public int countEvents(String status) {
        return eventRepo.countEvents(status);
    }

    @Override
    public Events acceptEventUpdating(String eventID) {
        Events eventOpt = getEvent(eventID); // Lấy sự kiện dựa trên eventID
        String eventStatus = eventOpt.getStatus();

        switch (eventStatus) {
            case "Waiting":
                eventOpt.setStatus("Updating");

                break;
            case "Updating":
                eventOpt.setStatus("Published"); // Chuyển từ Updating sang Published
                break;
            default:
                System.out.println("Unknown status: " + eventStatus);
                break;
        }
        List<Notification> notificationsToDelete = notificationRepo.findAll()
                .stream()
                .filter(noti -> {
                    String extractedEventId = notificationService.extractEventIdFromMessage(noti);
                    return extractedEventId.equals(eventID);
                })
                .collect(Collectors.toList());

        if (!notificationsToDelete.isEmpty()) {
            Notification latestNoti = notificationsToDelete.get(0);
            latestNoti.setStatus(false);
            notificationRepo.save(latestNoti);
            notificationRepo.deleteAll(notificationsToDelete);
        }

        return eventRepo.save(eventOpt); // Lưu sự kiện và trả về đối tượng
    }


}
