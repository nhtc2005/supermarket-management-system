package com.example.supermarket.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "batches_imported")
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class BatchImported {
    @Id
    @Column(name = "batch_import_id")
    Long batchImportId;

    @Column(name = "import_details_id")
    private Long importDetailsId;

    private Integer quantity;
}
