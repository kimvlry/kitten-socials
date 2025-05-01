package ru.kimvlry.kittens.web.service.filters;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
public class OwnerFilter {
    @Schema(description = "Exact owner name")
    private String name;

    @Schema(description = "Born after this date")
    private LocalDateTime birthAfter;

    @Schema(description = "Born before this date")
    private LocalDateTime birthBefore;

    @Schema(description = "Owned kittens' IDs to match")
    private Set<Long> ownedKittensIds;
}
