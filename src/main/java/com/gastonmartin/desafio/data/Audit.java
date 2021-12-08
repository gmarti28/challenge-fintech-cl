package com.gastonmartin.desafio.data;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.ZonedDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Audit {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Schema(example = "113", description = "ID of the Audit row in the database")
    private Long id;
    @Schema(example = "GET", description = "HTTP Method used to access the endpoint")
    private String method;
    @Schema(example = "username", description = "Username logged in when the endpoint was accessed")
    private String userId;
    @Schema(example = "/math/add", description = "Endpoint accessed")
    private String url;
    @Schema(example = "2021-12-08T14:58:56.432612-03:00", description = "Date and time of the event")
    private ZonedDateTime eventTime;
}
