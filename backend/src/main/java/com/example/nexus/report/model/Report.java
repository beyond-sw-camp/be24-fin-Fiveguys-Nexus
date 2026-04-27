package com.example.nexus.report.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "report")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "report_idx")
    private Long idx;

    @Column(name = "report_title", nullable = false)
    private String reportTitle;

    @Column(name = "report_file_path", nullable = false)
    private String reportFilePath;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}
