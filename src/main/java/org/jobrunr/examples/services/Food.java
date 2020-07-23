package org.jobrunr.examples.services;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * @author Alexander Hofmeister
 */
@Entity
public class Food {

  @Id
  @GeneratedValue
  public Long id;

  public String name;

}
