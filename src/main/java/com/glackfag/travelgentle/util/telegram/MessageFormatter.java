package com.glackfag.travelgentle.util.telegram;

import com.glackfag.travelgentle.models.Address;
import com.glackfag.travelgentle.models.IntermediatePoint;
import com.glackfag.travelgentle.models.Person;
import com.glackfag.travelgentle.models.Travel;
import com.glackfag.travelgentle.services.IntermediatePointService;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class MessageFormatter {
    private final Environment env;
    private final IntermediatePointService intermediatePointService;

    @Autowired
    public MessageFormatter(Environment env, IntermediatePointService intermediatePointService) {
        this.env = env;
        this.intermediatePointService = intermediatePointService;
    }

    public String indexSingleTravel(@NotNull Travel travel){
        List<IntermediatePoint> points = intermediatePointService.findByTravelId(travel.getId());
        return String.format(env.getRequiredProperty("travel.index.single.template"),
                travel.getTitle(), travel.getDescription(), points.size(), travel.getOrganizer().getId());
    }

    public String indexSinglePoint(@NotNull IntermediatePoint point){
        Address address = point.getAddress();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yy");

        return String.format(env.getRequiredProperty("point.index.single.template"),
                address.getCity() + ", " + address.getCountry(),
                point.getStartDate().toLocalDate().format(formatter),
                point.getEndDate().toLocalDate().format(formatter));
    }

    public String invitationCode(String code){
        return String.format(env.getRequiredProperty("invitation.send.template"), code);
    }

    public String indexSingleProfile(Person person, Address personsAddress){

        return String.format(env.getRequiredProperty("person.index.single"),
                person.getName(),
                person.getAge() == 0 ? "Unknown" : String.valueOf(person.getAge()),
                person.getBio() == null ? "" : person.getBio(),
                personsAddress.getCity() + ", " + personsAddress.getCountry());
    }
}
