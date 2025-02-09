package com.qrbats.qrbats.functionalities.eventcreation.services;

import com.qrbats.qrbats.entity.event.Event;
import com.qrbats.qrbats.entity.event.EventRepository;
import com.qrbats.qrbats.entity.event.EventRole;
import com.qrbats.qrbats.entity.lecture.Lecture;
import com.qrbats.qrbats.entity.lecture.LectureRepository;
import com.qrbats.qrbats.functionalities.attendance.service.AttendanceEventService;
import com.qrbats.qrbats.functionalities.eventcreation.dto.RegisterEventRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final AttendanceEventService attendanceEventService;
    private final LectureRepository lectureRepository;

    public Event createEvent(RegisterEventRequest request) {

        Optional<Event> existEvent = eventRepository.findByEventNameAndEventDateAndEventTime(
                request.getEventName(),request.getEventDate(),request.getEventTime()
        );
        if (existEvent.isPresent()){
            throw new RuntimeException("The Event Already Exist.");
        }

        // check events overlapping
        Optional<List<Event>> allEventByDateAndVenue = eventRepository.findAllByEventVenueAndEventDate(
                request.getEventVenue(),request.getEventDate());
        for (Event checkEvent : allEventByDateAndVenue.get()){
            LocalTime startTime = request.getEventTime();
            LocalTime endTime = request.getEventEndTime();
            LocalTime checkStartTime = checkEvent.getEventTime();
            LocalTime checkEndTime = checkEvent.getEventEndTime();

            boolean isEventStartAndEndTimesEqualsToCheckEventStartAndEndTimes =
                    startTime.equals(checkStartTime) || startTime.equals(checkEndTime)
                            || endTime.equals(checkStartTime) || endTime.equals(checkEndTime);
            boolean isEventStartTimeInCheckEventTimeDuration = startTime.isAfter(checkStartTime) && startTime.isBefore(checkEndTime);
            boolean isEventEndTimeInCheckEventTimeDuration = endTime.isAfter(checkStartTime) && endTime.isBefore(checkEndTime);
            boolean isCheckEventTimeDurationInEventTimeDuration = startTime.isBefore(checkStartTime) && endTime.isAfter(checkEndTime);
            if (isEventStartAndEndTimesEqualsToCheckEventStartAndEndTimes || isEventStartTimeInCheckEventTimeDuration || isEventEndTimeInCheckEventTimeDuration || isCheckEventTimeDurationInEventTimeDuration){
                throw new RuntimeException("There is Another Event For The Venue-"+request.getEventVenue()+" At The This Time.");
            }
        }

        // check lectures overlapping
        String lectureDay = request.getEventDate().getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
        Optional<List<Lecture>> allLecturesBySameVenueAndDate = lectureRepository.findAllByLectureDayAndLectureVenue(
                lectureDay,request.getEventVenue()
        );
        for (Lecture checkLecture : allLecturesBySameVenueAndDate.get()){
            LocalTime startTime = request.getEventTime();
            LocalTime endTime = request.getEventEndTime();
            LocalTime checkStartTime = checkLecture.getLectureStartTime().toLocalTime();
            LocalTime checkEndTime = checkLecture.getLectureEndTime().toLocalTime();

            boolean isEventStartAndEndTimesEqualsToCheckLectureStartAndEndTimes =
                    startTime.equals(checkStartTime) || startTime.equals(checkEndTime)
                            || endTime.equals(checkStartTime) || endTime.equals(checkEndTime);
            boolean isEventStartTimeInCheckLectureTimeDuration = startTime.isAfter(checkStartTime) && startTime.isBefore(checkEndTime);
            boolean isEventEndTimeInCheckLectureTimeDuration = endTime.isAfter(checkStartTime) && endTime.isBefore(checkEndTime);
            boolean isCheckLectureTimeDurationInEventTimeDuration = startTime.isBefore(checkStartTime) && endTime.isAfter(checkEndTime);
            if (isEventStartAndEndTimesEqualsToCheckLectureStartAndEndTimes || isEventStartTimeInCheckLectureTimeDuration || isEventEndTimeInCheckLectureTimeDuration || isCheckLectureTimeDurationInEventTimeDuration){
                throw new RuntimeException("There is Another Lecture For The Venue-"+request.getEventVenue()+" At The This Time.");
            }
        }

        Event event = new Event();
        event.setEventName(request.getEventName());
        event.setEventDate(request.getEventDate());
        event.setEventTime(request.getEventTime());
        event.setEventEndTime(request.getEventEndTime());
        event.setEventVenue(request.getEventVenue());
        event.setEventRole(EventRole.valueOf(request.getEventRole()));
        event.setEventAssignedUserId(request.getEventAssignedUserId());


        Event save = eventRepository.save(event);
        if (!eventRepository.findById(save.getEventId()).isPresent()) throw new RuntimeException("Event Saved Failed.");
        attendanceEventService.createEventAttendanceTable(save.getEventId());
        return save;
    }

    public Event updateEvent(RegisterEventRequest request) {
        Optional<Event> existEvent=eventRepository.findById(request.getEventId());
        Event event = existEvent.orElseThrow(()-> new RuntimeException("There is No Any Event For This Id."));

        // check events overlapping
        Optional<List<Event>> allEventByDateAndVenue = eventRepository.findAllByEventVenueAndEventDate(
                request.getEventVenue(),request.getEventDate());
        for (Event checkEvent : allEventByDateAndVenue.get()){
            if (checkEvent.getEventId().equals(request.getEventId())){
                continue;
            }
            LocalTime startTime = request.getEventTime();
            LocalTime endTime = request.getEventEndTime();
            LocalTime checkStartTime = checkEvent.getEventTime();
            LocalTime checkEndTime = checkEvent.getEventEndTime();

            boolean isEventStartAndEndTimesEqualsToCheckEventStartAndEndTimes =
                    startTime.equals(checkStartTime) || startTime.equals(checkEndTime)
                            || endTime.equals(checkStartTime) || endTime.equals(checkEndTime);
            boolean isEventStartTimeInCheckEventTimeDuration = startTime.isAfter(checkStartTime) && startTime.isBefore(checkEndTime);
            boolean isEventEndTimeInCheckEventTimeDuration = endTime.isAfter(checkStartTime) && endTime.isBefore(checkEndTime);
            boolean isCheckEventTimeDurationInEventTimeDuration = startTime.isBefore(checkStartTime) && endTime.isAfter(checkEndTime);
            if (isEventStartAndEndTimesEqualsToCheckEventStartAndEndTimes || isEventStartTimeInCheckEventTimeDuration || isEventEndTimeInCheckEventTimeDuration || isCheckEventTimeDurationInEventTimeDuration){
                throw new RuntimeException("There is Another Event For The Venue-"+request.getEventVenue()+" At The This Time.");
            }
        }

        // check lectures overlapping
        String lectureDay = request.getEventDate().getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
        Optional<List<Lecture>> allLecturesBySameVenueAndDate = lectureRepository.findAllByLectureDayAndLectureVenue(
                lectureDay,request.getEventVenue()
        );
        for (Lecture checkLecture : allLecturesBySameVenueAndDate.get()){
            LocalTime startTime = request.getEventTime();
            LocalTime endTime = request.getEventEndTime();
            LocalTime checkStartTime = checkLecture.getLectureStartTime().toLocalTime();
            LocalTime checkEndTime = checkLecture.getLectureEndTime().toLocalTime();

            boolean isEventStartAndEndTimesEqualsToCheckLectureStartAndEndTimes =
                    startTime.equals(checkStartTime) || startTime.equals(checkEndTime)
                            || endTime.equals(checkStartTime) || endTime.equals(checkEndTime);
            boolean isEventStartTimeInCheckLectureTimeDuration = startTime.isAfter(checkStartTime) && startTime.isBefore(checkEndTime);
            boolean isEventEndTimeInCheckLectureTimeDuration = endTime.isAfter(checkStartTime) && endTime.isBefore(checkEndTime);
            boolean isCheckLectureTimeDurationInEventTimeDuration = startTime.isBefore(checkStartTime) && endTime.isAfter(checkEndTime);
            if (isEventStartAndEndTimesEqualsToCheckLectureStartAndEndTimes || isEventStartTimeInCheckLectureTimeDuration || isEventEndTimeInCheckLectureTimeDuration || isCheckLectureTimeDurationInEventTimeDuration){
                throw new RuntimeException("There is Another Lecture For The Venue-"+request.getEventVenue()+" At The This Time.");
            }
        }

        if(event.getEventName() != null) event.setEventName(request.getEventName());
        if(event.getEventDate() != null) event.setEventDate(request.getEventDate());
        if(event.getEventTime() != null) event.setEventTime(request.getEventTime());
        if(event.getEventEndTime() != null) event.setEventEndTime(request.getEventEndTime());
        if(event.getEventVenue() != null) event.setEventVenue(request.getEventVenue());
        //if(event.getEventRole() != null) event.setEventRole(EventRole.valueOf(request.getEventRole()));
        //if(event.getEventAssignedUserId() != null) event.setEventAssignedUserId(request.getEventAssignedUserId());


        Event save = eventRepository.save(event);
        if (!eventRepository.findById(save.getEventId()).isPresent()) throw new RuntimeException("Event Saved Failed.");
        return save;
    }

    public Optional<Event> findAlreadyExistEvent(RegisterEventRequest request) {
        return eventRepository.findByEventNameAndEventDateAndEventTime(
                request.getEventName(), request.getEventDate(), request.getEventTime()
        );
    }

    public List<Event> getAllEvent() {
        return eventRepository.findAll();
    }

    public List<Event> getAllEventByUserId(Integer userId) {
        Optional<List<Event>> eventList = eventRepository.findAllByEventAssignedUserId(userId);
        if (eventList.isPresent()) {
            return eventList.get();
        } else {
            throw new RuntimeException("Fail to get event for given userId.");
        }
    }

    public boolean deleteEvent(Integer id) {
        Optional<Event> event = eventRepository.findById(id);
        if (!event.isPresent()) throw new RuntimeException("The event not found for given eventId");
        try{
            attendanceEventService.dropEventAttendanceTable(id);
            eventRepository.deleteById(id);
            return true;
        }catch (Exception ex){
            throw new RuntimeException("Error deleteEvent. ",ex);
        }

    }

    public Event getEventByEventId(Integer eventId) {
        Optional<Event> event = eventRepository.findById(eventId);
        if (!event.isPresent()) throw new RuntimeException("No Event Found For This Event Id.");
        return event.get();
    }

}
