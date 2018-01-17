package net.jonathangiles.azure.shorturl.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Entity
@Data
public class Visit {

    @Id
    @GeneratedValue
    private int id;

    private LocalDateTime visitDateTime;
}
