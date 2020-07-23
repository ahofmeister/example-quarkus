package org.jobrunr.examples;

import io.agroal.api.AgroalDataSource;
import io.vertx.ext.web.common.template.test;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.CDI;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.sql.DataSource;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.h2.jdbcx.JdbcDataSource;
import org.jobrunr.dashboard.JobRunrDashboardWebServer;
import org.jobrunr.examples.services.CustomDatasource;
import org.jobrunr.jobs.mappers.JobMapper;
import org.jobrunr.scheduling.BackgroundJob;
import org.jobrunr.scheduling.JobScheduler;
import org.jobrunr.server.BackgroundJobServer;
import org.jobrunr.server.JobActivator;
import org.jobrunr.storage.StorageProvider;
import org.jobrunr.storage.sql.h2.H2StorageProvider;
import org.jobrunr.utils.mapper.JsonMapper;
import org.jobrunr.utils.mapper.jsonb.JsonbJsonMapper;

public class JobRunrProvider {

  @ConfigProperty(name = "dashboardPort")
  String dashboardPort;

  @ConfigProperty(name = "quarkus.datasource.url")
  String url;

  @ConfigProperty(name = "quarkus.datasource.username")
  String username;


  @Produces
  @Singleton
  public JobRunrDashboardWebServer dashboardWebServer(StorageProvider storageProvider,
      JsonMapper jsonMapper) {
    return new JobRunrDashboardWebServer(storageProvider, jsonMapper,
        Integer.parseInt(dashboardPort));
  }

  @Produces
  @Singleton
  public BackgroundJobServer backgroundJobServer(StorageProvider storageProvider,
      JobActivator jobActivator) {
    return new BackgroundJobServer(storageProvider, jobActivator);
  }

  @Produces
  @Singleton
  public JobActivator jobActivator() {
    return new JobActivator() {
      @Override
      public <T> T activateJob(Class<T> aClass) {
        return CDI.current().select(aClass).get();
      }
    };
  }

  @Produces
  @Singleton
  public JobScheduler jobScheduler(StorageProvider storageProvider) {
    final JobScheduler jobScheduler = new JobScheduler(storageProvider);
    BackgroundJob.setJobScheduler(jobScheduler);
    return jobScheduler;
  }

  @Produces
  @Singleton
  public StorageProvider storageProvider(@CustomDatasource DataSource dataSource,
      JobMapper jobMapper) {
    H2StorageProvider h2StorageProvider = new H2StorageProvider(dataSource);
    h2StorageProvider.setJobMapper(jobMapper);
    return h2StorageProvider;
  }

  @Produces
  @Singleton
  @CustomDatasource
  public DataSource createDataSource() {
    JdbcDataSource jdbcDataSource = new JdbcDataSource();
    jdbcDataSource.setURL(this.url);
    jdbcDataSource.setUser(this.username);
    return jdbcDataSource;
  }

  @Produces
  @Singleton
  public JobMapper jobMapper(JsonMapper jsonMapper) {
    return new JobMapper(jsonMapper);
  }

  @Produces
  @Singleton
  public JsonMapper jsonMapper() {
    return new JsonbJsonMapper();
  }
}
