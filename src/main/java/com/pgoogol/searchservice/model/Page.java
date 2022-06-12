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

    public static final int PAGE_NUMBER = 0;
    public static final int SIZE_NUMBER = 100;

    @Schema(description = "Number of page", example = "0")
    private int page = PAGE_NUMBER;
    @Schema(description = "Number of rows per page", example = "100")
    private int size = SIZE_NUMBER;

    public static int getTotalPages(long total, int size) {
        return (int) Math.ceil((double) total / size);
    }

    @JsonIgnore
    public int getFrom() {
        int from = this.page * this.size;
        return this.page == 0 ? from : ++from;
    }

}
