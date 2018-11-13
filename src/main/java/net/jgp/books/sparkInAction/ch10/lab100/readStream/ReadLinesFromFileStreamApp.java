package net.jgp.books.sparkInAction.ch10.lab100.readStream;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.streaming.OutputMode;
import org.apache.spark.sql.streaming.StreamingQuery;
import org.apache.spark.sql.streaming.StreamingQueryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.jgp.books.sparkInAction.ch10.x.utils.streaming.lib.StreamingUtils;

/**
 * Reads a stream from a stream (files) and 
 * @author jgp
 */
public class ReadLinesFromFileStreamApp {
  private static transient Logger log = LoggerFactory.getLogger(
      ReadLinesFromFileStreamApp.class);

  public static void main(String[] args) {
    ReadLinesFromFileStreamApp app = new ReadLinesFromFileStreamApp();
    app.start();
  }

  private void start() {
    log.debug("-> start()");

    SparkSession spark = SparkSession.builder()
        .appName("Read lines over a file stream")
        .master("local")
        .getOrCreate();

    Dataset<Row> df = spark
        .readStream()
        .format("text")
        .load(StreamingUtils.getInputDirectory());

    StreamingQuery query = df
        .writeStream()
        .outputMode(OutputMode.Update())
        .format("console")
        .start();

    try {
      query.awaitTermination(50000); // the query will stop in 5000ms
    } catch (StreamingQueryException e) {
      log.error(
          "Exception while waiting for query to end {}.",
          e.getMessage(),
          e);
    }

    log.debug("<- start()");
  }
}