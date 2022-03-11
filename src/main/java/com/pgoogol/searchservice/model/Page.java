package com.pgoogol.searchservice.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Page {

    @Schema(description = "Number of page", example = "0")
    private int page = 0;
    @Schema(description = "Number of rows per page", example = "100")
    private int size = 100;

    public static int getTotalPages(long total, int size) {
        return (int) Math.ceil((double) total / size);
    }

    @JsonIgnore
    public int getFrom() {
        int from = this.page * this.size;
        return this.page == 0 ? from : ++from;
    }

}
