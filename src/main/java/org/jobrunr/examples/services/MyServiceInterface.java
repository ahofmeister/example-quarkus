package org.jobrunr.examples.services;

import io.quarkus.runtime.annotations.RegisterForReflection;
import java.time.Instant;

@RegisterForReflection
public interface MyServiceInterface {

  void doSimpleJob(String anArgument);

  void add(Instant date, String id);

}
