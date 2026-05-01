package com.example.supermarket.entity;

import com.example.supermarket.entity.compositePk.BatchExportedId;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "batches_exported")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@IdClass(BatchExportedId.class)
public class BatchExported {

    @Id
    @Column(name = "batch_export_id")
    private Long batchExportId;

    @Id
    @Column(name = "export_details_id")
    private Long exportDetailsId;

    private Integer quantity;
}

