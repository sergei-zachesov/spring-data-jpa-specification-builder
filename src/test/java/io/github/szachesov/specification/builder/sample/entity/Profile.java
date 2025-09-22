package io.github.szachesov.specification.builder.sample.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "profiles")
public class Profile extends BaseEntity {

  private String bio;

  @OneToOne
  @JoinColumn(name = "user_id")
  private User user;
}
