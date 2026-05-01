package com.example.supermarket.entity.compositePk;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BatchExportedId implements Serializable {
    private Long batchExportId;
    private Long exportDetailsId;
}
