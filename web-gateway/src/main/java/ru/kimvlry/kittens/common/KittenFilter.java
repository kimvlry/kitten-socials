package ru.kimvlry.kittens.common;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.Set;

@Getter
@Setter
public class KittenFilter {

    @Schema(description = "Exact kitten name")
    private String name;

    @Schema(description = "List of acceptable breeds")
    private Set<KittenBreed> breeds;

    @Schema(description = "List of acceptable coat colors")
    private Set<KittenCoatColor> coatColors;

    @Schema(description = "Minimum purr loudness")
    private Integer minPurr;

    @Schema(description = "Maximum purr loudness")
    private Integer maxPurr;

    @Schema(description = "Born after this date")
    private Instant birthAfter;

    @Schema(description = "Born before this date")
    private Instant birthBefore;

    @Schema(description = "Owner IDs to match")
    private Set<Long> ownerIds;

    @Schema(description = "Friend kitten IDs to match")
    private Set<Long> friendIds;
}
