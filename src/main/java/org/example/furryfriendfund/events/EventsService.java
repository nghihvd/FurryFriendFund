package org.example.furryfriendfund.events;


import org.example.furryfriendfund.notification.NotificationService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Service
public class EventsService implements IEventsService {
    private static final Path CURRENT_FOLDER = Paths.get(System.getProperty("user.dir"));


    @Autowired
    private NotificationService notificationService;

    @Autowired
    private EventsRepository eventRepo;


    @Override
    public Events addEvent(EventsDTO eventsDTO) throws IOException {
        Path staticPath = Paths.get("static");
        Path imagePath = Paths.get("imageEvent");

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
            notificationService.eventStatusNotification(a_event);
            return a_event;
        }
        return null;
    }



    @Override
    public boolean deleteEvent(String eventID) {
        boolean success = false;
        if (eventRepo.existsById(eventID)) {
            eventRepo.deleteById(eventID);
            success = true;
        }
        return success;
    }




    /**
     *
     * public Events updateEvents(Events updatedEvent) {
     *     // Tìm sự kiện trong cơ sở dữ liệu dựa trên eventID
     *     Events existingEvent = getEvent(updatedEvent.getEventID());
     *     // Nếu sự kiện không tồn tại, ném ra ngoại lệ
     *     if (existingEvent == null) {
     *         throw new EntityNotFoundException("Event with ID " + updatedEvent.getEventID() + " not found");
     *     }
     *     // Kiểm tra trạng thái
     *     if (!"Updating".equalsIgnoreCase(existingEvent.getStatus())) {
     *         throw new IllegalStateException("Event is not in 'Updating' status");
     *     }
     *     // Tạo một bản sao của sự kiện hiện tại để lưu thông tin cũ
     *     Events oldEventInfo = new Events();
     *     BeanUtils.copyProperties(existingEvent, oldEventInfo);
     *     boolean isUpdated = false;
     *     // So sánh và cập nhật các trường khác nhau
     *     if (updatedEvent.getEvent_name() != null && !updatedEvent.getEvent_name().equalsIgnoreCase(existingEvent.getEvent_name())) {
     *         existingEvent.setEvent_name(updatedEvent.getEvent_name());
     *         isUpdated = true;
     *     }
     *     if (updatedEvent.getStart_date() != null && !updatedEvent.getStart_date().equals(existingEvent.getStart_date())) {
     *         existingEvent.setStart_date(updatedEvent.getStart_date());
     *         isUpdated = true;
     *     }
     *     if (updatedEvent.getEnd_date() != null && !updatedEvent.getEnd_date().equals(existingEvent.getEnd_date())) {
     *         existingEvent.setEnd_date(updatedEvent.getEnd_date());
     *         isUpdated = true;
     *     }
     *     if (updatedEvent.getDescription() != null && !updatedEvent.getDescription().equalsIgnoreCase(existingEvent.getDescription())) {
     *         existingEvent.setDescription(updatedEvent.getDescription());
     *         isUpdated = true;
     *     }
     *     if (updatedEvent.getImg_url() != null && !updatedEvent.getImg_url().equalsIgnoreCase(existingEvent.getImg_url())) {
     *         existingEvent.setImg_url(updatedEvent.getImg_url());
     *         isUpdated = true;
     *     }
     *     // Nếu có thay đổi, lưu cập nhật và gửi thông báo
     *     if (isUpdated) {
     *         Events savedEvent = eventRepo.save(existingEvent);
     *         notificationService.sendUpdateNotification(oldEventInfo, savedEvent);
     *         return savedEvent;
     *     }
     *     // Trả về sự kiện cũ nếu không có thay đổi
     *     return existingEvent;
     * }
     * // Trong NotificationService
     * public void sendUpdateNotification(Events oldEvent, Events newEvent) {
     *     Notification noti = new Notification();
     *     noti.setNotiID(UUID.randomUUID().toString().substring(0, 8));
     *     noti.setMessage(createUpdateMessage(oldEvent, newEvent));
     *     noti.setRoleID(1);
     *     noti.setButton_status(true);
     *     notificationRepository.save(noti);
     * }
     * private String createUpdateMessage(Events oldEvent, Events newEvent) {
     *     StringBuilder message = new StringBuilder();
     *     message.append("Event update request for: ").append(newEvent.getEventID()).append("_").append(newEvent.getEvent_name()).append("\n");
     *     message.append("Changes:\n");
     *     if (!oldEvent.getEvent_name().equals(newEvent.getEvent_name())) {
     *         message.append("Name: ").append(oldEvent.getEvent_name()).append(" -> ").append(newEvent.getEvent_name()).append("\n");
     *     }
     *     if (!oldEvent.getStart_date().equals(newEvent.getStart_date())) {
     *         message.append("Start Date: ").append(oldEvent.getStart_date()).append(" -> ").append(newEvent.getStart_date()).append("\n");
     *     }
     *     if (!oldEvent.getEnd_date().equals(newEvent.getEnd_date())) {
     *         message.append("End Date: ").append(oldEvent.getEnd_date()).append(" -> ").append(newEvent.getEnd_date()).append("\n");
     *     }
     *     if (!oldEvent.getDescription().equals(newEvent.getDescription())) {
     *         message.append("Description updated\n");
     *     }
     *     if (!oldEvent.getImg_url().equals(newEvent.getImg_url())) {
     *         message.append("Image updated\n");
     *     }
     *     return message.toString();
     * }
     */
    @Override
    public Events updateEvents(String eventID, EventsDTO eventsDTO) throws IOException {
        // Lấy sự kiện cũ từ cơ sở dữ liệu
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
        BeanUtils.copyProperties(eventUpdate, oldEventInfo);

        // Sao chép thuộc tính từ DTO sang eventUpdate, trừ image và eventID
        BeanUtils.copyProperties(eventsDTO, eventUpdate, "image", "eventID");

        // Kiểm tra và cập nhật hình ảnh nếu có
        if (eventsDTO.getImage() != null && !eventsDTO.getImage().isEmpty()) {
            // Lấy tên file mới
            String originalFileName = eventsDTO.getImage().getOriginalFilename();

            // Kiểm tra định dạng file
            String fileExtension = originalFileName.substring(originalFileName.lastIndexOf(".") + 1).toLowerCase();
            List<String> allowedExtensions = Arrays.asList("jpg", "jpeg", "png", "gif");

            if (!allowedExtensions.contains(fileExtension)) {
                throw new IllegalArgumentException("Invalid file format. Only accept file format: .jpg, .jpeg, .png, .gif");
            }

            // Tạo tên file mới với UUID
            String newImageFileName = UUID.randomUUID().toString() + "_" + originalFileName;

            // Lấy tên file hiện tại từ URL và so sánh
            String currentFileName = Paths.get(eventUpdate.getImg_url()).getFileName().toString();
            if (!newImageFileName.equalsIgnoreCase(currentFileName)) {
                // Đường dẫn tới thư mục lưu trữ file
                Path imagePath = Paths.get("uploads", "imageEvent");

                // Tạo thư mục nếu chưa tồn tại
                if (!Files.exists(CURRENT_FOLDER.resolve(imagePath))) {
                    Files.createDirectories(CURRENT_FOLDER.resolve(imagePath));
                }

                // Đường dẫn file mới
                Path file = CURRENT_FOLDER.resolve(imagePath).resolve(newImageFileName);

                // Lưu file ảnh
                try (OutputStream outputStream = Files.newOutputStream(file)) {
                    outputStream.write(eventsDTO.getImage().getBytes());
                } catch (IOException e) {
                    throw new RuntimeException("Can't save file: " + e.getMessage(), e);
                }

                // Cập nhật URL hình ảnh có thể truy cập từ trình duyệt
                String imageUrl = "/uploads/imageEvent/" + newImageFileName;
                eventUpdate.setImg_url(imageUrl);
            }
        }


        // So sánh và cập nhật các trường khác
        boolean isUpdated = false;

        if (!Objects.equals(eventUpdate.getEvent_name(), oldEventInfo.getEvent_name())) {
            isUpdated = true;
        }
        if (!Objects.equals(eventUpdate.getStart_date(), oldEventInfo.getStart_date())) {
            isUpdated = true;
        }
        if (!Objects.equals(eventUpdate.getEnd_date(), oldEventInfo.getEnd_date())) {
            isUpdated = true;
        }
        if (!Objects.equals(eventUpdate.getDescription(), oldEventInfo.getDescription())) {
            isUpdated = true;
        }
        if (!Objects.equals(eventUpdate.getImg_url(), oldEventInfo.getImg_url())) {
            isUpdated = true;
        }

        // Nếu có thay đổi, lưu vào cơ sở dữ liệu
        if (isUpdated) {
            eventUpdate.setStatus("Updating");
            Events savedEvent = eventRepo.save(eventUpdate);
            notificationService.eventStatusNotification(savedEvent);
            return savedEvent;
        }


        // Nếu không có thay đổi, trả về sự kiện hiện tại
        return eventUpdate;
    }






    @Override
    public List<Events> showEvents() {
        return eventRepo.findByEventStatusIgnoreCase("Available");
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
        eventRepo.deleteById(eventID);
        Events events = getEvent(eventID);
        return events == null;
    }
    @Override
    public Events acceptEventUpdating(String eventID) {
        Events eventOpt = getEvent(eventID); // Lấy sự kiện dựa trên eventID
        String eventStatus = eventOpt.getStatus();

        switch (eventStatus) {
            case "Waiting":
                eventOpt.setStatus("Updating");
                System.out.println("Status updated to: Updating");
                break;
            case "Updating":
                eventOpt.setStatus("Published"); // Chuyển từ Updating sang Published
                System.out.println("Status updated to: Published");
                break;
            default:
                System.out.println("Unknown status: " + eventStatus);
                break;
        }

        return eventRepo.save(eventOpt); // Lưu sự kiện và trả về đối tượng
    }




}
