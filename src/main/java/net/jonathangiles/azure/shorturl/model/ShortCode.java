package net.jonathangiles.azure.shorturl.model;

import lombok.Data;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class ShortCode {

//    @Id
    private String longUrl;

    @Id
    private String shortCode;

//    @OneToMany(cascade = CascadeType.PERSIST)
//    private List<Visit> visits;

    public ShortCode() {
//        visits = new ArrayList<>();
    }

    public ShortCode(String longUrl, String shortCode) {
        this();
        this.longUrl = longUrl;
        this.shortCode = shortCode;
    }


}
