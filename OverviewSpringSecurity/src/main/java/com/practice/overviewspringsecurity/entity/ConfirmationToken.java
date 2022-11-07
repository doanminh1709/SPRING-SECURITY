package com.practice.overviewspringsecurity.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class ConfirmationToken {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String token;

  @Column(nullable = false)
  private LocalDateTime createAt;

  @Column(nullable = false)
  private LocalDateTime expressAt;

  @Column(nullable = true)
  private LocalDateTime confirmedAt;

  @ManyToOne
  @JoinColumn(nullable = false, name = "user_id")
  private User user;

  public ConfirmationToken(String token, LocalDateTime createAt, LocalDateTime expressAt,
                           User user) {
    this.token = token;
    this.createAt = createAt;
    this.expressAt = expressAt;
    this.user = user;
  }
}
