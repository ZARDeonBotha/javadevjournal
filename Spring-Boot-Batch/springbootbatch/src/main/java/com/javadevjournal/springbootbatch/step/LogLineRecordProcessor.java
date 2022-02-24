package com.javadevjournal.springbootbatch.step;

import com.javadevjournal.springbootbatch.model.LogLineRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

import java.util.Date;

public class LogLineRecordProcessor
    implements ItemProcessor<LogLineRecord, String> {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(LogLineRecordProcessor.class);

  @Override
  public String process(LogLineRecord logRecord) throws Exception {
    System.out.println("------Reading a record-------");
    /*String message = stockInfo.getStockName() + " is trading at  "
        + stockInfo.getStockPrice() + " on " + stockInfo.getMarket()+" at "+ new Date().toString()+ "!";
    LOGGER.info("printing '{}' to output file", message);*/

    String message = logRecord.getLogRecord();
    LOGGER.info("printing log line to output file '{}'", message);
    return message;
   }
}

/*
if (str.contains("NoOfRows[") && (str.contains("time["))) {

        //System.out.println("I have found a row " + str);

        String[] data = str.split(" ");
        String date, time;
        Integer rowCount, timeElapsed;
        date = data[0];
        time = data[1];
        rowCount = Integer.parseInt(data[14].replaceAll("[a-zA-Z\\[\\]]",""));
        timeElapsed = Integer.parseInt(data[16].replaceAll("[a-zA-Z\\[\\]\\.]",""));

        if(timeElapsed >= 5000) {

        //System.out.println(str);
        System.out.println("Date : " + date + " time : " + time + " Nr Rows : " + rowCount + " Elapsed Time : " + timeElapsed / 1000 + " seconds");

        try {
        FileWriter myWriter = new FileWriter("C:\\Users\\deonb\\Downloads\\ProdLogs\\KE_LOGS_ENGINE\\keniaLog.txt", true);
        myWriter.write("Date : " + date + " time : " + time + " Nr Rows : " + rowCount + " Elapsed Time : " + timeElapsed / 1000 + " seconds" + "\n");
        myWriter.close();
        //System.out.println("Successfully wrote to the file.");
        } catch (IOException e) {
        //System.out.println("An error occurred writing to the file.");
        e.printStackTrace();
        }
        }
        }
        }*/
